package baseConfig

import io.gatling.core.Predef._
import io.gatling.core.body.CompositeByteArrayBody
import io.gatling.http.Predef._

class IdnMembershipApiSimulation extends Simulation {
  val LOGIN_STATUS = "ACTIVE"
  val httpConf = http
    .baseURL("http://idn-membership-dev.kgbh39purd.ap-southeast-1.elasticbeanstalk.com/")
//    .baseURL("http://IDN-Membership-Production.kgbh39purd.ap-southeast-1.elasticbeanstalk.com/")
    .header("Accept", "application/json")
    .header("auth", "vYKif5ZR3PEGDsG_mC9ok7yjH6fWLlqnnQNfP0Zywqo=")
//    .header("user-id", "hihitest1")
    .header("login-status", LOGIN_STATUS)

  val episodeId = 456

  val ticketPurchaseBody: CompositeByteArrayBody = StringBody(
    """{
          "contentId": 18,
          "ticketMap": {
              "TPCTT010000000000000000016": 2,
              "TPCTT020000000000000000017": 2
          }
      }""".stripMargin)

  val ticketUseQueryMap = Map(
    "contentId" -> 18,
    "episodeId" -> 456,
    "sUsingWaitfree" -> false,
    "viewExpired" -> "NONE"
  )
}
