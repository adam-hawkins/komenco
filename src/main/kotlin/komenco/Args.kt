package komenco
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default

enum class AppType(val type: String) {
    WEB("web"),
    CLI("cli"),
    GUI("gui"),
    BASIC("basic")
}

class Args(parser: ArgParser) {
    val allowedLanguages = arrayOf("python", "rust", "javascript", "ruby", "kotlin", "java", "scala", "go")
    val language by parser.positional(
        "LANGUAGE",
        help = "language to generate a project for")
        .addValidator {
            if (!allowedLanguages.contains(value))
                throw InvalidArgumentException(
                    "invalid language argument, allowed values are: $allowedLanguages")
            }

    val name by parser.storing(
        "-n", "--name",
        help = "name of of the project")

    val type by parser.mapping(
        "--web" to AppType.WEB,
        "--cli" to AppType.CLI,
        "--gui" to AppType.GUI,
        "--basic" to AppType.BASIC,
        help = "what kind of application").default(AppType.BASIC)
}
