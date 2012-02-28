import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.regex.Pattern;

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

public class PartialMergeHelper {

    static final Pattern fileBreakPattern = Pattern.compile("^Index: ");

    public static void main(String[] args) throws Exception {
        String partialMergeDiffFileName = args[0];
        String completeMergeDiffFileName = args[1];
        String mergeRemainderDiffFileName = args[2];

        HashSet<String> filesInPartialMerge = new HashSet<String>();

        // first, go through the partial merge file and extract all the updated file names
        // build a list of the file break patterns and save in a HashSet
        File partialMergeFile = new File( partialMergeDiffFileName );
        System.out.println( "Partial Merge file: " + partialMergeFile.getAbsolutePath() );
        BufferedReader r = new BufferedReader(new FileReader( partialMergeFile ) );
        String line;
        while ( (line = r.readLine()) != null ) {
            if ( fileBreakPattern.matcher(line).find() ) {
                System.out.println( "Adding to partial merge file list: " + line );
                filesInPartialMerge.add(line);
            }
        }

        // second, loop over the complete merge file, suppressing any entries which appear in the
        // first merge file
        File inputFile = new File( completeMergeDiffFileName );
        System.out.println( "Attemping to read from file: " + inputFile.getAbsolutePath() );
        File outputFile = new File ( mergeRemainderDiffFileName );
        System.out.println( "Output file: " + outputFile.getAbsolutePath() );
        r = new BufferedReader(new FileReader( inputFile ) );
        BufferedWriter w = new BufferedWriter( new FileWriter(outputFile) );
        boolean inFileSuppressionMode = false;
        int suppressionLineCount = 0;
        while ( (line = r.readLine()) != null ) {
            if ( fileBreakPattern.matcher(line).find() ) {
//                if ( inFileSuppressionMode ) {
//                    System.out.println( "Completing File Suppression.  Lines Removed: " + suppressionLineCount );
//                }
                inFileSuppressionMode = false;
                System.out.println( "Found File Break: " + line );
                if ( filesInPartialMerge.contains(line) ) {
                    System.out.println( "    HAS ALREADY BEEN MERGED - SUPPRESSING OUTPUT" );
                    inFileSuppressionMode = true;
                    suppressionLineCount = 0;
                }
            }
            if ( !inFileSuppressionMode ) {
                w.write(line);
                w.newLine();
            } else {
                suppressionLineCount++;
            }
        }
        if ( inFileSuppressionMode ) {
            System.out.println( "Completing File Suppression.  Lines Removed: " + suppressionLineCount );
        }
        r.close();
        w.close();
    }

}
