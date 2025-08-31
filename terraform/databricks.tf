provider "databricks" {
  // other configuration
  account_id = "cdb29855-f207-42bd-865b-9ca3b82aa619"
}

data "databricks_spark_version" "latest" {}
data "databricks_node_type" "smallest" {
  local_disk = true
}

resource "databricks_cluster" "this" {
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
  }
}