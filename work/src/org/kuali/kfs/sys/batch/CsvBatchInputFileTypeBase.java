/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.exception.ParseException;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Base class for BatchInputFileType implementations that validate using an Enum class (use as the CSV file header) 
 * and parse using CSV comma delimited
 */
public abstract class CsvBatchInputFileTypeBase<CSVEnum extends Enum<CSVEnum>> extends BatchInputFileTypeBase {
    private static final Logger LOG = Logger.getLogger(CsvBatchInputFileTypeBase.class);

    private Class<?> csvEnumClass;
    
    /**
     * Constructs a BatchInputFileTypeBase.java.
     */
    public CsvBatchInputFileTypeBase() {
        super();
    }

    public void setCsvEnumClass(Class<?> csvEnumClass) {
        this.csvEnumClass = csvEnumClass;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        // default impl does nothing
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     * 
     * @return parsed object in structure - List<Map<String, String>>
     */
    public Object parse(byte[] fileByteContent) throws ParseException {
        
        // handle null objects and zero byte contents
        String errorMessage = fileByteContent == null? "an invalid(null) argument was given" : 
            fileByteContent.length == 0? "an invalid argument was given, empty input stream" : "";
        
        if (!errorMessage.isEmpty()){
            LOG.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<String> headerList = getCsvHeaderList();
        Object parsedContents = null;
        try {
            // validate csv header
            ByteArrayInputStream validateFileContents = new ByteArrayInputStream(fileByteContent);
            validateCSVFileInput(headerList, validateFileContents);
            
            //use csv reader to parse the csv content
            CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
            List<String[]>dataList = csvReader.readAll();
            
            //remove first header line
            dataList.remove(0);
            
            //parse and create List of Maps base on enum value names as map keys
            List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();
            Map<String, String>rowMap;
            int index = 0;
            for (String[] row : dataList){
                rowMap = new LinkedHashMap<String, String>();
                // reset index
                index = 0;
                
                for (String header : headerList){
                    rowMap.put(header, row[index++]);
                }
                dataMapList.add(rowMap);
            }
            
            parsedContents = dataMapList;
        }catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParseException(ex.getMessage(), ex);
        }
        return parsedContents;    
    }
    
    /**
     * Validates the CSV file content against the CSVEnum items as header
     * 1. content header must match CSVEnum order/value
     * 2. each data row should have same size as the header
     * 
     * @param expectedHeaderList        expected CSV header String list 
     * @param fileContents              contents to validate 
     * 
     * @throws IOException
     */
    private void validateCSVFileInput(final List<String> expectedHeaderList, InputStream fileContents) throws IOException {

        //use csv reader to parse the csv content
        CSVReader csvReader = new CSVReader(new InputStreamReader(fileContents));
        List<String> inputHeaderList = Arrays.asList(csvReader.readNext());

        String errorMessage = null;
        
        // validate
        if (!CollectionUtils.isEqualCollection(expectedHeaderList, inputHeaderList)){
            errorMessage = "CSV Batch Input File contains incorrect number of headers";
            //collection has same elements, now check the exact content orders by looking at the toString comparisons
        }else if (!expectedHeaderList.equals(inputHeaderList)){
            errorMessage = "CSV Batch Input File headers are different";
        }else {
            
            //check the content size as well if headers are validated
            int line = 1;
            List<String> inputDataList = null;
            while ((inputDataList = Arrays.asList(csvReader.readNext())) != null && errorMessage != null){
                //if the data list size does not match header list (its missing data)
                if (inputDataList.size() != expectedHeaderList.size()){
                    errorMessage = "line " + line + " layout does not match the header";
                }
                line++;
            }
        }
        
        if (errorMessage != null){
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
    
    /**
     * build the csv header list base on provided csv enum class
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List<String> getCsvHeaderList(){
        List<String> headerList = new ArrayList<String>();
        EnumSet<CSVEnum> enums = EnumSet.allOf((Class)csvEnumClass);
        for (Enum<CSVEnum> e : enums ){
            headerList.add(e.name());
        }
        return headerList;
    }
    
    /**
     * convert the parsed content into VOs
     * 
     * @param parsedContent
     * @return
     */
    abstract protected Object convertParsedObjectToVO(Object parsedContent);

}
