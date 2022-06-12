package movielens.calc

import movielens.framework.BronzeTable
import movielens.struct.Movies

class S2LoadMovies extends BronzeTable[Movies] {
  override val cleanUps: List[DataCleanUp] = List(epochToTimestamp)
  override def tableName: String = "movies"
}

object S2LoadMovies extends S2LoadMovies
