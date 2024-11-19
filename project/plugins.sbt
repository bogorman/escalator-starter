addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "7.4.0")

addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.5.6")

addSbtPlugin("com.vmunier"               % "sbt-web-scalajs"           % "1.2.0")

addSbtPlugin("org.scala-js"              % "sbt-scalajs"               % "1.16.0") //1.10

// addSbtPlugin("io.spray"                  % "sbt-revolver"              % "0.9.1") //
// addSbtPlugin("com.eed3si9n"              % "sbt-assembly"              % "1.0.0")//
// addSbtPlugin("com.typesafe.sbt" 		 % "sbt-twirl" 					% "1.5.1")
// addSbtPlugin("com.typesafe.sbt"          % "sbt-native-packager"       % "1.8.1")//


addSbtPlugin("io.spray"                  % "sbt-revolver"              % "0.10.0") //"0.9.1"
addSbtPlugin("com.eed3si9n"              % "sbt-assembly"              % "2.3.0")//"1.0.0"
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.4")
addSbtPlugin("com.typesafe.play" % "sbt-twirl" % "1.6.1")


// addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta40")

addDependencyTreePlugin

// java 11 with old plugins and 1.7
// java 11 with new plugins and 1.8 
// java 17 with new plugins and 1.8 - works with the extra compile commands