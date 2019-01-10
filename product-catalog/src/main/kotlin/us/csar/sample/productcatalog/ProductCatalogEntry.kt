/*
    ProductCatalog - A library for parsing a particular kind of Product Catalog.
    Copyright (C) 2019 Christian Csar

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package us.csar.sample.productcatalog

import java.util.*

/**
 */
typealias Currency = Int

/**
 * Represents an entry from the ProductCatalog file as opposed to the fully resolved ProductRecord. While for this
 * sample project there is no defined use for these, it may be helpful to keep track of this data, or simply to have
 * this intermediate type of record for testing purposes.
 */
data class ProductCatalogEntry(val productId: Int,
                               val productDescription : String,
                               val regularPrice : Price,
                               val promotionalPrice : Price?,
                               val flags: EnumSet<ProductFlags>,
                               val productSize : String) {
    /**
     * Represents a given level of price, Regular or Promotional.
     * Either a single price or a split price will exist.
     */
    data class Price(val singlePrice : Currency? = null, val splitPrice : SplitPrice? = null)

    data class SplitPrice(val combinedPrice : Currency, val quantityReceived : Int)

    enum class ProductFlags(/** The index in flags. 1 indexed.*/val position : Int) {
        PER_WEIGHT(3),
        TAXABLE(5)
    }

    companion object {
        /**
         * Parses a line of the ProductCatalog that (should) form a ProductCatalogEntry
         * Whether this should be here in a companion object, or a function or an extension
         * method on String is likely a matter of taste. Having it here feels closer to Java
         * than to Kotlin (ie String.toInt vs Integer.parseInt)
         */
        fun parse(line : String) : ProductCatalogEntry? {
            if(line.length != 142) {
                //Better error handling is outside the scope of this library for the present.
                return null
            }
            val productId : Int = parseNumber(line, 1)?: return null
            val productDescription = line.substring(9, 68).trimEnd() //Trim end since in theory only right padded.
            //Are regular prices that are in fact 0 acceptable?
            val regularSinglePrice : Int = parseNumber(line, 70)?: return null
            val promotionalSinglePrice : Int = parseNumber(line, 79)?: return null
            val regularSplitPrice : Int = parseNumber(line, 88)?: return null
            val promotionalSplitPrice : Int = parseNumber(line, 97)?: return null
            val regularForX : Int = parseNumber(line, 106)?: return null
            val promotionalForX :Int = parseNumber(line, 115)?: return null

            val flags = EnumSet.noneOf(ProductFlags::class.java)
            for(flag in ProductFlags.values()) {
                //124 is the 1 indexed column where flags start. -2 for 1 indexing the column and the flag position.
                if(line[flag.position+124-2] == 'Y'){
                    flags.add(flag)
                }
            }

            //Should likely be changed to trimStart to reflect the sample data rather than the specification.
            val productSize = line.substring(133, 142).trimEnd()

            val regularPrice : Price = when {
                regularSinglePrice !=0 -> Price(singlePrice = regularSinglePrice)
                regularSplitPrice != 0 && regularForX != 0 -> Price(splitPrice = SplitPrice(regularSplitPrice, regularForX))
                else -> return null
            }
            val promotionalPrice : Price? = when {
                promotionalSinglePrice != 0 -> Price(singlePrice = promotionalSinglePrice)
                promotionalSplitPrice != 0 && promotionalForX != 0 -> Price(splitPrice = SplitPrice(promotionalSplitPrice, promotionalForX))
                promotionalSplitPrice == 0 && promotionalForX == 0 -> null
                //More error checking than required.
                else -> return null
            }
            return ProductCatalogEntry(productId, productDescription, regularPrice, promotionalPrice, flags, productSize)
        }

        /**
         * Parses a number or currency column (which are all the same width)
         * @param startColumn 1 - Indexed like the specification
         */
        private fun parseNumber(line : String, startColumn : Int) : Int? {
            return line.substring(startColumn-1, startColumn+7).toIntOrNull()
        }
    }
}
