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

class SnsMessageCodecLaws
    extends FunSuiteDiscipline
    with AnyFunSuiteLike
    with ScalaCheckPropertyChecks {

  given Arbitrary[Json] = Arbitrary {
    for {
      snsMessage <- Arbitrary.arbitrary[SnsMessage]
    } yield snsMessage.asJson
  }

  checkAll(
    "SnsMessage Codec Laws",
    CodecTests[SnsMessage].codec
  )
}
