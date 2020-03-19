package komenco

import java.io.File

class Project(var name: String?, var language: String?, var type: String) {
    fun initProject() {
        File("./test/test2").mkdirs()
    }

    fun create() {
        when (language) {
            "rust" -> createRustProject()
            "python" -> createPythonProject()
            "kotlin", "scala", "java" -> createJVMProject()
            "go" -> createGoProject()
            "ruby" -> createRubyProject()
            else -> println("not a valid language")
        }
    }

    private fun createRustProject() {
        val command = "cargo new $name"
        val workingDir = File(".")
        executeRuntime(command, workingDir)
    }

    private fun executeRuntime(command: String, workingDir: File) {
        try {
            val parts = command.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            println(proc.inputStream.bufferedReader().readText())
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private fun createPythonProject() {
        val workingDir = File("./$name/")
        workingDir.mkdirs()
        val command = "pipenv install"
        executeRuntime(command, workingDir)
    }

    private fun createJVMProject() {
        val workingDir = File("./$name/")
        workingDir.mkdirs()
        print("Enter a package name: ")
        var projectPackage = readLine()
        var command: String
        if (type == "scala") {
            command = "gradle init --type $language-library --dsl groovy --package $projectPackage"
        } else {
            command = "gradle init --type $language-application --dsl groovy --package $projectPackage"
        }
        executeRuntime(command, workingDir)
    }

    private fun createGoProject() {
        val workingDir = File("./$name/")
        workingDir.mkdirs()
        val command = "go mod init $name"
        executeRuntime(command, workingDir)
    }

    private fun createRubyProject() {
        println("not implemented yet")
    }
}
