package eu.throup
package komoot
package usecase

import cats.*
import cats.implicits.*
import client.*
import domain.*
import messages.*

trait SendWelcomeNotification[F[_]] {
  def apply(user: User, others: Set[User]): F[Unit]
}

object SendWelcomeNotification {
  def make[F[_]: Monad](
      client: KomootClient[F],
      generateMessage: GenerateMessage[F],
      sender: Email
  ): SendWelcomeNotification[F] = (user: User, others: Set[User]) =>
    for {
      message     <- generateMessage(user, others)
      notification = WelcomeNotification(
                       sender,
                       user.id,
                       message,
                       others.toSeq.map(_.id)
                     )
      _           <- client.postWelcomeNotification(notification)
    } yield ()
}
