/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
