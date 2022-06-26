package eu.throup
package komoot
package usecase

import cats.*
import domain.{*, given}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class GenerateMessageSpec
    extends AnyFreeSpec
    with should.Matchers
    with ScalaCheckPropertyChecks {

  "GenerateMessage usecase" - {
    "generates expected messages" - {
      "for three additional users" in {
        forAll { (user: User, other1: User, other2: User, other3: User) =>
          {
            val expected =
              s"Hi ${user.name.value}, welcome to komoot. ${other1.name.value}, ${other2.name.value} and ${other3.name.value} also joined recently."

            val usecase = GenerateMessage.make[Id]

            usecase(user, Set(other1, other2, other3)) shouldBe expected
          }
        }
      }

      "for two additional users" in {
        forAll { (user: User, other1: User, other2: User) =>
          {
            val expected =
              s"Hi ${user.name.value}, welcome to komoot. ${other1.name.value} and ${other2.name.value} also joined recently."

            val usecase = GenerateMessage.make[Id]

            usecase(user, Set(other1, other2)) shouldBe expected
          }
        }
      }

      "for one additional user" in {
        forAll { (user: User, other1: User) =>
          {
            val expected =
              s"Hi ${user.name.value}, welcome to komoot. ${other1.name.value} also joined recently."

            val usecase = GenerateMessage.make[Id]

            usecase(user, Set(other1)) shouldBe expected
          }
        }
      }

      "for no additional users" in {
        forAll { (user: User) =>
          {
            val expected =
              s"Hi ${user.name.value}, welcome to komoot."

            val usecase = GenerateMessage.make[Id]

            usecase(user, Set.empty) shouldBe expected
          }
        }
      }
    }
  }
}
