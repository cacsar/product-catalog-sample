/*
    ProductCatalogExample - Example code for using ProductCatalog library
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
package us.csar.sample.productcatalog.example

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SequenceWriter
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.Namespace
import us.csar.sample.productcatalog.ProductCatalog
import us.csar.sample.productcatalog.generateErrorHandler
import us.csar.sample.productcatalog.toProductRecord
import java.io.*
import java.lang.IllegalArgumentException

fun main(args : Array<String>) {
    val parser = ArgumentParsers.newFor("ProductCatalogExample").build()
        .defaultHelp(true)
        .description("Turn Product Catalog file to JSON.")
    parser.addArgument("-i", "--infile")
        .setDefault("-")
        .dest(INPUT_FIELD)
        .help("Specify the input file to use. - for stdin")
    parser.addArgument("-o", "--outfile")
        .dest(OUTPUT_FIELD)
        .setDefault("-")
        .help("Specify the output file to use. - for stdout")
    parser.addArgument("-f", "--format")
        .choices("Record", "CatalogEntry")
        .setDefault("Record")
        .dest(FORMAT_FIELD)
        .help("File to calculate checksum")
    val ns : Namespace
    try {
        ns = parser.parseArgs(args)
    } catch (e: ArgumentParserException) {
        parser.handleError(e)
        System.exit(1)
        return //System exit isn't marked as not returning
    }
    val inputFile = ns.getString(INPUT_FIELD)
    val outputFile = ns.getString(OUTPUT_FIELD)
    val format = ns.getString(FORMAT_FIELD)

    val readerGenerator : () -> BufferedReader =
        //If in common use it could be nice to print an nice error message here.
        if(inputFile == STD_STREAM) {
            { BufferedReader(InputStreamReader(System.`in`)) }
        } else {
            { BufferedReader(FileReader(inputFile)) }
        }

    val outputStream : OutputStream =
            if(outputFile == STD_STREAM) {
                System.out
            } else {
                FileOutputStream(outputFile)
            }

    val mapper : ObjectMapper = ObjectMapper()
    mapper.registerModule(KotlinModule())
    val writer =  mapper.writerWithDefaultPrettyPrinter().writeValuesAsArray(outputStream)

    val catalog = ProductCatalog(readerGenerator, generateErrorHandler(true))

    when(format) {
        "Record" -> writeRecords(catalog, writer)
        "CatalogEntry" -> writeEntries(catalog, writer)
        else -> throw IllegalArgumentException("Incorrect format specified.")
    }
    writer.close()
}

fun writeRecords(catalog : ProductCatalog, writer : SequenceWriter) {
    for(entry in catalog) {
        //We refrain from using .map as it puts it into an intermediate list,
        //which suggests a Java8 stream might have been a better interface.
        writer.write(entry.toProductRecord())
    }
}

fun writeEntries(catalog : ProductCatalog, writer : SequenceWriter) {
    writer.writeAll(catalog)
}

private const val INPUT_FIELD = "input"
private const val OUTPUT_FIELD = "output"
private const val FORMAT_FIELD = "format"
private const val STD_STREAM = "-"