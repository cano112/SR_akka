package pl.edu.agh.sr.server.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorInitializationException, ActorKilledException, ActorLogging, OneForOneStrategy}
import pl.edu.agh.sr.model._
import pl.edu.agh.sr.server.actors.order.OrderActor
import pl.edu.agh.sr.server.actors.search.SearchActor
import pl.edu.agh.sr.server.actors.stream.BookStreamActor

class DispatcherActor extends Actor with ActorLogging {

  private var actorId: Int = 0

  override def receive = {
    case request: SearchRequest =>
      actorId += 1
      val searcher = context.actorOf(SearchActor.props("db1.txt", "db2.txt"), "searcher-" + actorId)
      searcher ! request
    case request: OrderRequest =>
      actorId += 1
      val orderActor = context.actorOf(OrderActor.props(), "order-" + actorId)
      orderActor ! request
    case request: BookStreamRequest =>
      actorId += 1
      val streamActor = context.actorOf(BookStreamActor.props(), "stream-" + actorId)
      streamActor ! request
  }

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy () {
    case _: ActorKilledException => Escalate
    case _: ActorInitializationException => Escalate
    case _ => Restart
  }
}
