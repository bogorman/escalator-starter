import sbt._
import sbt.Keys._
import fm.sbt.S3ResolverPlugin.autoImport._

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

  // S3 endpoint for R2 publishing (write access)
  private def r2Endpoint: String = {
    val accountId = sys.env.getOrElse("R2_ACCOUNT_ID", 
      throw new RuntimeException("R2_ACCOUNT_ID environment variable not set"))
    s"s3://maven.$accountId.r2.cloudflarestorage.com"
  }

  val publishSettings: Seq[Setting[_]] = Seq(
    // Add public resolver for reading dependencies
    resolvers += escalatorResolver,

    // S3/R2 credentials from environment
    s3CredentialsProvider := { _ =>
      val accessKey = sys.env.getOrElse("R2_ACCESS_KEY_ID",
        throw new RuntimeException("R2_ACCESS_KEY_ID environment variable not set"))
      val secretKey = sys.env.getOrElse("R2_SECRET_ACCESS_KEY",
        throw new RuntimeException("R2_SECRET_ACCESS_KEY environment variable not set"))
      
      new com.amazonaws.auth.AWSStaticCredentialsProvider(
        new com.amazonaws.auth.BasicAWSCredentials(accessKey, secretKey)
      )
    },

    // R2 endpoint configuration
    s3Region := com.amazonaws.services.s3.model.Region.US_East_1,

    // Publish to R2
    publishTo := {
      val accountId = sys.env.get("R2_ACCOUNT_ID")
      accountId.map { id =>
        "Escalator R2" at s"s3://maven.$id.r2.cloudflarestorage.com/releases"
      }
    },

    // Publish sources and docs
    publishMavenStyle := true,
    publishArtifact in (Compile, packageSrc) := true,
    publishArtifact in (Compile, packageDoc) := false // faster builds
  )

  // Settings for projects that only consume (no publishing)
  val resolverSettings: Seq[Setting[_]] = Seq(
    resolvers += escalatorResolver
  )
}
