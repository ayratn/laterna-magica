package net.slightlymagic.laterna.magica.util

import net.slightlymagic.laterna.magica.MagicObject
import net.slightlymagic.laterna.magica.player.Player

object MagicaFunction {
  def controller() = (c: MagicObject) => c.getController()
}
