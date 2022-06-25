package eu.throup
package komoot

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import org.http4s.server.middleware.Logger

object Server {
  def stream[F[_]: Async]: Stream[F, Nothing] = {
    val routes: HttpRoutes[F]    = Routes.routes[F]
    val httpApp: HttpApp[F]      = routes.orNotFound
    val finalHttpApp: HttpApp[F] = Logger.httpApp(true, true)(httpApp)

    Stream.resource(
      EmberServerBuilder
        .default[F]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(finalHttpApp)
        .build >>
        Resource.eval(Async[F].never)
    )
  }.drain
}
