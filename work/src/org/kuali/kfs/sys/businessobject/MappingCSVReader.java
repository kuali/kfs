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
package org.kuali.kfs.sys.businessobject;

import java.io.IOException;
import java.io.InputStreamReader;

import org.kuali.kfs.sys.exception.ParseException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;

/**
 * Created for Research Participant Upload
 * This class uses the readNext method in CSVReader
 * to read a line from spreadsheet and parses the
 * line into an array of String of the columns in
 * the line.
 * It also uses the processLine method of the
 * CsvToBean to convert an array of String into
 * an Object. The type of Object it's creating
 * depends on the MappingStrategy passed into the
 * input parameter.
 *
 */
public class MappingCSVReader extends CsvToBean {

    private CSVReader csvReader;

    public MappingCSVReader(InputStreamReader inputStreamReader) {
        csvReader = new CSVReader(inputStreamReader);
    }

    public String[] readNext() throws IOException {
        return csvReader.readNext();
    }

    @Override
    public Object processLine(au.com.bytecode.opencsv.bean.MappingStrategy strat, java.lang.String[] line) throws ParseException {
        try {
            return super.processLine(strat, line);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
