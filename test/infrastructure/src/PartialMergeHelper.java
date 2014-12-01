/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.regex.Pattern;

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
