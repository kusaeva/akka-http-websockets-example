package example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws._

import scala.io.StdIn

object WSServer extends App {
  implicit val system: ActorSystem = ActorSystem("example")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def greeterService: Flow[Message, Message, Any] = {
    Flow[Message]
      .collect {
        case TextMessage.Strict(t) => t
      }
      .map { name =>
        TextMessage.Strict(s"Hello, $name!")
      }
  }

  val requestHandler: HttpRequest => HttpResponse = {
    case req @ HttpRequest(GET, Uri.Path("/greeter"), _, _, _) =>
      req.header[UpgradeToWebSocket] match {
        case Some(upgrade) => upgrade.handleMessages(greeterService)
        case None =>
          HttpResponse(400, entity = "Not a valid websocket request!")
      }
    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture =
    Http().bindAndHandleSync(
      requestHandler,
      interface = "localhost",
      port = 8080
    )

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  import system.dispatcher
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
