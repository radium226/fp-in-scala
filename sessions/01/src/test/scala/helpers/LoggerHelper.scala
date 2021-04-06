package helpers

import org.slf4j.LoggerFactory

trait LoggerHelper {

  val logger = LoggerFactory.getLogger(getClass)

  def info(message: String, arguments: Any*): Unit = {
    logger.info(message, arguments: _*)
  }

}
