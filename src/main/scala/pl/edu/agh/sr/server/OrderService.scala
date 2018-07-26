package pl.edu.agh.sr.server

import java.io.{BufferedWriter, File, FileWriter}

object OrderService {
  private val bufferedWriter = new BufferedWriter(new FileWriter(new File("orders.txt"), true))

  def placeOrder(title: String): Unit = bufferedWriter.synchronized {
    bufferedWriter.write(title)
    bufferedWriter.newLine()
    bufferedWriter.flush()
  }

  def close(): Unit = bufferedWriter.close()
}
