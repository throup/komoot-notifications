package eu.throup
package komoot
package server

import cats.*
import cats.effect.*
import cats.effect.implicits.*
import cats.implicits.*
import eu.throup.komoot.domain.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import messages.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*

object Routes {
  implicit def tweetDecoder[F[_]: Concurrent]
      : EntityDecoder[F, NewUserNotification] = jsonOf[F, NewUserNotification]

  def routes[F[_]: Concurrent]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      // TODO: replace this into an endpoint which meets AWS SNS specs
      case req @ POST -> Root / "demo" =>
        req
          .as[NewUserNotification]
          .flatMap(handleNewUserNotification)
          .flatMap(Ok(_))

      // TODO: remove this; it is just mocking the komoot endpoint for now
      // purely for debugging purposes, on the way to a final implemention
      case req @ POST -> Root / "mock" / "push" =>
        req
          .as[WelcomeNotification]
          .flatMap(mockTheFinalEndpoint)
          .flatMap(Ok(_))
    }
  }

  def handleNewUserNotification[F[_]: Applicative](
      a: NewUserNotification
  ): F[WelcomeNotification] = {
    for {
      _       <- Applicative[F].unit
      receiver = a.id
      no       = WelcomeNotification(
                   Email.unsafeCast("email@example.com"),
                   receiver,
                   "This is the message. Suck it up!",
                   Seq()
                 )
    } yield no
  }

  def mockTheFinalEndpoint[F[_]: Applicative](
      a: WelcomeNotification
  ): F[Unit] = {
    // Don't do anything here.
    // I just want to see the request appear in the logs.
    Applicative[F].unit
  }
}
