import baseConfig.IdnMembershipApiSimulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class MyFirstTest extends IdnMembershipApiSimulation {
//class MyFirstTest extends Simulation {

  // 1 Common HTTP Configuration
//  val httpConf = http
//    .baseURL("http://localhost:8080/app/")
//    .header("Accept", "application/json")

  def getMembershipBalance() = {
//    repeat(3) {
      exec(http("Get Balance")
        .get("api/membership/v1/cash/balance")
        .check(status.is(200)))
//    }

  }

  // 2 Scenario Definition
  val scn = scenario("Membership Scenario")
    .exec(getMembershipBalance())

  // 3 Load Scenario
  setUp(
//    scn.inject(atOnceUsers(10))
    scn.inject(
      nothingFor(5 seconds),
      constantUsersPerSec(50) during (300 seconds)
//      rampUsersPerSec(1) to (5) during (300 seconds)

    )
  ).protocols(httpConf)


}
