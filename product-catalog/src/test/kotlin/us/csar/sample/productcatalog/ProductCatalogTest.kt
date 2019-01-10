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
import java.io.BufferedReader
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.fail

class ProductCatalogTest {
    @Test
    fun goodCatalog() {
        val catalog = ProductCatalog( generateErrorHandler((false))) { ProductCatalogTest::class.java.getResourceAsStream("/sample-catalog.txt") }
        val entries = catalog.toList()
        assertEquals(expectedReturn, entries)
    }

    @Test
    fun badCatalog() {
        val catalog = ProductCatalog( {lineNumber : Int, line : String , e: Exception? ->
            assertEquals(3, lineNumber)
            assertEquals(badLine, line)
            assertNull(e)
        }, {ProductCatalogTest::class.java.getResourceAsStream("/sample-catalog-bad.txt")})
        val entries = catalog.toList()
        assertEquals(expectedReturn, entries)
    }

    @Test
    fun emptyCatalog() {
        val catalog = ProductCatalog({ BufferedReader(StringReader(""))}, { _, _, _ ->
            fail("Some error generated on empty file")
        })
        val entries = catalog.toList()
        assert(entries.isEmpty())
    }

    private val badLine = "14963801 NoPrice                                                     00000000 00000000 00000000 00000000 00000000 00000000 XXXXXXXXX        55"

    private val expectedReturn = listOf(
        ProductCatalogEntryTest.kimchiPCE,
        ProductCatalogEntryTest.sodaPCE,
        ProductCatalogEntryTest.cigarettePCE,
        ProductCatalogEntryTest.applePCE
    )
}