package jiro.app.model

data class ConfigModel(val versions: List<VersionModel>)
data class VersionModel(val id: String, val name: String, val image: ImageModel)
data class ImageModel(val columnCount: Int, val rowCount: Int, val oneTile: OneTileModel)
data class OneTileModel(val width: Int, val height: Int)

