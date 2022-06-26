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

class WelcomeNotificationSpec
    extends AnyFreeSpec
    with should.Matchers
    with ScalaCheckPropertyChecks {

  given Arbitrary[String] = Arbitrary { Gen.alphaNumStr }

  val exampleJsonString: String =
    """{
      |  "sender" : "{your@mail.com}",
      |  "receiver" : 1589278470,
      |  "message" : "Hi Marcus, welcome to komoot. Lise, Anna and Stephen also joined recently.",
      |  "recent_user_ids" : [
      |    627362498,
      |    1093883245,
      |    304390273
      |  ]
      |}""".stripMargin

  val exampleNotification: WelcomeNotification =
    WelcomeNotification(
      Email.unsafeCast("{your@mail.com}"),
      UserId(1589278470),
      "Hi Marcus, welcome to komoot. Lise, Anna and Stephen also joined recently.",
      Seq(627362498L, 1093883245L, 304390273L).map(UserId.apply)
    )

  "WelcomeNotification" - {
    "can be decoded" - {
      "for fixed example" in {
        decode[WelcomeNotification](exampleJsonString) shouldBe Right(
          exampleNotification
        )
      }

      "for any inputs" in {
        forAll {
          (
              sender: Email,
              receiver: UserId,
              message: String,
              recentUserIds: Seq[UserId]
          ) =>
            {
              val jsonString =
                jsonStringFor(sender, receiver, message, recentUserIds)

              decode[WelcomeNotification](jsonString) shouldBe Right(
                WelcomeNotification(sender, receiver, message, recentUserIds)
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
              sender: Email,
              receiver: UserId,
              message: String,
              recentUserIds: Seq[UserId]
          ) =>
            {
              val notification =
                WelcomeNotification(sender, receiver, message, recentUserIds)
              notification.asJson.toString shouldBe jsonStringFor(
                sender,
                receiver,
                message,
                recentUserIds
              )
            }
        }
      }
    }
  }

  // Naive string interpolation, but works fine with the test inputs we control.
  private def jsonStringFor(
      sender: Email,
      receiver: UserId,
      message: String,
      recentUserIds: Seq[UserId]
  ) = {
    val formattedIds =
      recentUserIds.map(_.value).map("\n    " + _).mkString(",")

    s"""{
       |  "sender" : "${sender.value}",
       |  "receiver" : ${receiver.value},
       |  "message" : "$message",
       |  "recent_user_ids" : [$formattedIds
       |  ]
       |}""".stripMargin
  }
}
