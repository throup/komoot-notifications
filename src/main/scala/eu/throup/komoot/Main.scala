package eu.throup
package komoot

import cats.effect.{ExitCode, IO, IOApp}
import eu.throup.komoot.server.Server

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Server.stream[IO].compile.drain.as(ExitCode.Success)
}
