package komenco

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.MissingRequiredPositionalArgumentException
import com.xenomachina.argparser.MissingValueException

class App {
    val notGreeting: String
        get() {
            return ""
        }
}

fun main(args: Array<String>) {
    try {
        ArgParser(args).parseInto(::Args).run {
            var project = Project(name, language, type.name)
            try {
                project.create()
            } catch (e: Exception) {
                println(e)
            }
        }
    } catch (e: InvalidArgumentException) {
        println("Invalid arguments supplied")
    } catch (e: MissingValueException) {
        println(e.message)
    } catch (e: MissingRequiredPositionalArgumentException) {
        println(e.message)
    }
}
