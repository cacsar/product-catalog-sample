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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.lang.Exception
import java.lang.IllegalArgumentException

private typealias OnError = (Int, String, Exception?) -> Unit

/**
 * Class to read instances of CatalogEntry from a file or stream.
 * @param readerSupplier - Function to get the reader containing the lines to be
 * parsed. One call is made per Iterator generated.
 * @param onError Function called whenever there's an error. If an exception is
 * thrown inside then it will be called when the line is read (which may be on
 * Iterator creation).
 */
class ProductCatalog(private val readerSupplier : ()-> BufferedReader, private val onError: OnError) : Iterable<ProductCatalogEntry> {

    constructor(filePath: String, onError : OnError) : this( { BufferedReader(FileReader(filePath)) }, onError)

    /* Not a great way to avoid clashes due to type erasure*/
    constructor(onError: OnError, inputStreamSupplier : () -> InputStream) : this( { BufferedReader(InputStreamReader(inputStreamSupplier.invoke()))}, onError)

    override fun iterator(): Iterator {
        return Iterator(readerSupplier.invoke(), onError)
    }

    class Iterator(private val reader : BufferedReader, private val onError : OnError) : kotlin.collections.Iterator<ProductCatalogEntry> {
        private var lineNumber = 0
        private var nextEntry : ProductCatalogEntry? = null

        private fun readNext() {
            var nextLine : String?
            do {
                lineNumber++
                nextLine = reader.readLine()
                if(nextLine == null) {
                    nextEntry = null
                    return
                }
                try
                {
                    nextEntry = ProductCatalogEntry.parse(nextLine)
                } catch (e : Exception) {
                    nextEntry == null
                    //In case there were some we didn't catch and we wanted to given that we claim to be able to skip
                    //over errors.
                    onError.invoke(lineNumber, nextLine, e)
                }
                if(nextEntry == null) {
                    onError.invoke(lineNumber, nextLine, null)
                }
            } while(nextEntry == null)
        }

        init {
            readNext()
        }

        override fun hasNext(): Boolean {
            return nextEntry != null
        }

        /**
         * Returns null if the entry does not parse.
         */
        override fun next(): ProductCatalogEntry {
            val ret = nextEntry ?: throw NoSuchElementException()
            readNext()
            return ret
        }
    }
}

fun generateErrorHandler(throwOnError: Boolean,
                         log : Logger = LoggerFactory.getLogger(ProductCatalog::class.java)) : OnError {
    return {lineNumber : Int, _ : String, e: Exception? ->
        if(e != null) {
            log.warn("Unable to process line $lineNumber")
        } else {
            log.warn("Unable to process line $lineNumber", e)
        }
        if(throwOnError) {
            throw IllegalArgumentException("Failed to parse line number $lineNumber", e)
        }
    }
}

/**
 * Convenience function for reading all the entries in a file skipping errors.
 * Using the iterator will tend to be more memory efficient.
 * @param filePath Path to the catalog to read
 * @param throwOnError If true will throw an IllegalArgumentException if any line cannot be parsed.
 */
fun readEntries(filePath: String, throwOnError : Boolean = false) : List<ProductCatalogEntry> {
    val catalog = ProductCatalog(filePath, generateErrorHandler(throwOnError))
    return catalog.toList()
}

/**
 * Convenience function for reading all the ProductRecords resulting from the Entries in a catalog.
 * Using the iterator directly will tend to be more memory efficient.
 * @param filePath Path to the catalog to read
 * @param throwOnError if true will throw an IllegalArgumentException if any line cannot be parsed.
 */
fun readRecords(filePath: String, throwOnError: Boolean = false) : List<ProductRecord> {
    val catalog = ProductCatalog(filePath, generateErrorHandler(throwOnError))
    return catalog.map { it.toProductRecord() }
}