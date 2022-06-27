package eu.throup
package komoot
package server

import cats.*
import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Random
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
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.syntax.*
import messages.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import io.circe.*
import io.circe.parser.*
import org.http4s.client.Client

object Routes {
  given [F[_]: Concurrent]: EntityDecoder[F, NewUserNotification] =
    jsonOf[F, NewUserNotification]

  def routes[F[_]: Concurrent: Random](using
      client: Client[F],
      komootClient: KomootClient[F]
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    val userRepository = UserRepository.make[F]

    HttpRoutes.of[F] {
      // Endpoint to receive real SNS messages.
      case req @ POST -> Root / "hook" =>
        req
          .as[SnsMessage]
          .flatMap(handleSnsMessage(userRepository))
          .flatMap(Ok(_))

      // Endpoint for testing; receive NewUserNotifications directly.
      case req @ POST -> Root / "demo" =>
        req
          .as[NewUserNotification]
          .flatMap(handleNewUserNotification(userRepository))
          .flatMap(Ok(_))

      // Mocks the komoot endpoint for now.
      // purely for debugging purposes, service defaults to pushing here.
      case req @ POST -> Root / "mock" / "push" =>
        req
          .as[WelcomeNotification]
          .flatMap(mockTheFinalEndpoint)
          .flatMap(Ok(_))

      // Healthcheck
      case GET -> Root =>
        healthcheck().flatMap(Ok(_))
    }
  }

  def handleSnsMessage[F[_]: MonadThrow: Random](
      userRepository: UserRepository[F]
  )(
      message: SnsMessage
  )(using client: Client[F], komootClient: KomootClient[F]): F[Unit] = {
    message.theType match {
      case "Notification" =>
        MonadThrow[F]
          .fromEither(
            for {
              json         <- io.circe.parser.parse(message.message)
              notification <- json.as[NewUserNotification]
            } yield notification
          )
          .flatMap(s => handleNewUserNotification(userRepository)(s))

      case "SubscriptionConfirmation" =>
        for {
          urlString <-
            MonadThrow[F].fromOption(message.subscribeUrl, new Exception)
          uri       <- MonadThrow[F].fromTry(Uri.fromString(urlString).toTry)
          _         <- client.get(uri)(_ => Applicative[F].unit)
        } yield ()

      case _ => Applicative[F].unit
    }
  }

  def handleNewUserNotification[F[_]: Monad: Random](
      userRepository: UserRepository[F]
  )(
      notification: NewUserNotification
  )(using client: KomootClient[F]): F[Unit] = {
    val usecase = ProcessNewUserNotification.make(
      IdentifyNewUser.make,
      SendWelcomeNotification.make(
        summon,
        GenerateMessage.make,
        senderEmail(sys.env)
      ),
      userRepository
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

  def healthcheck[F[_]: Applicative](): F[Unit] = {
    // Just return unit so AWS doesn't terminate the container.
    Applicative[F].unit
  }

  private def senderEmail(config: Map[String, String]): Email =
    config
      .get("SENDER_EMAIL")
      .flatMap(Email.from(_).toOption)
      .getOrElse(Email.unsafeCast("email@example.com"))
}
