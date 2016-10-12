# Avro Scala demo

Sample project using [avro4s][] and [sbt-avro4s][sbtavro4s] for [Apache Avro][avro] serialization.

## Here's how it works

The Avro schema definitions are placed located in the classpath of the project. In this
project, they're placed in the directory `src/main/resources/avro/`.

During the compilation phase, `sbt-avro4s` picks up the schema definitions,
builds compatible Scala classes, and writes the classes to the managed source
directory: `target/scala-2.11/src_managed/`

In the example `Main` program, the schema is read from the file in the
classpath. This schema can be used for reading and writing binary data to
generic Avro records.

During the program compilation, `avro4s` will automatically generate code for
mapping the generic Avro records to the generated classes. This compile time
code generation is done using Scala macros. `avro4s` can then use the generated
code in combination with the previously read schema to read and write binary
data to the generated classes. This way the mapping code is transparent to the
user.

## Running the demo

You have to have [sbt][] installed. After you've installed `sbt`, run command:

    sbt run

[avro]: http://avro.apache.org/
[avro4s]: https://github.com/sksamuel/avro4s
[sbtavro4s]: https://github.com/sksamuel/sbt-avro4s
[sbt]: http://www.scala-sbt.org/
