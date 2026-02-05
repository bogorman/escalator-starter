import sbt._
import sbt.Keys._

/**
 * R2/Maven Publishing Configuration
 * 
 * Publishing to Cloudflare R2 via S3-compatible API.
 * Public artifacts served from https://maven.escalator.dev
 * 
 * Required environment variables for publishing:
 *   R2_ACCESS_KEY_ID     - Cloudflare R2 Access Key ID
 *   R2_SECRET_ACCESS_KEY - Cloudflare R2 Secret Access Key
 *   R2_ACCOUNT_ID        - Cloudflare Account ID
 * 
 * To publish: sbt <project>/publish
 * To resolve: artifacts are auto-resolved from maven.escalator.dev
 */
object R2Publishing {

  // Public resolver - no credentials needed for reading
  val escalatorResolver = "Escalator Maven" at "https://maven.escalator.dev/releases"

  val publishSettings: Seq[Setting[_]] = Seq(
    // Add public resolver for reading dependencies
    resolvers += escalatorResolver,

    // Publish to R2 via S3 resolver
    publishTo := {
      val accountId = sys.env.get("R2_ACCOUNT_ID")
      accountId.map { id =>
        "Escalator R2" at s"s3://$id.r2.cloudflarestorage.com/scalajs-artifacts/releases"
      }
    },

    // Publish sources but not docs (faster builds)
    publishMavenStyle := true,
    Compile / packageSrc / publishArtifact := true,
    Compile / packageDoc / publishArtifact := false
  )

  // Settings for projects that only consume (no publishing)
  val resolverSettings: Seq[Setting[_]] = Seq(
    resolvers += escalatorResolver
  )
}
