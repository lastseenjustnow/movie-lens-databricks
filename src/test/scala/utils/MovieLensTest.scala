package utils

import org.apache.spark.sql.{DataFrame, Encoders}
import org.scalatest.{Matchers, WordSpec}

import java.util.UUID
import scala.reflect.runtime.universe._

trait MovieLensTest extends WordSpec with Matchers with Spark {

  def readInputCsv[T <: Product: TypeTag](path: String): DataFrame = {
    val fullPath = getClass.getClassLoader.getResource(path).getPath

    val csvOptions = Map(
      "header" -> "true",
      "delimiter" -> ","
    )

    val schema = Encoders.product[T].schema

    spark.read
      .schema(schema)
      .options(csvOptions)
      .format("csv")
      .load(fullPath)
  }

  /* Read data as csv and save it in delta format*/
  def prepareDataForTest[T <: Product: TypeTag](fileName: String): String = {
    val csvData = readInputCsv[T](s"ml-latest-small/$fileName")
    val testDeltaPath = s"/tmp/spark-output/${UUID.randomUUID()}/deltaratings/"
    csvData.write.format("delta").save(testDeltaPath)
    testDeltaPath
  }
}
