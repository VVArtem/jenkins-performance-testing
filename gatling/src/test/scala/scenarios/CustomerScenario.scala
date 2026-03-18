package scenarios

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import api._
import _root_.configuration.ScnConfig
import feeders._
import io.gatling.core.structure.ScenarioBuilder

object CustomerScenario {
  val scn: ScenarioBuilder = scenario("Customer with two categories flow")
    .feed(UserFeeder.users)
    .exec(flushCookieJar)
    .forever {
      exitBlockOnFail {
        exec(flushHttpCache)

          .exec(OpenMainPage.mainPage)
          .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

          .exec(session => session.set("categorySlug", "tables"))
          .exec(OpenProductListPage.openPLP)
          .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

          .exec(OpenProductDetailedPage.openPDP)
          .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

          .exec(AddToCart.addToCart)
          .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

          .randomSwitch(
            //50% Add chair to cart
            50.0 ->
              exec(session => session.set("categorySlug", "chairs"))
                .exec(OpenProductListPage.openPLP)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

                .exec(OpenProductDetailedPage.openPDP)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

                .exec(AddToCart.addToCart)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)
          )

          .randomSwitch(
            //30% Place an order
            30.0 ->
              exec(OpenCart.openCart)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

                .exec(CheckoutOrder.checkoutOrder)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)

                .exec(FillAndPlaceOrder.placeOrder)
                .pause(ScnConfig.thinkTimeMin, ScnConfig.thinkTimeMax)
          )
      }
    }
}
