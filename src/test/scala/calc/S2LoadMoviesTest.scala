package calc

import movielens.calc.S2LoadMovies
import movielens.struct.Movies
import utils.MovieLensTest
import movielens.framework.Configuration

class S2LoadMoviesTest extends MovieLensTest with Configuration {
  def mkObj(testDeltaPath: String): S2LoadMovies = {
    object S2LoadMovies extends S2LoadMovies {
      override def deltaPath: String = testDeltaPath
    }
    S2LoadMovies
  }

  "S2LoadMovies" should {

    "correctly read the dataset" in {
      val testPath: String = prepareDataForTest[Movies]("movies.csv")
      val obj: S2LoadMovies = mkObj(testPath)
      obj.deltaTable.toDF.count() shouldEqual 9742
    }

    "correctly read configuration & construct read path" in {
      val o = S2LoadMovies
      o.readFilePath shouldEqual s"${conf.readFilePath}${o.tableName}.csv"
    }
  }
}
