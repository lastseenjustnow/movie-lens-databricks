package movielens.framework

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{LongType, StructType, TimestampType}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

abstract class BronzeTable extends Table {

  // To scale data cleaning ops
  type DataCleanUp = DataFrame => DataFrame
  val cleanUps: List[DataCleanUp]

  def readFilePath = "dbfs:/mnt/s3_movies22/"

  override def writeDataFormat = "delta"

  def schema: StructType

  /** Converts ALL Long types to Timestamp. Move to a package with all the cleaning tools? */
  val epochToTimestamp: DataCleanUp = df => {
    val cols = df.schema
      .map(sf => (sf.name, sf.dataType))

    df.select(cols.map { case (columnName, dataType) =>
      if (dataType == LongType) col(columnName).cast(TimestampType)
      else col(columnName)
    }: _*)
  }

  private def cleanUpData(initialDF: DataFrame): DataFrame =
    cleanUps.foldLeft(initialDF) { case (dataFrame, cleanUp) =>
      cleanUp(dataFrame)
    }

  def extract(path: String)(implicit spark: SparkSession): DataFrame = {
    val csvOptions = Map(
      "header" -> "true",
      "delimiter" -> ","
    )

    log.info(s"Reading data from $readFilePath...")

    lazy val f = spark.read
      .schema(schema)
      .options(csvOptions)
      .format("csv")
      .load(path)

    Try(f) match {
      case Success(df) =>
        log.info(
          f"Dataset was successfully read $tableName from $readFilePath"
        )
        df
      case Failure(_) =>
        log.info(
          f"Error was occurred while reading $tableName from $readFilePath"
        )
        // handle exception properly
        spark.emptyDataFrame
    }
  }

  override def refresh(implicit spark: SparkSession): Unit =
    write(
      cleanUpData(extract(readFilePath))
    )

}
