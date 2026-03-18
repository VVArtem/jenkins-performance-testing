package objects

object OrderBody {
  val constOrderParameters : Map[String, Any] = Map(
    "shipping"                -> "order",
    "cart_type"               -> "order",
    "cart_submit"             -> "Place Order",

    "cart_inside_header_1"    -> "<b>BILLING ADDRESS</b>",
    "cart_state"              -> "",
    "cart_company"            -> "",
    "cart_comment"            -> "",

    "cart_inside_header_2"    -> "<b>DELIVERY ADDRESS</b> (FILL ONLY IF DIFFERENT FROM THE BILLING ADDRESS)",
    "cart_s_company"          -> "",
    "cart_s_name"             -> "",
    "cart_s_address"          -> "",
    "cart_s_postal"           -> "",
    "cart_s_city"             -> "",
    "cart_s_country"          -> "",
    "cart_s_state"            -> "",
    "cart_s_phone"            -> "",
    "cart_s_email"            -> "",
    "cart_s_comment"          -> "",

    "cart_submit"             -> "Place Order"
  )
}
