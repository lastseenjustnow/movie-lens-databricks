variable "jar_name" {
  type    = string
  default = "movie-lens-assembly-1.0.jar"
}

resource "aws_s3_object" "movielens_jar" {
  bucket = data.aws_s3_bucket.movielens_databricks_bucket.id
  key    = "jars/${var.jar_name}"
  source = "${path.module}/../target/scala-2.12/${var.jar_name}"
}
