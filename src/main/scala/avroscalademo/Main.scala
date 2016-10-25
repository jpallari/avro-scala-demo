package avroscalademo

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.apache.avro.Schema
import com.sksamuel.avro4s._
import avroscalademo.v1.Person

import scala.util.{Failure, Success}

object PersonSchema {
  // Schema file as a input stream
  private val schemaFile = getClass.getResourceAsStream("/avro/demo.avsc")

  // Schema parsed from the schema file
  val personSchema: Schema = {
    val schemaParser = new Schema.Parser
    schemaParser.parse(schemaFile)
  }

  // Schema provided implicitly for avro4s code
  implicit val personSchemaImplicit: SchemaFor[Person] =
    new SchemaFor[Person] {
      override def apply(): Schema = personSchema
    }
}

case class Company(persons: Seq[Person])

object Company {
  // Generate a schema based on an existing class using avro4s at compile time.
  val companySchema = AvroSchema[Company]
}

object Main extends App {
  import PersonSchema._
  import Company._

  // Example person object
  val person = Person(name = "Harry", email = Seq("harryh@company.com", "h4rr1@hacker.xyz"))

  // Object converted to bytes
  val personAsBytes: Array[Byte] = {
    val baos = new ByteArrayOutputStream
    val output = AvroOutputStream.binary[Person](baos)
    output.write(person)
    output.close()
    baos.toByteArray
  }

  // Bytes converted back to an object
  val personFromBytes: Person = {
    val in = new ByteArrayInputStream(personAsBytes)
    val input = AvroInputStream.binary[Person](in)
    input.iterator.toSeq.head
  }

  // Object converted to JSON string
  val personAsJson: String = {
    val baos = new ByteArrayOutputStream()
    val output = AvroOutputStream.json[Person](baos)
    output.write(person)
    output.close()
    baos.toString("UTF-8")
  }

  // JSON string converted back to an object
  val personFromJson: Person = {
    val in = new ByteArrayInputStream(personAsJson.getBytes("UTF-8"))
    val input = AvroInputStream.json[Person](in)
    input.singleEntity match {
      case Success(p) => p
      case Failure(e) => sys.error("could not convert from json")
    }
  }

  // Conversion from an object to a generic Avro record back to an object
  val format = RecordFormat[Person]
  val personRecord = format.to(person)
  val personFromRecord = format.from(personRecord)

  println("Company schema             : " + companySchema)
  println("Person schema              : " + personSchema)
  println("Person before serializing  : " + person)
  println("Person after deserializing : " + personFromBytes)
  println("Person as Json             : " + personAsJson)
  println("Person from Json           : " + personFromJson)
  println("Person as Record           : " + personRecord)
  println("Person from Record         : " + personFromRecord)
}
