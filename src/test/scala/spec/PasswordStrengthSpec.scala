package spec

import com.outr.scalapass.{PasswordStrength, PasswordTester}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PasswordStrengthSpec extends AnyWordSpec with Matchers {
  "PasswordTester" when {
    "PerCharacter" should {
      val tester = PasswordTester.PerCharacter()
      "score based on password length" in {
        tester.valueFor("abc") should be(3.0)
        tester.valueFor("abcdefgh") should be(8.0)
      }
      "score zero for empty string" in {
        tester.valueFor("") should be(0.0)
      }
      "apply custom weight" in {
        PasswordTester.PerCharacter(weight = 2.0).valueFor("abc") should be(6.0)
      }
    }
    "PerWord" should {
      val tester = PasswordTester.PerWord()
      "count words separated by spaces" in {
        tester.valueFor("correct horse battery") should be(6.0) // 3 words * 2.0
      }
      "count words separated by hyphens" in {
        tester.valueFor("correct-horse-battery") should be(6.0)
      }
      "count words separated by underscores" in {
        tester.valueFor("correct_horse_battery") should be(6.0)
      }
      "count words separated by mixed delimiters" in {
        tester.valueFor("correct-horse_battery staple") should be(8.0) // 4 words * 2.0
      }
      "count a single word" in {
        tester.valueFor("password") should be(2.0)
      }
      "score zero for empty string" in {
        tester.valueFor("") should be(0.0)
      }
      "score zero for only symbols" in {
        tester.valueFor("---") should be(0.0)
      }
    }
    "PerUppercase" should {
      val tester = PasswordTester.PerUppercase()
      "count uppercase characters" in {
        tester.valueFor("HeLLo") should be(4.5) // 3 uppercase * 1.5
      }
      "score zero when no uppercase" in {
        tester.valueFor("hello") should be(0.0)
      }
    }
    "PerDigit" should {
      val tester = PasswordTester.PerDigit()
      "count digits" in {
        tester.valueFor("abc123") should be(4.5) // 3 digits * 1.5
      }
      "score zero when no digits" in {
        tester.valueFor("abcdef") should be(0.0)
      }
    }
    "PerSymbol" should {
      val tester = PasswordTester.PerSymbol()
      "count symbols (non-letter, non-digit, non-space)" in {
        tester.valueFor("a!b@c#") should be(6.0) // 3 symbols * 2.0
      }
      "not count spaces as symbols" in {
        tester.valueFor("a b c") should be(0.0)
      }
      "score zero when no symbols" in {
        tester.valueFor("abc123") should be(0.0)
      }
    }
    "EnglishWordMatch" should {
      val tester = PasswordTester.EnglishWordMatch()
      "penalize passwords containing english words" in {
        val score = tester.valueFor("password")
        score should be < 0.0
      }
      "not penalize random strings" in {
        tester.valueFor("xqzjvk") should be(0.0)
      }
      "handle empty string" in {
        tester.valueFor("") should be(0.0)
      }
      "handle single character" in {
        tester.valueFor("a") should be(0.0)
      }
    }
  }
  "PasswordStrength" should {
    val strength = PasswordStrength()
    "score a complex password higher than a simple one" in {
      val complex = strength("C0mpl3x!Pass#word")
      val simple = strength("aaa")
      complex should be > simple
    }
    "handle empty password" in {
      strength("") should be(0.0)
    }
  }
}
