package controllers.forms

import play.api.data._
import play.api.data.Forms._

object NotebookFormObj {
  case class NotebookForm(title: String, mainText: String, tags: Seq[String])

  val noteBookForm = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 40),
      "mainText" -> text,
      "tags" -> seq(text))(NotebookForm.apply)(NotebookForm.unapply))
}