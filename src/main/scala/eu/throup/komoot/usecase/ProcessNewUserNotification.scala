package eu.throup
package komoot
package usecase

import cats.*
import cats.implicits.*
import messages.*
import repository.*

trait ProcessNewUserNotification[F[_]] {
  def apply(nun: NewUserNotification): F[Unit]
}

object ProcessNewUserNotification {
  def make[F[_]: Monad](
      identifyNewUser: IdentifyNewUser[F],
      sendWelcomeNotification: SendWelcomeNotification[F],
      repo: UserRepository[F]
  ): ProcessNewUserNotification[F] = (nun: NewUserNotification) => {
    for {
      user   <- identifyNewUser(nun)
      others <- repo.select(3, Set(user.id))
      _      <- repo.create(user)
      _      <- sendWelcomeNotification(user, others)
    } yield ()
  }
}
