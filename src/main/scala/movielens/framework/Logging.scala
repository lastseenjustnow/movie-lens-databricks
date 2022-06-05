package movielens.framework
import org.apache.log4j.Logger

trait Logging {
  @transient lazy val log: Logger =
    org.apache.log4j.LogManager.getLogger("sparkLogger")
}
