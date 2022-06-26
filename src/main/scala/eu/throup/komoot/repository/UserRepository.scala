package eu.throup
package komoot
package repository

import cats.*
import cats.effect.*
import cats.effect.std.Random
import cats.implicits.*
import domain.*

import scala.collection.concurrent.TrieMap

// This is not a complete CRUD repository, because the current usecases do not require it.
trait UserRepository[F[_]] {
  // Store a new User in the repository.
  def create(user: User): F[Unit]

  // Fetch (up to) n unique Users from the repository.
  // If n is greater than the current capacity, it will return a Set containing the full contents of the repository.
  def select(n: Int): F[Set[User]]
}

object UserRepository {
  def make[F[_]: Monad: Random]: UserRepository[F] = new UserRepository[F] {
    // Using a thread-safe in-memory store for now, as this is a simple project.
    // If this were ever to go into production, we should replace this with a
    // real data-store, like PostgreSQL, DynamoDB or Redis.
    private val inMem: TrieMap[UserId, User] = TrieMap()

    override def create(user: User): F[Unit] = {
      // This service is not the source of truth for user data.
      // As such, we will simply replace any existing users, if the upstream service provides two users with the same ID.
      inMem(user.id) = user
      Applicative[F].unit
    }

    override def select(n: Int): F[Set[User]] = {
      for {
        shuffled <- Random[F].shuffleList(inMem.keys.toList)
        ids       = shuffled.take(n)
        users     = ids.map(inMem(_))
      } yield users.toSet
    }
  }
}
