package movielens.calc

import movielens.framework.SilverTable
import org.apache.spark.sql.functions.{avg, col, count}
import org.apache.spark.sql.{DataFrame, SparkSession}

class S5MoviesTop10 extends SilverTable {
  override def tableName: String = "movies_top10"

  override def transform(
      df: DataFrame
  )(implicit spark: SparkSession): DataFrame = {

    df
      .groupBy(col("movieId"), col("title"))
      .agg(
        avg("rating").as("avg_rating"),
        count("userId").as("count_users")
      )
      .where(col("count_users") >= 5)
      .orderBy(col("avg_rating").desc)

  }

  override def refresh(implicit spark: SparkSession): Unit = {
    import spark.implicits._
    val ratings = S1LoadRatings.get
    val movies = S2LoadMovies.get

    write(
      transform(
        ratings
          .as("l")
          .join(movies.as("r"), $"l.movieId" === $"r.movieId")
          .select($"l.movieId", $"title", $"rating", $"userId")
      )
    )

  }
}

object S5MoviesTop10 extends S5MoviesTop10
