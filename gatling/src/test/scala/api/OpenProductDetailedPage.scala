package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object OpenProductDetailedPage {
  val openPDP : ChainBuilder = exec(
    http("GET ProductDetailedPage")
      .get("/products/${productSlug}")

      .check(

        status.is(200),

        regex("""\?p=(\d+)""")
          .saveAs("productID")
      )
  )
}
