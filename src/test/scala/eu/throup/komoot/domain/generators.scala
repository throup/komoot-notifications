package eu.throup
package komoot
package domain

import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDateTime

val nonEmptyAlphaNumStrGen: Gen[String] = for {
  head <- Gen.alphaNumChar
  tail <- Gen.alphaNumStr
} yield head + tail

given Arbitrary[Email] = Arbitrary {
  for {
    local  <- nonEmptyAlphaNumStrGen
    domain <- nonEmptyAlphaNumStrGen
  } yield Email.unsafeCast(s"$local@$domain")
}

given Arbitrary[UserId] = Arbitrary {
  for {
    // Challenge spec does not define the format of a user id.
    // Based on the examples given, assuming positive integers for test generators.
    id <- Gen.posNum[Int]
  } yield UserId(id)
}

given Arbitrary[UserName] = Arbitrary {
  for {
    // Challenge spec does not define the format of a username.
    // Based on the examples given, assuming alphanumerical strings for test generators.
    name <- Gen.alphaNumStr
  } yield UserName(name)
}

given Arbitrary[User] = Arbitrary {
  for {
    name      <- Arbitrary.arbitrary[UserName]
    id        <- Arbitrary.arbitrary[UserId]
    createdAt <- Arbitrary.arbitrary[LocalDateTime]
  } yield User(name, id, createdAt)
}
