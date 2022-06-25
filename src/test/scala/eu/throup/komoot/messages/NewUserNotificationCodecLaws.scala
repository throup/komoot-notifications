package eu.throup
package komoot
package messages

import io.circe.Json
import io.circe.syntax.*
import io.circe.testing.CodecTests
import org.scalacheck.Arbitrary
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class NewUserNotificationCodecLaws
    extends FunSuiteDiscipline
    with AnyFunSuiteLike
    with ScalaCheckPropertyChecks {

  given Arbitrary[Json] = Arbitrary {
    for {
      notification <- Arbitrary.arbitrary[NewUserNotification]
    } yield notification.asJson
  }

  checkAll(
    "NewUserNotification Codec Laws",
    CodecTests[NewUserNotification].codec
  )
}
