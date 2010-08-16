/**
 * <p>
 * This package contains a few utility classes that implement bidirectional relationships of cardinality m:n, 1:n
 * and 1:1. The m:n relationship can store a "relation" object that is associated with the connection between two
 * ManyToMany objects. Changing the relationship on one side automatically reflects the change on the other side.
 * Generally, the implementation is not threadsafe.
 * </p><p>
 * For example, to represent relationships between two classes A and B, these classes would receive a field of an
 * appropriate type and provide a few wrapper methods around them.
 * 
 * <ul>
 * <li>A:B 1:1<br/>
 * A gets a {@code OneToOne<A, B>}, B gets a {@code OneToOne<B, A>}
 * </li>
 * <li>A:B 1:n<br/>
 * A gets a {@code OneSide<A, B>}, B gets a {@code ManySide<B, A>}
 * </li>
 * <li>A:B m:n<br/>
 * A gets a {@code ManyToMany<A, B>}, B gets a {@code ManyToMany<B, A>}
 * </li>
 * </ul>
 * </p>
 */

package net.slightlymagic.laterna.magica.util.relational;


