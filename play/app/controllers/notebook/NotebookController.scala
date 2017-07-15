package controllers.notebook

import com.google.inject.Singleton
import play.api.Logger
import play.api.mvc.{Action, Controller}

@Singleton
class NotebookController extends Controller {
  
  private val logger = Logger(classOf[NotebookController])

  
  def list = Action { implicit rs =>
    Ok
  }

  def create = Action { implicit rs =>
    Ok
  }

  def update(id: Int) = Action { implicit rs =>
    Ok
  }

  def delete(id: Int) = Action { implicit rs =>
    Ok
  }
}