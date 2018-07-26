package pl.edu.agh.sr.model

import akka.actor.ActorRef

case class BookStreamRequest(title: String, actorRef: ActorRef) extends Serializable
