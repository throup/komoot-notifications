package eu.throup
package komoot

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Random

import server.Server

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = for {
    random   <- Random.scalaUtilRandom[IO]
    exitCode <- Server
                  .stream(using summon[Async[IO]], random)
                  .compile
                  .drain
                  .as(ExitCode.Success)
  } yield exitCode
}
