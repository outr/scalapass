package com.outr.scalapass

import fabric.rw._

case class Encrypted(data: Bytes, salt: Bytes)

object Encrypted {
  implicit val rw: RW[Encrypted] = RW.gen
}