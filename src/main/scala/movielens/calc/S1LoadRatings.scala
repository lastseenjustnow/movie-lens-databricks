package movielens.calc

import movielens.framework.BronzeTable
import movielens.struct.Ratings
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Encoders, SparkSession}

class S1LoadRatings extends BronzeTable {

  override def schema: StructType = Encoders.product[Ratings].schema

  override def tableName: String = "ratings"

  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)

  // TODO: abstract upserts
  def upsert(df: DataFrame)(implicit spark: SparkSession): Unit = {
    deltaTable
      .alias("oldData")
      .merge(
        df.alias("newData"),
        "oldData.userId  = newData.userId and oldData.movieId = newData.movieId"
      )
      .whenMatched()
      .updateAll()
      .whenNotMatched()
      .insertAll()
      .execute()
  }

  override def write(df: DataFrame)(implicit spark: SparkSession): Unit = {
    // TODO: add partitioning strategy, see README.md
    upsert(df)
  }

}

object S1LoadRatings extends S1LoadRatings
