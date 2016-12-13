name := "spray-demo"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing-shapeless2" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % "2.4.7",
    "org.julienrf" %% "play-json-derived-codecs" % "3.3"
  )
}
