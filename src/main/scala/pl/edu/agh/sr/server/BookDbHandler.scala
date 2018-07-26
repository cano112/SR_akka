package pl.edu.agh.sr.server

import pl.edu.agh.sr.exceptions.FormatException
import pl.edu.agh.sr.model.Book

import scala.io.Source

class BookDbHandler(private val filename: String) {
  private val bufferedSource = Source.fromFile(filename)

  def findBook(title: String): Option[Book] = {
    for (line <- bufferedSource.getLines) {
      val book = parseBookFromLine(line)
      if(book.isDefined && book.get.title == title) {
        return book
      }
    }
    None
  }

  private def parseBookFromLine(line: String): Option[Book] = {
    val splittedLine = line.split(",")
    try {
      if (splittedLine.length != 2) throw new FormatException
      Option(Book(splittedLine(0), BigDecimal(splittedLine(1))))
    } catch {
      case e: RuntimeException => None
    }
  }

  def close(): Unit = bufferedSource.close
}
