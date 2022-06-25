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

class WelcomeNotificationCodecLaws
    extends FunSuiteDiscipline
    with AnyFunSuiteLike
    with ScalaCheckPropertyChecks {

  given Arbitrary[Json] = Arbitrary {
    for {
      notification <- Arbitrary.arbitrary[WelcomeNotification]
    } yield notification.asJson
  }

  checkAll(
    "WelcomeNotification Codec Laws",
    CodecTests[WelcomeNotification].codec
  )
}
