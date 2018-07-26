package pl.edu.agh.sr.actors

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import pl.edu.agh.sr.model.{OrderRequest, OrderResponse}
import pl.edu.agh.sr.model.client.OrderClientRequest

object OrderClientActor {
  def props(dispatcher: ActorSelection): Props = Props(new OrderClientActor(dispatcher))
}

class OrderClientActor(val dispatcher: ActorSelection) extends Actor with ActorLogging {
  override def receive = {
    case request: OrderClientRequest => dispatcher ! OrderRequest(request.title, self)
    case response: OrderResponse =>
      if(response.status) {
        println("Order has been placed")
      } else {
        println("Cannot place order")
      }
  }
}
