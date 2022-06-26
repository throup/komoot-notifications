package eu.throup
package komoot
package domain

import cats.Eq
import io.circe.{Decoder, Encoder, HCursor, Json}

// Challenge spec does not define the format of a user id.
// For maximum compatibility, we will accept any Long which can be JSON-encoded/decoded.
// For a production system, expect a stricter definition.
case class UserId(value: Long) extends AnyVal

object UserId {
  given Eq[UserId] = Eq.fromUniversalEquals

  given Decoder[UserId] = (c: HCursor) =>
    for {
      value <- c.as[Long]
    } yield UserId(value)

  given Encoder[UserId] = (userid: UserId) => Json.fromLong(userid.value)
}
