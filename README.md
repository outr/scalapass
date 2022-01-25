# scalapass
[![CI](https://github.com/outr/scalapass/actions/workflows/ci.yml/badge.svg)](https://github.com/outr/scalapass/actions/workflows/ci.yml)

Straight-forward password hashing and verification using the latest algorithms

## SBT
```sbt
libraryDependencies += "com.outr" %% "scalapass" % "1.0.5"
```

## Creating a hash
```scala
import com.outr.scalapass.PasswordFactory

val password: String = "your-password-in-clear-text"
val hashed: String = PasswordFactory.hash(password)
```

Now store the one-way hashed password safely in your database.

## Verifying a hash
```scala
import com.outr.scalapass.PasswordFactory

val attemptedPassword: String = "your-password-in-clear-text"
val hashedPassword: String = ???  // From the database
val valid: Boolean = PasswordFactory.verify(attemptedPassword, hashedPassword)
```

Will return `true` if the `attemptedPassword` is the same as the one used to create the `hashedPassword`