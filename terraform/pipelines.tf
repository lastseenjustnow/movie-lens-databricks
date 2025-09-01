resource "databricks_job" "movielens_job" {
  name = "Run MovieLens"

  task {
    task_key = "Run_MovieLens"

    existing_cluster_id = databricks_cluster.movielens_cluster.id

    spark_jar_task {
      main_class_name = "movielens.SparkApp"
    }
    library {
      jar = "s3://${data.aws_s3_bucket.movielens_databricks_bucket.id}/${aws_s3_object.movielens_jar.id}"
    }
  }
}
