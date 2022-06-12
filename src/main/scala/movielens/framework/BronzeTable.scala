package movielens.framework

import org.apache.spark.sql.{Dataset, Encoder, SparkSession}

import scala.util.{Failure, Success, Try}
import scala.reflect.runtime.universe.TypeTag

// Should I make Table class typed instead of BronzeTable? Depends on convention
abstract class BronzeTable[T <: Product: TypeTag]
    extends Table
    with DataCleansing {

  implicit val encoder: Encoder[T] = org.apache.spark.sql.Encoders.product[T]

  def readFilePath: String = f"${conf.readFilePath}$tableName.csv"

  override def writeDataFormat = "delta"

  def extract(path: String)(implicit spark: SparkSession): Dataset[T] = {
    val csvOptions = Map(
      "header" -> "true",
      "delimiter" -> ","
    )

    log.info(s"Reading data from $readFilePath...")

    lazy val f = spark.read
      .schema(encoder.schema)
      .options(csvOptions)
      .format("csv")
      .load(path)
      .as[T]

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
        spark.emptyDataset[T]
    }
  }

  override def refresh(implicit spark: SparkSession): Unit =
    write(cleanUpData(extract(readFilePath).toDF))
}
