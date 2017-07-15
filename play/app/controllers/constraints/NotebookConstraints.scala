package controllers.constraints

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import controllers.forms._
import javax.inject.Inject
import models.Tables.Notebook
import play.api.db.slick._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import controllers.forms.NotebookFormObj.NotebookForm

class NotebookConstraints @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  def updateVerifying(form: NotebookForm): Future[Either[Seq[String], String]] = {
    for {
      c1 <- existsTitle(form.title)
    } yield {
      val errors = Seq(c1.fold(l => l, r => r)).filter { x => x.length() > 0 }
      errors.size == 0 match {
        case true => Right("")
        case false => Left(errors)
      }
    }
  }

  private def existsTitle(title: String): Future[Either[String, String]] = {
    val exists = db.run(Notebook.filter { _.title === title.bind }.exists.result)
    exists.map { x =>
      x match {
        case true => Right("")
        case false => Left("error.not.exists.notebook")
      }
    }
  }
}