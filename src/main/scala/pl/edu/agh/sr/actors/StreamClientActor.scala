package pl.edu.agh.sr.actors

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import pl.edu.agh.sr.model.client.BookStreamClientRequest
import pl.edu.agh.sr.model.{BookStreamLine, BookStreamRequest, BookStreamStatus, StreamStatus}

object StreamClientActor {
  def props(dispatcher: ActorSelection): Props = Props(new StreamClientActor(dispatcher))
}

class StreamClientActor(val dispatcher: ActorSelection) extends Actor with ActorLogging {
  override def receive = {
    case request: BookStreamClientRequest =>
      dispatcher ! BookStreamRequest(request.title, self)
    case line: BookStreamLine =>
      println(line.line)
    case status: BookStreamStatus =>
      status.status match {
        case StreamStatus.NOT_AVAILABLE => println("Book is not available")
        case StreamStatus.FINISHED => println("THE END")
        case StreamStatus.ERROR => println("Error occurred while streaming a book")
      }
  }
}
