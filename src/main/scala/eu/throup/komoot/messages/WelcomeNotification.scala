package eu.throup
package komoot
package messages

import cats.Eq
import domain.*
import io.circe.*
import io.circe.Encoder.*
import io.circe.syntax.*

case class WelcomeNotification(
    sender: Email,
    receiver: UserId,
    message: String,
    recentUserIds: Seq[UserId]
)

object WelcomeNotification {
  given Eq[WelcomeNotification] = Eq.fromUniversalEquals

  given Decoder[WelcomeNotification] = (c: HCursor) =>
    for {
      sender        <- c.downField("sender").as[Email]
      receiver      <- c.downField("receiver").as[UserId]
      message       <- c.downField("message").as[String]
      recentUserIds <- c.downField("recent_user_ids").as[Seq[UserId]]
    } yield WelcomeNotification(sender, receiver, message, recentUserIds)

  given Encoder[WelcomeNotification] = (notification: WelcomeNotification) =>
    Json.obj(
      ("sender", notification.sender.asJson),
      ("receiver", notification.receiver.asJson),
      ("message", Json.fromString(notification.message)),
      ("recent_user_ids", notification.recentUserIds.asJson)
    )
}
