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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.module.tem.batch.service.PerDiemFileParsingService;
import org.kuali.kfs.sys.ObjectUtil;

import au.com.bytecode.opencsv.CSVReader;

public class PerDiemFileParsingServiceImpl implements PerDiemFileParsingService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemFileParsingServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemFileParsingService#buildPerDiemsFromFlatFile(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<PerDiemForLoad> buildPerDiemsFromFlatFile(String fileName, String deliminator, List<String> fieldsToPopulate) {
        try {
            Reader fileReader = new FileReader(fileName);

            return this.buildPerDiemsFromFlatFile(fileReader, deliminator, fieldsToPopulate);
        }
        catch (FileNotFoundException ex) {
            LOG.error("Failed to process data file: " + fileName);
            throw new RuntimeException("Failed to process data file: " + fileName, ex);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemFileParsingService#buildPerDiemsFromFlatFile(java.io.Reader,
     *      java.lang.String)
     */

    @Override
    public List<PerDiemForLoad> buildPerDiemsFromFlatFile(Reader reader, String deliminator, List<String> fieldsToPopulate) {
        List<PerDiemForLoad> perDiemList = new ArrayList<PerDiemForLoad>();

        CSVReader csvReader = null;
        try {
            char charDeliminator = deliminator.charAt(0);
            csvReader = new CSVReader(reader, charDeliminator);

            String[] perDiemInString = null;
            while ((perDiemInString = csvReader.readNext()) != null) {
                if (ArrayUtils.contains(perDiemInString, "FOOTNOTES: ")) {
                    break;
                }

                PerDiemForLoad perDiem = new PerDiemForLoad();
                ObjectUtil.buildObject(perDiem, perDiemInString, fieldsToPopulate);

                perDiemList.add(perDiem);
            }
        }
        catch (Exception ex) {
            LOG.error("Failed to process data file. ");
            throw new RuntimeException("Failed to process data file. ", ex);
        }
        finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                }
                catch (IOException ex) {
                    LOG.info(ex);
                }
            }
            
            IOUtils.closeQuietly(reader);
        }

        return perDiemList;
    }
}
