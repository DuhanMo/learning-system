package com.system.batch.killbatchsystem

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.exitProcess

@SpringBootApplication
class KillBatchSystemApplication

fun main(args: Array<String>) {
	val context = runApplication<KillBatchSystemApplication>(*args)
	val exitCode = SpringApplication.exit(context)
    exitProcess(exitCode)
}