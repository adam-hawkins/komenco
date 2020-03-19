package komenco

import java.io.File

class Project(var name: String?, var language: String?, var type: String) {
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
        var command = "pipenv install"
        if (type == "WEB") {
            File("$workingDir/requirements.txt").writeText("Django\n")
            executeRuntime(command, workingDir)
            command = "pipenv run django-admin startproject $name"
            executeRuntime(command, workingDir)
        } else {
            executeRuntime(command, workingDir)
            File("$workingDir/$name.py").writeText(generatePythonMain())
        }
        executeRuntime("git init", workingDir)
    }

    private fun createJVMProject() {
        val workingDir = File("./$name/")
        workingDir.mkdirs()
        print("Enter a package name: ")
        var projectPackage = readLine()
        var command: String
        if (language == "scala") {
            command = "gradle init --type $language-library --dsl groovy --package $projectPackage"
            executeRuntime(command, workingDir)
            File("$workingDir/build.gradle").writeText(generateScalaGradle(projectPackage.toString()).toString())
            File("$workingDir/src/main/scala/$projectPackage/Main.scala").writeText(generateScalaMain(projectPackage.toString()))
        } else {
            if (type == "WEB") {
                command = """curl https://start.spring.io/starter.zip -d dependencies=web,devtools \\
                -d bootVersion=2.2.5.RELEASE \\
                -d language=$language \\
                -d groupId=$projectPackage \\
                -d type=gradle-project \\
                -d artifactId=$name \\
                -o $name.zip"""
                executeRuntime(command, workingDir)
                executeRuntime("unzip $name.zip", workingDir)
                executeRuntime("rm $name.zip", workingDir)
            } else {
                command = "gradle init --type $language-application --dsl groovy --package $projectPackage"
            }
            executeRuntime(command, workingDir)
        }
    }

    private fun createGoProject() {
        val workingDir = File("./$name/")
        workingDir.mkdirs()
        val command = "go mod init $name"
        executeRuntime(command, workingDir)
        File("$workingDir/main.go").writeText(generateGoMain())
        executeRuntime("git init", workingDir)
    }

    private fun createRubyProject() {
        println("not implemented yet")
    }

    private fun generateScalaGradle(projectPackage: String): String {
        return """
        plugins {
            // Apply the scala plugin to add support for Scala
            id 'scala'
            id 'application'

            // Apply the java-library plugin for API and implementation separation.
            id 'java-library'
        }

        repositories {
            // Use jcenter for resolving dependencies.
            // You can declare any Maven/Ivy/file repository here.
            jcenter()
        }

        dependencies {
            // Use Scala 2.13 in our library project
            implementation 'org.scala-lang:scala-library:2.13.1'

            // Use Scalatest for testing our library
            testImplementation 'junit:junit:4.12'
            testImplementation 'org.scalatest:scalatest_2.13:3.1.0'
            testImplementation 'org.scalatestplus:junit-4-12_2.13:3.1.0.0'

            // Need scala-xml at test runtime
            testRuntimeOnly 'org.scala-lang.modules:scala-xml_2.13:1.2.0'
        }

        application {
            mainClassName = '$projectPackage.Main'
        }
        """.trimIndent()
    }

    private fun generateScalaMain(projectPackage: String): String {
        return """
        package $projectPackage
        object Main {
            def main(args: Array[String]): Unit = {
                println("Hello, World!")
            }
        }
        """.trimIndent()
    }

    private fun generateGoMain(): String {
        return """
        package main

        import (
            "fmt"
        )

        func main() {
            fmt.Println("Hello, World!")
        }
        """.trimIndent()
    }

    private fun generatePythonMain(): String {
        return """
        def main():
            print("Hello, World!")

        if __name__ == '__main__':
            main()
        """.trimIndent()
    }
}
