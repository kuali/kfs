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
import org.kuali.rice.core.api.uif.RemotableAttributeField;
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
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

//RICE20 This class needs to be fixed to support pre-rice2.0 features
public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    // used to map the special fields to the DD Entry that validate it.
    private static Map<String, String> magicFields = new HashMap<String, String>();

    static {
        magicFields.put("chartOfAccountsCode", "SourceAccountingLine");
        magicFields.put("organizationCode", "Organization");
        magicFields.put("accountNumber", "SourceAccountingLine");
        magicFields.put("financialDocumentTypeCode", "GeneralLedgerPendingEntry");
        magicFields.put("financialDocumentTotalAmount", "FinancialSystemDocumentHeader");
    }

    @Override
    protected List<Row> getSearchingRows(String documentTypeName) {
        DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

        List<Row> docSearchRows = super.getSearchingRows(documentTypeName);

        DocumentEntry entry = ddService.getDataDictionary().getDocumentEntry(documentTypeName);

        if (entry == null) {
            return docSearchRows;
        }
        Class<? extends Document> docClass = entry.getDocumentClass();

        List<String> displayedFieldNames = new ArrayList<String>();

        if (AccountingDocument.class.isAssignableFrom(docClass)) {
            Map<String, AccountingLineGroupDefinition> alGroups = ((FinancialSystemTransactionalDocumentEntry) entry).getAccountingLineGroups();
            Class alClass = SourceAccountingLine.class;

            if (ObjectUtils.isNotNull(alGroups)) {
                if (alGroups.containsKey("source")) {
                    alClass = alGroups.get("source").getAccountingLineClass();
                }
            }

            BusinessObject alBusinessObject = null;

            Class orgClass = Organization.class;
            BusinessObject orgBusinessObject = null;

            try {
                alBusinessObject = (BusinessObject) alClass.newInstance();
                orgBusinessObject = (BusinessObject) orgClass.newInstance();

            }
            catch (Exception cnfe) {
                throw new RuntimeException(cnfe);
            }

            Field chartField = FieldUtils.getPropertyField(alClass, "chartOfAccountsCode", true);
            chartField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.add("chartOfAccountsCode");
            LookupUtils.setFieldQuickfinder(alBusinessObject, "chartOfAccountsCode", chartField, displayedFieldNames);

            Field orgField = FieldUtils.getPropertyField(orgClass, "organizationCode", true);
            orgField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.clear();
            displayedFieldNames.add("organizationCode");
            LookupUtils.setFieldQuickfinder(new Account(), "organizationCode", orgField, displayedFieldNames);

            Field accountField = FieldUtils.getPropertyField(alClass, "accountNumber", true);
            accountField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.clear();
            displayedFieldNames.add("accountNumber");
            LookupUtils.setFieldQuickfinder(alBusinessObject, "accountNumber", accountField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(chartField);
            docSearchRows.add(new Row(fieldList));

            fieldList = new ArrayList<Field>();
            fieldList.add(accountField);
            docSearchRows.add(new Row(fieldList));

            fieldList = new ArrayList<Field>();
            fieldList.add(orgField);
            docSearchRows.add(new Row(fieldList));
        }

        boolean displayedLedgerPostingDoc = false;
        if (LaborLedgerPostingDocumentForSearching.class.isAssignableFrom(docClass)) {
            Class boClass = GeneralLedgerPendingEntry.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTypeCode", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);

            displayedFieldNames.clear();
            displayedFieldNames.add("financialDocumentTypeCode");
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), "financialDocumentTypeCode", searchField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));
            displayedLedgerPostingDoc = true;
        }

        if (GeneralLedgerPostingDocument.class.isAssignableFrom(docClass) && !displayedLedgerPostingDoc) {
            Class boClass = GeneralLedgerPendingEntry.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTypeCode", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);

            displayedFieldNames.clear();
            displayedFieldNames.add("financialDocumentTypeCode");
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), "financialDocumentTypeCode", searchField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));

        }

        if (AmountTotaling.class.isAssignableFrom(docClass)) {
            Class boClass = FinancialSystemDocumentHeader.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTotalAmount", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_FLOAT);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));

        }


        Row resultType = createSearchResultReturnRow();
        docSearchRows.add(resultType);
        return docSearchRows;
    }

    @Override
    public List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition, DocumentWithContent documentWithContent) {
      List<DocumentAttribute> searchAttrValues =  super.extractDocumentAttributes(extensionDefinition, documentWithContent);

      String docId = documentWithContent.getDocument().getDocumentId();
      DocumentService docService = SpringContext.getBean(DocumentService.class);
      Document doc = null;
      try  {
          doc = docService.getByDocumentHeaderIdSessionless(docId);
      } catch (WorkflowException we) {

      }
      if ( doc != null ) {
      if (doc instanceof AmountTotaling) {
          DocumentAttributeDecimal.Builder searchableAttributeValue = DocumentAttributeDecimal.Builder.create("financialDocumentTotalAmount");
          searchableAttributeValue.setValue(((AmountTotaling)doc).getTotalDollarAmount().bigDecimalValue());
          searchAttrValues.add(searchableAttributeValue.build());
      }

      if (doc instanceof AccountingDocument) {
          AccountingDocument accountingDoc = (AccountingDocument)doc;
          searchAttrValues.addAll(harvestAccountingDocumentSearchableAttributes(accountingDoc));
      }

      boolean indexedLedgerDoc = false;
      if (doc instanceof LaborLedgerPostingDocumentForSearching) {
          LaborLedgerPostingDocumentForSearching LLPostingDoc = (LaborLedgerPostingDocumentForSearching)doc;
          searchAttrValues.addAll(harvestLLPDocumentSearchableAttributes(LLPostingDoc));
          indexedLedgerDoc = true;
      }

      if (doc instanceof GeneralLedgerPostingDocument && !indexedLedgerDoc) {
          GeneralLedgerPostingDocument GLPostingDoc = (GeneralLedgerPostingDocument)doc;
          searchAttrValues.addAll(harvestGLPDocumentSearchableAttributes(GLPostingDoc));
      }

      }
      return searchAttrValues;
    }

    @Override
    public List<RemotableAttributeError> validateDocumentAttributeCriteria(ExtensionDefinition extensionDefinition, DocumentSearchCriteria documentSearchCriteria) {
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
        DocumentAttributeString.Builder searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        searchableAttributeValue.setValue(accountingLine.getChartOfAccountsCode());
        searchAttrValues.add(searchableAttributeValue.build());

        searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.ACCOUNT_NUMBER);
        searchableAttributeValue.setValue(accountingLine.getAccountNumber());
        searchAttrValues.add(searchableAttributeValue.build());

        searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.ORGANIZATION_CODE);
        searchableAttributeValue.setValue(accountingLine.getAccount().getOrganizationCode());
        searchAttrValues.add(searchableAttributeValue.build());
    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode - from a given accounting line and populates
     * the searchable attribute values in the given list
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param glpe a GeneralLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForGLPE(List<DocumentAttribute> searchAttrValues, GeneralLedgerPendingEntry glpe) {
        DocumentAttributeString.Builder searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        searchableAttributeValue.setValue(glpe.getFinancialDocumentTypeCode());
        searchAttrValues.add(searchableAttributeValue.build());

    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode from a given accounting line and populates
     * the searchable attribute values in the given list
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param llpe a LaborLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForLLPE(List<DocumentAttribute> searchAttrValues, LaborLedgerPendingEntryForSearching llpe) {
        DocumentAttributeString.Builder searchableAttributeValue = DocumentAttributeString.Builder.create(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        searchableAttributeValue.setValue(llpe.getFinancialDocumentTypeCode());
        searchAttrValues.add(searchableAttributeValue.build());
    }

    private Row createSearchResultReturnRow() {
        String attributeName = "displayType";
        Field searchField = new Field();
        searchField.setPropertyName(attributeName);
        searchField.setFieldType(Field.RADIO);
        searchField.setFieldLabel("Search Result Type");
        searchField.setIndexedForSearch(false);
        searchField.setBusinessObjectClassName("");
        searchField.setFieldHelpName("");
        searchField.setFieldHelpSummary("");
        searchField.setColumnVisible(false);
        List<KeyValue> values = new ArrayList<KeyValue>();
        values.add(new ConcreteKeyValue("document", "Document Specific Data"));
        values.add(new ConcreteKeyValue("workflow", "Workflow Data"));
        searchField.setFieldValidValues(values);
        searchField.setPropertyValue("document");

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.add(searchField);

        return new Row(fieldList);

    }

    // RICE20: fixes to allow document search to function until Rice 2.0.1
    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
        List<Row> searchRows = getSearchingRows(documentTypeName);
        for ( Row row : searchRows ) {
            for ( Field field : row.getFields() ) {
                if ( field.getFieldType().equals(Field.CURRENCY) ) {
                    field.setFieldType(Field.TEXT);
                }
                if ( field.getMaxLength() < 1 ) {
                    field.setMaxLength(100);
                }
            }
        }
        return FieldUtils.convertRowsToAttributeFields(searchRows);
    }
}
