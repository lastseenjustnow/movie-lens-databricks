package movielens.framework

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{LongType, StructType, TimestampType}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

abstract class BronzeTable extends Table {

  // To scale data cleaning ops
  type DataCleanUp = DataFrame => DataFrame
  val cleanUps: List[DataCleanUp]
  def readFilePath: String = f"${conf.readFilePath}$tableName.csv"

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

  private def cleanUpData(initialDF: DataFrame): DataFrame = {
    log.info(s"Starting data cleansing for $readFilePath...")
    cleanUps.foldLeft(initialDF) { case (dataFrame, cleanUp) =>
      cleanUp(dataFrame)
    }
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
          f"Dataset '$tableName' was successfully read from $readFilePath"
        )
        df
      case Failure(e) =>
        log.info(
          f"Error has occurred while reading $tableName from $readFilePath. Reason: $e"
        )
        // handle exception properly
        spark.emptyDataFrame
    }
  }

  override def refresh(implicit spark: SparkSession): Unit =
    super.write(cleanUpData(extract(readFilePath)))
}
