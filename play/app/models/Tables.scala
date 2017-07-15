package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Notebook.schema ++ TagMapping.schema ++ TagMst.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Notebook
   *  @param title Database column title SqlType(VARCHAR), PrimaryKey, Length(190,true)
   *  @param mainText Database column main_text SqlType(VARCHAR), Length(10000,true), Default(None)
   *  @param upadtedAt Database column upadted_at SqlType(DATETIME), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME) */
  case class NotebookRow(title: String, mainText: Option[String] = None, upadtedAt: Option[java.sql.Timestamp] = None, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching NotebookRow objects using plain SQL queries */
  implicit def GetResultNotebookRow(implicit e0: GR[String], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[java.sql.Timestamp]): GR[NotebookRow] = GR{
    prs => import prs._
    val r = (<<[String], <<?[String], <<?[java.sql.Timestamp], <<[java.sql.Timestamp])
    import r._
    NotebookRow.tupled((_1, _2, _3, _4)) // putting AutoInc last
  }
  /** Table description of table notebook. Objects of this class serve as prototypes for rows in queries. */
  class Notebook(_tableTag: Tag) extends Table[NotebookRow](_tableTag, "notebook") {
    def * = (title, mainText, upadtedAt, createdAt) <> (NotebookRow.tupled, NotebookRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(title), mainText, upadtedAt, Rep.Some(createdAt)).shaped.<>({r=>import r._; _1.map(_=> NotebookRow.tupled((_1.get, _2, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column title SqlType(VARCHAR), PrimaryKey, Length(190,true) */
    val title: Rep[String] = column[String]("title", O.PrimaryKey, O.Length(190,varying=true))
    /** Database column main_text SqlType(VARCHAR), Length(10000,true), Default(None) */
    val mainText: Rep[Option[String]] = column[Option[String]]("main_text", O.Length(10000,varying=true), O.Default(None))
    /** Database column upadted_at SqlType(DATETIME), Default(None) */
    val upadtedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("upadted_at", O.Default(None))
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
  }
  /** Collection-like TableQuery object for table Notebook */
  lazy val Notebook = new TableQuery(tag => new Notebook(tag))

  /** Entity class storing rows of table TagMapping
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param tagId Database column tag_id SqlType(INT), Default(None) */
  case class TagMappingRow(title: String, tagId: Option[Int] = None)
  /** GetResult implicit for fetching TagMappingRow objects using plain SQL queries */
  implicit def GetResultTagMappingRow(implicit e0: GR[String], e1: GR[Option[Int]]): GR[TagMappingRow] = GR{
    prs => import prs._
    val r = (<<[String], <<?[Int])
    import r._
    TagMappingRow.tupled((_1, _2)) // putting AutoInc last
  }
  /** Table description of table tag_mapping. Objects of this class serve as prototypes for rows in queries. */
  class TagMapping(_tableTag: Tag) extends Table[TagMappingRow](_tableTag, "tag_mapping") {
    def * = (title, tagId) <> (TagMappingRow.tupled, TagMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(title), tagId).shaped.<>({r=>import r._; _1.map(_=> TagMappingRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column tag_id SqlType(INT), Default(None) */
    val tagId: Rep[Option[Int]] = column[Option[Int]]("tag_id", O.Default(None))

    /** Foreign key referencing TagMst (database name tag_mapping_ibfk_1) */
    lazy val tagMstFk = foreignKey("tag_mapping_ibfk_1", tagId, TagMst)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table TagMapping */
  lazy val TagMapping = new TableQuery(tag => new TagMapping(tag))

  /** Entity class storing rows of table TagMst
   *  @param name Database column name SqlType(VARCHAR), Length(100,true), Default(None)
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey */
  case class TagMstRow(name: Option[String] = None, id: Option[Int] = None)
  /** GetResult implicit for fetching TagMstRow objects using plain SQL queries */
  implicit def GetResultTagMstRow(implicit e0: GR[Option[String]], e1: GR[Option[Int]]): GR[TagMstRow] = GR{
    prs => import prs._
    val r = (<<?[Int], <<?[String])
    import r._
    TagMstRow.tupled((_2, _1)) // putting AutoInc last
  }
  /** Table description of table tag_mst. Objects of this class serve as prototypes for rows in queries. */
  class TagMst(_tableTag: Tag) extends Table[TagMstRow](_tableTag, "tag_mst") {
    def * = (name, Rep.Some(id)) <> (TagMstRow.tupled, TagMstRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (name, Rep.Some(id)).shaped.<>({r=>import r._; _2.map(_=> TagMstRow.tupled((_1, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(VARCHAR), Length(100,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(100,varying=true), O.Default(None))
    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table TagMst */
  lazy val TagMst = new TableQuery(tag => new TagMst(tag))
}
