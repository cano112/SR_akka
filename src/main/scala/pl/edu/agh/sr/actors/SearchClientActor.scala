package pl.edu.agh.sr.actors

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import pl.edu.agh.sr.model.{SearchRequest, SearchResponse}
import pl.edu.agh.sr.model.client.SearchClientRequest

object SearchClientActor {
  def props(dispatcher: ActorSelection): Props = Props(new SearchClientActor(dispatcher))
}

class SearchClientActor(val dispatcher: ActorSelection) extends Actor with ActorLogging {
  override def receive = {
    case request: SearchClientRequest =>
      dispatcher ! SearchRequest(request.title, self)
    case response: SearchResponse =>
      if(response.book.isDefined) {
        println(response.book.get.title + ": " + response.book.get.price + "PLN")
      } else {
        println("No such book available")
      }

  }
}
