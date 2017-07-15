package utils

import play.api.data.Form
import play.api.i18n.Messages

object FormHandleHelper {
  
  def withErrors[T](msgCodes: Seq[String], form: Form[T])(implicit messages: Messages): Form[T] = {
    msgCodes.headOption match {
      case Some(msgCode) => {
        val msgCode = msgCodes.head
        withErrors(msgCodes.drop(1), form.withError(msgCode, Messages(msgCode), None))
      }
      case None => form
    }
  }
}