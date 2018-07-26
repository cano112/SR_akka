package pl.edu.agh.sr.model

import akka.actor.ActorRef

case class SearchRequest(title: String, clientRef: ActorRef) extends Serializable
