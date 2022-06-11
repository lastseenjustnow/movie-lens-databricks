package calc

import movielens.calc.{S2LoadMovies, S4SplitGenres}
import movielens.struct.Movies
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.lit
import utils.MovieLensTest

class S4SplitGenresTest extends MovieLensTest {

  def mkObjMovies(testDeltaPath: String): S2LoadMovies = {
    object S2LoadMovies extends S2LoadMovies {
      override def deltaPath: String = testDeltaPath
      override def get(implicit spark: SparkSession): DataFrame =
        spark.read.format(writeDataFormat).load(s"$deltaPath")
    }
    S2LoadMovies
  }

  def mkObjGenres(testDeltaPath: String): S4SplitGenres = {
    object S4SplitGenres extends S4SplitGenres {
      override def deltaPath: String = testDeltaPath
      override def get(implicit spark: SparkSession): DataFrame =
        spark.read.format(writeDataFormat).load(s"$deltaPath")
    }
    S4SplitGenres
  }

  "S4SplitGenres" should {

    "correctly transform the data" in {
      import spark.implicits._
      val testPath: String = prepareDataForTest[Movies]("movies.csv")
      val moviesObj = mkObjMovies(testPath)
      val genresObj = mkObjGenres(testPath)
      val tDF = genresObj.transform(moviesObj.get)

      tDF.where($"movieId" === lit(1)).count() shouldEqual 5
    }

  }
}
