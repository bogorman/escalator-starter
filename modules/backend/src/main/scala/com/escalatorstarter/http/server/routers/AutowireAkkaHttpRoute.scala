package com.escalatorstarter.http.server

import io.circe.Json
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import com.github.pjfanning.pekkohttpcirce.FailFastCirceSupport._
// import io.circe.generic.auto._

trait JsonSerializers extends autowire.Serializers[Json, Decoder, Encoder] {
 //TODO error handling improvements (left cases)
 override def write[AnyClassToWrite: Encoder](obj: AnyClassToWrite): Json = {
  // println("JsonSerializers write")
  obj.asJson
 }
 override def read[AnyClassToRead](json: Json)(implicit ev: Decoder[AnyClassToRead]): AnyClassToRead = {
  // println("JsonSerializers read")
  
  val either = ev.decodeJson(json)
  if (either.isLeft) throw new Exception(either.left.get)
  either.right.get
 }
}
object AutoWireCirceServer extends autowire.Server[Json, Decoder, Encoder] with JsonSerializers


object AutowireAkkaHttpRoute {
  import org.apache.pekko.http.scaladsl.server.Directives._
  import org.apache.pekko.http.scaladsl.server._

  /** @param f Need to expose this to user in order to not break macro
    * @return Akka Http route
    */
  def apply(uri: PathMatcher[Unit], f: AutoWireCirceServer.type => AutoWireCirceServer.Router): Route =
    post {
      path(uri / Segments) { paths: List[String] =>
        entity(as[Map[String,Json]]) { argsJson =>
          complete {
            // println("CALLED HERE." + paths)
            // println("argsJson." + argsJson)
            
            // val decodedArgs: Map[String, Json] =
              // readJson[List[(String, String)]](argsString).toMap
              // decode[List[(String, String)]](argsString).right.get

            // val decodedArgs: Map[String, Json] = decode[Map[String, Json]](argsString).right.get

            val router: AutoWireCirceServer.Router = f(AutoWireCirceServer)

            // decode[Map[String, Json]](request.body).right.get
            // router(autowire.Core.Request(paths, decodedArgs))
            
            // onSuccess(request)(buffer => complete(ByteString(buffer)))

            // "ok"
            router(autowire.Core.Request(paths, argsJson))
          }
        }
      }
    }
}