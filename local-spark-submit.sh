#!/bin/bash

# for local tests
$SPARK_HOME/bin/spark-submit \
  --packages io.delta:delta-core_2.12:1.2.1 \
  --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
  --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
  --class "movielens.SparkApp" \
  --master local[4] \
  target/scala-2.12/movie-lens-assembly-1.0.jar 'movielens.calc.S1LoadRatings'