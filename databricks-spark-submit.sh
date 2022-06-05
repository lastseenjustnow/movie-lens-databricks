#!/bin/bash

# In production env those are the steps of Jenkins pipeline
sbt assembly
dbfs cp target/scala-2.12/movie-lens-assembly-1.0.jar dbfs:/docs/movie-lens-assembly-1.0.jar --overwrite

# run job
curl -n \
-X POST -H 'Content-Type: application/json' \
-d '{ "job_id": "905478279219699" }' https://adb-7869468897916071.11.azuredatabricks.net/api/2.0/jobs/run-now