package csvfeed

import baseConfig.IdnMembershipApiSimulation
import io.gatling.commons.stats.OK
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TicketPurchase extends IdnMembershipApiSimulation {

  val csvFeeder = csv("migratedUserNamesDev.csv").circular



  def postGiftGiveEventCash() = {
//    feed(csvFeeder).
    exec(http("Post Gift Give Event Cash")
        .post("/internal/membership/v1/gift/give-event-cash")
        .header("Content-Type", "application/json")
        .body(StringBody(
        """{ "campaignId": 1,
           "campaignName": "testCampaign",
           "endDt": "2020-01-30T08:56:59Z",
           "rewardAmount": 100,
           "rewardId": 1,
           "userId": "${id}" }""".stripMargin))
        .check(status.is(200))
        .check(jsonPath("$.resultMessage").is("success"))

    )
  }

  def getBalance() = {
//    repeat(1) {
//      feed(csvFeeder).
        exec(http("Get Specific User")
            .get("api/membership/v1/cash/balance")
            .header("user-id", "${id}")
            .check(jsonPath("$.result").greaterThan("-1"))
            .check(status.is(200)))
//        .pause(1)
//    }
  }



  def postTicketPurchase() = {
    exec(http("Post Ticket Purchase")
        .post("api/membership/v1/ticket/purchase")
        .header("user-id", "${id}")
        .body(ticketPurchaseBody)
        .check(status.is(200))
    )
  }

  def postTicketUse() = {
    exec(http("Post Ticket Use")
        .post("api/membership/v1/ticket/use")
        .header("user-id", "${id}")
        .queryParamMap(ticketUseQueryMap)
        .check(status.is(200))
    )
  }

  def getViewers() = {
    exec(http("Get Viewer End")
        .get("api/membership/v1/viewer/episode/" + episodeId.toString +"/end")
        .header("user-id", "${id}")
    ).
    exec(http("Get Viewer Meta")
        .get("api/membership/v1/viewer/episode/" + episodeId.toString +"/meta")
        .header("user-id", "${id}")
    ).
    exec(http("Get Viewer Download Data")
      .get("api/membership/v1/viewer/get-download-data")
      .header("user-id", "${id}")
        .queryParam("resourceType","ZIP")
        .queryParam("episodeId", episodeId.toString())
    )
  }

  def execAllSimulations() = {
    repeat(2) {
      feed(csvFeeder)
        //.exec(postGiftGiveEventCash())
        .exec(getBalance())
        .exec(postTicketPurchase())
        .doIfOrElse( session => session.status == OK ) {
          exec(postTicketUse())
            .exitHereIfFailed
            .exec(getViewers())
        } {
          exec { session =>
            println("Else Case Triggered session >>>>>>>> " + session)
            println("episodeId: " + episodeId)
            println("ticketPurchaseBody: " + ticketPurchaseBody.bytes.toString())
            session
          }
        }


    }
  }

  val scn = scenario("Balance check scenario with feeder")
      .exec(execAllSimulations())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
