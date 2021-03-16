package itemapp


import io.gatling.core.Predef._
import io.gatling.http.Predef._
//import scala.concurrent.duration._

class LoadTest extends Simulation {


  val httpProtocol = http
    .baseUrl("http://localhost:5000/api/v1")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  object GetMainList {
    val get = exec(http("get default list").get("/items?sort=0"))
      .pause(2)
  }

  object Sort {
    val sort = repeat(5, "i"){
      exec(http(s"get sorted list${i}").get("/items?sort=1"))
        .pause(1)
        .exec(http(s"get default sorted list ${i}").get("/items?sort=0"))
        .pause(5)
    }
  }

  object GetItem {
    val getItem = exec(http("get item name = apple").get("/item?name=apple"))
      .pause(3)
  }

  val scn = scenario("guest user get main list").exec(
    GetMainList.get,
    Sort.sort,
    GetItem.getItem
  )

  setUp(scn.inject(atOnceUsers(100)).protocols(httpProtocol))
}
