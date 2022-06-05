package calc

import movielens.calc.S1LoadRatings
import movielens.struct.Ratings
import org.apache.spark.sql.Dataset
import utils.MovieLensTest

// Delta Lake functionality tested only here
class S1LoadRatingsTest extends MovieLensTest {

  // TODO: Composition over inheritance || pass deltaPath to constructor
  // ensure that testDeltaPath does not overlap
  def mkObj(testDeltaPath: String): S1LoadRatings = {
    object S1LoadRatings extends S1LoadRatings {
      override def deltaPath: String = testDeltaPath
    }
    S1LoadRatings
  }

  implicit val ratingsOrdering: Ordering[Ratings] =
    Ordering.by(s => (s.movieId, s.userId))

  "S1LoadRatings" should {

    "correctly read the dataset" in {
      val testPath: String = prepareDataForTest[Ratings]("ratings.csv")
      val obj: S1LoadRatings = mkObj(testPath)
      obj.deltaTable.toDF.count() shouldEqual 100836
    }

    "not change initial dataframe on empty new data" in {
      import spark.implicits._

      val testPath: String = prepareDataForTest[Ratings]("ratings_small.csv")
      val obj: S1LoadRatings = mkObj(testPath)

      val emptyNewRatings: Dataset[Ratings] = spark.emptyDataset[Ratings]
      val initialRatings = obj.deltaTable.toDF.as[Ratings].collect()
      initialRatings.length should be > 0 // empty initial dataset will not give any valuable info
      obj.write(emptyNewRatings.toDF)
      val afterCalcRatings = obj.deltaTable.toDF.as[Ratings].collect()
      initialRatings.sorted should contain theSameElementsAs afterCalcRatings.sorted
    }

    "update data if it was present in the initial data" in {
      import spark.implicits._

      val newRatings = Seq(
        Ratings(1, 1, 3, 964982703), // rating decreased from 4 -> 3
        Ratings(305, 2987, 5, 1460306169) // rating increased from 4.5 -> 5
      )

      val testPath: String = prepareDataForTest[Ratings]("ratings_small.csv")
      val ratingsETL: S1LoadRatings = mkObj(testPath)
      val initialRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      ratingsETL.write(newRatings.toDF)
      val afterCalcRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      afterCalcRatings.sorted diff initialRatings.sorted shouldBe newRatings
    }

    "add new data if it was not present in the initial data" in {
      import spark.implicits._

      val newRatings = Seq(
        Ratings(10, 1, 3, 964982703),
        Ratings(1, 7, 2, 964982781)
      )

      val testPath: String = prepareDataForTest[Ratings]("ratings_small.csv")
      val ratingsETL: S1LoadRatings = mkObj(testPath)

      val initialRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      ratingsETL.write(newRatings.toDF)
      val afterCalcRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      afterCalcRatings.sorted diff initialRatings.sorted shouldBe newRatings
    }

    "add new data and update old data in the initial data" in {
      import spark.implicits._

      val newRatings = Seq(
        Ratings(1, 1, 3, 964982703), // rating decreased from 4 -> 3
        Ratings(10, 1, 3, 964982703), // new row
        Ratings(1, 7, 2, 964982781), // new row
        Ratings(305, 2987, 5, 1460306169) // rating increased from 4.5 -> 5
      )

      val testPath: String = prepareDataForTest[Ratings]("ratings_small.csv")
      val ratingsETL: S1LoadRatings = mkObj(testPath)
      val initialRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      ratingsETL.write(newRatings.toDF)
      val afterCalcRatings = ratingsETL.deltaTable.toDF.as[Ratings].collect()
      afterCalcRatings.sorted diff initialRatings.sorted shouldBe newRatings
    }
  }

}
