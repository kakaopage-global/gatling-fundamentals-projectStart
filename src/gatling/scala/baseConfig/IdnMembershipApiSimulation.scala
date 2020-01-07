package baseConfig

import io.gatling.core.Predef._
import io.gatling.core.body.CompositeByteArrayBody
import io.gatling.http.Predef._

class IdnMembershipApiSimulation extends Simulation {

  def loadTestEnv: String = getProperty("LOAD_TEST_ENV", "dev")
  def numberOfUsers = getProperty("NUMBER_OF_USERS", "2").toInt
  def loadDuringSeconds = getProperty("LOAD_DURING_SEC", "10").toInt
  def isDebugModeOn = getProperty("IS_DEBUG_MODE_ON", "true").toBoolean
  def numberOfRepeat = getProperty("NUMBER_OF_REPEAT", "1").toInt

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val LOGIN_STATUS = "ACTIVE"
  val httpConf = if (loadTestEnv.equals("dev") ) {
    println("----- dev ENVIRONMENT ---")
    http
      .baseURL("http://idn-membership-dev.kgbh39purd.ap-southeast-1.elasticbeanstalk.com/")
      .header("Accept", "application/json")
      .header("auth", "vYKif5ZR3PEGDsG_mC9ok7yjH6fWLlqnnQNfP0Zywqo=")
      .header("login-status", LOGIN_STATUS)
  } else if (loadTestEnv.equals("prod") ) {
    println("----- prod ENVIRONMENT ---")
    http
      .baseURL("http://IDN-Membership-Production.kgbh39purd.ap-southeast-1.elasticbeanstalk.com/")
      .header("Accept", "application/json")
      .header("auth", "vYKif5ZR3PEGDsG_mC9ok7yjH6fWLlqnnQNfP0Zywqo=")
      .header("login-status", LOGIN_STATUS)
  } else {
    println("----- Unknown ENVIRONMENT --- : " + loadTestEnv)
    http
      .baseURL("http://localhost:8080/")
      .header("Accept", "application/json")
      .header("auth", "vYKif5ZR3PEGDsG_mC9ok7yjH6fWLlqnnQNfP0Zywqo=")
      .header("login-status", LOGIN_STATUS)
  }

  //  val episodeId = 7

  val giftGiveEventCashBody = StringBody(
    """{ "campaignId": 1,
           "campaignName": "testCampaign",
           "endDt": "2020-01-30T08:56:59Z",
           "rewardAmount": 200,
           "rewardId": 1,
           "userId": "${id}" }""".stripMargin)

  val ticketPurchaseBodyProd: CompositeByteArrayBody = StringBody(
    """{
       |   "contentId": 18,
       |   "ticketMap": {
       |       "TPCTT010000000000000000016": 2
       |   }
       |}""".stripMargin)

  val ticketPurchaseBodyDev: CompositeByteArrayBody = StringBody(
  """{
     |    "contentId": 4,
     |    "ticketMap": {
     |        "TPCTT010000000000000000240": 2
     |    }
     |}""".stripMargin)

  val ticketPurchaseBody = if (loadTestEnv.equals("dev") ) {
    ticketPurchaseBodyDev
  } else if (loadTestEnv.equals("prod") ) {
    ticketPurchaseBodyProd
  } else {
    ticketPurchaseBodyDev
  }

  val ticketUseQueryMapProd = Map(
    "contentId" -> 18,
    "episodeId" -> 456,
    "isUsingWaitfree" -> false,
    "viewExpired" -> "NONE"
  )

  val ticketUseQueryMapDev = Map(
    "contentId" -> 4,
    "episodeId" -> 7,
    "isUsingWaitfree" -> false,
    "viewExpired" -> "NONE"
  )

  val ticketUseQueryMap = if (loadTestEnv.equals("dev") ) {
    ticketUseQueryMapDev
  } else if (loadTestEnv.equals("prod") ) {
    ticketUseQueryMapProd
  } else {
    ticketUseQueryMapDev
  }
}
