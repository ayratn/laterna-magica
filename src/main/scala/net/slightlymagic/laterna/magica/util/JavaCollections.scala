package net.slightlymagic.laterna.magica.util

object JavaCollections {
  def toScala(list:java.util.List[_]):List[_] = List(list.toArray(): _ *)
}