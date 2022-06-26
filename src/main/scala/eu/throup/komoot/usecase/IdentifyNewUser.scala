package eu.throup
package komoot
package usecase

import cats.Applicative
import domain.*
import messages.*

trait IdentifyNewUser[F[_]] {
  def apply(notification: NewUserNotification): F[User]
}

object IdentifyNewUser {
  def make[F[_]: Applicative]: IdentifyNewUser[F] =
    (notification: NewUserNotification) =>
      Applicative[F].pure(conversion(notification))

  // Using a simple Conversion for now, as the structures are almost identical.
  // If either structure changes in the future, we may isolate most of the changes here.
  def conversion: Conversion[NewUserNotification, User] =
    (notification: NewUserNotification) =>
      User(
        notification.name,
        notification.id,
        notification.createdAt
      )
}
