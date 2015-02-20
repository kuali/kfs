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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.kfs.gl.batch.service.BatchSortService;

/**
 * This class...
 */
public class BatchSortServiceImpl implements BatchSortService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSortServiceImpl.class);
    
    public void sortTextFileWithFields(String inputFileName, String outputFileName, Comparator comparator){
        FileReader inputFile = null;
        PrintStream outputFileStream = null;
        try {
            inputFile = new FileReader(inputFileName);
            outputFileStream = new PrintStream(outputFileName);
        }
         catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> lineList = new ArrayList();
        BufferedReader inputBufferedReader = new BufferedReader(inputFile);
        
        try {
            String currentLine = inputBufferedReader.readLine();
            while (currentLine != null) {
                lineList.add(currentLine);
            }
            inputBufferedReader.close();    
            outputFileStream.close();
        } catch (IOException e) {
            // FIXME: do whatever should be done here
            LOG.error("performDemerger Stopped: " + e.getMessage());
            throw new RuntimeException("sortTextFileWithFields() Stopped: " + e.getMessage(), e);
        }
        
        Collections.sort(lineList, comparator);
        
        for (String line: lineList){
            outputFileStream.printf("%s\n", line);
        }
        outputFileStream.close();
             
    }
    
    
}
