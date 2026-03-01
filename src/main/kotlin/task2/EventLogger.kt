package org.example.task2

interface EventLogger {
    fun log(event: TraceEvent, msg: String = "")
}