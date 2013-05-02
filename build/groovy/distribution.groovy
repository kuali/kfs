inputFile = new File( "${properties['all.temp.directory']}/kfs/build-foundation.xml" );
outputFile = new File( "${properties['all.temp.directory']}/kfs/build-foundation.tmp" );
skip = false

outputFile.withWriter { writer ->

	inputFile.eachLine { line ->
		if ( !skip && line =~ "REMOVE FROM DISTRIBUTION" ) {
			println "Starting Exclusion: " + line
			skip = true;
		} else if ( skip && line =~ "END REMOVE FROM DISTRIBUTION" ) {
			println "Ending Exclusion: " + line
			skip = false;
		} else {
			if ( !skip ) {
				writer.writeLine( line );
			}
		}
	}
}