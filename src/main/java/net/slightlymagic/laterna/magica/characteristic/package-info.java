/**
 * <p>
 * This package contains interfaces that define the capabilities for storing characteristics. There are three types
 * of characteristics:
 * </p>
 * <ul>
 * <li>{@link net.slightlymagic.laterna.magica.characteristic.OverridingCharacteristic}s store Characteristics that have exactly one
 * value, such as name.</li>
 * <li>{@link net.slightlymagic.laterna.magica.characteristic.SetCharacteristic}s store Characteristics that have any number of values,
 * such as card type.</li>
 * <li>{@link net.slightlymagic.laterna.magica.characteristic.PTCharacteristic}s store Power and Toughness.</li>
 * </ul>
 * <p>
 * The primary purpose of a characteristic is to apply {@link net.slightlymagic.laterna.magica.effect.characteristic.CharacteristicEffect}s
 * that alter its original value(s).
 * </p>
 */

package net.slightlymagic.laterna.magica.characteristic;


