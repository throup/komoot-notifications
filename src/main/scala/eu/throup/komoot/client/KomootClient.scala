package eu.throup
package komoot
package client

import cats.effect.{Async, Resource}
import messages.*
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.Async
import io.circe.Encoder
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.client.EmberClientBuilder

trait KomootClient[F[_]] {
  def postWelcomeNotification(notification: WelcomeNotification): F[Unit]
}

object KomootClient {
  def make[F[_]: Async](config: Map[String, String] = Map.empty)(implicit
      h4sClient: Client[F]
  ): KomootClient[F] = new KomootClient[F] {
    override def postWelcomeNotification(
        notification: WelcomeNotification
    ): F[Unit] = postJson(notification, postWelcomeNotificationUri(config))

    private def postJson[J: Encoder](json: J, uri: Uri): F[Unit] = {
      // Http4sDsl provides a DSL instance in IO, but
      // we need to create our own for the provided F.
      val dsl       = new Http4sDsl[F] {}
      val clientDsl = new Http4sClientDsl[F] {}
      import clientDsl.*
      import dsl.*

      h4sClient.expect(POST(json, uri))
    }
  }

  def resource[F[_]: Async](
      config: Map[String, String] = Map.empty
  ): Resource[F, KomootClient[F]] = for {
    client      <- EmberClientBuilder.default.build
    komootClient = make(config)(using summon[Async[F]], client)
  } yield komootClient

  private def postWelcomeNotificationUri(config: Map[String, String]): Uri =
    config
      .get("URL_POST_WELCOME_NOTIFICATION")
      .flatMap(Uri.fromString(_).toOption)
      .getOrElse(Uri.unsafeFromString("http://localhost:8080/mock/push"))
}
