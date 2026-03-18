package configuration

object Headers {
  // Headers for /wp-admin/admin-ajax.php requests
  val ajaxHeaders: Map[String, String] = Map(
    "Content-Type"     -> "application/x-www-form-urlencoded; charset=UTF-8",
    "X-Requested-With" -> "XMLHttpRequest",
    "Origin"           -> s"${ScnConfig.appBaseUrl}",
    "Accept"           -> "*/*"
  )

  // Headers for requests with forms
  val formPostHeaders: Map[String, String] = Map(
    "Origin" -> s"${ScnConfig.appBaseUrl}"
  )
}
