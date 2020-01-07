package csvfeed

import baseConfig.IdnMembershipApiSimulation
import io.gatling.commons.stats.OK
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class TicketPurchase extends IdnMembershipApiSimulation {

  val csvFeeder = csv("migratedUserNames-" + loadTestEnv.toLowerCase + ".csv").circular

  def postGiftGiveEventCash() = {
    exec(http("Post Gift Give Event Cash")
        .post("/internal/membership/v1/gift/give-event-cash")
        .header("Content-Type", "application/json")
        .body(giftGiveEventCashBody)
        .check(status.is(200))
        .check(jsonPath("$.resultMessage").is("success"))
    )
  }

  def getBalance() = {
    exec(http("Get Balance")
        .get("api/membership/v1/cash/balance")
        .header("user-id", "${id}")
        .check(jsonPath("$.result").greaterThan("0"))
        .check(status.is(200)))
  }

  def postTicketPurchase() = {
    exec(http("Post Ticket Purchase")
        .post("api/membership/v1/ticket/purchase")
        .header("user-id", "${id}")
        .header("Content-Type", "application/json")
        .body(ticketPurchaseBody)
        .check(status.is(200))
        .check(jsonPath("$.resultCode").is("200"))
    )
  }

  def postTicketUse() = {
    exec(http("Post Ticket Use")
        .post("api/membership/v1/ticket/use")
        .header("user-id", "${id}")
        .header("Content-Type", "application/json")
        .queryParamMap(ticketUseQueryMap)
        .check(status.is(200))
        .check(jsonPath("$.resultCode").is("200"))
    )
  }

  def getViewers() = {
    exec(http("Get Viewer End")
        .get("api/membership/v1/viewer/episode/" + ticketUseQueryMap("episodeId").toString +"/end")
        .header("user-id", "${id}")
    ).
    exec(http("Get Viewer Meta")
        .get("api/membership/v1/viewer/episode/" + ticketUseQueryMap("episodeId").toString +"/meta")
        .header("user-id", "${id}")
    ).
    exec(http("Get Viewer Download Data")
      .get("api/membership/v1/viewer/get-download-data")
      .header("user-id", "${id}")
        .queryParam("resourceType","ZIP")
        .queryParam("episodeId", ticketUseQueryMap("episodeId").toString())
    )
  }

  def execAllSimulations() = {
    repeat(numberOfRepeat) {
      feed(csvFeeder)
        .exec(postGiftGiveEventCash())
        .pause(pauseAfterGiftCash millisecond)
        .exec(getBalance())
        .exec(postTicketPurchase())
        .pause(10 millisecond)
        .doIfOrElse( session => session.status == OK ) {
          exec(postTicketUse())
            .pause(1 seconds)
            .doIfOrElse( session => session.status == OK) {
              exec(getViewers())
            } {
              exec { session =>
                // here some println can be entered to do some debuggin, but it may slow-down the process
                if (isDebugModeOn) {
                  //println("message: " +  )
                  println("Else Case Triggered postTicketUse session >>>>>>>> " + session)
                }
                session
              }
            }


        } {
          exec { session =>
            if (isDebugModeOn) {
              println("Else Case Triggered postTicketPurchase session >>>>>>>> " + session)
            }
            session
          }
        }
        .pause(pauseAfterEachRepeatMs milliseconds)
    }

  }

  val scn = scenario("Purchase and Use Ticket Scenario check scenario with feeder")
      .exec(execAllSimulations())


  setUp(
//    scn.inject(atOnceUsers(2))

    scn.inject(
//      nothingFor(5 seconds)
//      ,constantUsersPerSec(numberOfUsers) during (loadDuringSeconds seconds)
        rampUsers(numberOfUsers).over(5 seconds)
    )

  ).protocols(httpConf)
//    .throttle(
//      reachRps(100) in (5 seconds),
//      holdFor(10 seconds)
//    )

}
