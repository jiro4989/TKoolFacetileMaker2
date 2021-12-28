package com.jiro4989.tkfm.model

import java.io.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class ImageFilesModelTest {
  val path = resourcePath("/sample1.png")

  @Test
  fun testAddAndClear() {
    val ci = crop()
    val i = ImageFilesModel(ci)
    assertEquals(0, i.files.size, "初期化直後は要素数は0である")

    i.add(File(path))
    i.add(File(path))
    assertEquals(2, i.files.size, "要素を追加したらリストに追加される")

    i.clear()
    assertEquals(0, i.files.size, "clearすると要素は空になる")
  }

  @ParameterizedTest
  @CsvSource(
      "ok 範囲外アクセスしてもエラーにならない, 2,2,false,false",
      "ok 途中から範囲外アクセスになる, 1,1,false,false",
      "ok 一回だけ削除するので1つ残る, 0,1,false,false",
      "ok 追加で1回削除するので0個になる, 0,0,true,false",
      "ok 要素数が0個のときに削除を試行してもエラーにならない, 0,0,true,true",
      "ok 範囲外にアクセスを試行してもエラーにならない, -1,2,false,false")
  fun testRemove(desc: String, index: Int, wantSize: Int, remove: Boolean, remove2: Boolean) {
    val ci = crop()
    var i = ImageFilesModel(ci)

    i.add(File(path))
    i.add(File(path))
    i.remove(index)
    if (remove) i.remove(index)
    if (remove2) i.remove(index)
    assertEquals(wantSize, i.files.size, desc)
  }

  @ParameterizedTest
  @CsvSource("-1", "0", "1", "2")
  fun testSelect(index: Int) {
    val ci = crop()
    val i = ImageFilesModel(ci)

    i.add(File(path))
    i.add(File(path))

    // 選択すると内部で画像がセットされるが、画像の検証までやるのは大変。
    // この単体テストでは呼び出してもエラーにならないことだけ検証する
    i.select(index)
  }

  private fun crop() = CroppingImageModel(croppingRectangle = RectangleModel(144.0, 144.0))
  private fun resourcePath(path: String) = this.javaClass.getResource(path).getPath()
}
