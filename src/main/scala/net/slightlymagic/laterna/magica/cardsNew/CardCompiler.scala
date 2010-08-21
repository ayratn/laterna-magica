package net.slightlymagic.laterna.magica.cardsNew

import java.util.regex._, Pattern._

import net.slightlymagic.treeProperties._

import net.slightlymagic.laterna.magica.LaternaMagica._
import net.slightlymagic.laterna.magica.util._, JavaCollections._

/**
 * Handles static stuff, like finding all CardCompiler implementations and applying them to the right cards
 */
object CardCompiler extends Object with Logged {
    private val COMPILER_CLASS  = "/laterna/res/cards/uncompiled/([^/]+)/class";
    private val UNCOMPILED_PATH = "/laterna/res/cards/uncompiled/%s/path";
	
	private val compilers = {
	  val p = Pattern.compile(COMPILER_CLASS)
	  
	  //Add all compilers to an empty map
	  Map() ++ toScala(PROPS().getAllProperty(COMPILER_CLASS)).map({case pr: TreeProperty =>
	    //determine the class
        val clazz = pr.getValue().asInstanceOf[String]
        //create a compiler object
        val compiler = try {
          Class.forName(clazz).newInstance().asInstanceOf[CardCompiler]
        } catch {
          case ex: Exception => {
            log.warn(clazz + " couldn't be instantiated", ex)
            null
          }
        }
        //determine the compiler's name
        val name = {
          val m = p.matcher(pr.getName())
          if(m.matches()) m.group(1)
          else null
        }
        
        //return a tuple of name and compiler for the map
        (name, compiler)
	  }).filter({case (name, compiler) =>
	    //filter unsuccessful entries from the map
	    name != null && compiler != null
	  })
	}
}

trait CardCompiler extends Logged {}

