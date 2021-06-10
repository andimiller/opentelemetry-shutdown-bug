name := "opentelemetry-shutdown-bug"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= List(
  "io.opentelemetry"  % "opentelemetry-exporter-otlp" % "1.2.0",
  "io.grpc" % "grpc-okhttp" % "1.38.0",
  "io.opentelemetry" % "opentelemetry-api" % "1.2.0",
  "io.opentelemetry" % "opentelemetry-sdk" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
