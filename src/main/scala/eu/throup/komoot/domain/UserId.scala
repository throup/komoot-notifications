package eu.throup
package komoot
package domain

import cats.Eq
import io.circe.{Decoder, Encoder, HCursor, Json}

// Challenge spec does not define the format of a user id.
// For maximum compatibility, we will accept any Int which can be JSON-encoded/decoded.
// For a production system, expect a stricter definition.
case class UserId(value: Int) extends AnyVal

object UserId {
  given Eq[UserId] = Eq.fromUniversalEquals

  given Decoder[UserId] = (c: HCursor) =>
    for {
      value <- c.as[Int]
    } yield UserId(value)

  given Encoder[UserId] = (userid: UserId) => Json.fromInt(userid.value)
}
