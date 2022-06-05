#!/bin/bash

curl -n \
-X POST -H 'Content-Type: application/json' \
-d '{
     "name": "S1LoadRatings",
     "new_cluster": {
       "spark_version": "7.3.x-scala2.12",
       "node_type_id": "Standard_DS3_v2",
       "num_workers": 1
       },
    "spark_submit_task": {
       "parameters": [
         "--num-executors",
         "1",
         "--class",
         "movielens.calc.S1LoadRatings",
         "--packages",
         "io.delta:delta-core_2.12:1.2.1",
         "--conf",
         "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension",
         "--conf",
         "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog",
         "dbfs:/docs/movie-lens-assembly-1.0.jar",
         "10"
      ]
    }
}' https://adb-7869468897916071.11.azuredatabricks.net/api/2.0/jobs/create