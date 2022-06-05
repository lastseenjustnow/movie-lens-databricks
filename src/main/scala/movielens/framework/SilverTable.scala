package movielens.framework

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class SilverTable extends Table {
  override def deltaPath: String = "dbfs:/mnt/s3_movies22/"
  def transform(df: DataFrame)(implicit spark: SparkSession): DataFrame
}
