package movielens.struct

final case class Ratings(
    userId: Int,
    movieId: Int,
    rating: Double,
    timestamp: Long
)
