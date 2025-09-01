variable "movielens_catalog" {
  type    = string
  default = "movielens_catalog"
}

resource "databricks_storage_credential" "movielens-storage-credential" {
  name = aws_iam_role.s3_movielens_access_role.name
  aws_iam_role {
    role_arn = aws_iam_role.s3_movielens_access_role.arn
  }
  comment = "Managed by TF"
}

resource "databricks_external_location" "movielens-external-location" {
  name            = "movielens-data-external-location"
  url             = local.movielens_data
  credential_name = databricks_storage_credential.movielens-storage-credential.name
  comment         = "Managed by TF"
}

resource "databricks_external_location" "movielens-uc-external-location" {
  name            = "movielens-uc-external-location"
  url             = local.movielens_unity_catalog
  credential_name = databricks_storage_credential.movielens-storage-credential.name
  comment         = "Managed by TF"
}

resource "databricks_catalog" "movielens_catalog" {
  name         = var.movielens_catalog
  comment      = "This catalog is managed by Terraform"
  storage_root = databricks_external_location.movielens-uc-external-location.url
}

resource "databricks_schema" "movielens_schema" {
  catalog_name = var.movielens_catalog
  name         = "t_team_movielens"
  comment      = "This schema is managed by Terraform"
  depends_on   = [databricks_catalog.movielens_catalog]
}
