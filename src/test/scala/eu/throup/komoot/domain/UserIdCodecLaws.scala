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

class UserIdCodecLaws
    extends FunSuiteDiscipline
    with AnyFunSuiteLike
    with ScalaCheckPropertyChecks {

  given Arbitrary[Json] = Arbitrary {
    for {
      userid <- Arbitrary.arbitrary[UserId]
    } yield userid.asJson
  }

  checkAll(
    "UserId Codec Laws",
    CodecTests[UserId].codec
  )
}
