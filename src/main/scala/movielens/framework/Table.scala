package movielens.framework

import io.delta.tables.DeltaTable
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

abstract class Table extends Logging with Configuration {
  def schemaName: String = conf.schemaName
  def tableName: String
  def qualifiedName: String = s"$schemaName.$tableName"
  def writeDataFormat: String = "delta"
  def partitionColNames: Seq[String] = Seq()
  def deltaPath = s"${conf.deltaPath}$tableName"
  lazy val deltaTable: DeltaTable = DeltaTable.forPath(deltaPath)

  def refresh(implicit spark: SparkSession): Unit
  def write(df: DataFrame)(implicit spark: SparkSession): Unit = {

    log.info(f"Writing $tableName to $deltaPath...")

    lazy val dfw = {
      df.show()
      df.write
        .format(writeDataFormat)
        .mode("overwrite")
        .partitionBy(partitionColNames: _*)
    }

    lazy val overwrite: Unit =
      dfw.option("path", f"$deltaPath").saveAsTable(f"$schemaName.$tableName")

    lazy val createTable: Unit = {
      dfw.save(deltaPath)
      val sqlQ =
        f"CREATE TABLE $qualifiedName USING DELTA LOCATION '$deltaPath'"
      log.info(s"Performing sql query: $sqlQ...")
      spark.sql(sqlQ)
    }

    Try(overwrite) match {
      case Success(_) =>
        log.info(
          f"Table $tableName was successfully written to $deltaPath."
        )
      case Failure(e) =>
        log.error(
          f"Error has occurred while attempting to write '$tableName' to $deltaPath. Reason: $e"
        )
        log.info(f"Attempting to create a table...")
        createTable
    }
  }

  def get(implicit spark: SparkSession): DataFrame = {
    spark.read.format(writeDataFormat).table(f"$schemaName.$tableName")
  }

}
