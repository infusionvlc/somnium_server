package com.infusionvlc.somniumserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SomniumServerApplication

fun main(args: Array<String>) {
  runApplication<SomniumServerApplication>(*args)
}
