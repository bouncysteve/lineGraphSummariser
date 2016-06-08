# lineGraphSummariser [![Build Status](https://travis-ci.org/bouncysteve/lineGraphSummariser.svg?branch=master)](https://travis-ci.org/bouncysteve/lineGraphSummariser)

This program generates a text summary of [a line graph containing two series](https://en.wikipedia.org/wiki/Wikipedia:Size_of_Wikipedia#/media/File:PercentWikipediasGraph.png)

At the moment the graphs must be in [iScatter](http://michel.wermelinger.ws/chezmichel/iscatter/) format, i.e. comprise two csv files, one for the data, and one for the schema.

The program writes up to two files in the same directory as the original data:

1. "summary.txt", a paragraph containing details about the graph (title, labels, etc.) and a paragraph containing, for subsequent pairs of values, a sentence about the behaviour of both series (rising/falling/constant).
2. (optionally) "collatedSummary.txt", as above, but where consecutive segments with the same gradient type (positive/negative/0) are "squished" together. If no collation is possible then this file is not written.


To build:

`gradlew.bat build` or `./gradlew build`

To run:

`java -jar <PATH_TO_JAR> <FULL_PATH_TO_DIRECTORY_CONTAINING_SCHEMA_AND_DATA_CSV_FILES>`
