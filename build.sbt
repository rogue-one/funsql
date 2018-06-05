name := """funsql"""

version := "1.0"

scalaVersion := "2.12.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"


libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "com.github.julien-truffaut" %%  "monocle-core"  % "1.5.0",
  "com.github.julien-truffaut" %%  "monocle-macro" % "1.5.0",
  "com.github.julien-truffaut" %%  "monocle-law"   % "1.5.0" % "test"
)



unmanagedJars in Compile += file("lib/glide.jar")