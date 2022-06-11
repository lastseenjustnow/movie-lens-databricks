package movielens.framework

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class SilverTable extends Table {
  def transform(df: DataFrame)(implicit spark: SparkSession): DataFrame
}
