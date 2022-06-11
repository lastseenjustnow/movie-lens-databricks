package movielens.framework

import pureconfig.generic.auto._
import pureconfig.ConfigSource

trait Configuration {
  protected val conf: ServiceConf =
    ConfigSource.default.load[ServiceConf] match {
      case Right(x) => x
      case Left(_) =>
        throw new ExceptionInInitializerError(
          "application.conf was not loaded successfully!"
        )
    }
}
