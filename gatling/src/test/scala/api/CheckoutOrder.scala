package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object CheckoutOrder {
  val requestParams: Map[String, String] = Map(
    "cart_content"  -> "${cartContent}",
    "total_net"     -> "${totalNet}",
    "trans_id"      -> "${transID}",
    "shipping"      -> "order"
  )

  val checkoutOrder : ChainBuilder = exec(
    http("POST CheckoutOrder")
      .post("/checkout")

      .formParamMap(requestParams)
      .formParamSeq(session => {
        val json = session("cartContent").as[String]

        val pattern = new scala.util.matching.Regex(""""(\d+?__)":(\d+?)""")

        pattern.findAllMatchIn(json).flatMap(matchResult => Seq(
          "p_id[]"       -> matchResult.group(1),
          "p_quantity[]" -> matchResult.group(2)
        )).toSeq
      })

      .check(
        status.is(200),

        regex("""(product_price_\d+?__)" value="(.+?)"""")
          .ofType[(String, String)]
          .findAll
          .transform(_.toMap)
          .saveAs("productPriceMap")
      )
  )
}
