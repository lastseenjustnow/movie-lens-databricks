package calc

import movielens.calc.S2LoadMovies
import movielens.struct.Movies
import utils.MovieLensTest

class S2LoadMoviesTest extends MovieLensTest {
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

  }
}
