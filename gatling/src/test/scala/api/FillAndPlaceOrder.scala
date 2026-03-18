package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import _root_.configuration.ScnConfig
import _root_.configuration.Headers._
import io.gatling.core.structure.ChainBuilder
import objects.OrderBody.constOrderParameters

object FillAndPlaceOrder {
    val requestParams: Map[String, String] = Map(
    "ic_formbuilder_redirect"  -> s"${ScnConfig.appBaseUrl}/thank-you",
    "cart_content"             -> "${cartContent}",
    "trans_id"                 -> "${transID}",
    "total_net"                -> "${totalNet}",
    "cart_name"                -> "${firstname} ${lastname}",
    "cart_address"             -> "${address}",
    "cart_postal"              -> "${zip}",
    "cart_city"                -> "${city}",
    "cart_phone"               -> "${phone}",
    "cart_email"               -> "${email}"
  )

  val placeOrder : ChainBuilder = group("FillAndPlaceOrder") {
    exec(
      http("POST UpdateCountryCode")
        .post("/wp-admin/admin-ajax.php")
        .headers(ajaxHeaders)

        .formParam("action", "ic_state_dropdown")
        .formParam("country_code", "UA")
        .formParam("state_code", "")

        .check(status.is(200))
    )
      .pause(1)

      .exec(http("POST PlaceOrder")
        .post("/checkout")
        .headers(formPostHeaders)

        .formParamMap("${productPriceMap}")
        .formParam("cart_country", "UA")
        .formParam("state_code", "")
        .formParamMap(constOrderParameters)
        .formParamMap(requestParams)

        .check(status.is(200))
        .check(
          currentLocation.is(s"${ScnConfig.appBaseUrl}/thank-you")
        )
      )
  }
}
