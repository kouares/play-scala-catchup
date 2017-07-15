package services

import scala.concurrent.Future

import controllers.forms.NotebookFormObj.NotebookForm
import javax.inject.Inject
import play.api.Logger
import services.dao.NotebookDao

class NotebookService @Inject() (notebookDao: NotebookDao) {

  private def logger = Logger(classOf[NotebookService])

  def save(form: NotebookForm): Future[Either[String, String]] = {
    notebookDao.add(form)
  }

  def listByTitle(form: NotebookForm): Future[Seq[NotebookForm]] = {
    notebookDao.freeword(form.mainText)
  }

  def list: Future[Seq[NotebookForm]] = {
    notebookDao.list
  }

  def findByTitle(title: String): Future[NotebookForm] = {
    notebookDao.findByTitle(title)
  }

  def update(form: NotebookForm): Future[Either[String, String]] = {
    notebookDao.update(form)
  }

  def remove(title: String): Future[Either[String, String]] = {
    notebookDao.remove(title)
  }
}