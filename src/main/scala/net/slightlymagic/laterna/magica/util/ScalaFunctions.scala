package net.slightlymagic.laterna.magica.util

import com.google.common.base.{Function, Functions, Predicate, Predicates, Supplier, Suppliers}
import Functions._, Predicates._, Suppliers._

object ScalaFunctions {
  //to Java
  
  def toFunction[F, T](function: (F) => T)       = new Function[F, T]() { def apply(arg: F) = function(arg) }
  
  def toPredicate[T](predicate: (T) => Boolean)  = new Predicate[T]() { def apply(arg: T) = predicate(arg) }
  
  def toSupplier[T](supplier: () => T)           = new Supplier[T]() { def get() = supplier() }
  
  //to Scala
  
  def toFunction[F, T](function: Function[F, T]) = (arg: F) => function.apply(arg)
  
  def toPredicate[T](predicate: Predicate[T])    = (arg: T) => predicate.apply(arg)
  
  def toSupplier[T](supplier: Supplier[T])       = () => supplier.get()
}
