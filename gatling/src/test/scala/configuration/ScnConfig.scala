package configuration

object ScnConfig {

  val appBaseUrl: String = System.getProperty("baseUrl", "http://localhost")

  val users: Int = System.getProperty("users", "1").toInt
  val rampUp: Int = System.getProperty("rampUp", "0").toInt
  val duration: Int = System.getProperty("duration", "60").toInt

  val loadType: String = System.getProperty("workloadType", "open")

  val thinkTimeMin: Int = System.getProperty("thinkMin", "1").toInt
  val thinkTimeMax: Int = System.getProperty("thinkMax", "3").toInt
}
