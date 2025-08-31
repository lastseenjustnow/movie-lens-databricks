terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.92"
    }
    databricks = {
      source  = "databricks/databricks"
      version = "1.82.0"
    }
  }

  required_version = ">= 1.2"
}

provider "aws" {
  profile = "AdministratorAccess-502487623068"
  region  = var.region
}
