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
