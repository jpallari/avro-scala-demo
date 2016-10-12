package avroscalademo

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.avro.Schema
import com.sksamuel.avro4s.{SchemaFor, ToRecord, AvroOutputStream, AvroInputStream}

import avroscalademo.v1.Person

object PersonSchema {
  // Schema file as a input stream
  private val schemaFile = getClass.getResourceAsStream("/avro/demo.avsc")

  // Schema parsed from the schema file
  val personSchema: Schema = {
    val schemaParser = new Schema.Parser
    schemaParser.parse(schemaFile)
  }

  // Schema provided implicitly
  implicit val personSchemaImplicit: SchemaFor[Person] =
    new SchemaFor[Person] {
      override def apply(): Schema = personSchema
    }

  // Person to generic record converter
  implicit val personToRecord: ToRecord[Person] = ToRecord[Person]
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

  println("Schema: " + personSchema)
  println("Person before serializing  : " + person)
  println("Person after deserializing : " + personFromBytes)
}
