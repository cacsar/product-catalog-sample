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

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(Parameterized::class)
class ProductCatalogEntryTest(private val line: String, private val expected : ProductCatalogEntry) {

    @Test
    fun testParse() {
        val parsed : ProductCatalogEntry= ProductCatalogEntry.parse(line)?: fail("No product catalog entry parsed.")
        assertEquals(parsed, expected)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Iterable<Array<Any>> {
            return listOf(
                arrayOf("80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                    kimchiPCE),
                arrayOf("14963801 Generic Soda 12-pack                                        00000000 00000549 00001300 00000000 00000002 00000000 NNNNYNNNN   12x12oz",
                    sodaPCE),
                arrayOf("40123401 Marlboro Cigarettes                                         00001000 00000549 00000000 00000000 00000000 00000000 YNNNNNNNN          ",
                    cigarettePCE),
                arrayOf("50133333 Fuji Apples (Organic)                                       00000349 00000000 00000000 00000000 00000000 00000000 NNYNNNNNN        lb",
                    applePCE)
            )
        }

        val applePCE = ProductCatalogEntry(50133333, "Fuji Apples (Organic)",
            ProductCatalogEntry.Price(349), null, EnumSet.of(ProductCatalogEntry.ProductFlags.PER_WEIGHT),
            "       lb")
        val cigarettePCE = ProductCatalogEntry(40123401, "Marlboro Cigarettes",
            ProductCatalogEntry.Price(1000), //There's another flag there but we know nothing about it.
            ProductCatalogEntry.Price(549), EnumSet.noneOf(ProductCatalogEntry.ProductFlags::class.java),
            "")

        val sodaPCE = ProductCatalogEntry(14963801, "Generic Soda 12-pack",
            ProductCatalogEntry.Price(splitPrice = ProductCatalogEntry.SplitPrice(1300, 2)),
            ProductCatalogEntry.Price(549), EnumSet.of(ProductCatalogEntry.ProductFlags.TAXABLE),
            "  12x12oz")

        val kimchiPCE = ProductCatalogEntry(80000001, "Kimchi-flavored white rice",
            ProductCatalogEntry.Price(567), null, EnumSet.noneOf(ProductCatalogEntry.ProductFlags::class.java),
            "     18oz")

    }
}