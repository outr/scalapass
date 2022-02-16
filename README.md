# scalapass
[![CI](https://github.com/outr/scalapass/actions/workflows/ci.yml/badge.svg)](https://github.com/outr/scalapass/actions/workflows/ci.yml)

Straight-forward password hashing and verification using the latest algorithms. Currently supports:
* Argon2(i, d, and id)
* PBKDF2

## SBT
```sbt
libraryDependencies += "com.outr" %% "scalapass" % "1.1.0"
```

## Creating a hash
```scala
import com.outr.scalapass.Argon2PasswordFactory

val factory = Argon2PasswordFactory()
val password: String = "your-password-in-clear-text"
val hashed: String = factory.hash(password)
```

Now store the one-way hashed password safely in your database.

## Verifying a hash
```scala
import com.outr.scalapass.Argon2PasswordFactory

val factory = Argon2PasswordFactory()
val attemptedPassword: String = "your-password-in-clear-text"
val hashedPassword: String = ???  // From the database
val valid: Boolean = factory.verify(attemptedPassword, hashedPassword)
```

Will return `true` if the `attemptedPassword` is the same as the one used to create the `hashedPassword`