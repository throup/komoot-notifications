package eu.throup
package komoot
package domain

import cats.Eq
import io.circe.*
import io.circe.generic.semiauto.*

// Challenge spec does not define the format of a username.
// For maximum compatibility, we will accept any String which can be JSON-encoded/decoded.
// For a production system, expect a stricter definition.
case class UserName(value: String) extends AnyVal

object UserName {
  given Eq[UserName] = Eq.fromUniversalEquals

  given Decoder[UserName] = (c: HCursor) =>
    for {
      value <- c.as[String]
    } yield UserName(value)

  given Encoder[UserName] = (username: UserName) =>
    Json.fromString(username.value)
}
