name := "workdistribution"

version := "0.1"

scalaVersion := "3.0.0"

libraryDependencies += ("com.typesafe.akka" %% "akka-actor-typed" % "2.6.3").cross(CrossVersion.for3Use2_13)

libraryDependencies += ("com.typesafe.akka" %% "akka-slf4j"       % "2.6.3").cross(CrossVersion.for3Use2_13)

libraryDependencies += ("com.typesafe.akka" %% "akka-stream"      % "2.6.3").cross(CrossVersion.for3Use2_13)

libraryDependencies += ("org.scalatest"     %% "scalatest"        % "3.1.0").cross(CrossVersion.for3Use2_13)
