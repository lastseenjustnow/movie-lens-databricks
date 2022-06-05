package movielens.calc

import movielens.framework.BronzeTable
import movielens.struct.Movies
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.types.StructType

class S2LoadMovies extends BronzeTable {
  override def schema: StructType = Encoders.product[Movies].schema
  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)
  override def tableName: String = "movies"
}

object S2LoadMovies extends S2LoadMovies
