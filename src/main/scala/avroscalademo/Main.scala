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

object Main extends App {
  import PersonSchema._

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

  val personAsJson: String = {
    val baos = new ByteArrayOutputStream()
    val output = AvroOutputStream.json[Person](baos)
    output.write(person)
    output.close()
    baos.toString("UTF-8")
  }

  val personFromJson: Person = {
    val in = new ByteArrayInputStream(personAsJson.getBytes("UTF-8"))
    val input = AvroInputStream.json[Person](in)
    input.singleEntity match {
      case Success(p) => p
      case Failure(e) => throw JsonParseException("could not convert from json")
    }
  }

  val format = RecordFormat[Person]
  val personRecord = format.to(person)
  val personFromRecord = format.from(personRecord)

  case class JsonParseException(msg: String) extends Exception(msg)

  case class Company(persons: Seq[Person])
  val companySchema = AvroSchema[Company]

  println("Schema: " + personSchema)
  println("Person before serializing  : " + person)
  println("Person after deserializing : " + personFromBytes)
  println("Person as Json : "  + personAsJson)
  println("Person from Json : " + personFromJson)
  println("Person as Record : "  + personRecord)
  println("Person from Record : " + personFromRecord)
  println("Company schema: " + companySchema)
}
