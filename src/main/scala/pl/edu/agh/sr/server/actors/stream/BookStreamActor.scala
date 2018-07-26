package pl.edu.agh.sr.server.actors.stream

import java.io.IOException
import java.nio.file.{Files, NoSuchFileException, Paths}

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.stream._
import akka.util.ByteString
import pl.edu.agh.sr.model.{BookStreamLine, BookStreamRequest, BookStreamStatus, StreamStatus}

import scala.concurrent.Future
import scala.concurrent.duration._

object BookStreamActor {
  def props() = Props(new BookStreamActor)
}

class BookStreamActor extends Actor with ActorLogging {
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def receive = {
    case request: BookStreamRequest =>
      import context.dispatcher

      val path = Paths.get("books/" + request.title + ".txt")
      val source: Source[ByteString, Future[IOResult]] = FileIO
        .fromPath(path)
      val flow = Flow[ByteString]
        .via(Framing.delimiter(ByteString(System.lineSeparator), 10000))
        .throttle(1, 1 second, 1, ThrottleMode.shaping)
        .map(bs => bs.utf8String)
      val sink  = Sink.foreach[String](elem => request.actorRef ! BookStreamLine(elem))

      try {
        if(!Files.exists(path)) {
          throw new NoSuchFileException("The book does not exist")
        }

        source
          .via(flow)
          .runWith(sink)
          .onComplete {
            done =>
              if (done.isSuccess) {
                request.actorRef ! BookStreamStatus(StreamStatus.FINISHED)
              } else {
                request.actorRef ! BookStreamStatus(StreamStatus.ERROR)
              }
              context.stop(self)
          }
      } catch {
        case e: IOException =>
          request.actorRef ! BookStreamStatus(StreamStatus.NOT_AVAILABLE)
          context.stop(self)
        case e: Exception =>
          request.actorRef ! BookStreamStatus(StreamStatus.ERROR)
          context.stop(self)
      }

  }
}
