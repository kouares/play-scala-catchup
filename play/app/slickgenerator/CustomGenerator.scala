package slickgenerator

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable
import slick.model.Model

object CustomGenerator extends App {
  Console println "*****table model generate start*****"

  val slickDriver = "slick.driver.MySQLDriver"
  val jdbcDriver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://35.187.215.164:3306/play_catchup"
  val username = "playcatchup"
  val password = "playcatchup"

  // 出力
  val outputDir = "app/"
  val pkg = "models"
  val container = "Tables"
  val fileName = "Tables.scala"

  class SourceCodeGeneratorEx(model: Model) extends SourceCodeGenerator(model) {
    override def Table = new Table(_) {
      // auto_incrementを識別できるように変更
      override def autoIncLastAsOption = true
      override def Column = new Column(_) {
        // javaのBlob型へのマッピングをArray[Byte]型に変更、他の型はそのまま
        override def rawType = model.tpe match {
          case "java.sql.Blob" => "Array[Byte]"
          case _ => super.rawType
        }
      }
    }
  }

  def run(slickDriver: String, jdbcDriver: String, url: String, outputDir: String, pkg: String, user: Option[String], password: Option[String], tableName: Option[String]): Unit = {
    val model = tableName.flatMap { tableName => Some(MTable.getTables(tableName)) }
    val driver = Class.forName(slickDriver + "$").getField("MODULE$").get(null).asInstanceOf[JdbcProfile]
    val dbFactory = driver.api.Database
    val db = dbFactory.forURL(url, driver = jdbcDriver, user = user.getOrElse(null), password = password.getOrElse(null), keepAliveConnection = true)
    try {
      val m = Await.result(db.run(driver.createModel(model, false)(ExecutionContext.global).withPinnedSession), Duration.Inf)
      new SourceCodeGeneratorEx(m).writeToFile(slickDriver, outputDir, pkg)
    } finally db.close
  }

  // DB接続を取得
  val driver = slick.driver.MySQLDriver
  val db = { slick.driver.MySQLDriver.api.Database.forURL(url, driver = jdbcDriver, user = username, password = password) }

  // DBモデル取得
  val model = Await.result(db.run(driver.createModel(None, false)(ExecutionContext.global).withPinnedSession), Duration.Inf)

  // カスタマイズしたジェネレータと取得したDBモデルでscalaファイルを作成
  val generator = new SourceCodeGeneratorEx(model)
  generator.writeToFile(slickDriver, outputDir, pkg, "Tables", fileName)

  Console println "*****table model generate finish*****"
}