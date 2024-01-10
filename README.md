# scalapass
[![CI](https://github.com/outr/scalapass/actions/workflows/ci.yml/badge.svg)](https://github.com/outr/scalapass/actions/workflows/ci.yml)

Straight-forward password hashing and verification using the latest algorithms. Currently, supports:
* Argon2(i, d, and id)
* PBKDF2

## SBT
```sbt
libraryDependencies += "com.outr" %% "scalapass" % "1.2.8"
```

## Creating a hash
```scala
import com.outr.scalapass.Argon2PasswordFactory

val factory = Argon2PasswordFactory()
// factory: Argon2PasswordFactory = Argon2PasswordFactory(
//   iterations = 50,
//   memory = 65536,
//   parallelism = 8,
//   argon2 = id,
//   saltLength = 16,
//   hashLength = 32
// )
val password: String = "your-password-in-clear-text"
// password: String = "your-password-in-clear-text"
val hashed: String = factory.hash(password)
// hashed: String = "$argon2id$v=19$m=65536,t=50,p=8$ERV4kw6eytqaCeZkb9+ocA$ECwWfIUIu7n/is0ryxNrbK4szxEgpkfV6eVAkWKF4AU"
```

Now store the one-way hashed password safely in your database.

## Verifying a hash
```scala
val attemptedPassword: String = "your-password-in-clear-text"
// attemptedPassword: String = "your-password-in-clear-text"
val hashedPassword: String = hashed  // From the database
// hashedPassword: String = "$argon2id$v=19$m=65536,t=50,p=8$ERV4kw6eytqaCeZkb9+ocA$ECwWfIUIu7n/is0ryxNrbK4szxEgpkfV6eVAkWKF4AU"  // From the database
val valid: Boolean = factory.verify(attemptedPassword, hashedPassword)
// valid: Boolean = true
```

Will return `true` if the `attemptedPassword` is the same as the one used to create the `hashedPassword`