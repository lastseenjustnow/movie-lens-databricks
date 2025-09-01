resource "databricks_storage_credential" "movielens-storage-credential" {
  name = aws_iam_role.s3_movielens_access_role.name
  aws_iam_role {
    role_arn = aws_iam_role.s3_movielens_access_role.arn
  }
  comment = "Managed by TF"
}

resource "databricks_external_location" "movielens-external-location" {
  name            = "movielens-data-external-location"
  url             = var.movielens_data
  credential_name = databricks_storage_credential.movielens-storage-credential.name
  comment         = "Managed by TF"
}
