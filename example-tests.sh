#!/bin/bash
PCE="java -jar product-catalog-example/target/product-catalog-example.jar "
$PCE -i sample-catalog.txt -o - -f CatalogEntry | diff sample-catalog.json - || exit 1
$PCE -i sample-catalog.txt -o - -f Record | diff sample-catalog-record.json - || exit 1

