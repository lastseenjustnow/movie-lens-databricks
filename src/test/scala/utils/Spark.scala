package utils

import org.apache.spark.sql.SparkSession

trait Spark {
  implicit lazy val spark: SparkSession = SparkSession.builder
    .master("local[3]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
}
