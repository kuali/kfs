/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.endow.util;

import static org.kuali.kfs.module.endow.EndowConstants.TRANSACTION_LINE_ERRORS;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.KEMID;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_DESCRIPTION;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNIT_ADJUSTMENT_AMOUNT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants.EndowmentTransactionDocumentConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.exception.LineParserException;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class LineParserBase implements LineParser {

    /**
     * The default format defines the expected line property names and their order in the import file. Please update this if the
     * import file format changes (i.e. adding/deleting line properties, changing their order).
     */
    protected static final String[] DEFAULT_LINE_FORMAT = { KEMID, TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, TRANSACTION_LINE_DESCRIPTION, TRANSACTION_LINE_IP_INDICATOR_CODE, TRANSACTION_LINE_TRANSACTION_UNITS, TRANSACTION_LINE_TRANSACTION_AMOUNT, TRANSACTION_LINE_TRANSACTION_UNIT_ADJUSTMENT_AMOUNT };

    private Integer lineNo = 0;

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#getItemFormat()
     */
    public String[] getLineFormat() {
        return DEFAULT_LINE_FORMAT;
    }


    /**
     * Retrieves the attribute label for the specified attribute.
     * 
     * @param clazz the class in which the specified attribute is defined
     * @param attributeName the name of the specified attribute
     * @return the attribute label for the specified attribute
     */
    protected String getAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * Checks whether the specified Line class is a subclass of EndowmentTransactionLine; throws exceptions if not.
     * 
     * @param lineClass the specified line class
     */
    protected void checkLineClass(Class<? extends EndowmentTransactionLine> lineClass) {
        if (!EndowmentTransactionLine.class.isAssignableFrom(lineClass))
            throw new IllegalArgumentException("Unknown Line class: " + lineClass);
    }

    /**
     * Checks whether the specified line import file is not null and of a valid format; throws exceptions if conditions not
     * satisfied.
     * 
     * @param lineClass the specified line import file
     */
    protected void checkLineFile(FormFile lineFile) {
        if (lineFile == null)
            throw new LineParserException("Invalid (null) Line import file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);

        if (lineFile.getFileSize() == 0)
            throw new LineParserException("Invalid (null) Line import file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);

        String fileName = lineFile.getFileName();
        if (StringUtils.isNotBlank(fileName) && !StringUtils.lowerCase(fileName).endsWith(".csv"))
            throw new LineParserException("unsupported Line import file format: " + fileName, KFSKeyConstants.ERROR_LINEPARSER_INVALID_FILE_FORMAT, fileName);
    }

    /**
     * Parses a line of transactions data from a csv file and retrieves the attributes as key-value string pairs into a map.
     * 
     * @param line a string read from a line in the line import file
     * @return a map containing line attribute name-value string pairs
     */
    protected Map<String, String> retrieveLineAttributes(String line, Class<? extends EndowmentTransactionLine> lineClass) {
        String[] attributeNames = getLineFormat();
        String[] attributeValues = StringUtils.splitPreserveAllTokens(line, ',');

        if (attributeNames.length != attributeValues.length) {
            String[] errorParams = { "" + attributeNames.length, "" + attributeValues.length, "" + lineNo };
            GlobalVariables.getMessageMap().putError(TRANSACTION_LINE_ERRORS, EndowmentTransactionDocumentConstants.ERROR_LINEPARSER_WRONG_PROPERTY_NUMBER, errorParams);
            throw new LineParserException("Wrong number of Line properties: " + attributeValues.length + " exist, " + attributeNames.length + " expected (line " + lineNo + ")", EndowmentTransactionDocumentConstants.ERROR_LINEPARSER_WRONG_PROPERTY_NUMBER, errorParams);
        }

        Map<String, String> lineMap = new HashMap<String, String>();
        for (int i = 0; i < attributeNames.length; i++) {
            String attributeName = attributeNames[i];
            String attributeValue = attributeValues[i];
            // if the attribute has an forceUpper = true in the data dictionary, convert the value to upper case...
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(lineClass, attributeName)) {
                attributeValue = attributeValue.toUpperCase();
            }
            
            lineMap.put(attributeName, attributeValue);
        }
        return lineMap;
    }

    /**
     * Generates an line instance and populates it with the specified attribute map.
     * 
     * @param lineMap the specified attribute map from which attributes are populated
     * @param lineClass the class of which the new line instance shall be created
     * @return the populated line
     */
    protected EndowmentTransactionLine genLineWithRetrievedAttributes(Map<String, String> lineMap, Class<? extends EndowmentTransactionLine> lineClass) {
        EndowmentTransactionLine line;
        try {
            line = lineClass.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Unable to complete line line population.", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("Unable to complete line line population.", e);
        }

        boolean failed = false;
        for (Entry<String, String> entry : lineMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                try {
                    ObjectUtils.setObjectProperty(line, key, value);
                }
                catch (FormatException e) {
                    String[] errorParams = { value, key, "" + lineNo };
                    throw new LineParserException("Invalid property value: " + key + " = " + value + " (line " + lineNo + ")", EndowmentTransactionDocumentConstants.ERROR_LINEPARSER_INVALID_PROPERTY_VALUE, errorParams);
                }
            }
            catch (LineParserException e) {
                // Continue to parse the rest of the line properties after the current property fails
                GlobalVariables.getMessageMap().putError(TRANSACTION_LINE_ERRORS, e.getErrorKey(), e.getErrorParameters());
                failed = true;
            }
            catch (IllegalAccessException e) {
                throw new InfrastructureException("unable to complete line line population.", e);
            }
            catch (NoSuchMethodException e) {
                throw new InfrastructureException("unable to complete line line population.", e);
            }
            catch (InvocationTargetException e) {
                throw new InfrastructureException("unable to complete line line population.", e);
            }
        }

        if (failed) {
            throw new LineParserException("empty or invalid line properties in line " + lineNo + ")", EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_PARSE_INVALID, "" + lineNo);
        }
        return line;
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(org.apache.struts.upload.FormFile,java.lang.Class,java.lang.String)
     */
    public List<EndowmentTransactionLine> importLines(FormFile lineFile, Class<? extends EndowmentTransactionLine> lineClass, String documentNumber) {
        InputStream is;
        BufferedReader br = null;

        // Open input stream
        List<EndowmentTransactionLine> importedLines = new ArrayList<EndowmentTransactionLine>();

        try {
            // Check input File.
            checkLineFile(lineFile);

            is = lineFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            // Parse lines line by line
            lineNo = 0;
            boolean failed = false;
            String tranLine = null;
            try {
                while ((tranLine = br.readLine()) != null) {
                    lineNo++;
                    try {
                        EndowmentTransactionLine line = parseLine(tranLine, lineClass, documentNumber);
                        importedLines.add(line);
                    }
                    catch (RuntimeException e) {
                        // continue to parse the rest of the lines after the current line fails
                        // error messages are already dealt with inside parseItem, so no need to do anything here
                        failed = true;
                    }
                }

                if (failed) {
                    throw new LineParserException("Errors in parsing lines in file " + lineFile.getFileName(), EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_PARSE_INVALID, lineFile.getFileName());
                }
            }
            catch (IOException e) {
                throw new InfrastructureException("Unable to read line from BufferReader in LineParserBase", e);
            }
        }
        catch (IOException e) {
            throw new InfrastructureException("Unable to read line from BufferReader in LineParserBase", e);
        }
        finally {
            try {
                if (null != br)
                    br.close();
            }
            catch (IOException e) {
                throw new InfrastructureException("Unable to close BufferReader in LineParserBase", e);
            }
        }

        return importedLines;
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(java.lang.String,java.lang.Class,java.lang.String)
     */
    public EndowmentTransactionLine parseLine(String transactionLine, Class<? extends EndowmentTransactionLine> lineClass, String documentNumber) {
        Map<String, String> lineMap = retrieveLineAttributes(transactionLine, lineClass);
        EndowmentTransactionLine line = genLineWithRetrievedAttributes(lineMap, lineClass);
        // populateExtraAttributes( line, documentNumber );
        line.refresh();
        return line;
    }
}
