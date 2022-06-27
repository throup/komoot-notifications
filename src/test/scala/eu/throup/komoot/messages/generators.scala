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

given Arbitrary[SnsMessage] = Arbitrary {
  for {
    theType          <- Arbitrary.arbitrary[String]
    messageId        <- Arbitrary.arbitrary[String]
    topicArn         <- Arbitrary.arbitrary[String]
    subject          <- Arbitrary.arbitrary[String]
    message          <- Arbitrary.arbitrary[String]
    timestamp        <- Arbitrary.arbitrary[String]
    signatureVersion <- Arbitrary.arbitrary[String]
    signature        <- Arbitrary.arbitrary[String]
    signingCertUrl   <- Arbitrary.arbitrary[String]
    unsubscribeUrl   <- Arbitrary.arbitrary[String]
  } yield SnsMessage(
    theType = theType,
    messageId = messageId,
    topicArn = topicArn,
    subject = Some(subject),
    message = message,
    timestamp = timestamp,
    signatureVersion = signatureVersion,
    signature = signature,
    signingCertUrl = signingCertUrl,
    unsubscribeUrl = Some(unsubscribeUrl)
  )
}

given Arbitrary[WelcomeNotification] = Arbitrary {
  for {
    sender        <- Arbitrary.arbitrary[Email]
    receiver      <- Arbitrary.arbitrary[UserId]
    message       <- Arbitrary.arbitrary[String]
    recentUserIds <- Gen.listOf(Arbitrary.arbitrary[UserId])
  } yield WelcomeNotification(sender, receiver, message, recentUserIds)
}
