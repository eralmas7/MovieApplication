
name := "helloWorldProject"

organization := "com.sample"

version := "1.0"

scalaVersion := "2.12.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.5.4"
  val akkaHttpV   = "10.0.9"
  val scalaTestV  = "3.0.3"
  val slickV      = "3.2.1"
  val h2V         = "1.4.196"
  val scalaMockV  = "3.6.0"
  Seq(
    "com.h2database"     % "h2"                           % h2V,
    "com.typesafe.akka"  %% "akka-actor"                  % akkaV,
    "com.typesafe.akka"  %% "akka-stream"                 % akkaV,
    "com.typesafe.akka"  %% "akka-testkit"                % akkaV,
    "com.typesafe.akka"  %% "akka-http"                   % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-spray-json"        % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-testkit"           % akkaHttpV,
    "com.typesafe.slick" %% "slick"                       % slickV,
    "com.typesafe.slick" %% "slick-hikaricp"              % slickV,
    "org.flywaydb"       % "flyway-core"                  % slickV,
    "org.scalatest"      %% "scalatest"                   % scalaTestV % "test",
    "org.scalamock"      %% "scalamock-scalatest-support" % scalaMockV % "test",
    "com.typesafe.akka"  %% "akka-http-testkit"           % akkaHttpV % "test"
  )
}