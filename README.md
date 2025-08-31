![build](https://github.com/lastseenjustnow/movie-lens-databricks/actions/workflows/scala.yml/badge.svg)
[![codecov](https://codecov.io/gh/lastseenjustnow/movie-lens-databricks/branch/master/graph/badge.svg)](https://codecov.io/gh/lastseenjustnow/movie-lens-databricks)

***Important !!! To build and run tests locally:***

1. JDK must be 8 or 11:
   Local tests runtime:
   openjdk version "11.0.15" 2022-04-19 OpenJDK Runtime Environment Homebrew (build 11.0.15+0)
   OpenJDK 64-Bit Server VM Homebrew (build 11.0.15+0, mixed mode)

2. $SPARK_HOME must be set

Instruction to build:

```
> git clone
> install scala through Coursier to ensure presence of sbt
> sbt assembly
```

Run all tests with:

```
sbt test
```

Deployment via utility dbx:

```
dbx deploy --no-rebuild --no-package
```

For local development Terraform & Databricks CLI should be installed.

**Requirements:**

0. Logical division between the staging and transformation is realized at two levels.

    - Staging and transformation split into two classes:
        - BronzeTable abstract class implementing readSource method; ingestion part of a data pipeline
        - SilverTable abstract class; transformation/feature engineering part
    - Every read-write step implemented as a separate class, hence in the pipeline they must be parallelized

1. Ratings data write implemented in S1LoadRatings class, .write method. Upsert to delta table realized through
   DeltaTable class. Partitioning considerations:
    1. Chosen strategy - partition by movieId hash. Allows eliminating shuffle and evenly distributing data within
       partitions. Hash function is used to avoid potential skewed hot partitions. A bit tough to implement, naive
       solution .partitionBy(hash(movieId) % mod <number of nodes%>)
    2. Default partitioning strategy - partitioning by time - for swift daily insertion of increments. Useless as the
       table is implied to be upserted, so on new ratings all the previous partitions must be read to execute updates.
    3. partition by movie - to eliminate shuffle when grouping. Bottleneck - too many partitions by movie, potential
       query performance degradation when building query plan
    4. partition by movie with buckets - to resolve previous step bottleneck and accelerate joins, but more complex
       implementation - other tables have to be bucketed by movieId as well. This option is to explore with new spark
    5. fixed amount of partitions - for extremely stable environments. Bottlenecks - addition of new cluster nodes
       causes degradation of the service.

2. For all staging tables (bronze) write data format "delta" defined at BronzeTable abstract class.
3. Columns are cast to appropriate data formats through case classes and method .schema defined in BronzeTable Also,
   epochToTimestamp method was added to cast all epochs to timestamps correctly.

4. method .transform of class S4SplitGenres
5. method .transform of class S5MoviesTop10

10 units tests were added. All unit tests pass.


Requirements and improvements:

- [x] Delta functionality
- [x] Business logic
- [x] Partitioning strategy
- [ ] Make main generic
- [ ] Abstract upserts
- [ ] Refactor tests - objects for abstract types are impossible to create

Important docs to recreate env:  

**Azure Databricks:**
Authenticate dbfs with token: https://docs.microsoft.com/en-us/azure/databricks/dev-tools/api/latest/authentication
Copy file to dbfs: https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/#copy-a-file-to-dbfs
Deployment executed via databricks-spark-submit.sh bash script

**AWS (Azure does not allocate enough resources for spark-submit):**
S3 connected to cluster via instance-profiles:
https://docs.databricks.com/administration-guide/cloud-configurations/aws/instance-profiles.html