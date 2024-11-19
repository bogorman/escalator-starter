import sbt._
import sbt.Keys._

object JavaVersionCheck {

  // Define the required Java version
  val requiredJavaVersion = "21"

  // A helper function to extract the current Java version
  def getJavaVersion: String = {
    val version = sys.props("java.version")
    version.split("\\.").take(2) match {
      case Array("1", minor) => minor // For older Java versions like 1.8
      case Array(major, _*)  => major // For newer versions like 11, 17, etc.
      case _                 => version // Fallback if structure is different
    }
  }

  // Define an initialize task for Java version checking
  val javaVersionCheck: Def.Initialize[Unit] = Def.setting {
    val currentJavaVersion = getJavaVersion
    if (currentJavaVersion != requiredJavaVersion) {
      sys.error(
        s"Incompatible Java version detected: $currentJavaVersion. Required: $requiredJavaVersion."
      )
    } else {
      println(s"Java version $currentJavaVersion is compatible.")
    }
  }
}
