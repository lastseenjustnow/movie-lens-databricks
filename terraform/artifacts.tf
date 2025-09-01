variable "jar_name" {
  type    = string
  default = "movie-lens-assembly-1.0.jar"
}

locals {
  jar_path = "${path.module}/../target/scala-2.12/${var.jar_name}"
}

resource "aws_s3_object" "movielens_jar" {
  bucket = data.aws_s3_bucket.movielens_databricks_bucket.id
  key    = "jars/${var.jar_name}"
  source = local.jar_path
  etag   = filemd5(local.jar_path)
}
