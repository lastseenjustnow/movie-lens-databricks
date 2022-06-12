package movielens.calc

import movielens.framework.BronzeTable
import movielens.struct.Tags

class S3LoadTags extends BronzeTable[Tags] {
  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)
  override def tableName: String = "tags"
}

object S3LoadTags extends S3LoadTags
