package com.jiro4989.tkfm.dao

import com.jiro4989.tkfm.model.ConfigModel
import com.jiro4989.tkfm.model.ImageModel
import com.jiro4989.tkfm.model.OneTileModel
import com.jiro4989.tkfm.model.VersionModel
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/**
 * Configを取得する
 */
fun load(file: File): ConfigModel {
    val root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).documentElement
    val nodes = root.childNodes
    val versions = mutableListOf<VersionModel>()
    (0 until nodes.length).forEach {
        val node = nodes.item(it)
        if (node is Element) {
            val version = node.getVersion()
            version?.let { versions += version }
        }
    }
    return ConfigModel(versions)
}

private fun Element.getVersion(): VersionModel? {
    val id = this.getAttribute("id")
    val name = this.getAttribute("name")
    var image: ImageModel? = null

    val nodes = this.childNodes
    (0 until nodes.length).forEach {
        val node = nodes.item(it)
        if (node is Element) {
            if (node.nodeName == "image") image = node.getImage()
        }
    }

    return image?.let { VersionModel(id, name, it) } ?: null
}

private fun Element.getImage(): ImageModel? {
    var columnCount = 4
    var rowCount = 2
    var oneTile: OneTileModel? = null

    val nodes = this.childNodes
    (0 until nodes.length).forEach {
        val node = nodes.item(it)
        when (node.nodeName) {
            "columnCount" -> columnCount = node.textContent.toInt()
            "rowCount" -> rowCount = node.textContent.toInt()
            "oneTile" -> if (node is Element) oneTile = node.getOneTile()
        }
    }
    return oneTile?.let { ImageModel(columnCount, rowCount, it) } ?: null
}

private fun Element.getOneTile(): OneTileModel {
    var width = 144
    var height = 144

    val nodes = this.childNodes
    (0 until nodes.length).forEach {
        val node = nodes.item(it)
        if (node is Element) {
            when (node.nodeName) {
                "width" -> width = node.textContent.toInt()
                "height" -> height = node.textContent.toInt()
            }
        }
    }
    return OneTileModel(width, height)
}
