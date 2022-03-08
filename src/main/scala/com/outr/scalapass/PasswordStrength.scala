package com.outr.scalapass

import scala.io.Source

case class PasswordStrength(testers: List[PasswordTester] = List(
  PasswordTester.PerCharacter(),
  PasswordTester.PerWord(),
  PasswordTester.PerUppercase(),
  PasswordTester.PerDigit(),
  PasswordTester.PerSymbol(),
//  PasswordTester.EnglishWordMatch()
)) {
  def apply(password: String): Double = testers.map(_.valueFor(password)).sum
}

trait PasswordTester {
  def valueFor(password: String): Double
}

object PasswordTester {
  case class PerCharacter(weight: Double = 1.0) extends PasswordTester {
    override def valueFor(password: String): Double = password.length * weight
  }

  case class PerWord(weight: Double = 2.0) extends PasswordTester {
    override def valueFor(password: String): Double = password.split(' ').length * weight
  }

  case class PerUppercase(weight: Double = 1.5) extends PasswordTester {
    override def valueFor(password: String): Double = password.count(_.isUpper) * weight
  }

  case class PerDigit(weight: Double = 1.5) extends PasswordTester {
    override def valueFor(password: String): Double = password.count(_.isDigit) * weight
  }

  case class PerSymbol(weight: Double = 2.0) extends PasswordTester {
    override def valueFor(password: String): Double = password.count(c => !c.isLetterOrDigit && c != ' ') * weight
  }

  case class EnglishWordMatch(weight: Double = -5.0, minimumLength: Int = 3) extends PasswordTester {
    override def valueFor(password: String): Double = {
      val b = new StringBuilder
      var wordMatches = 0
      (0 until password.length - 1).foreach { start =>
        b.clear()
        password.substring(start).foreach { char =>
          b.append(char)
          if (b.length >= minimumLength && EnglishWords.contains(b.toString())) {
            println(s"Found word match: ${b.toString()}")
            wordMatches += 1
          }
        }
      }
      wordMatches * weight
    }
  }
}

/**
  * TODO: Update to use files from https://github.com/dropbox/zxcvbn
  */
object EnglishWords {
  private val set: Set[String] = {
    val stream = getClass.getClassLoader.getResourceAsStream("words.txt")
    val source = Source.fromInputStream(stream)
    try {
      source.getLines().map(_.toLowerCase).toSet
    } finally {
      source.close()
    }
  }

  def contains(word: String): Boolean = set.contains(word.toLowerCase)
}