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
