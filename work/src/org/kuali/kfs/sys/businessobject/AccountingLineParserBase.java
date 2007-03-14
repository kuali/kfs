/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.kfs.bo;

import static org.kuali.KeyConstants.AccountingLineParser.*;
import static org.kuali.KeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.AMOUNT;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.PropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.PropertyConstants.OVERRIDE_CODE;
import static org.kuali.PropertyConstants.POSTING_YEAR;
import static org.kuali.PropertyConstants.PROJECT_CODE;
import static org.kuali.PropertyConstants.SEQUENCE_NUMBER;
import static org.kuali.PropertyConstants.SUB_ACCOUNT_NUMBER;

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
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.FormatException;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.exceptions.AccountingLineParserException;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.PropertyConstants;

/**
 * Base class for parsing serialized <code>AccountingLine</code>s for <code>TransactionalDocument</code>s
 */
public class AccountingLineParserBase implements AccountingLineParser {
    protected static final String[] DEFAULT_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, AMOUNT };
    private String fileName;
    private int lineNo = 0;

    /**
     * @see org.kuali.core.bo.AccountingLineParser#getSourceAccountingLineFormat()
     */
    public String[] getSourceAccountingLineFormat() {
        return DEFAULT_FORMAT;
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    public String[] getTargetAccountingLineFormat() {
        return DEFAULT_FORMAT;
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParser#getExpectedAccountingLineFormatAsString(java.lang.Class)
     */
    public final String getExpectedAccountingLineFormatAsString(Class<? extends AccountingLine> accountingLineClass) {
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
     * @see org.kuali.core.bo.AccountingLineParser#parseSourceAccountingLine(org.kuali.core.document.TransactionalDocument,
     *      java.lang.String)
     */
    public final SourceAccountingLine parseSourceAccountingLine(AccountingDocument transactionalDocument, String sourceAccountingLineString) {
        Class sourceAccountingLineClass = transactionalDocument.getSourceAccountingLineClass();
        SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) populateAccountingLine(transactionalDocument, sourceAccountingLineClass, sourceAccountingLineString, parseAccountingLine(sourceAccountingLineClass, sourceAccountingLineString), transactionalDocument.getNextSourceLineNumber());
        return sourceAccountingLine;
    }

    /**
     * @see org.kuali.core.bo.AccountingLineParser#parseTargetAccountingLine(org.kuali.core.document.TransactionalDocument,
     *      java.lang.String)
     */
    public final TargetAccountingLine parseTargetAccountingLine(AccountingDocument transactionalDocument, String targetAccountingLineString) {
        Class targetAccountingLineClass = transactionalDocument.getTargetAccountingLineClass();
        TargetAccountingLine targetAccountingLine = (TargetAccountingLine) populateAccountingLine(transactionalDocument, targetAccountingLineClass, targetAccountingLineString, parseAccountingLine(targetAccountingLineClass, targetAccountingLineString), transactionalDocument.getNextTargetLineNumber());
        return targetAccountingLine;
    }

    /**
     * populates a source/target line with values
     * 
     * @param transactionalDocument
     * @param accountingLineClass
     * @param accountingLineAsString
     * @param attributeValueMap
     * @param sequenceNumber
     * @return AccountingLine
     */
    private final AccountingLine populateAccountingLine(AccountingDocument transactionalDocument, Class<? extends AccountingLine> accountingLineClass, String accountingLineAsString, Map<String, String> attributeValueMap, Integer sequenceNumber) {

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
                    throw new AccountingLineParserException("invalid '" + entry.getKey() + "=" + entry.getValue() + "for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
                }
            }
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
        SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(accountingLine);
        accountingLine.refresh();

        return accountingLine;

    }

    /**
     * places fields common to both source/target accounting lines in the attribute map
     * 
     * @param attributeValueMap
     * @param document
     * @param sequenceNumber
     */
    private final void putCommonAttributesInMap(Map<String, String> attributeValueMap, AccountingDocument document, Integer sequenceNumber) {
        attributeValueMap.put(PropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributeValueMap.put(POSTING_YEAR, document.getPostingYear().toString());
        attributeValueMap.put(SEQUENCE_NUMBER, sequenceNumber.toString());
    }

    /**
     * parses the csv line
     * 
     * @param accountingLineClass
     * @param lineToParse
     * @return Map containing accounting line attribute,value pairs
     */
    private final Map<String, String> parseAccountingLine(Class<? extends AccountingLine> accountingLineClass, String lineToParse) {
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
     * should be voerriden by documents to perform any additional <code>SourceAccountingLine</code> population
     * 
     * @param attributeValueMap
     * @param sourceAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {
    }

    /**
     * should be overridden by documents to perform any additional <code>TargetAccountingLine</code> attribute population
     * 
     * @param attributeValueMap
     * @param targetAccountingLine
     * @param accountingLineAsString
     */
    protected void performCustomTargetAccountingLinePopulation(Map<String, String> attributeValueMap, TargetAccountingLine targetAccountingLine, String accountingLineAsString) {
    }

    /**
     * calls the appropriate parseAccountingLine method
     * 
     * @param stream
     * @param transactionalDocument
     * @param isSource
     * @return List
     */
    private final List<AccountingLine> importAccountingLines(String fileName, InputStream stream, AccountingDocument transactionalDocument, boolean isSource) {


        List<AccountingLine> importedAccountingLines = new ArrayList<AccountingLine>();
        this.fileName = fileName;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {
            String accountingLineAsString = null;
            lineNo = 0;
            while ((accountingLineAsString = br.readLine()) != null) {
                lineNo++;
                AccountingLine accountingLine = null;
                if (isSource) {
                    accountingLine = parseSourceAccountingLine(transactionalDocument, accountingLineAsString);
                }
                else {
                    accountingLine = parseTargetAccountingLine(transactionalDocument, accountingLineAsString);
                }

                validateImportedAccountingLine(accountingLine, accountingLineAsString);
                importedAccountingLines.add(accountingLine);
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
     * 
     * @see org.kuali.core.bo.AccountingLineParser#importSourceAccountingLines(java.io.InputStream,
     *      org.kuali.core.document.TransactionalDocument)
     */
    public final List importSourceAccountingLines(String fileName, InputStream stream, AccountingDocument document) {
        return importAccountingLines(fileName, stream, document, true);
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParser#importTargetAccountingLines(java.io.InputStream,
     *      org.kuali.core.document.TransactionalDocument)
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
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    private String[] chooseFormat(Class<? extends AccountingLine> accountingLineClass) {
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
