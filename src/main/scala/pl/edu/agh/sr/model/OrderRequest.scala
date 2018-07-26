package pl.edu.agh.sr.model

import akka.actor.ActorRef

case class OrderRequest(title: String, clientRef: ActorRef) extends Serializable
