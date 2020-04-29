package jiro.app.model

data class ConfigModel(val versions: List<VersionModel>)
data class VersionModel(val id: String, val name: String, val image: ImageModel) {
    fun getImageOneTileWidth() = image.oneTile.width
    fun getImageOneTileHeight() = image.oneTile.height
    fun getImageColumnCount() = image.columnCount
    fun getImageRowCount() = image.rowCount
    fun getMaxImageCount() = image.maxImageCount
}
data class ImageModel(val columnCount: Int, val rowCount: Int, val oneTile: OneTileModel, val maxImageCount: Int = columnCount*rowCount)
data class OneTileModel(val width: Int, val height: Int)

