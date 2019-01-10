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
import us.csar.sample.productcatalog.ProductCatalogEntryTest.Companion.applePCE
import us.csar.sample.productcatalog.ProductCatalogEntryTest.Companion.cigarettePCE
import us.csar.sample.productcatalog.ProductCatalogEntryTest.Companion.kimchiPCE
import us.csar.sample.productcatalog.ProductCatalogEntryTest.Companion.sodaPCE
import java.math.BigDecimal
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class ProductRecordTest(private val entry : ProductCatalogEntry, private val expected: ProductRecord) {

    @Test
    fun convert() {
        val calculated : ProductRecord= entry.toProductRecord()
        assertEquals(calculated, expected)
    }


    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Iterable<Array<Any>> {
            //This test is arguably over precise as we only care that the BigDecimal values have the same value not that
            //they are necessarily at any particular scale.
            return listOf(
                arrayOf(applePCE, ProductRecord(applePCE.productId, applePCE.productDescription,
                    ProductRecord.Price("$3.49", BigDecimal("3.4900")),
                    null, ProductRecord.UnitOfMeasure.Pound, applePCE.productSize, BigDecimal.ZERO)),
                arrayOf(cigarettePCE, ProductRecord(cigarettePCE.productId, cigarettePCE.productDescription,
                    ProductRecord.Price("$10.00", BigDecimal("10.0000")),
                    ProductRecord.Price("$5.49", BigDecimal("5.4900")), ProductRecord.UnitOfMeasure.Each, cigarettePCE.productSize, BigDecimal.ZERO)),
                arrayOf(sodaPCE, ProductRecord(sodaPCE.productId, sodaPCE.productDescription,
                    ProductRecord.Price("2 For $13.00", BigDecimal("6.5000")),
                    ProductRecord.Price("$5.49", BigDecimal("5.4900")), ProductRecord.UnitOfMeasure.Each, sodaPCE.productSize, ProductRecord.constantTaxRate)),
                arrayOf(kimchiPCE, ProductRecord(kimchiPCE.productId, kimchiPCE.productDescription,
                    ProductRecord.Price("$5.67", BigDecimal("5.6700")),
                    null, ProductRecord.UnitOfMeasure.Each, kimchiPCE.productSize, BigDecimal.ZERO
                ))
            )
        }
    }
}