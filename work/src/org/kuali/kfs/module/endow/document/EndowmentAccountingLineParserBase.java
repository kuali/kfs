/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import static org.kuali.kfs.module.endow.EndowKeyConstants.EndowmentAccountingLineParser.ERROR_INVALID_FILE_FORMAT;
import static org.kuali.kfs.module.endow.EndowKeyConstants.EndowmentAccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_ACCT_NBR;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_AMOUNT;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_CHART_CD;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_NBR;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_OBJECT_CD;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_ORG_REF_ID;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_PROJECT_CD;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_SUBACCT_NBR;
import static org.kuali.kfs.module.endow.EndowPropertyConstants.ENDOWMENT_ACCOUNTING_LINE_SUBOBJ_CD;

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
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class EndowmentAccountingLineParserBase implements EndowmentAccountingLineParser {

    protected static final String[] DEFAULT_FORMAT = { ENDOWMENT_ACCOUNTING_LINE_CHART_CD, ENDOWMENT_ACCOUNTING_LINE_ACCT_NBR, ENDOWMENT_ACCOUNTING_LINE_SUBACCT_NBR, ENDOWMENT_ACCOUNTING_LINE_OBJECT_CD, ENDOWMENT_ACCOUNTING_LINE_SUBOBJ_CD, ENDOWMENT_ACCOUNTING_LINE_PROJECT_CD, ENDOWMENT_ACCOUNTING_LINE_ORG_REF_ID, ENDOWMENT_ACCOUNTING_LINE_AMOUNT };
    private String fileName;
    private Integer lineNo = 0;


    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#getSourceEndowmentAccountingLineFormat()
     */
    public String[] getSourceEndowmentAccountingLineFormat() {
        return DEFAULT_FORMAT;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#getTargetEndowmentAccountingLineFormat()
     */
    public String[] getTargetEndowmentAccountingLineFormat() {
        return DEFAULT_FORMAT;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#getExpectedEndowmentAccountingLineFormatAsString(java.lang.Class)
     */
    public String getExpectedEndowmentAccountingLineFormatAsString(Class<? extends EndowmentAccountingLine> accountingLineClass) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String attributeName : chooseFormat(accountingLineClass)) {
            if (!first) {
                sb.append(",");
            }
            else {
                first = false;
            }
            sb.append(retrieveAttributeLabel(accountingLineClass, attributeName));
        }
        return sb.toString();
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#parseSourceEndowmentAccountingLine(org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument,
     *      java.lang.String)
     */
    public SourceEndowmentAccountingLine parseSourceEndowmentAccountingLine(EndowmentAccountingLinesDocument transactionalDocument, String sourceAccountingLineString) {
        Class sourceAccountingLineClass = getSourceEndowmentAccountingLineClass(transactionalDocument);
        SourceEndowmentAccountingLine sourceAccountingLine = (SourceEndowmentAccountingLine) populateAccountingLine(transactionalDocument, sourceAccountingLineClass, sourceAccountingLineString, parseAccountingLine(sourceAccountingLineClass, sourceAccountingLineString), transactionalDocument.getNextSourceLineNumber());
        return sourceAccountingLine;
    }

    /**
     * Given a document, determines what class the source lines of that document uses
     * 
     * @param accountingDocument the document to find the class of the source lines for
     * @return the class of the source lines
     */
    protected Class getSourceEndowmentAccountingLineClass(final EndowmentAccountingLinesDocument accountingDocument) {
        return accountingDocument.getSourceAccountingLineClass();
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#parseTargetEndowmentAccountingLine(org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument,
     *      java.lang.String)
     */
    public TargetEndowmentAccountingLine parseTargetEndowmentAccountingLine(EndowmentAccountingLinesDocument transactionalDocument, String targetAccountingLineString) {
        Class targetAccountingLineClass = getTargetEndowmentAccountingLineClass(transactionalDocument);
        TargetEndowmentAccountingLine targetAccountingLine = (TargetEndowmentAccountingLine) populateAccountingLine(transactionalDocument, targetAccountingLineClass, targetAccountingLineString, parseAccountingLine(targetAccountingLineClass, targetAccountingLineString), transactionalDocument.getNextTargetLineNumber());
        return targetAccountingLine;
    }

    /**
     * Given a document, determines what class that document uses for target accounting lines
     * 
     * @param accountingDocument the document to determine the target accounting line class for
     * @return the class of the target lines for the given document
     */
    protected Class getTargetEndowmentAccountingLineClass(final EndowmentAccountingLinesDocument accountingDocument) {
        return accountingDocument.getTargetAccountingLineClass();
    }

    /**
     * Populates a source/target line with values
     * 
     * @param transactionalDocument
     * @param accountingLineClass
     * @param accountingLineAsString
     * @param attributeValueMap
     * @param sequenceNumber
     * @return AccountingLine
     */
    protected EndowmentAccountingLine populateAccountingLine(EndowmentAccountingLinesDocument transactionalDocument, Class<? extends EndowmentAccountingLine> accountingLineClass, String accountingLineAsString, Map<String, String> attributeValueMap, Integer sequenceNumber) {

        putCommonAttributesInMap(attributeValueMap, transactionalDocument, sequenceNumber);

        // create line and populate fields
        EndowmentAccountingLine accountingLine;

        try {
            accountingLine = (EndowmentAccountingLine) accountingLineClass.newInstance();

            // perform custom line population
            if (SourceEndowmentAccountingLine.class.isAssignableFrom(accountingLineClass)) {
                performCustomSourceAccountingLinePopulation(attributeValueMap, (SourceEndowmentAccountingLine) accountingLine, accountingLineAsString);
            }
            else if (TargetEndowmentAccountingLine.class.isAssignableFrom(accountingLineClass)) {
                performCustomTargetAccountingLinePopulation(attributeValueMap, (TargetEndowmentAccountingLine) accountingLine, accountingLineAsString);
            }
            else {
                throw new IllegalArgumentException("invalid (unknown) endowment accounting line type: " + accountingLineClass);
            }

            for (Entry<String, String> entry : attributeValueMap.entrySet()) {
                try {
                    try {
                        Class entryType = ObjectUtils.easyGetPropertyType(accountingLine, entry.getKey());
                        if (String.class.isAssignableFrom(entryType)) {
                            entry.setValue(entry.getValue().toUpperCase());
                        }
                        ObjectUtils.setObjectProperty(accountingLine, entry.getKey(), entryType, entry.getValue());
                    }
                    catch (IllegalArgumentException e) {
                        throw new InfrastructureException("unable to complete endowment accounting line population.", e);
                    }
                }
                catch (FormatException e) {
                    String[] errorParameters = { entry.getValue().toString(), retrieveAttributeLabel(accountingLine.getClass(), entry.getKey()), accountingLineAsString };
                    GlobalVariables.getMessageMap().putError(EndowConstants.ACCOUNTING_LINE_ERRORS, ERROR_INVALID_PROPERTY_VALUE, entry.getValue().toString(), entry.getKey(), accountingLineAsString + "  : Line Number " + lineNo.toString());
                    throw new AccountingLineParserException("invalid '" + entry.getKey() + "=" + entry.getValue() + " for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
                }
            }

            // override chart code if accounts can't cross charts
            // TODO: check if this is needed
            // SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(accountingLine);
        }
        catch (SecurityException e) {
            throw new InfrastructureException("unable to complete endowment accounting line population.", e);
        }
        catch (NoSuchMethodException e) {
            throw new InfrastructureException("unable to complete endowment accounting line population.", e);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to complete endowment accounting line population.", e);
        }
        catch (InvocationTargetException e) {
            throw new InfrastructureException("unable to complete endowment accounting line population.", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to complete endowment accounting line population.", e);
        }

        // force input to uppercase
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(accountingLine);
        accountingLine.refresh();

        return accountingLine;
    }

    /**
     * Places fields common to both source/target endowment accounting lines in the attribute map
     * 
     * @param attributeValueMap
     * @param document
     * @param sequenceNumber
     */
    protected void putCommonAttributesInMap(Map<String, String> attributeValueMap, EndowmentAccountingLinesDocument document, Integer sequenceNumber) {
        attributeValueMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributeValueMap.put(ENDOWMENT_ACCOUNTING_LINE_NBR, sequenceNumber.toString());
    }

    /**
     * Parses the csv line
     * 
     * @param accountingLineClass
     * @param lineToParse
     * @return Map containing accounting line attribute,value pairs
     */
    protected Map<String, String> parseAccountingLine(Class<? extends EndowmentAccountingLine> accountingLineClass, String lineToParse) {
        if (StringUtils.isNotBlank(fileName) && !StringUtils.lowerCase(fileName).endsWith(".csv")) {
            throw new AccountingLineParserException("unsupported file format: " + fileName, ERROR_INVALID_FILE_FORMAT, fileName);
        }
        String[] attributes = chooseFormat(accountingLineClass);
        String[] attributeValues = StringUtils.splitPreserveAllTokens(lineToParse, ",");

        Map<String, String> attributeValueMap = new HashMap<String, String>();

        for (int i = 0; i < Math.min(attributeValues.length, attributes.length); i++) {
            attributeValueMap.put(attributes[i], attributeValues[i]);
        }

        return attributeValueMap;
    }

    /**
     * Should be overriden by documents to perform any additional <code>SourceAccountingLine</code> population
     * 
     * @param attributeValueMap
     * @param sourceAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceEndowmentAccountingLine sourceAccountingLine, String accountingLineAsString) {
    }

    /**
     * Should be overridden by documents to perform any additional <code>TargetAccountingLine</code> attribute population
     * 
     * @param attributeValueMap
     * @param targetAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomTargetAccountingLinePopulation(Map<String, String> attributeValueMap, TargetEndowmentAccountingLine targetAccountingLine, String accountingLineAsString) {
    }

    /**
     * Calls the appropriate parseAccountingLine method
     * 
     * @param stream
     * @param transactionalDocument
     * @param isSource
     * @return List
     */
    protected List<EndowmentAccountingLine> importAccountingLines(String fileName, InputStream stream, EndowmentAccountingLinesDocument transactionalDocument, boolean isSource) {
        List<EndowmentAccountingLine> importedAccountingLines = new ArrayList<EndowmentAccountingLine>();
        this.fileName = fileName;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {
            String accountingLineAsString = null;
            lineNo = 0;
            while ((accountingLineAsString = br.readLine()) != null) {
                lineNo++;
                EndowmentAccountingLine accountingLine = null;

                try {
                    if (isSource) {
                        accountingLine = parseSourceEndowmentAccountingLine(transactionalDocument, accountingLineAsString);
                    }
                    else {
                        accountingLine = parseTargetEndowmentAccountingLine(transactionalDocument, accountingLineAsString);
                    }

                    importedAccountingLines.add(accountingLine);
                }
                catch (AccountingLineParserException e) {
                    GlobalVariables.getMessageMap().putError((isSource ? EndowPropertyConstants.EXISTING_SOURCE_ACCT_LINE_PREFIX : EndowPropertyConstants.EXISTING_TARGET_ACCT_LINE_PREFIX), EndowKeyConstants.ERROR_ENDOW_ACCOUNTING_LINES_DOCUMENT_ACCOUNTING_LINE_IMPORT_GENERAL, new String[] { e.getMessage() });
                }
            }
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to readLine from bufferReader in endowmentAccountingLineParserBase", e);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                throw new InfrastructureException("unable to close bufferReader in endowmentAccountingLineParserBase", e);
            }
        }

        return importedAccountingLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#importSourceEndowmentAccountingLines(java.lang.String,
     *      java.io.InputStream, org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument)
     */
    public final List importSourceEndowmentAccountingLines(String fileName, InputStream stream, EndowmentAccountingLinesDocument document) {
        return importAccountingLines(fileName, stream, document, true);
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser#importTargetEndowmentAccountingLines(java.lang.String,
     *      java.io.InputStream, org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument)
     */
    public final List importTargetEndowmentAccountingLines(String fileName, InputStream stream, EndowmentAccountingLinesDocument document) {
        return importAccountingLines(fileName, stream, document, false);
    }


    /**
     * Retrieves label for given attribute.
     * 
     * @param clazz
     * @param attributeName
     * @return
     */
    protected String retrieveAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * Gets the accounting line format.
     * 
     * @param accountingLineClass
     * @return
     */
    protected String[] chooseFormat(Class<? extends EndowmentAccountingLine> accountingLineClass) {
        String[] format = null;
        if (SourceEndowmentAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getSourceEndowmentAccountingLineFormat();
        }
        else if (TargetEndowmentAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getTargetEndowmentAccountingLineFormat();
        }
        else {
            throw new IllegalStateException("unknow endowment accounting line class: " + accountingLineClass);
        }
        return format;
    }

}
