package eu.throup
package komoot
package messages

import domain.{*, given}
import io.circe.parser.decode
import io.circe.syntax.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class SnsMessageSpec
    extends AnyFreeSpec
    with should.Matchers
    with ScalaCheckPropertyChecks {

  // Allows us to naively construct JSON strings without encoding random characters.
  given Arbitrary[String] = Arbitrary { Gen.alphaNumStr }

  "SNS Notification messages" - {
    val exampleJsonString: String =
      """{
      |  "Type" : "Notification",
      |  "MessageId" : "da41e39f-ea4d-435a-b922-c6aae3915ebe",
      |  "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
      |  "Subject" : "test",
      |  "Message" : "test message",
      |  "Timestamp" : "2012-04-25T21:49:25.719Z",
      |  "SignatureVersion" : "1",
      |  "Signature" : "EXAMPLElDMXvB8r9R83tGoNn0ecwd5UjllzsvSvbItzfaMpN2nk5HVSw7XnOn/49IkxDKz8YrlH2qJXj2iZB0Zo2O71c4qQk1fMUDi3LGpij7RCW7AW9vYYsSqIKRnFS94ilu7NFhUzLiieYr4BKHpdTmdD6c0esKEYBpabxDSc=",
      |  "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem",
      |  "UnsubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:123456789012:MyTopic:2bcfbf39-05c3-41de-beaa-fcfcc21c8f55"
      |}""".stripMargin

    val exampleNotification: SnsMessage = SnsMessage(
      theType = "Notification",
      messageId = "da41e39f-ea4d-435a-b922-c6aae3915ebe",
      topicArn = "arn:aws:sns:us-west-2:123456789012:MyTopic",
      subject = Some("test"),
      message = "test message",
      timestamp = "2012-04-25T21:49:25.719Z",
      signatureVersion = "1",
      signature =
        "EXAMPLElDMXvB8r9R83tGoNn0ecwd5UjllzsvSvbItzfaMpN2nk5HVSw7XnOn/49IkxDKz8YrlH2qJXj2iZB0Zo2O71c4qQk1fMUDi3LGpij7RCW7AW9vYYsSqIKRnFS94ilu7NFhUzLiieYr4BKHpdTmdD6c0esKEYBpabxDSc=",
      signingCertUrl =
        "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem",
      unsubscribeUrl = Some(
        "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:123456789012:MyTopic:2bcfbf39-05c3-41de-beaa-fcfcc21c8f55"
      )
    )

    "can be decoded" - {
      "for fixed example" in {
        decode[SnsMessage](exampleJsonString) shouldBe Right(
          exampleNotification
        )
      }

      "for any inputs" in {
        forAll {
          (
              messageId: String,
              topicArn: String,
              subject: String,
              message: String,
              timestamp: String,
              signatureVersion: String,
              signature: String,
              signingCertUrl: String,
              unsubscribeUrl: String
          ) =>
            {
              val theType = "Notification"

              val jsonString = jsonStringFor(
                theType,
                messageId,
                topicArn,
                subject,
                message,
                timestamp,
                signatureVersion,
                signature,
                signingCertUrl,
                unsubscribeUrl
              )

              decode[SnsMessage](jsonString) shouldBe Right(
                SnsMessage(
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
              )
            }
        }
      }
    }

    "can be encoded" - {
      "for fixed example" in {
        exampleNotification.asJson.toString shouldBe exampleJsonString
      }

      "for any inputs" in {
        forAll {
          (
              messageId: String,
              topicArn: String,
              subject: String,
              message: String,
              timestamp: String,
              signatureVersion: String,
              signature: String,
              signingCertUrl: String,
              unsubscribeUrl: String
          ) =>
            {
              val theType = "Notification"

              val notification = SnsMessage(
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
              notification.asJson.toString shouldBe jsonStringFor(
                theType,
                messageId,
                topicArn,
                subject,
                message,
                timestamp,
                signatureVersion,
                signature,
                signingCertUrl,
                unsubscribeUrl
              )
            }
        }
      }
    }

    // Naive string interpolation, but works fine with the test inputs we control.
    def jsonStringFor(
        theType: String,
        messageId: String,
        topicArn: String,
        subject: String,
        message: String,
        timestamp: String,
        signatureVersion: String,
        signature: String,
        signingCertUrl: String,
        unsubscribeUrl: String
    ) =
      s"""{
       |  "Type" : "$theType",
       |  "MessageId" : "$messageId",
       |  "TopicArn" : "$topicArn",
       |  "Subject" : "$subject",
       |  "Message" : "$message",
       |  "Timestamp" : "$timestamp",
       |  "SignatureVersion" : "$signatureVersion",
       |  "Signature" : "$signature",
       |  "SigningCertURL" : "$signingCertUrl",
       |  "UnsubscribeURL" : "$unsubscribeUrl"
       |}""".stripMargin
  }

  "SNS SubscriptionConfirmation message" - {
    val exampleJsonString: String =
      """{
      |  "Type" : "SubscriptionConfirmation",
      |  "MessageId" : "165545c9-2a5c-472c-8df2-7ff2be2b3b1b",
      |  "Token" : "2336412f37...",
      |  "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
      |  "Message" : "You have chosen to subscribe to the topic arn:aws:sns:us-west-2:123456789012:MyTopic.\nTo confirm the subscription, visit the SubscribeURL included in this message.",
      |  "SubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:us-west-2:123456789012:MyTopic&Token=2336412f37...",
      |  "Timestamp" : "2012-04-26T20:45:04.751Z",
      |  "SignatureVersion" : "1",
      |  "Signature" : "EXAMPLEpH+DcEwjAPg8O9mY8dReBSwksfg2S7WKQcikcNKWLQjwu6A4VbeS0QHVCkhRS7fUQvi2egU3N858fiTDN6bkkOxYDVrY0Ad8L10Hs3zH81mtnPk5uvvolIC1CXGu43obcgFxeL3khZl8IKvO61GWB6jI9b5+gLPoBc1Q=",
      |  "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem"
      |}""".stripMargin

    val exampleNotification: SnsMessage = SnsMessage(
      theType = "SubscriptionConfirmation",
      messageId = "165545c9-2a5c-472c-8df2-7ff2be2b3b1b",
      token = Some("2336412f37..."),
      topicArn = "arn:aws:sns:us-west-2:123456789012:MyTopic",
      message =
        "You have chosen to subscribe to the topic arn:aws:sns:us-west-2:123456789012:MyTopic.\nTo confirm the subscription, visit the SubscribeURL included in this message.",
      subscribeUrl = Some(
        "https://sns.us-west-2.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:us-west-2:123456789012:MyTopic&Token=2336412f37..."
      ),
      timestamp = "2012-04-26T20:45:04.751Z",
      signatureVersion = "1",
      signature =
        "EXAMPLEpH+DcEwjAPg8O9mY8dReBSwksfg2S7WKQcikcNKWLQjwu6A4VbeS0QHVCkhRS7fUQvi2egU3N858fiTDN6bkkOxYDVrY0Ad8L10Hs3zH81mtnPk5uvvolIC1CXGu43obcgFxeL3khZl8IKvO61GWB6jI9b5+gLPoBc1Q=",
      signingCertUrl =
        "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem"
    )

    "can be decoded" - {
      "for fixed example" in {
        decode[SnsMessage](exampleJsonString) shouldBe Right(
          exampleNotification
        )
      }

      "for any inputs" in {
        forAll {
          (
              messageId: String,
              token: String,
              topicArn: String,
              message: String,
              subscribeUrl: String,
              timestamp: String,
              signatureVersion: String,
              signature: String,
              signingCertUrl: String
          ) =>
            {
              val theType = "SubscriptionConfirmation"

              val jsonString = jsonStringFor(
                theType,
                messageId,
                token,
                topicArn,
                message,
                subscribeUrl,
                timestamp,
                signatureVersion,
                signature,
                signingCertUrl
              )

              decode[SnsMessage](jsonString) shouldBe Right(
                SnsMessage(
                  theType = theType,
                  messageId = messageId,
                  token = Some(token),
                  topicArn = topicArn,
                  message = message,
                  subscribeUrl = Some(subscribeUrl),
                  timestamp = timestamp,
                  signatureVersion = signatureVersion,
                  signature = signature,
                  signingCertUrl = signingCertUrl
                )
              )
            }
        }
      }
    }

    "can be encoded" - {
      "for fixed example" in {
        exampleNotification.asJson.toString shouldBe exampleJsonString
      }

      "for any inputs" in {
        forAll {
          (
              messageId: String,
              token: String,
              topicArn: String,
              message: String,
              subscribeUrl: String,
              timestamp: String,
              signatureVersion: String,
              signature: String,
              signingCertUrl: String
          ) =>
            {
              val theType = "SubscriptionConfirmation"

              val notification = SnsMessage(
                theType = theType,
                messageId = messageId,
                token = Some(token),
                topicArn = topicArn,
                message = message,
                subscribeUrl = Some(subscribeUrl),
                timestamp = timestamp,
                signatureVersion = signatureVersion,
                signature = signature,
                signingCertUrl = signingCertUrl
              )
              notification.asJson.toString shouldBe jsonStringFor(
                theType,
                messageId,
                token,
                topicArn,
                message,
                subscribeUrl,
                timestamp,
                signatureVersion,
                signature,
                signingCertUrl
              )
            }
        }
      }
    }

    // Naive string interpolation, but works fine with the test inputs we control.
    def jsonStringFor(
        theType: String,
        messageId: String,
        token: String,
        topicArn: String,
        message: String,
        subscribeUrl: String,
        timestamp: String,
        signatureVersion: String,
        signature: String,
        signingCertUrl: String
    ) =
      s"""{
       |  "Type" : "$theType",
       |  "MessageId" : "$messageId",
       |  "Token" : "$token",
       |  "TopicArn" : "$topicArn",
       |  "Message" : "$message",
       |  "SubscribeURL" : "$subscribeUrl",
       |  "Timestamp" : "$timestamp",
       |  "SignatureVersion" : "$signatureVersion",
       |  "Signature" : "$signature",
       |  "SigningCertURL" : "$signingCertUrl"
       |}""".stripMargin
  }
}
