package com.escalatorstarter

object EscalatorStarterReplServer {

  import org.apache.sshd.server.auth.password.PasswordAuthenticator
  import org.apache.sshd.server.session.ServerSession
  object passwordChecker extends PasswordAuthenticator {
    def authenticate(username: String, password: String, session: ServerSession): Boolean = {
      username == "es" && password == "es"
    }
  }

  def start() = {
    import ammonite.sshd._
    // val bmProps = BMConfig.getProperties

    // val port = bmProps.get("replPort", "22222").toInt

    val port = 44444

    val replServer = new SshdRepl(
      SshServerConfig(
        address = "localhost", // or "0.0.0.0" for public-facing shells
        port = port, // Any available port
        passwordAuthenticator = Some(passwordChecker)))
    replServer.start()
    println("REPL Started on " + port)
    replServer
  }

}
