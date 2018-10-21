package org.samberry.bravochallenge

import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class MyCommands {

    @ShellMethod("Add two integers together.")
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}