package movielens.framework

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{TimestampType, LongType}

trait DataCleansing {

  // To scale data cleaning ops
  type DataCleanUp = DataFrame => DataFrame
  val cleanUps: List[DataCleanUp]

  protected def cleanUpData(initialDF: DataFrame): DataFrame = {
    cleanUps.foldLeft(initialDF) { case (dataFrame, cleanUp) =>
      cleanUp(dataFrame)
    }
  }

  // From here on I'll put all data cleansing tools
  /** Converts ALL Long types to Timestamp. */
  val epochToTimestamp: DataCleanUp = df => {
    val cols = df.schema
      .map(sf => (sf.name, sf.dataType))

    df.select(cols.map { case (columnName, dataType) =>
      if (dataType == LongType) col(columnName).cast(TimestampType)
      else col(columnName)
    }: _*)
  }

}
