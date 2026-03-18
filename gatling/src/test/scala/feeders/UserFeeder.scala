package feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder

object UserFeeder {

  private val usersFilePath = "data/users.csv"

  val users: BatchableFeederBuilder[String]#F = csv(usersFilePath).circular
}
