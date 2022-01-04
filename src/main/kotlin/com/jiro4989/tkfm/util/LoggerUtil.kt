package com.jiro4989.tkfm.util

fun initLogger() {
  // WIP
}

fun info(msg: String) {
  println(msg)
}

fun warning(msg: String) {
  println(msg)
}

/*
// WIP
import java.io.File
import java.nio.file.Paths
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

private const val APPLICATION_NAME = "tkfm"

private const val LOG_DIRECTORY = "log"

private const val INFO_LOG_FILE = "info.log"

private const val ERROR_LOG_FILE = "error.log"

private lateinit var infoLogger: Logger

private lateinit var errorLogger: Logger

private lateinit var consoleLogger: Logger

fun initLogger() {
  createLogDirectory()

  infoLogger =
      Logger.getLogger(APPLICATION_NAME).apply {
        val filePath = Paths.get(".", LOG_DIRECTORY, INFO_LOG_FILE).toString()
        val handler: Handler = FileHandler(filePath)
        addHandler(handler)
      }

  errorLogger =
      Logger.getLogger(APPLICATION_NAME).apply {
        val filePath = Paths.get(".", LOG_DIRECTORY, ERROR_LOG_FILE).toString()
        val handler: Handler = FileHandler(filePath)
        addHandler(handler)
      }

  consoleLogger =
      Logger.getLogger(APPLICATION_NAME).apply {
        val handler: Handler = ConsoleHandler()
        addHandler(handler)
      }
}

fun info(msg: String) {
  consoleLogger.info(msg)
  infoLogger.info(msg)
}

fun warning(msg: String) {
  consoleLogger.warning(msg)
  errorLogger.warning(msg)
}

private fun createLogDirectory() {
  File(LOG_DIRECTORY).mkdirs()
}

*/
