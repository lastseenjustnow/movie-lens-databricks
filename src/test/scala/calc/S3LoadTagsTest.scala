package calc

import movielens.calc.S3LoadTags
import movielens.struct.Tags
import utils.MovieLensTest

class S3LoadTagsTest extends MovieLensTest {
  def mkObj(testDeltaPath: String): S3LoadTags = {
    object S3LoadTags extends S3LoadTags {
      override def deltaPath: String = testDeltaPath
    }
    S3LoadTags
  }

  "S3LoadTags" should {

    "correctly read the dataset" in {
      val testPath: String = prepareDataForTest[Tags]("tags.csv")
      val obj: S3LoadTags = mkObj(testPath)
      obj.deltaTable.toDF.count() shouldEqual 3683
    }

  }
}
