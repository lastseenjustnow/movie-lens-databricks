package movielens.struct

final case class Tags(
    userId: Int,
    movieId: Int,
    tag: String,
    timestamp: Long
)
