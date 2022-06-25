package eu.throup
package komoot
package messages

import cats.Eq
import domain.*
import io.circe.*
import io.circe.syntax.*

import java.time.LocalDateTime

case class NewUserNotification(
    name: UserName,
    id: UserId,
    createdAt: LocalDateTime
)

object NewUserNotification {
  given Eq[NewUserNotification] = Eq.fromUniversalEquals

  given Decoder[NewUserNotification] = (c: HCursor) =>
    for {
      name      <- c.downField("name").as[UserName]
      id        <- c.downField("id").as[UserId]
      createdAt <- c.downField("created_at").as[LocalDateTime]
    } yield NewUserNotification(name, id, createdAt)

  given Encoder[NewUserNotification] = (notification: NewUserNotification) =>
    Json.obj(
      ("name", notification.name.asJson),
      ("id", notification.id.asJson),
      ("created_at", notification.createdAt.asJson)
    )
}
