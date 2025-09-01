data "databricks_spark_version" "latest" {}
data "databricks_node_type" "smallest" {
  local_disk = true
}

resource "databricks_cluster" "movielens_cluster" {
  cluster_name            = var.cluster_name
  spark_version           = data.databricks_spark_version.latest.id
  node_type_id            = data.databricks_node_type.smallest.id
  autotermination_minutes = 20
  autoscale {
    min_workers = 1
    max_workers = 2
  }
  aws_attributes {
    availability           = "SPOT"
    zone_id                = var.region
    first_on_demand        = 1
    spot_bid_price_percent = 100
    instance_profile_arn   = aws_iam_instance_profile.s3_movielens_profile.arn
  }
  no_wait = true
  depends_on = [
    databricks_instance_profile.s3_movielens_instance_profile
  ]
}
