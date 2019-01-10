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
import java.math.BigDecimal
import kotlin.test.assertEquals

class PriceConversionTest {
    @Test
    fun confirmResolution() {
        assertEquals(ProductRecord.Price("10000 For $1.00", BigDecimal("0.0001")),
            ProductCatalogEntry.Price(splitPrice = ProductCatalogEntry.SplitPrice(100, 10000)).toProductRecordPrice())
    }

    @Test
    fun confirmHalfDown() {
        assertEquals(ProductRecord.Price("200 For $10.01", BigDecimal("0.0500")),
            ProductCatalogEntry.Price(splitPrice = ProductCatalogEntry.SplitPrice(1001, 200)).toProductRecordPrice())
    }

    @Test
    fun confirmRoundingUpHappens() {
        assertEquals(ProductRecord.Price("75 For $10.01", BigDecimal("0.1335")),
            ProductCatalogEntry.Price(splitPrice = ProductCatalogEntry.SplitPrice(1001, 75)).toProductRecordPrice())
    }
}
