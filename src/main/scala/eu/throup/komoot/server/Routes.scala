package eu.throup
package komoot
package server

import cats.*
import cats.effect.*
import cats.effect.implicits.*
import cats.implicits.*
import eu.throup.komoot.client.KomootClient
import eu.throup.komoot.domain.*
import eu.throup.komoot.repository.UserRepository
import eu.throup.komoot.usecase.{
  GenerateMessage,
  IdentifyNewUser,
  ProcessNewUserNotification,
  SendWelcomeNotification
}
import io.circe.generic.auto.*
import io.circe.syntax.*
import messages.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*

object Routes {
  given [F[_]: Concurrent]: EntityDecoder[F, NewUserNotification] =
    jsonOf[F, NewUserNotification]

  def routes[F[_]: Concurrent](using client: KomootClient[F]): HttpRoutes[F] = {
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

  def handleNewUserNotification[F[_]: Monad](
      notification: NewUserNotification
  )(using client: KomootClient[F]): F[Unit] = {
    val usecase = ProcessNewUserNotification.make(
      IdentifyNewUser.make,
      SendWelcomeNotification.make(
        summon,
        GenerateMessage.make,
        Email.unsafeCast("email@example.com")
      ),
      UserRepository.make
    )

    usecase(notification)
  }

  def mockTheFinalEndpoint[F[_]: Applicative](
      a: WelcomeNotification
  ): F[Unit] = {
    // Don't do anything here.
    // I just want to see the request appear in the logs.
    Applicative[F].unit
  }
}
