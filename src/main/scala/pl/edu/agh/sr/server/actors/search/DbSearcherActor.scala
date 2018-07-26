package pl.edu.agh.sr.server.actors.search

import akka.actor.{Actor, ActorLogging, Props}
import pl.edu.agh.sr.model.{SearchRequest, SearchResponse}
import pl.edu.agh.sr.server.BookDbHandler

object DbSearcherActor {
  def props(dbHandler: BookDbHandler) = Props(new DbSearcherActor(dbHandler))
}

class DbSearcherActor(dbHandler: BookDbHandler) extends Actor with ActorLogging {
  override def receive = {
    case SearchRequest(title, _) =>
      val book = dbHandler.findBook(title)
      sender() ! SearchResponse(book)
  }
}
