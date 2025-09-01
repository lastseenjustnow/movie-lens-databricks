variable "account_id" {
  type    = string
  default = "502487623068"
}

variable "databricks_external_id" {
  type    = string
  default = "cdb29855-f207-42bd-865b-9ca3b82aa619"
}

variable "databricks_aws_account_id" {
  type    = string
  default = "414351767826"
}

variable "databricks_workspace_role" {
  type    = string
  default = "databricks-workspace-stack-ff618-role"
}

variable "region" {
  type    = string
  default = "ap-south-1"
}

variable "cluster_name" {
  type    = string
  default = "MovieLens cluster"
}

variable "movielens_role_name" {
  type    = string
  default = "s3-movielens-access-role"
}

variable "movielens_policy_name" {
  type    = string
  default = "s3-movielens-full-access-policy"
}

variable "movielens_bucket" {
  type    = string
  default = "s3://movielens-databricks/"
}

locals {
  movielens_data          = "${var.movielens_bucket}movielens-data/"
  movielens_unity_catalog = "${var.movielens_bucket}movielens-unity-catalog/"
}
