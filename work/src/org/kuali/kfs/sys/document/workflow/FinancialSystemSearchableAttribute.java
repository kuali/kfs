/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.ld.LaborLedgerPendingEntryForSearching;
import org.kuali.kfs.integration.ld.LaborLedgerPostingDocumentForSearching;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants.SearchableAttributeConstants;
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDecimal;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

//RICE20 This class needs to be fixed to support pre-rice2.0 features
public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemSearchableAttribute.class);

    protected static final String DISPLAY_TYPE_SEARCH_ATTRIBUTE_LABEL = "Search Result Type";
    protected static final String WORKFLOW_DISPLAY_TYPE_LABEL = "Workflow Data";
    protected static final String DOCUMENT_DISPLAY_TYPE_LABEL = "Document Specific Data";
    protected static final String WORKFLOW_DISPLAY_TYPE_VALUE = "workflow";
    protected static final String DOCUMENT_DISPLAY_TYPE_VALUE = "document";
    protected static final String DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME = "displayType";

    protected static final List<KeyValue> SEARCH_RESULT_TYPE_OPTION_LIST = new ArrayList<KeyValue>(2);
    static {
        SEARCH_RESULT_TYPE_OPTION_LIST.add(new ConcreteKeyValue(DOCUMENT_DISPLAY_TYPE_VALUE, DOCUMENT_DISPLAY_TYPE_LABEL));
        SEARCH_RESULT_TYPE_OPTION_LIST.add(new ConcreteKeyValue(WORKFLOW_DISPLAY_TYPE_VALUE, WORKFLOW_DISPLAY_TYPE_LABEL));
    }

    // used to map the special fields to the DD Entry that validate it.
    private static final Map<String, String> magicFields = new HashMap<String, String>();

    static {
        magicFields.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, SourceAccountingLine.class.getSimpleName());
        magicFields.put(KFSPropertyConstants.ORGANIZATION_CODE, Organization.class.getSimpleName());
        magicFields.put(KFSPropertyConstants.ACCOUNT_NUMBER, SourceAccountingLine.class.getSimpleName());
        magicFields.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, GeneralLedgerPendingEntry.class.getSimpleName());
        magicFields.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, FinancialSystemDocumentHeader.class.getSimpleName() );
    }

    @Override
    protected List<Row> getSearchingRows(String documentTypeName) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "getSearchingRows( " + documentTypeName + " )" );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("Stack Trace at point of call", new Throwable());
            }
        }

        DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

        List<Row> docSearchRows = super.getSearchingRows(documentTypeName);

        DocumentEntry entry = ddService.getDataDictionary().getDocumentEntry(documentTypeName);

        if (entry == null) {
            return docSearchRows;
        }
        Class<? extends Document> docClass = entry.getDocumentClass();

        if (AccountingDocument.class.isAssignableFrom(docClass)) {
            Map<String, AccountingLineGroupDefinition> alGroups = ((FinancialSystemTransactionalDocumentEntry) entry).getAccountingLineGroups();
            Class alClass = SourceAccountingLine.class;

            if (ObjectUtils.isNotNull(alGroups)) {
                if (alGroups.containsKey("source")) {
                    alClass = alGroups.get("source").getAccountingLineClass();
                }
            }

            BusinessObject alBusinessObject = null;

            try {
                alBusinessObject = (BusinessObject) alClass.newInstance();
            } catch (Exception cnfe) {
                throw new RuntimeException( "Unable to instantiate accounting line class: " + alClass, cnfe);
            }

            Field chartField = FieldUtils.getPropertyField(alClass, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, true);
            chartField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            chartField.setColumnVisible(false);
            LookupUtils.setFieldQuickfinder(alBusinessObject, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartField, Collections.singletonList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
            docSearchRows.add(new Row(Collections.singletonList(chartField)));

            Field orgField = FieldUtils.getPropertyField(Organization.class, KFSPropertyConstants.ORGANIZATION_CODE, true);
            orgField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            orgField.setColumnVisible(false);
            LookupUtils.setFieldQuickfinder(new Account(), KFSPropertyConstants.ORGANIZATION_CODE, orgField, Collections.singletonList(KFSPropertyConstants.ORGANIZATION_CODE));
            docSearchRows.add(new Row(Collections.singletonList(orgField)));

            Field accountField = FieldUtils.getPropertyField(alClass, KFSPropertyConstants.ACCOUNT_NUMBER, true);
            accountField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            accountField.setColumnVisible(false);
            LookupUtils.setFieldQuickfinder(alBusinessObject, KFSPropertyConstants.ACCOUNT_NUMBER, accountField, Collections.singletonList(KFSPropertyConstants.ACCOUNT_NUMBER));
            docSearchRows.add(new Row(Collections.singletonList(accountField)));
        }

        boolean displayedLedgerPostingDoc = false;
        if (LaborLedgerPostingDocumentForSearching.class.isAssignableFrom(docClass)) {
            Field searchField = FieldUtils.getPropertyField(GeneralLedgerPendingEntry.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, searchField, Collections.singletonList(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE));
            docSearchRows.add(new Row(Collections.singletonList(searchField)));
            displayedLedgerPostingDoc = true;
        }

        if (GeneralLedgerPostingDocument.class.isAssignableFrom(docClass) && !displayedLedgerPostingDoc) {
            Field searchField = FieldUtils.getPropertyField(GeneralLedgerPendingEntry.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, searchField, Collections.singletonList(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE));
            docSearchRows.add(new Row(Collections.singletonList(searchField)));
        }

        if (AmountTotaling.class.isAssignableFrom(docClass)) {
            Field searchField = FieldUtils.getPropertyField(FinancialSystemDocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_FLOAT);
            docSearchRows.add(new Row(Collections.singletonList(searchField)));
        }

        Row resultType = createSearchResultDisplayTypeRow();
        docSearchRows.add(resultType);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Returning Rows: " + docSearchRows );
        }
        return docSearchRows;
    }

    @Override
    public List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition, DocumentWithContent documentWithContent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractDocumentAttributes( " + extensionDefinition + ", " + documentWithContent + " )");
        }
        List<DocumentAttribute> searchAttrValues = super.extractDocumentAttributes(extensionDefinition, documentWithContent);

        String docId = documentWithContent.getDocument().getDocumentId();
        DocumentService docService = SpringContext.getBean(DocumentService.class);
        Document doc = null;
        try {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        }
        catch (WorkflowException we) {

        }
        if (doc != null) {
            if (doc instanceof AmountTotaling && ((AmountTotaling)doc).getTotalDollarAmount() != null) {
                DocumentAttributeDecimal.Builder searchableAttributeValue = DocumentAttributeDecimal.Builder.create(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT);
                searchableAttributeValue.setValue(((AmountTotaling) doc).getTotalDollarAmount().bigDecimalValue());
                searchAttrValues.add(searchableAttributeValue.build());
            }

            if (doc instanceof AccountingDocument) {
                AccountingDocument accountingDoc = (AccountingDocument) doc;
                searchAttrValues.addAll(harvestAccountingDocumentSearchableAttributes(accountingDoc));
            }

            boolean indexedLedgerDoc = false;
            if (doc instanceof LaborLedgerPostingDocumentForSearching) {
                LaborLedgerPostingDocumentForSearching LLPostingDoc = (LaborLedgerPostingDocumentForSearching) doc;
                searchAttrValues.addAll(harvestLLPDocumentSearchableAttributes(LLPostingDoc));
                indexedLedgerDoc = true;
            }

            if (doc instanceof GeneralLedgerPostingDocument && !indexedLedgerDoc) {
                GeneralLedgerPostingDocument GLPostingDoc = (GeneralLedgerPostingDocument) doc;
                searchAttrValues.addAll(harvestGLPDocumentSearchableAttributes(GLPostingDoc));
            }

        }
        return searchAttrValues;
    }

    @Override
    public List<RemotableAttributeError> validateDocumentAttributeCriteria(ExtensionDefinition extensionDefinition, DocumentSearchCriteria documentSearchCriteria) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateDocumentAttributeCriteria( " + extensionDefinition + ", " + documentSearchCriteria + " )");
        }
        // this list is irrelevant. the validation errors are put on the stack in the validationService.
        List<RemotableAttributeError> errors =  super.validateDocumentAttributeCriteria(extensionDefinition, documentSearchCriteria);

        DictionaryValidationService validationService = SpringContext.getBean(DictionaryValidationService.class);
        Map<String,List<String>> paramMap = documentSearchCriteria.getDocumentAttributeValues();
        for (String key : paramMap.keySet()) {
            List<String> values = paramMap.get(key);
            if ( values != null && !values.isEmpty() ) {
                for ( String value : values ) {
                    if (!StringUtils.isEmpty(value)) {
                        if (magicFields.containsKey(key)) {
                            validationService.validateAttributeFormat(magicFields.get(key), key, value, key);
                        }
                    }
                }
            }
        }

        retrieveValidationErrorsFromGlobalVariables(errors);

        return errors;
    };

    /**
     * Harvest chart of accounts code, account number, and organization code as searchable attributes from an accounting document
     * @param accountingDoc the accounting document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestAccountingDocumentSearchableAttributes(AccountingDocument accountingDoc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for ( AccountingLine line : (List<AccountingLine>)accountingDoc.getSourceAccountingLines() ) {
            addSearchableAttributesForAccountingLine(searchAttrValues, line);
        }
        for ( AccountingLine line : (List<AccountingLine>)accountingDoc.getTargetAccountingLines() ) {
            addSearchableAttributesForAccountingLine(searchAttrValues, line);
        }

        return searchAttrValues;
    }

    /**
     * Harvest GLPE document type as searchable attributes from a GL posting document
     * @param GLPDoc the GLP document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestGLPDocumentSearchableAttributes(GeneralLedgerPostingDocument doc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for ( GeneralLedgerPendingEntry glpe : doc.getGeneralLedgerPendingEntries() ) {
            addSearchableAttributesForGLPE(searchAttrValues, glpe);
        }
        return searchAttrValues;
    }

    /**
     * Harvest LLPE document type as searchable attributes from a LL posting document
     * @param LLPDoc the LLP document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestLLPDocumentSearchableAttributes(LaborLedgerPostingDocumentForSearching LLPDoc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for (Iterator itr = LLPDoc.getLaborLedgerPendingEntriesForSearching().iterator(); itr.hasNext();) {
            LaborLedgerPendingEntryForSearching llpe = (LaborLedgerPendingEntryForSearching)itr.next();
            addSearchableAttributesForLLPE(searchAttrValues, llpe);
        }
        return searchAttrValues;
    }


    /**
     * Pulls the default searchable attributes - chart code, account number, and account organization code - from a given accounting line and populates
     * the searchable attribute values in the given list
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param accountingLine an AccountingLine to get values from
     */
    protected void addSearchableAttributesForAccountingLine(List<DocumentAttribute> searchAttrValues, AccountingLine accountingLine) {
        DocumentAttributeString.Builder searchableAttributeValue;
        if (!ObjectUtils.isNull(accountingLine)) {
            if (!StringUtils.isBlank(accountingLine.getChartOfAccountsCode())) {
                searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                searchableAttributeValue.setValue(accountingLine.getChartOfAccountsCode());
                searchAttrValues.add(searchableAttributeValue.build());
            }

            if (!StringUtils.isBlank(accountingLine.getAccountNumber())) {
                searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.ACCOUNT_NUMBER);
                searchableAttributeValue.setValue(accountingLine.getAccountNumber());
                searchAttrValues.add(searchableAttributeValue.build());
            }

            if (!ObjectUtils.isNull(accountingLine.getAccount()) && !StringUtils.isBlank(accountingLine.getAccount().getOrganizationCode())) {
                searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.ORGANIZATION_CODE);
                searchableAttributeValue.setValue(accountingLine.getAccount().getOrganizationCode());
                searchAttrValues.add(searchableAttributeValue.build());
            }
        }
    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode - from a given accounting line and populates
     * the searchable attribute values in the given list
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param glpe a GeneralLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForGLPE(List<DocumentAttribute> searchAttrValues, GeneralLedgerPendingEntry glpe) {
        if (glpe != null && !StringUtils.isBlank(glpe.getFinancialDocumentTypeCode())) {
            DocumentAttributeString.Builder searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            searchableAttributeValue.setValue(glpe.getFinancialDocumentTypeCode());
            searchAttrValues.add(searchableAttributeValue.build());
        }

    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode from a given accounting line and populates
     * the searchable attribute values in the given list
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param llpe a LaborLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForLLPE(List<DocumentAttribute> searchAttrValues, LaborLedgerPendingEntryForSearching llpe) {
        if (llpe != null && !StringUtils.isBlank(llpe.getFinancialDocumentTypeCode())) {
            DocumentAttributeString.Builder searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            searchableAttributeValue.setValue(llpe.getFinancialDocumentTypeCode());
            searchAttrValues.add(searchableAttributeValue.build());
        }
    }

    protected Row createSearchResultDisplayTypeRow() {
        Field searchField = new Field(DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME, DISPLAY_TYPE_SEARCH_ATTRIBUTE_LABEL);
        searchField.setFieldType(Field.RADIO);
        searchField.setIndexedForSearch(false);
        searchField.setBusinessObjectClassName("");
        searchField.setFieldHelpName("");
        searchField.setFieldHelpSummary("");
        searchField.setColumnVisible(false);
        searchField.setFieldValidValues(SEARCH_RESULT_TYPE_OPTION_LIST);
        searchField.setPropertyValue(DOCUMENT_DISPLAY_TYPE_VALUE);
        searchField.setDefaultValue(DOCUMENT_DISPLAY_TYPE_VALUE);

        return new Row(Collections.singletonList(searchField));
    }


    // RICE20: fixes to allow document search to function until Rice 2.0.1
//    @Override
//    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("getSearchFields( " + extensionDefinition + ", " + documentTypeName + " )");
//        }
//        List<Row> searchRows = getSearchingRows(documentTypeName);
//        for ( Row row : searchRows ) {
//            for ( Field field : row.getFields() ) {
//                if ( field.getFieldType().equals(Field.CURRENCY) ) {
//                    field.setFieldType(Field.TEXT);
//                }
//                if ( field.getMaxLength() < 1 ) {
//                    field.setMaxLength(100);
//                }
//            }
//        }
//        return FieldUtils.convertRowsToAttributeFields(searchRows);
//    }
}
