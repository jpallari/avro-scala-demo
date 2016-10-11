package avroscalademo

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import com.sksamuel.avro4s.{SchemaFor, ToRecord, AvroOutputStream, AvroInputStream}

import avroscalademo.v1.Person

object Main {
  implicit val personSchema: SchemaFor[Person] = SchemaFor[Person]
  implicit val personToRecord: ToRecord[Person] = ToRecord[Person]

  def main(args: Array[String]): Unit = {
    val person = Person(name = "Harry", email = Seq("harryh@company.com", "h4rr1@hacker.xyz"))

    val personAsBytes: Array[Byte] = {
      val baos = new ByteArrayOutputStream
      val output = AvroOutputStream.binary[Person](baos)
      output.write(person)
      output.close()
      baos.toByteArray
    }

    val personFromBytes: Person = {
      val in = new ByteArrayInputStream(personAsBytes)
      val input = AvroInputStream.binary[Person](in)
      input.iterator.toSeq.head
    }

    println("Schema: " + personSchema())
    println("Person before serializing  : " + person)
    println("Person after deserializing : " + personFromBytes)
  }
}
