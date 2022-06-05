package calc

import movielens.calc.{S1LoadRatings, S5MoviesTop10}
import movielens.struct.Ratings
import utils.MovieLensTest
import org.apache.spark.sql.functions.max

class S5MoviesTop10Test extends MovieLensTest {

  def mkObjRatings(testDeltaPath: String): S1LoadRatings = {
    object S1LoadRatings extends S1LoadRatings {
      override def deltaPath: String = testDeltaPath
    }
    S1LoadRatings
  }

  def mkObjMoviesTop10(testDeltaPath: String): S5MoviesTop10 = {
    object S5MoviesTop10 extends S5MoviesTop10 {
      override def deltaPath: String = testDeltaPath
    }
    S5MoviesTop10
  }

  "S5MoviesTop10" should {

    "correctly transform the data" in {
      import spark.implicits._
      val testPath: String = prepareDataForTest[Ratings]("ratings.csv")
      val ratingsObj = mkObjRatings(testPath)
      val moviesTopObj = mkObjMoviesTop10(testPath)
      val tDF = moviesTopObj.transform(ratingsObj.get)

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
