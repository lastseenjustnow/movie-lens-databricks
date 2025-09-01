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
  default = "414351767826" # example for us-east-1, check docs for your region
}

variable "region" {
  type    = string
  default = "ap-south-1"
}

variable "cluster_name" {
  type    = string
  default = "MovieLens cluster"
}

variable "movielens-role-name" {
  type    = string
  default = "s3-movielens-access-role"
}


variable "movielens_data" {
  type    = string
  default = "s3://movielens-databricks/movielens-data/"
}
