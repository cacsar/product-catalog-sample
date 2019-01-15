# Sample Product Catalog Ingestion Library
[![Build Status](https://travis-ci.org/cacsar/product-catalog-sample.svg?branch=master)](https://travis-ci.org/cacsar/product-catalog-sample)

## Purpose

This library and repository serve as sample code resulting from the take home 
assignment of a certain potential employer. Thus while it presents a view into 
the author's coding style and practices at the time of writing, this should not 
be considered a maintained project. It is unlikely that this library will be of
use in a production environment.

## Build Instructions

Builds are done with maven, thus `mvn package`

## Usage

Since this is not really intended for use the library is not published to any
maven repository. However, if it were in such a repository then the below would
be the appropriate dependency to add.
```
<dependency>
    <groupId>us.csar.sample</groupId>
    <artifactId>product-catalog</artifactId>
    <version>1.0.0</version>
<dependency>
```

A cli example using the library is included in product-catalog-example. Its usage
can be seen in its help message `java -jar product-catalog-example.jar --help`.
For basic usage `java -jar product-catalog-example.jar <sample-catalog.txt > sample-catalog.json`
will convert sample-catalog.txt to an easier to parse JSON
representation whereas `java -jar product-catalog-example.jar -f Record <sample-catalog.txt > sample-catalog-record.json`
converts each row to a more processed form as defined by the problem specification
this sample was written in response to.

## Releases
Tagged releases built by the CI system can be found at https://github.com/cacsar/product-catalog-sample/releases
