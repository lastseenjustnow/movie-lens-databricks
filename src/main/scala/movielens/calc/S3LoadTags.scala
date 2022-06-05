package movielens.calc

import movielens.framework.BronzeTable
import movielens.struct.Tags
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.types.StructType

class S3LoadTags extends BronzeTable {
  override def schema: StructType = Encoders.product[Tags].schema
  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)
  override def tableName: String = "tags"
}

object S3LoadTags extends S3LoadTags
