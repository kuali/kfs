/*
 * Copyright 2008-2009 The Kuali Foundation
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
