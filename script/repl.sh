#!/bin/bash

files=`find src`
for f in $files; do
  if [ -f $f ]; then
    cat $f |
      sed 's/tkoolVersion.image.oneTile.width/tkoolVersion.getImageOneTileWidth()/g' |
      sed 's/tkoolVersion.image.oneTile.height/tkoolVersion.getImageOneTileHeight()/g' |
      sed 's/tkoolVersion.image.columnCount/tkoolVersion.getImageColumnCount()/g' |
      sed 's/tkoolVersion.image.rowCount/tkoolVersion.getImageRowCount()/g' |
      sed 's/tkoolVersion.image.maxImageCount/tkoolVersion.getMaxImageCount()/g' > tmp.txt
    cp tmp.txt $f
    rm tmp.txt
  fi
done
