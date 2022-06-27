package eu.throup
package komoot
package messages

import cats.*
import cats.implicits.*
import domain.*
import io.circe.*
import io.circe.syntax.*

case class SnsMessage(
    theType: String,
    messageId: String,
    token: Option[String] = None,
    topicArn: String,
    subject: Option[String] = None,
    message: String,
    subscribeUrl: Option[String] = None,
    timestamp: String,
    signatureVersion: String,
    signature: String,
    signingCertUrl: String,
    unsubscribeUrl: Option[String] = None
)

object SnsMessage {
  given Eq[SnsMessage] = Eq.fromUniversalEquals

  given Decoder[SnsMessage] = (c: HCursor) =>
    for {
      theType          <- c.downField("Type").as[String]
      messageId        <- c.downField("MessageId").as[String]
      token            <- c.get[Option[String]]("Token")
      topicArn         <- c.downField("TopicArn").as[String]
      subject          <- c.get[Option[String]]("Subject")
      message          <- c.downField("Message").as[String]
      subscribeUrl     <- c.get[Option[String]]("SubscribeURL")
      timestamp        <- c.downField("Timestamp").as[String]
      signatureVersion <- c.downField("SignatureVersion").as[String]
      signature        <- c.downField("Signature").as[String]
      signingCertUrl   <- c.downField("SigningCertURL").as[String]
      unsubscribeUrl   <- c.get[Option[String]]("UnsubscribeURL")
    } yield SnsMessage(
      theType,
      messageId,
      token,
      topicArn,
      subject,
      message,
      subscribeUrl,
      timestamp,
      signatureVersion,
      signature,
      signingCertUrl,
      unsubscribeUrl
    )

  given Encoder[SnsMessage] = (message: SnsMessage) => {
    Json.fromFields(
      Seq(
        Option(("Type", message.theType.asJson)),
        Option(("MessageId", message.messageId.asJson)),
        message.token.map(s => ("Token", s.asJson)),
        Option(("TopicArn", message.topicArn.asJson)),
        message.subject.map(s => ("Subject", s.asJson)),
        Option(("Message", message.message.asJson)),
        message.subscribeUrl.map(s => ("SubscribeURL", s.asJson)),
        Option(("Timestamp", message.timestamp.asJson)),
        Option(("SignatureVersion", message.signatureVersion.asJson)),
        Option(("Signature", message.signature.asJson)),
        Option(("SigningCertURL", message.signingCertUrl.asJson)),
        message.unsubscribeUrl.map(s => ("UnsubscribeURL", s.asJson))
      ).collect { case Some(s) => s }
    )
  }
}
