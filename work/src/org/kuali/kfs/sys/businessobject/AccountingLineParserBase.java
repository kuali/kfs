/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.sys.businessobject;

import static org.kuali.kfs.sys.KFSKeyConstants.AccountingLineParser.ERROR_INVALID_FILE_FORMAT;
import static org.kuali.kfs.sys.KFSKeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.OVERRIDE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.POSTING_YEAR;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SEQUENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

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
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Base class for parsing serialized <code>AccountingLine</code>s for <code>TransactionalDocument</code>s
 */
public class AccountingLineParserBase implements AccountingLineParser {
    protected static final String[] DEFAULT_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, AMOUNT };
    private String fileName;
    private Integer lineNo = 0;

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getSourceAccountingLineFormat()
     */
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(DEFAULT_FORMAT);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    public String[] getTargetAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(DEFAULT_FORMAT);
    }

    /**
     * If accounts can cross charts, returns the given format; 
     * otherwise returns the format with ChartOfAccountsCode field removed.
     */
    public String[] removeChartFromFormatIfNeeded(String[] format) {
        if (SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            return format;
        }
        
        // if accounts can't cross charts, exclude ChartOfAccountsCode field from the format
        String[] formatNoChart = new String[format.length-1];
        int idx = 0;
        for (int i=0; i<format.length; i++) {
            if (format[i].equals(CHART_OF_ACCOUNTS_CODE)) 
                continue;
            else {
                formatNoChart[idx] = format[i];
                idx++;
            }
        }
        return formatNoChart;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getExpectedAccountingLineFormatAsString(java.lang.Class)
     */
    public String getExpectedAccountingLineFormatAsString(Class<? extends AccountingLine> accountingLineClass) {
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
     * @see org.kuali.rice.krad.bo.AccountingLineParser#parseSourceAccountingLine(org.kuali.rice.krad.document.TransactionalDocument,
     *      java.lang.String)
     */
    public SourceAccountingLine parseSourceAccountingLine(AccountingDocument transactionalDocument, String sourceAccountingLineString) {
        Class sourceAccountingLineClass = getSourceAccountingLineClass(transactionalDocument);
        SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) populateAccountingLine(transactionalDocument, sourceAccountingLineClass, sourceAccountingLineString, parseAccountingLine(sourceAccountingLineClass, sourceAccountingLineString), transactionalDocument.getNextSourceLineNumber());
        return sourceAccountingLine;
    }
    
    /**
     * Given a document, determines what class the source lines of that document uses
     * @param accountingDocument the document to find the class of the source lines for
     * @return the class of the source lines
     */
    protected Class getSourceAccountingLineClass(final AccountingDocument accountingDocument) {
        return accountingDocument.getSourceAccountingLineClass();
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#parseTargetAccountingLine(org.kuali.rice.krad.document.TransactionalDocument,
     *      java.lang.String)
     */
    public TargetAccountingLine parseTargetAccountingLine(AccountingDocument transactionalDocument, String targetAccountingLineString) {
        Class targetAccountingLineClass = getTargetAccountingLineClass(transactionalDocument);
        TargetAccountingLine targetAccountingLine = (TargetAccountingLine) populateAccountingLine(transactionalDocument, targetAccountingLineClass, targetAccountingLineString, parseAccountingLine(targetAccountingLineClass, targetAccountingLineString), transactionalDocument.getNextTargetLineNumber());
        return targetAccountingLine;
    }
    
    /**
     * Given a document, determines what class that document uses for target accounting lines
     * @param accountingDocument the document to determine the target accounting line class for
     * @return the class of the target lines for the given document
     */
    protected Class getTargetAccountingLineClass(final AccountingDocument accountingDocument) {
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
    protected AccountingLine populateAccountingLine(AccountingDocument transactionalDocument, Class<? extends AccountingLine> accountingLineClass, String accountingLineAsString, Map<String, String> attributeValueMap, Integer sequenceNumber) {

        putCommonAttributesInMap(attributeValueMap, transactionalDocument, sequenceNumber);

        // create line and populate fields
        AccountingLine accountingLine;

        try {
            accountingLine = (AccountingLine) accountingLineClass.newInstance();
            
            // perform custom line population
            if (SourceAccountingLine.class.isAssignableFrom(accountingLineClass)) {
                performCustomSourceAccountingLinePopulation(attributeValueMap, (SourceAccountingLine) accountingLine, accountingLineAsString);
            }
            else if (TargetAccountingLine.class.isAssignableFrom(accountingLineClass)) {
                performCustomTargetAccountingLinePopulation(attributeValueMap, (TargetAccountingLine) accountingLine, accountingLineAsString);
            }
            else {
                throw new IllegalArgumentException("invalid (unknown) accounting line type: " + accountingLineClass);
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
                        throw new InfrastructureException("unable to complete accounting line population.", e);
                    }
                }
                catch (FormatException e) {
                    String[] errorParameters = { entry.getValue().toString(), retrieveAttributeLabel(accountingLine.getClass(), entry.getKey()), accountingLineAsString };
                    // KULLAB-408
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, ERROR_INVALID_PROPERTY_VALUE, entry.getValue().toString(), entry.getKey(), accountingLineAsString + "  : Line Number " + lineNo.toString());
                    throw new AccountingLineParserException("invalid '" + entry.getKey() + "=" + entry.getValue() + "for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
                }
            }
            
            // override chart code if accounts can't cross charts
            SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(accountingLine);            
        }
        catch (SecurityException e) {
            throw new InfrastructureException("unable to complete accounting line population.", e);
        }
        catch (NoSuchMethodException e) {
            throw new InfrastructureException("unable to complete accounting line population.", e);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to complete accounting line population.", e);
        }
        catch (InvocationTargetException e) {
            throw new InfrastructureException("unable to complete accounting line population.", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to complete accounting line population.", e);
        }

        // force input to uppercase
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(accountingLine);
        //accountingLine.refresh();
        accountingLine.refreshNonUpdateableReferences();

        return accountingLine;
    }

    /**
     * Places fields common to both source/target accounting lines in the attribute map
     * 
     * @param attributeValueMap
     * @param document
     * @param sequenceNumber
     */
    protected void putCommonAttributesInMap(Map<String, String> attributeValueMap, AccountingDocument document, Integer sequenceNumber) {
        attributeValueMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributeValueMap.put(POSTING_YEAR, document.getPostingYear().toString());
        attributeValueMap.put(SEQUENCE_NUMBER, sequenceNumber.toString());
    }

    /**
     * Parses the csv line
     * 
     * @param accountingLineClass
     * @param lineToParse
     * @return Map containing accounting line attribute,value pairs
     */
    protected Map<String, String> parseAccountingLine(Class<? extends AccountingLine> accountingLineClass, String lineToParse) {
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
     * Should be voerriden by documents to perform any additional <code>SourceAccountingLine</code> population
     * 
     * @param attributeValueMap
     * @param sourceAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {
    }

    /**
     * Should be overridden by documents to perform any additional <code>TargetAccountingLine</code> attribute population
     * 
     * @param attributeValueMap
     * @param targetAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomTargetAccountingLinePopulation(Map<String, String> attributeValueMap, TargetAccountingLine targetAccountingLine, String accountingLineAsString) {
    }

    /**
     * Calls the appropriate parseAccountingLine method
     * 
     * @param stream
     * @param transactionalDocument
     * @param isSource
     * @return List
     */
    protected List<AccountingLine> importAccountingLines(String fileName, InputStream stream, AccountingDocument transactionalDocument, boolean isSource) {
        List<AccountingLine> importedAccountingLines = new ArrayList<AccountingLine>();
        this.fileName = fileName;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {
            String accountingLineAsString = null;
            lineNo = 0;
            while ((accountingLineAsString = br.readLine()) != null) {
                lineNo++;
                
                if (StringUtils.isBlank(StringUtils.remove(StringUtils.deleteWhitespace(accountingLineAsString),KFSConstants.COMMA))) {
                    continue;
                }
                
                AccountingLine accountingLine = null;

                try {
                    if (isSource) {
                        accountingLine = parseSourceAccountingLine(transactionalDocument, accountingLineAsString);
                    }
                    else {
                        accountingLine = parseTargetAccountingLine(transactionalDocument, accountingLineAsString);
                    }

                    validateImportedAccountingLine(accountingLine, accountingLineAsString);
                    importedAccountingLines.add(accountingLine);
                }
                catch (AccountingLineParserException e) {
                    GlobalVariables.getMessageMap().putError((isSource ? "sourceAccountingLines" : "targetAccountingLines"), KFSKeyConstants.ERROR_ACCOUNTING_DOCUMENT_ACCOUNTING_LINE_IMPORT_GENERAL, new String[] { e.getMessage() });
                }
            }
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to readLine from bufferReader in accountingLineParserBase", e);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                throw new InfrastructureException("unable to close bufferReader in accountingLineParserBase", e);
            }
        }

        return importedAccountingLines;
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#importSourceAccountingLines(java.io.InputStream,
     *      org.kuali.rice.krad.document.TransactionalDocument)
     */
    public final List importSourceAccountingLines(String fileName, InputStream stream, AccountingDocument document) {
        return importAccountingLines(fileName, stream, document, true);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#importTargetAccountingLines(java.io.InputStream,
     *      org.kuali.rice.krad.document.TransactionalDocument)
     */
    public final List importTargetAccountingLines(String fileName, InputStream stream, AccountingDocument document) {
        return importAccountingLines(fileName, stream, document, false);
    }

    /**
     * performs any additional accounting line validation
     * 
     * @param line
     * @param accountingLineAsString
     * @throws AccountingLineParserException
     */
    protected void validateImportedAccountingLine(AccountingLine line, String accountingLineAsString) throws AccountingLineParserException {
        // This check isn't done for the web UI because the code is never input from the user and doesn't correspond to a displayed
        // property that could be error highlighted. Throwing an exception here follows the convention of TooFewFieldsException
        // and the unchecked NumberFormatException, altho todo: reconsider design, e.g., KULFDBCK-478
        String overrideCode = line.getOverrideCode();
        if (!AccountingLineOverride.isValidCode(overrideCode)) {
            String[] errorParameters = { overrideCode, retrieveAttributeLabel(line.getClass(), OVERRIDE_CODE), accountingLineAsString };
            throw new AccountingLineParserException("invalid overrride code '" + overrideCode + "' for:" + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
        }
    }

    protected String retrieveAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    protected String[] chooseFormat(Class<? extends AccountingLine> accountingLineClass) {
        String[] format = null;
        if (SourceAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getSourceAccountingLineFormat();
        }
        else if (TargetAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getTargetAccountingLineFormat();
        }
        else {
            throw new IllegalStateException("unknow accounting line class: " + accountingLineClass);
        }
        return format;
    }
}
