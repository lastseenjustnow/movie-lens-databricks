package calc

import movielens.calc.{S1LoadRatings, S2LoadMovies, S5MoviesTop10}
import movielens.struct.{Movies, Ratings}
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.MovieLensTest
import org.apache.spark.sql.functions.max

class S5MoviesTop10Test extends MovieLensTest {

  "S5MoviesTop10" should {

    "correctly transform the data" in {
      import spark.implicits._
      val ratingsDF = readInputCsv[Ratings]("ml-latest-small/ratings.csv")
      val moviesDF = readInputCsv[Movies]("ml-latest-small/movies.csv")
      val j = ratingsDF
        .as("l")
        .join(moviesDF.as("r"), $"l.movieId" === $"r.movieId")
        .select($"l.movieId", $"title", $"rating", $"userId")
      val tDF = S5MoviesTop10.transform(j)

      // First average rating in df should be maximum one
      tDF.select($"avg_rating").first() shouldEqual tDF
        .select(
          max($"avg_rating")
        )
        .first()

      // Requirement check: at least 5 users rated
      tDF.where($"count_users" < 5).count() shouldEqual 0

      // Requirement check: desc sorting
      val avg_ratings = tDF.select($"avg_rating").as[Double].collect()
      val ord = Ordering.Double.reverse
      avg_ratings.toList.sorted(ord) shouldEqual avg_ratings.toList
    }

  }
}
