/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Nate
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.item;

import java.io.File;
import java.util.List;
import java.util.Map;

import forge.SetUtils;
import forge.deck.Deck;
import forge.deck.io.DeckIOCore;
import forge.quest.SellRules;
import forge.util.FileUtil;
import forge.util.SectionUtil;

/**
 * TODO: Write javadoc for this type.
 * 
 */
public class PreconDeck implements InventoryItemFromSet {

    private final Deck deck;
    private final String imageFilename;
    private final String set;
    private final String description;

    private final SellRules recommendedDeals;

    /*
     * (non-Javadoc)
     * 
     * @see forge.item.InventoryItemFromSet#getName()
     */
    @Override
    public String getName() {
        return this.deck.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see forge.item.InventoryItemFromSet#getImageFilename()
     */
    @Override
    public String getImageFilename() {
        return "precons/" + this.imageFilename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see forge.item.InventoryItem#getType()
     */
    @Override
    public String getType() {
        return "Prebuilt Deck";
    }

    /**
     * Instantiates a new precon deck.
     * 
     * @param f
     *            the f
     */
    public PreconDeck(final File f) {
        final List<String> deckLines = FileUtil.readFile(f);
        final Map<String, List<String>> sections = SectionUtil.parseSections(deckLines);
        this.deck = DeckIOCore.readDeck(deckLines);

        String filenameProxy = null;
        String setProxy = "n/a";
        String descriptionProxy = "";
        final List<String> metadata = sections.get("metadata");
        if ((null != metadata) && !metadata.isEmpty()) {
            for (final String s : metadata) {
                final String[] kv = s.split("=");
                if ("Image".equalsIgnoreCase(kv[0])) {
                    filenameProxy = kv[1];
                } else if ("set".equalsIgnoreCase(kv[0]) && (SetUtils.getSetByCode(kv[1].toUpperCase()) != null)) {
                    setProxy = kv[1];
                } else if ("Description".equalsIgnoreCase(kv[0])) {
                    descriptionProxy = kv[1];
                }
            }
        }
        this.imageFilename = filenameProxy;
        this.set = setProxy;
        this.recommendedDeals = new SellRules(sections.get("shop"));
        this.description = descriptionProxy;
    }

    /**
     * Gets the deck.
     * 
     * @return the deck
     */
    public final Deck getDeck() {
        return this.deck;
    }

    /**
     * Gets the recommended deals.
     * 
     * @return the recommended deals
     */
    public final SellRules getRecommendedDeals() {
        return this.recommendedDeals;
    }

    /*
     * (non-Javadoc)
     * 
     * @see forge.item.InventoryItemFromSet#getSet()
     */
    @Override
    public String getSet() {
        return this.set;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

}
