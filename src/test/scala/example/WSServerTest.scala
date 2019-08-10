package example

import akka.http.scaladsl.testkit._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.language.postfixOps
import org.scalatest.{Matchers, WordSpec}

class WSServerTest extends WordSpec with Matchers with ScalatestRouteTest {
  val websocketRoute: Route =
    path("greeter") {
      handleWebSocketMessages(WSServer.greeterService)
    }

  "WebSocket request on /greeter" should {

    "upgrade connection" in {
      val wsClient = WSProbe()

      WS("/greeter", wsClient.flow) ~> websocketRoute ~>
        check {
          // check response for WS Upgrade headers
          isWebSocketUpgrade shouldEqual true
        }
    }

    "answer with \"Hello, $username!\"" in {
      val wsClient = WSProbe()

      WS("/greeter", wsClient.flow) ~> websocketRoute ~>
        check {
          wsClient.sendMessage("Olga")
          wsClient.expectMessage("Hello, Olga!")

          wsClient.sendCompletion()
          wsClient.expectCompletion()
        }
    }
  }
}
