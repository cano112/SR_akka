package pl.edu.agh.sr.server.actors.order

import akka.actor.{Actor, ActorLogging, Props}
import pl.edu.agh.sr.model.{OrderRequest, OrderResponse}
import pl.edu.agh.sr.server.OrderService

object OrderActor {
  def props(): Props = Props(new OrderActor())
}

class OrderActor extends Actor with ActorLogging {
  override def receive = {
    case request: OrderRequest =>
      try {
        OrderService.placeOrder(request.title)
        request.clientRef ! OrderResponse(true)
        context.stop(self)
      } catch {
        case _: Exception =>
          request.clientRef ! OrderResponse(false)
          context.stop(self)
      }
  }
}
