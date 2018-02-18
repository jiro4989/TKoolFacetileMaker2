gci .\src\ -recurse | ? { if (! $_.PsIsContainer) {return $_}} | % {
  $targetFile = $_.FullName
  $text = cat $targetFile -encoding utf8
  $text = $text -creplace "tkoolVersion.image.oneTile.width" , "tkoolVersion.getImageOneTileWidth()"
  $text = $text -creplace "tkoolVersion.image.oneTile.height" , "tkoolVersion.getImageOneTileHeight()"
  $text = $text -creplace "tkoolVersion.image.columnCount" , "tkoolVersion.getImageColumnCount()"
  $text = $text -creplace "tkoolVersion.image.rowCount" , "tkoolVersion.getImageRowCount()"
  $text = $text -creplace "tkoolVersion.image.maxImageCount" , "tkoolVersion.getMaxImageCount()"
  $text | % { [Text.Encoding]::UTF8.GetBytes($_) } ` |
    Set-Content -Path $targetFile -Encoding Byte
}
