package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object OpenProductListPage {
  val openPLP : ChainBuilder = exec(
    http("GET ProductListPage")
      .get("/${categorySlug}")

      .check(

        status.is(200),

        regex("""<a href="http:\/\/localhost\/products\/(.+?)">""")
          .findRandom
          .saveAs("productSlug")

      )
  )
}
