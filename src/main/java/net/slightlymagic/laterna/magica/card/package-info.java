/**
 * <p>
 * This package defines interfaces for things related to cards (more formally objects,
 * {@link net.slightlymagic.laterna.magica.MagicObject}s because of the conflict) in Magic. That is,
 * <ul>
 * <li>The oracle representation of a card, {@link net.slightlymagic.laterna.magica.card.CardTemplate}. The card template itself contains
 * {@link net.slightlymagic.laterna.magica.card.CardParts} to store the (possibly multiple) parts of the card. The meaning of the card parts
 * is defined by {@link net.slightlymagic.laterna.magica.card.CardTemplate.CardLayout}</li>
 * <li>A single instance of a card (or other object), {@link net.slightlymagic.laterna.magica.MagicObject}</li>
 * <li>The {@link net.slightlymagic.laterna.magica.card.State} of a permanent</li>
 * </ul>
 * </p>
 */

package net.slightlymagic.laterna.magica.card;


