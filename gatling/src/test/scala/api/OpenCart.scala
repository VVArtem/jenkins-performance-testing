package api

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object OpenCart {
  val openCart : ChainBuilder = exec(
    http("GET OpenCart")
      .get("/cart")
      .check(

        status.is(200),

        regex("""name="cart_content"\s+value='(.+?)'""")
          .saveAs("cartContent"),

        regex("""value="(\d+)" name="trans_id"""")
          .saveAs("transID"),

        regex("""<td class="total_net">(.+?)<\/td>""")
          .saveAs("totalNet")

      )
  )
}
