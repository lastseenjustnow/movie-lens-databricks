package movielens.calc

import movielens.framework.SilverTable
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, explode, split}

class S4SplitGenres extends SilverTable {
  override def tableName: String = "movies_split"

  override def transform(
      df: DataFrame
  )(implicit spark: SparkSession): DataFrame = {

    df
      .select(
        col("movieId"),
        col("title"),
        explode(split(col("genres"), """\|""")).as("genre")
      )

  }

  override def refresh(implicit spark: SparkSession): Unit = {
    val movies = S2LoadMovies.get
    write(transform(movies))
  }
}

object S4SplitGenres extends S4SplitGenres
