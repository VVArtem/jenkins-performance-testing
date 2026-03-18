package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import _root_.configuration.Headers._

object AddToCart {
  val requestParams: Map[String, String] = Map(
    "add_cart_data"  -> "current_product=${productID}&cart_content=&current_quantity=1",
    "action"         -> "ic_add_to_cart",
    "cart_widget"    -> "0",
    "cart_container" -> "0"
  )

  val addToCart : ChainBuilder = exec(
    http("POST AddToCart")
      .post("/wp-admin/admin-ajax.php")
      .headers(ajaxHeaders)

      .formParamMap(requestParams)

      .check(
        status.is(200)
      )
  )
}
