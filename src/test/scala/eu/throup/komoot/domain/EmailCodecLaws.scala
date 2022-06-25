package eu.throup
package komoot
package domain

import io.circe.Json
import io.circe.syntax.*
import io.circe.testing.CodecTests
import org.scalacheck.Arbitrary
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class EmailCodecLaws
    extends FunSuiteDiscipline
    with AnyFunSuiteLike
    with ScalaCheckPropertyChecks {

  given Arbitrary[Json] = Arbitrary {
    for {
      username <- Arbitrary.arbitrary[Email]
    } yield username.asJson
  }

  checkAll(
    "Email Codec Laws",
    CodecTests[Email].codec
  )
}
