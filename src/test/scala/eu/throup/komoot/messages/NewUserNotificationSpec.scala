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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class NewUserNotificationSpec
    extends AnyFreeSpec
    with should.Matchers
    with ScalaCheckPropertyChecks {

  val exampleJsonString: String =
    """{
      |  "name" : "Marcus",
      |  "id" : 1589278470,
      |  "created_at" : "2020-05-12T16:11:54"
      |}""".stripMargin

  val exampleNotification: NewUserNotification =
    NewUserNotification(
      UserName("Marcus"),
      UserId(1589278470),
      LocalDateTime.parse("2020-05-12T16:11:54.000")
    )

  "NewUserNotification" - {
    "can be decoded" - {
      "for fixed example" in {
        decode[NewUserNotification](exampleJsonString) shouldBe Right(
          exampleNotification
        )
      }

      "for any inputs" in {
        forAll { (name: UserName, id: UserId, createdAt: LocalDateTime) =>
          {
            val jsonString = jsonStringFor(name, id, createdAt)

            decode[NewUserNotification](jsonString) shouldBe Right(
              NewUserNotification(name, id, createdAt)
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
        forAll { (name: UserName, id: UserId, createdAt: LocalDateTime) =>
          {
            val notification =
              NewUserNotification(name, id, createdAt)
            notification.asJson.toString shouldBe jsonStringFor(
              name,
              id,
              createdAt
            )
          }
        }
      }
    }
  }

  // Naive string interpolation, but works fine with the test inputs we control.
  private def jsonStringFor(
      name: UserName,
      id: UserId,
      createdAt: LocalDateTime
  ) = s"""{
         |  "name" : "${name.value}",
         |  "id" : ${id.value},
         |  "created_at" : "${createdAt.format(ISO_DATE_TIME)}"
         |}""".stripMargin
}
