package net.slightlymagic.laterna.magica.util

import org.slf4j._;

trait Logged {
    val log = LoggerFactory.getLogger(getClass())
}