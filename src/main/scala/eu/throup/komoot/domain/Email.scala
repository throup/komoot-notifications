package eu.throup
package komoot
package domain

import cats.Eq
import io.circe.{Decoder, Encoder, HCursor, Json}

// Private constructor because we are applying validation rules.
case class Email private (value: String) extends AnyVal

object Email {
  // Convenience constructor, intended for known fixed inputs.
  private[komoot] def unsafeCast(value: String): Email = Email(value)

  // Very basic email validation; confirms there are both local and domain parts, separated by @.
  // RFC 2822 accepts far more values than most people realise!
  def isValid(con: String): Boolean =
    con.length > 2 && con.tail.dropRight(1).contains("@")

  def from(con: String): Either[String, Email] =
    Either.cond(isValid(con), Email(con), "Invalid email address")

  given Eq[Email] = Eq.fromUniversalEquals

  given Decoder[Email] = (c: HCursor) =>
    for {
      value <- c.as[String]
    } yield Email(value)

  given Encoder[Email] = (email: Email) => Json.fromString(email.value)
}
