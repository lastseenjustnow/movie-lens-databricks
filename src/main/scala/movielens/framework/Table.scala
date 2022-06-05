package movielens.framework

import io.delta.tables.DeltaTable
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

// TODO: make typed for Datasets usage
abstract class Table extends Logging {
  def schemaName: String = "t_team_movielens"

  def tableName: String

  def writeDataFormat: String = "delta"

  def deltaPath = s"/$tableName"

  val deltaTable: DeltaTable = DeltaTable.forPath(deltaPath)

  def refresh(implicit spark: SparkSession): Unit

  def write(df: DataFrame)(implicit spark: SparkSession): Unit = {

    log.info(f"Writing $tableName to $deltaPath...")

    lazy val f: Unit = df.write
      .format(writeDataFormat)
      .mode("overwrite")
      .save(f"$deltaPath")

    Try(f) match {
      case Success(_) =>
        log.info(f"Table $tableName was successfully written to $deltaPath ")
      case Failure(_) =>
        log.error(f"Error had occurred while $tableName to $deltaPath ")
    }
  }

  def get(implicit spark: SparkSession): DataFrame = {
    spark.read
      .format(writeDataFormat)
      .load(s"$deltaPath")
  }

}
