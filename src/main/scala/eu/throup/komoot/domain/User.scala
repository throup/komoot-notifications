package eu.throup
package komoot
package domain

import java.time.LocalDateTime

// Note: this definition is fundamentally the same as the NewUserNotification.
// Maintaining two distinct types because we control this internal representation,
// but the external service controls the notification structure.
case class User(
    name: UserName,
    id: UserId,
    createdAt: LocalDateTime
)
