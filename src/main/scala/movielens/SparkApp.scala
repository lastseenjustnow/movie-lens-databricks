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
        .set("spark.sql.warehouse.dir", "/user/hive/warehouse")
        .set(
          "spark.sql.catalog.spark_catalog",
          "org.apache.spark.sql.delta.catalog.DeltaCatalog"
        )
        .set(
          "spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation",
          "true"
        )

    implicit val spark: SparkSession =
      SparkSession.builder
        .config(conf)
        .enableHiveSupport()
        .getOrCreate()

    // TODO: resolve runtime issues with reflection to render main generic
    //    val mirror = runtimeMirror(getClass.getClassLoader)
    //    val module = mirror.staticModule(args.head)
    //    val table = mirror.reflectModule(module).instance.asInstanceOf[Table]
    //    table.refresh

    run(spark)
  }
}
