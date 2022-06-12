package movielens.calc

import io.delta.tables.DeltaTable
import movielens.framework.BronzeTable
import movielens.struct.Ratings
import org.apache.spark.sql.functions.{abs, xxhash64}
import org.apache.spark.sql.{DataFrame, SparkSession}

class S1LoadRatings extends BronzeTable[Ratings] {

  override def tableName: String = "ratings"
  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)
  override def partitionColNames: Seq[String] = Seq("partitionId")
  // Can be parameterized from cluster configuration
  val numPartitions = 3

  // TODO: abstract upserts
  def upsert(df: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    val newData =
      df.withColumn("partitionId", abs(xxhash64($"movieId") % numPartitions))

    deltaTable
      .alias("oldData")
      .merge(
        newData.alias("newData"),
        "oldData.userId  = newData.userId and oldData.movieId = newData.movieId"
      )
      .whenMatched()
      .updateAll()
      .whenNotMatched()
      .insertAll()
      .execute()
  }

  override def write(df: DataFrame)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    if (!DeltaTable.isDeltaTable(deltaPath)) {
      super.write(
        df.withColumn(
          "partitionId",
          abs(xxhash64($"movieId") % numPartitions)
        )
      )
    } else upsert(df)
  }

}

object S1LoadRatings extends S1LoadRatings
