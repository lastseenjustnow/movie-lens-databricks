#!/bin/bash

# In production env those are the steps of Jenkins pipeline.
# The script assumes that in the executed environment Databricks CLI is installed and configured under profile AZURE.
sbt assembly
dbfs cp target/scala-2.12/movie-lens-assembly-1.0.jar dbfs:/docs/movie-lens-assembly-1.0.jar --overwrite --profile AZURE

DATABRICKS_WORKSPACE_URL=`cat ~/.databrickscfg | grep "azuredatabricks" | awk '{ print substr ($0, 8 ) }'`
MOVIE_LENS_JOB_ID=`databricks jobs list --profile AZURE | grep "MovieLens_SparkJob" | grep -o '[0-9]\+'`

# run job
curl -n \
-X POST -H 'Content-Type: application/json' \
-d '{ "job_id": "'"$MOVIE_LENS_JOB_ID"'" }' $DATABRICKS_WORKSPACE_URL/api/2.0/jobs/run-now