package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import _root_.configuration.ScnConfig
import scenarios.CustomerScenario

import scala.concurrent.duration.DurationInt

class Simulation1 extends Simulation {

  private val httpProtocol = http
    .baseUrl(ScnConfig.appBaseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("uk,en-US;q=0.9,en;q=0.8")
    .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//    .proxy(Proxy("localhost", 8888).httpsPort(8888))


  setUp(
    ScnConfig.loadType match {
      case "closed" =>
        // Fixed amount of users during some time
        CustomerScenario.scn.inject(
          constantConcurrentUsers(ScnConfig.users).during(ScnConfig.duration)
        )

      case _ =>
        // Feed some amount of users without looping
        CustomerScenario.scn.inject(
          if (ScnConfig.rampUp > 0) {
            rampUsers(ScnConfig.users).during(ScnConfig.rampUp)
          } else {
            atOnceUsers(ScnConfig.users)
          }
        )
    }
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(900), //95 percentile
      global.successfulRequests.percent.gt(99)
    )
    .maxDuration(ScnConfig.duration)
}