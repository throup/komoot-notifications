package eu.throup
package komoot
package repository

import cats.*
import cats.implicits.*
import domain.*

// This is not a complete CRUD repository, because the current usecases do not require it.
trait UserRepository[F[_]] {
  // Store a new User in the repository.
  def create(user: User): F[Unit]

  // Fetch (up to) n unique Users from the repository.
  // If n is greater than the current capacity, it will return a Set containing the full contents of the repository.
  def select(n: Int): F[Set[User]]
}

object UserRepository {
  def make[F[_]]: UserRepository[F] = ???
}
