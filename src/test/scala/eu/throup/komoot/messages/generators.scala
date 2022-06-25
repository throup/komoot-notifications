package eu.throup
package komoot
package messages

import domain.{*, given}
import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDateTime

given Arbitrary[NewUserNotification] = Arbitrary {
  for {
    name      <- Arbitrary.arbitrary[UserName]
    id        <- Arbitrary.arbitrary[UserId]
    createdAt <- Arbitrary.arbitrary[LocalDateTime]
  } yield NewUserNotification(name, id, createdAt)
}

given Arbitrary[WelcomeNotification] = Arbitrary {
  for {
    sender        <- Arbitrary.arbitrary[Email]
    receiver      <- Arbitrary.arbitrary[UserId]
    message       <- Arbitrary.arbitrary[String]
    recentUserIds <- Gen.listOf(Arbitrary.arbitrary[UserId])
  } yield WelcomeNotification(sender, receiver, message, recentUserIds)
}
