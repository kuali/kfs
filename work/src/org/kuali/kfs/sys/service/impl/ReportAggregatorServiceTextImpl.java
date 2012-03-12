/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportAggregatorService;

public class ReportAggregatorServiceTextImpl implements ReportAggregatorService {
    protected String newLineCharacter;

    public void aggregateReports(File outputFile, List<File> files) {
        PrintWriter aggregateReportWriter = null;
        int pageNumber = 1;
        try {
            aggregateReportWriter = new PrintWriter(new FileWriter(outputFile));
        }
        catch (IOException e) {
            throw new RuntimeException("Error opening output file.", e);
        }
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (i != 0) {
                prepareForNewFile(aggregateReportWriter);
            }
            pageNumber = dumpFileContents(aggregateReportWriter, file, pageNumber);
            file.delete();
        }
        aggregateReportWriter.close();
    }

    protected int dumpFileContents(Writer outputWriter, File file, int currentPageNumber) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                String line = reader.readLine();
                while (line.contains(KFSConstants.REPORT_WRITER_SERVICE_PAGE_NUMBER_PLACEHOLDER)) {
                    line = StringUtils.replaceOnce(line, KFSConstants.REPORT_WRITER_SERVICE_PAGE_NUMBER_PLACEHOLDER, String.valueOf(currentPageNumber));
                    currentPageNumber++;
                }
                outputWriter.write(line);
                outputWriter.write(newLineCharacter);
            }
            reader.close();
            return currentPageNumber;
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading or writing file", e);
        }
    }
    
    protected void prepareForNewFile(PrintWriter outputWriter) {
        // 12 represents the ASCII Form Feed character
        outputWriter.printf("%c" + newLineCharacter, 12);
    }

    /**
     * Sets the newLineCharacter attribute value.
     * @param newLineCharacter The newLineCharacter to set.
     */
    public void setNewLineCharacter(String newLineCharacter) {
        this.newLineCharacter = newLineCharacter;
    }
}
