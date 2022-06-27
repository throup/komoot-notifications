package eu.throup
package komoot
package usecase

import cats.*
import domain.*

trait GenerateMessage[F[_]] {
  def apply(user: User, others: Set[User]): F[String]
}
object GenerateMessage      {
  def make[F[_]: Applicative]: GenerateMessage[F] =
    (user: User, others: Set[User]) => {
      // We always need this initial intro.
      val intro = Some(s"Hi ${user.name.value}, welcome to komoot.")

      // This additional sentence only makes sense if `others` is non-empty.
      val additional = joinNames(others.toSeq.map(_.name.value)).fold(None)(s =>
        Some(s"$s also joined recently.")
      )

      Applicative[F].pure(
        Seq(intro, additional).collect { case Some(s) => s }.mkString(" ")
      )
    }

  def joinNames(names: Seq[String]): Option[String] = {
    if (names.isEmpty) None
    else if (names.tail.isEmpty) names.headOption
    else {
      // These ones need to be comma-separated; all but the final name.
      val forCommas: Seq[String] = names.dropRight(1)
      val csv: String            = forCommas.mkString(", ")

      // Finally join everything together for a sentence.
      Some(s"$csv and ${names.last}")
    }
  }
}
