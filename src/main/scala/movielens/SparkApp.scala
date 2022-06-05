package movielens

import movielens.framework.Table
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.reflect.runtime.universe._

object SparkApp extends Run {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf =
      new SparkConf()
        .set("spark.delta.merge.repartitionBeforeWrite", "true")
        .set("spark.sql.shuffle.partitions", "10")
        .set("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
        .set(
          "spark.sql.catalog.spark_catalog",
          "org.apache.spark.sql.delta.catalog.DeltaCatalog"
        )

    implicit val spark: SparkSession =
      SparkSession.builder
        .config(conf)
        .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    // TODO: resolve runtime issues with reflection to make main generic
    //    val mirror = runtimeMirror(getClass.getClassLoader)
    //    val module = mirror.staticModule(args.head)
    //    val table = mirror.reflectModule(module).instance.asInstanceOf[Table]
    //    table.refresh

    run(spark)
  }
}
