package pl.edu.agh.sr.model

object StreamStatus extends Enumeration with Serializable {
    type StreamStatus = Value
    val ERROR, NOT_AVAILABLE, FINISHED = Value
}
