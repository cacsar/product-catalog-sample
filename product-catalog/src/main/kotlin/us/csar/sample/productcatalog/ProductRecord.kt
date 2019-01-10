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

import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * A more processed form of a ProductCatalogEntry with certain pricing details
 * worked out further.
 */
data class ProductRecord(val productId: Int,
                         val productDescription: String,
                         val regularPrice: Price,
                         val promotionalPrice : Price?,
                         val unitOfMeasure : UnitOfMeasure,
                         /** This will sometimes be an empty string.*/
                         val productSize : String,
                         /** Multiplier, not per hundred*/
                         val taxRate : BigDecimal
                         ) {

    /** If we're unlucky we may need to use a custom serializer for the BigDecimal.*/
    data class Price(val display: String, val calculatorPrice: BigDecimal)
    enum class UnitOfMeasure{
        Each,
        Pound
    }

    companion object {
        /** The constant tax rate used when converting ProductCatalog Entries. Perhaps should live outside of ProductRecord*/
        val constantTaxRate : BigDecimal= BigDecimal.valueOf(7775,5)
    }
}

/**
 * Extension function containing all the current logic for converting from a
 * ProductCatalogEntry to a ProductRecord. This assumes a single fixed tax rate.
 */
fun ProductCatalogEntry.toProductRecord() : ProductRecord {
    val unitOfMeasure : ProductRecord.UnitOfMeasure = if(this.flags.contains(ProductCatalogEntry.ProductFlags.PER_WEIGHT)) {
        ProductRecord.UnitOfMeasure.Pound
    } else {
        ProductRecord.UnitOfMeasure.Each
    }

    val taxRate = if(this.flags.contains(ProductCatalogEntry.ProductFlags.TAXABLE)) {
        ProductRecord.constantTaxRate
    } else {
        BigDecimal.ZERO
    }

    return ProductRecord(productId = this.productId,
        productDescription = this.productDescription,
        regularPrice = this.regularPrice.toProductRecordPrice(),
        promotionalPrice = this.promotionalPrice?.toProductRecordPrice(),
        unitOfMeasure = unitOfMeasure,
        productSize = this.productSize,
        taxRate = taxRate
        )
}

/**
 * Converts the prices in a CatalogEntry to a resolved form for use in standard
 * calculation. Calculation of display strings is done in a specific fashion
 * that may not be to taste.
 */
fun ProductCatalogEntry.Price.toProductRecordPrice() : ProductRecord.Price {
    val singlePrice = this.singlePrice
    val splitPrice = this.splitPrice
    return when {
        singlePrice != null -> {
            val calculatorPrice = BigDecimal.valueOf(singlePrice.toLong(), 2)
            //There's an open question here of whether this should be "Per Pound" in the case of Unit of Measure, but
            //since all the information about the quantity is in the outer record and there may be similar ambiguity around
            //packs of beverages we can take the simple approach.
            val displayString = "\$${calculatorPrice.toPlainString()}"
            ProductRecord.Price(displayString, calculatorPrice.setScale(4))
        }
        splitPrice != null -> {
            val combinedPrice : BigDecimal = BigDecimal.valueOf(splitPrice.combinedPrice.toLong(), 2)
            //It would seem like this ought to be localized.
            val displayPrice : String = "${splitPrice.quantityReceived} For \$${combinedPrice.toPlainString()}"
            val calculatorPrice : BigDecimal = combinedPrice.divide(BigDecimal.valueOf(splitPrice.quantityReceived.toLong()), 4, RoundingMode.HALF_DOWN)
            ProductRecord.Price(displayPrice, calculatorPrice)
        }
        else -> throw IllegalArgumentException("ProductCatalogEntry.Price with no price present")
    }
}
