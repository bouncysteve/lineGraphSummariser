# lineGraphSummariser
Eventually will generate a text summary of line graphs containing two series (e.g. https://en.wikipedia.org/wiki/Wikipedia:Size_of_Wikipedia#/media/File:PercentWikipediasGraph.png)

At the moment it outputs:

1) A String representation of the csv file containing the graph data
2) A String representation of the individual graph segments (including gradients of each segment)
3) A String representation of collated segments (where segments with the same gradient type (positive/negative/0) are "squished" together

At the moment the graphs must be in iScatter (http://michel.wermelinger.ws/chezmichel/iscatter/) format, i.e. comprise two csv files, one for the data, and one for the schema.

To build:

_gradlew.bat build_ or _./gradlew build_

To run:

java -jar <PATH_TO_JAR> <FULL_PATH_TO_DIRECTORY_CONTAINING_SCHEMA_AND_DATA_CSV_FILES>
