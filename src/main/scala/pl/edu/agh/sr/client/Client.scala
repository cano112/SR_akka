package pl.edu.agh.sr.client

import java.util.Scanner

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import pl.edu.agh.sr.actors.{OrderClientActor, SearchClientActor, StreamClientActor}
import pl.edu.agh.sr.model.client.{BookStreamClientRequest, OrderClientRequest, SearchClientRequest}

object Client extends App {
  val config = ConfigFactory.load()
  val actorSystem = ActorSystem("bookstore-client", config.getConfig("bookstore-client").withFallback(config))
  val dispatcher = actorSystem
    .actorSelection("akka.tcp://bookstore@127.0.0.1:2552/user/dispatcher")

  val streamClient = actorSystem.actorOf(StreamClientActor.props(dispatcher), "stream-client")
  val searchClient = actorSystem.actorOf(SearchClientActor.props(dispatcher), "search-client")
  val orderClient = actorSystem.actorOf(OrderClientActor.props(dispatcher), "order-client")


  val scanner = new Scanner(System.in)
  while(true) {
    printMenu()
    val key = scanner.nextLine()
    println("Book title: ")

    key match {
      case "1" =>
        val title = scanner.nextLine()
        searchClient ! SearchClientRequest(title)
      case "2" =>
        val title = scanner.nextLine()
        orderClient ! OrderClientRequest(title)
      case "3" =>
        val title = scanner.nextLine()
        streamClient ! BookStreamClientRequest(title)
      case _ =>
    }
  }

  private def printMenu(): Unit = {
    println("[1] Check price")
    println("[2] Place order")
    println("[3] Stream book")
  }
}
