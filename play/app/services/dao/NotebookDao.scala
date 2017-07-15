package services.dao

import java.sql.Timestamp

import scala.Left
import scala.Right
import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controllers.forms.NotebookFormObj.NotebookForm
import javax.inject.Inject
import models.Tables._
import play.api.Logger
import play.api.db.slick._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

class NotebookDao @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  private def logger = Logger(classOf[NotebookDao])

  /**
    *
    * @param form
    * @return
    */
  def add(form: NotebookForm): Future[Either[String, String]] = {
    val insertNoteAction = {
      Notebook += NotebookRow(form.title, Some(form.mainText), None, new Timestamp(System.currentTimeMillis()))
    }.map { x =>
      x == 1 match {
        case true => DBIO.successful(false)
        case false => throw new RuntimeException("db.create.notebook.error")
      }
    }

    val insertTagMstAction = for {
      tag <- form.tags
    } yield {
      TagMst.filter(_.name === tag).exists.result.flatMap {
        case true => DBIO.successful(false)
        case false => TagMst returning TagMst.map { _.id } += TagMstRow(Some(tag))
      }
    }

    val insertTagMappingAction = {
      TagMst.filter(x => x.name.inSetBind(form.tags)).result.flatMap { x =>
        TagMapping ++= x.map { x => TagMappingRow(form.title, Some(x.id.fold(throw new RuntimeException("db.tagmap.relation.error"))(f => f))) }
      }
    }

    val actions = DBIO.seq(
      insertNoteAction, DBIO.sequence(insertTagMstAction), insertTagMappingAction)
      .transactionally

    db.run(actions).map { _ =>
      Right("")
    }.recover {
      case e => {
        Left(e.getMessage)
      }
    }
  }

  def freeword(mainText: String): Future[Seq[NotebookForm]] = {
    val query1 = Notebook.filter(_.mainText like s"%$mainText%")
      .map { x => (x.title, x.mainText.getOrElse("")) }
      .result
    val query2 = Notebook.filter(_.mainText like s"%$mainText%")
      .join(TagMapping).on((n, tmp) => n.title === tmp.title)
      .joinLeft(TagMst).on((j1, t) => j1._2.tagId === t.id)
      .map(f => (f._1._1.title, f._2.map { x => x.name.getOrElse("") }))
      .result

    val result1 = db.run { query1 }
    val result2 = db.run { query2 }

    for {
      notes <- result1
      tags <- result2
    } yield {
      for {
        note <- notes
      } yield {
        NotebookForm(note._1, note._2, tags.filter(p => p._1 == note._1).map(f => f._2.getOrElse("")))
      }
    }
  }

  def list: Future[Seq[NotebookForm]] = {
    val query1 = Notebook.sortBy { n => n.title }
      .map { x => (x.title, x.mainText.getOrElse("")) }
      .result
    val query2 = Notebook.join(TagMapping).on((n, tmp) => n.title === tmp.title)
      .joinLeft(TagMst).on((j1, t) => j1._2.tagId === t.id)
      .map(f => (f._1._1.title, f._2.map { x => x.name.getOrElse("") }))
      .result

    val result1 = db.run { query1 }
    val result2 = db.run { query2 }

    for {
      notes <- result1
      tags <- result2
    } yield {
      for {
        note <- notes
      } yield {
        NotebookForm(note._1, note._2, tags.filter(p => p._1 == note._1).map(f => f._2.getOrElse("")))
      }
    }
  }

  def findByTitle(title: String): Future[NotebookForm] = {
    val query1 = Notebook.filter(t => t.title === title.bind)
      .map { x => (x.title, x.mainText.getOrElse("")) }
      .result.head

    val query2 = Notebook.filter { x => x.title === title.bind }
      .join(TagMapping).on((n, tmp) => n.title === tmp.title)
      .join(TagMst).on((j1, tm) => j1._2.tagId === tm.id)
      .map(f => f._2.name.getOrElse(""))
      .result

    val result1 = db.run(query1)
    val result2 = db.run(query2)

    for {
      note <- result1
      tags <- result2
    } yield {
      NotebookForm(note._1, note._2, tags)
    }
  }

  def update(form: NotebookForm): Future[Either[String, String]] = {
    val updatedAt = new Timestamp(System.currentTimeMillis())
    val note = NotebookRow(form.title, Some(form.mainText), Some(updatedAt), updatedAt)
    val noteBookUpdate = Notebook.filter { x => x.title === note.title.bind }
      .map { x => (x.title, x.mainText, x.upadtedAt) }
      .update { (note.title, note.mainText, note.upadtedAt) }
      .map { x =>
        x == 1 match {
          case true => DBIO.successful(false)
          case false => throw new RuntimeException("db.update.notebook.error")
        }
      }

    val tagMstInsert = for {
      tag <- form.tags.filter { !_.endsWith("-remove") }
    } yield {
      TagMst.filter { x => x.name === tag }.exists.result.flatMap {
        case true => DBIO.successful(false)
        case false => TagMst += TagMstRow(Some(tag))
      }
    }

    val tagMapUpdate = for {
      tag <- form.tags
    } yield {
      tag.endsWith("-remove") match {
        case true => {
          TagMst.filter { tagmst => tagmst.name === tag.replace("-remove", "") }.result.head.flatMap { tagmst =>
            TagMapping.filter { tagmap => tagmap.title === form.title.bind && tagmap.tagId === tagmst.id }.delete
          }
        }
        case false => {
          TagMst.filter { tagmst => tagmst.name === tag }.result.head.flatMap { tagmst =>
            TagMapping.filter { tagmap => tagmap.title === form.title.bind && tagmap.tagId === tagmst.id }.exists.result.flatMap {
              case true => DBIO.successful(false)
              case false => TagMapping += TagMappingRow(form.title, Some(tagmst.id.getOrElse(-1)))
            }
          }
        }
      }
    }

    val actions = DBIO.seq(
      noteBookUpdate, DBIO.sequence(tagMstInsert), DBIO.sequence(tagMapUpdate)).transactionally

    db.run(actions).map { _ =>
      Right("")
    }.recover {
      case e => {
        Left(e.getMessage)
      }
    }
  }

  def remove(title: String): Future[Either[String, String]] = {
    db.run(Notebook.filter { note => note.title === title.bind }.delete.flatMap { _ =>
      TagMapping.filter { tagmap => tagmap.title === title.bind }.delete
    }).map { _ =>
      Right("")
    }.recover {
      case e => {
        Left(e.getMessage)
      }
    }
  }
}