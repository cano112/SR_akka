package pl.edu.agh.sr.server

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.edu.agh.sr.server.actors.DispatcherActor

object Server extends App {
  val config = ConfigFactory.load()
  val actorSystem = ActorSystem("bookstore", config.getConfig("bookstore").withFallback(config))
  val dispatcher = actorSystem.actorOf(Props[DispatcherActor], "dispatcher")
}
