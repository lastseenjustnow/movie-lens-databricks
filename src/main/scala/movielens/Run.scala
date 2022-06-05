package movielens

import movielens.calc._
import org.apache.spark.sql.SparkSession

// Temporary trait until the prod runtime issue is resolved
trait Run {
  def run(implicit spark: SparkSession): Unit = {
    S1LoadRatings.refresh
    S2LoadMovies.refresh
    S3LoadTags.refresh
    S4SplitGenres.refresh
    S5MoviesTop10.refresh
  }
}
