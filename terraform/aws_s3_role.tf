data "aws_s3_bucket" "movielens_databricks_bucket" {
  bucket = "movielens-databricks"
}

resource "aws_iam_role" "s3_movielens_access_role" {
  name = "s3-movielens-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.databricks_aws_account_id}:root"
        }
        Action = "sts:AssumeRole"
        Condition = {
          StringEquals = {
            "sts:ExternalId" = var.databricks_external_id
          }
        }
      },
      {
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.account_id}:role/${var.movielens-role-name}"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "s3_movielens_full_access" {
  name        = "s3-movielens-full-access"
  description = "Policy granting full access to S3 Movielens bucket"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:*",
        ]
        Resource = [
          data.aws_s3_bucket.movielens_databricks_bucket.arn,
          "${data.aws_s3_bucket.movielens_databricks_bucket.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "s3_attach" {
  role       = aws_iam_role.s3_movielens_access_role.name
  policy_arn = aws_iam_policy.s3_movielens_full_access.arn
}
