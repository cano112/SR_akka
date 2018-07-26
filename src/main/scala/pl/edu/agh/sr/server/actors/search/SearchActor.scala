package pl.edu.agh.sr.server.actors.search

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorInitializationException, ActorKilledException, ActorLogging, ActorRef, OneForOneStrategy, Props}
import pl.edu.agh.sr.model.{SearchRequest, SearchResponse}
import pl.edu.agh.sr.server.BookDbHandler

object SearchActor {
  def props(dbFilenames: String*): Props = Props(new SearchActor(dbFilenames))
}

class SearchActor(private val dbFilenames: Seq[String]) extends Actor with ActorLogging {
  private var dbHandlers: List[BookDbHandler] = _
  private var actors: List[ActorRef] = _
  private var responsesCount: Int = 0
  private var finished: Boolean = false
  private var clientRef: ActorRef = _

  override def preStart(): Unit = {
    dbHandlers = dbFilenames.map(file => new BookDbHandler(file)).toList
  }

  override def postStop(): Unit = {
    dbHandlers foreach {handler => handler.close()}
  }

  override def receive = {
    case request: SearchRequest =>
      clientRef = request.clientRef
      search(request)
    case response: SearchResponse =>
      handleResponse(response)
  }

  private def handleResponse(response: SearchResponse): Unit = {
    responsesCount += 1
    if(response.book.isDefined) {
      clientRef ! response
      finished = true
    } else if(responsesCount == actors.length && !finished) {
      clientRef ! SearchResponse(None)
      finished = true
    }
    if(finished) { finishSearch() }
  }

  private def finishSearch(): Unit = {
    actors foreach { actor => context.stop(actor) }
    context.stop(self)
  }

  private def search(request: SearchRequest): Unit = {
    actors = dbHandlers
      .map { handler => context.actorOf(DbSearcherActor.props(handler)) }

    actors foreach { actor => actor ! request }
  }

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy () {
    case _: ActorKilledException => Escalate
    case _: ActorInitializationException => Escalate
    case _ => Restart
  }
}
