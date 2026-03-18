package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object OpenMainPage {
  val mainPage : ChainBuilder = exec(
    http("GET MainPage")
      .get("/")

      .check(
        status.is(200)
      )
  )
}
