data "aws_s3_bucket" "movielens_databricks_bucket" {
  bucket = "movielens-databricks"
}

resource "aws_iam_role" "s3_movielens_access_role" {
  name = "s3-movielens-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      # Allow Databricks EC2 nodes to assume this role
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      },
      # Allow Databricks AWS account to assume role (ExternalId)
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
          AWS = "arn:aws:iam::${var.account_id}:role/${var.movielens_role_name}"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "s3_movielens_full_access" {
  name        = var.movielens_policy_name
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
      },
      {
        "Action" : "sts:AssumeRole",
        "Resource" : "arn:aws:iam::${var.account_id}:role/${var.movielens_policy_name}",
        "Effect" : "Allow"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "s3_attach" {
  role       = aws_iam_role.s3_movielens_access_role.name
  policy_arn = aws_iam_policy.s3_movielens_full_access.arn
}

resource "aws_iam_policy" "databricks_passrole_policy" {
  name        = "databricks-passrole-policy"
  description = "Allow Databricks workspace to pass the S3 access role"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow"
        Action   = "iam:PassRole"
        Resource = aws_iam_role.s3_movielens_access_role.arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "databricks_passrole_attach" {
  role       = var.databricks_workspace_role
  policy_arn = aws_iam_policy.databricks_passrole_policy.arn
}

# Added specifically to connect to directory with artifacts
resource "aws_iam_instance_profile" "s3_movielens_profile" {
  name = "s3-movielens-profile"
  role = aws_iam_role.s3_movielens_access_role.name
}

resource "databricks_instance_profile" "s3_movielens_instance_profile" {
  instance_profile_arn = aws_iam_instance_profile.s3_movielens_profile.arn
  skip_validation      = false
}
