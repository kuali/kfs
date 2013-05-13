/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
if ( args.length < 1 ) {
    println "This script will convert source code using Rice 1.x packages and APIs into Rice 2.x versions."
    println ""
    println "It is not 100% and *WILL* result in code that does not compile.  "
    println "Furthermore, a number of the conversions are based on conventions used"
    println "by the KFS developers.  YMMV"
    println "But, due to the package changes, without this script, virtually all "
    println "of the code which references Rice will fail to compile." 
    return 0
}
testRules = false;

conversionRootDir = args[0]
def mappingDir = "."
def groovy.util.Node root
patternFileSuffix = "patterns.xml"

updateLog = new File(conversionRootDir, "updates.log")

excludeDirs = [ "classes", "tomcat", "lib", ".settings", "performance", "jaxb" ]
excludeFiles = [ patternFileSuffix ];

class ReplacementInfo {
    String regex
    String replacement
    String fileType
    String importChange
    String importChangeReplacement
    
    String toString() {
        return "(" + fileType + ") " + regex + " ==> " + replacement
    }
}

roots = []
replacementList = []
referencesToLog = []

def loadMappings( dir ) {
	println "Looking for pattern files in: " + new File(dir).getAbsolutePath() 
    def files = new File(dir).list()
	files.each {
		String fileName ->
		File file = new File(dir, fileName)
	    if ( file.isDirectory() ) {
            //println "Looking for pattern files in: " + file.getAbsolutePath() 
	        //loadMappings( file.getAbsolutePath() )
	    } else if (fileName.endsWith(patternFileSuffix)) {
            println "Parsing File: " + file.getAbsolutePath() 
            parser = new XmlParser()
            parser.setTrimWhitespace( false );
            roots.add( parser.parseText(file.getText()) );
	    }
    }
}

def loadRules( ruleName ) {
	println "Loading Rule Set: " + ruleName
    // get the rule node with the given name
    
    roots.each {
        root ->
            def rules = root.rule.findAll {
                rule ->
                if(rule.'@name'.contains(ruleName)) node = rule
            }    
    }
    
    // get all the patterns from the given rule set
    def numPatterns = 0;
	def pattern = node.pattern.findAll{
		pattern ->
        numPatterns++;
		strToMatch = pattern.match.text()
		replacement = pattern.replacement.text()
        fileType = "ALL";
        if ( pattern.'@fileType' ) {
            fileType = pattern.'@fileType'; 
        }
        ReplacementInfo ri = new ReplacementInfo()
        ri.regex = strToMatch
        ri.replacement = replacement
        ri.fileType = fileType;
        def importStr = pattern.replacement[0].'@addImport'
        if(importStr) {
            ri.importChange = "import " + importStr.split(":")[0] + ";" ;
            ri.importChangeReplacement = "import " + importStr.split(":")[1] + ";" ;
        }
        replacementList.add( ri );

        // if we are set to also rename the class, then we split off the component
        // after the last "." and add an additional rename based on that.
        if(node.'@alsoRenameClass') {
            def strToMatchSplit = strToMatch.split("\\.")
            def replacementSplit =  replacement.split("\\.")
            if ( strToMatchSplit[strToMatchSplit.length-1] != replacementSplit[replacementSplit.length-1] ) {
                ri = new ReplacementInfo()
                ri.fileType = fileType;
                ri.regex = strToMatchSplit[strToMatchSplit.length-1]
                ri.replacement = replacementSplit[replacementSplit.length-1]
                replacementList.add( ri );
            }
        }
	}
    println "Found " + numPatterns + " patterns"
}

def convertDir( dir ) {
    def files = new File(dir).list()    
    files.each {
        String fileName ->
        File file = new File(dir, fileName)
        if ( file.isDirectory()  && !excludeDirs.contains(fileName)) {
        	convertDir( file.getAbsolutePath() )
        } else {
            if ( (fileName.endsWith( ".java" ) 
                    || fileName.endsWith( ".xml" ) 
                    || fileName.endsWith( ".jrxml" ) 
                    || fileName.endsWith( ".jsp" ) 
                    || fileName.endsWith( ".tag" ) 
                    || fileName.endsWith( ".properties" ))
                    && !fileName.endsWith( patternFileSuffix )
                    && !excludeFiles.contains( fileName ) ) {
            	convertFile(file, replacementList)
            }
        }
    }
}

def convertFile( file, replacements ) {
	String originalFileText = file.text
    String convertedFileText = originalFileText
	updateLog.append("Processing File " + file.getAbsolutePath() + "\n")
	for ( e in replacements ) {
        if ( e.fileType == "ALL" || file.getAbsolutePath().endsWith( e.fileType ) ) {
            if ( convertedFileText =~ e.regex ) {
    			updateLog.append("Found " + e.regex + "\n")
                def matcher = ( convertedFileText =~ e.regex )
    			convertedFileText = matcher.replaceAll( e.replacement )
                if ( e.importChange ) {
    				convertedFileText = convertedFileText.replace( e.importChange, e.importChangeReplacement )
    				updateLog.append("Added import for: " + e.importChange)
    			}
    		}
        }
	}

	if ( !convertedFileText.equals( originalFileText ) ) {
		println "Converted File: " + file.getAbsolutePath()
		updateLog.append("Converted File: " + file.getAbsolutePath() + "\n")
		file.delete();
		file << convertedFileText;
	}
}

loadMappings(mappingDir)

if ( testRules ) {
	loadRules('new_rule_testing_1')
	loadRules('new_rule_testing_2')
} else {
    // handle explicit conversions first
    loadRules('handle_RICE_20_conversion_comments')
	// KNS
	loadRules('KNS_moved_classes')
	loadRules('KNS_moved_packages')
	loadRules('KNS_undo_moved_package_exceptions')
	loadRules('KNS_special_replacements')
	// KIM
	loadRules('KIM_moved_classes')
	loadRules('KIM_moved_packages')
	loadRules('KIM_undo_moved_package_exceptions')
	loadRules('KIM_special_replacements')
	// KEW
	loadRules('KEW_moved_classes')
	loadRules('KEW_moved_packages')
	loadRules('KEW_undo_moved_package_exceptions')
	loadRules('KEW_special_replacements')
	// Core
	loadRules('CORE_moved_classes')
	loadRules('CORE_moved_packages')
	loadRules('CORE_undo_moved_package_exceptions')
	loadRules('CORE_special_replacements')
	// KFS	
	loadRules('KFS_spring_context_updates')
	loadRules('KFS_kim_attribute_updates')
}

for ( e in replacementList ) {
    println e;
    def matcher = ( "" =~ e.regex ) // This will test each expression as it is displayed (for troubleshooting purposes)
} 

println "Starting Conversion"
convertDir(conversionRootDir)
