package com.jiro4989.tkfm.model

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Properties

private val configDir = "config"

internal fun configFile(filename: String) =
    File(configDir + File.separator + filename + ".properties")

private fun readFileFromProperties(prop: Properties, dirKey: String, fileKey: String): File? {
  val dir = prop.getProperty(dirKey) ?: return null
  val file = prop.getProperty(fileKey) ?: return null
  val path = dir + File.separator + file
  File(path).let {
    if (it.exists()) {
      return it
    } else if (it.getParentFile().exists()) {
      return it.getParentFile()
    }
  }
  return null
}

interface PropertiesInterface {
  fun load()
  fun store()
}

data class WindowPropertiesModel(
    private val prop: Properties = Properties(),
    private val file: File = configFile("window"),
    var x: Double = 100.0,
    var y: Double = 100.0,
    var width: Double = 1280.0,
    var height: Double = 760.0
) : PropertiesInterface {
  override fun load() {
    if (!file.exists()) {
      return
    }

    FileInputStream(file).use { stream: InputStream ->
      prop.load(InputStreamReader(stream, "UTF-8"))
      prop.getProperty("x")?.let {
        if (!it.isNullOrEmpty()) {
          x = it.toDouble()
        }
      }

      prop.getProperty("y")?.let {
        if (!it.isNullOrEmpty()) {
          y = it.toDouble()
        }
      }

      prop.getProperty("width")?.let {
        if (!it.isNullOrEmpty()) {
          width = it.toDouble()
        }
      }

      prop.getProperty("height")?.let {
        if (!it.isNullOrEmpty()) {
          height = it.toDouble()
        }
      }
    }
  }

  override fun store() {
    file.parentFile.mkdirs()

    prop.apply {
      setProperty("x", x.toString())
      setProperty("y", y.toString())
      setProperty("width", width.toString())
      setProperty("height", height.toString())
    }

    try {
      FileOutputStream(file).use { fos: FileOutputStream ->
        prop.store(OutputStreamWriter(fos, "UTF-8"), null)
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
}

data class ChoosedFilePropertiesModel(
    private val prop: Properties = Properties(),
    private val file: File = configFile("choosed_file"),
    var openedFile: File? = null,
    var savedFile: File? = null
) : PropertiesInterface {
  private val propertyKeyOpenedFileDir = "opened_file_dir"
  private val propertyKeyOpenedFileFile = "opened_file_file"
  private val propertyKeySavedFileDir = "saved_file_dir"
  private val propertyKeySavedFileFile = "saved_file_file"

  override fun load() {
    if (!file.exists()) {
      return
    }

    FileInputStream(file).use { stream: InputStream ->
      prop.load(InputStreamReader(stream, "UTF-8"))
      openedFile = readFileFromProperties(prop, propertyKeyOpenedFileDir, propertyKeyOpenedFileFile)
      savedFile = readFileFromProperties(prop, propertyKeySavedFileDir, propertyKeySavedFileFile)
    }
  }

  override fun store() {
    file.getParentFile().mkdirs()

    openedFile?.let {
      prop.setProperty(propertyKeyOpenedFileDir, it.parentFile.absolutePath)
      prop.setProperty(propertyKeyOpenedFileFile, it.name)
    }

    savedFile?.let {
      prop.setProperty(propertyKeySavedFileDir, it.parentFile.absolutePath)
      prop.setProperty(propertyKeySavedFileFile, it.name)
    }

    try {
      FileOutputStream(file).use { stream: FileOutputStream ->
        prop.store(OutputStreamWriter(stream, "UTF-8"), null)
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
}
