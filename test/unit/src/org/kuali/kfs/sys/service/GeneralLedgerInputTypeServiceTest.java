/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.UnknownGeneralLedgerInputTypeException;
import org.kuali.rice.kns.document.DocumentBase;
import org.kuali.rice.kns.document.authorization.PessimisticLock;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class tests the {@link GeneralLedgerInputTypeService}
 */
@ConfigureContext
public class GeneralLedgerInputTypeServiceTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(GeneralLedgerInputTypeServiceTest.class);

//    private static final String UNIT_OF_MEASURE_INPUT_TYPE_CODE_ACCT = "UOME";
    private static final String UNIT_OF_MEASURE_INPUT_TYPE_CODE_ACCT = "PMUM";
    private static final String UNIT_OF_MEASURE_DOCUMENT_TYPE_NAME = "UnitOfMeasureMaintenanceDocument";
    private static final Class NON_DOCUMENT_ASSIGNABLE_CLASS = PessimisticLock.class;
    
    private static final class VALID_DOCUMENT {
        private static final Class DOCUMENT_CLASS = CashReceiptDocument.class;
        private static final String GENERAL_LEDGER_INPUT_TYPE_CODE_ACCT = "CR";
        private static final String DOCUMENT_TYPE_NAME = "CashReceiptDocument";
    }

    private GeneralLedgerInputTypeService generalLedgerInputTypeService;

    /**
     * Initializes the services needed for this test
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (generalLedgerInputTypeService == null) {
            generalLedgerInputTypeService = SpringContext.getBean(GeneralLedgerInputTypeService.class);
        }
//        Set<String> alreadySaved = new HashSet<String>();
//        Map<String,DocumentEntry> entryMap = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries();
//        for (String key : entryMap.keySet()) {
//            DocumentEntry docEntry = entryMap.get(key);
//            String docTypeCode = docEntry.getDocumentTypeCode();
//            Map<String,String> primaryKeys = new HashMap<String,String>();
//            primaryKeys.put("documentTypeCode", docTypeCode);
//            DocumentType type = (DocumentType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, primaryKeys);
//            if (ObjectUtils.isNotNull(type)) {
//                GeneralLedgerInputType inputType = new GeneralLedgerInputType();
//                inputType.setInputTypeCode(type.getDocumentTypeCode());
//                inputType.setDocumentTypeActiveIndicator(type.isDocumentTypeActiveIndicator());
//                inputType.setDocumentTypeName(docEntry.getDocumentTypeName());
//                boolean scrubberOffset = false;
//                for (org.kuali.rice.kns.bo.DocumentTypeAttribute docTypeAttr : type.getDocumentTypeAttributes()) {
//                    if (KFSConstants.DocumentTypeAttributes.TRANSACTION_SCRUBBER_OFFSET_INDICATOR_ATTRIBUTE_KEY.equals(docTypeAttr.getKey())) {
//                        scrubberOffset = KFSConstants.DocumentTypeAttributes.INDICATOR_ATTRIBUTE_TRUE_VALUE.equals(docTypeAttr.getValue());
//                    }
//                }
//                inputType.setTransactionScrubberOffsetGenerationIndicator(scrubberOffset);
//                if (!alreadySaved.contains(type.getDocumentTypeCode())) {
//                    LOG.warn("Saving GL Input Type for key " + key + " and value " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
//                    SpringContext.getBean(BusinessObjectService.class).save(inputType);
//                    alreadySaved.add(type.getDocumentTypeCode());
//                }
//            } else {
//                LOG.warn("Could not find valid document type table record for " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
//            }
//        }
//        Map<String,BusinessObjectEntry> boEntryMap = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntries();
//        for (String key : boEntryMap.keySet()) {
//            BusinessObjectEntry boEntry = boEntryMap.get(key);
//            MaintenanceDocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(boEntry.getBusinessObjectClass());
//            if (ObjectUtils.isNotNull(docEntry)) {
//                String docTypeCode = docEntry.getDocumentTypeCode();
//                Map<String,String> primaryKeys = new HashMap<String,String>();
//                primaryKeys.put("documentTypeCode", docTypeCode);
//                DocumentType type = (DocumentType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, primaryKeys);
//                if (ObjectUtils.isNotNull(type)) {
//                    GeneralLedgerInputType inputType = new GeneralLedgerInputType();
//                    inputType.setInputTypeCode(type.getDocumentTypeCode());
//                    inputType.setDocumentTypeActiveIndicator(type.isDocumentTypeActiveIndicator());
//                    inputType.setDocumentTypeName(docEntry.getDocumentTypeName());
//                    boolean scrubberOffset = false;
//                    for (org.kuali.rice.kns.bo.DocumentTypeAttribute docTypeAttr : type.getDocumentTypeAttributes()) {
//                        if (KFSConstants.DocumentTypeAttributes.TRANSACTION_SCRUBBER_OFFSET_INDICATOR_ATTRIBUTE_KEY.equals(docTypeAttr.getKey())) {
//                            scrubberOffset = KFSConstants.DocumentTypeAttributes.INDICATOR_ATTRIBUTE_TRUE_VALUE.equals(docTypeAttr.getValue());
//                        }
//                    }
//                    inputType.setTransactionScrubberOffsetGenerationIndicator(scrubberOffset);
//                    if (!alreadySaved.contains(type.getDocumentTypeCode())) {
//                        LOG.warn("Saving GL Input Type for key " + key + " and value " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
//                        SpringContext.getBean(BusinessObjectService.class).save(inputType);
//                        alreadySaved.add(type.getDocumentTypeCode());
//                    }
//                } else {
//                    LOG.warn("Could not find valid document type table record for " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
//                }
//            }
//        }
//        Collection docTypes = SpringContext.getBean(BusinessObjectService.class).findAll(DocumentType.class);
//        for (Iterator iterator = docTypes.iterator(); iterator.hasNext();) {
//            DocumentType type = (DocumentType) iterator.next();
//            if (!alreadySaved.contains(type.getDocumentTypeCode())) {
//                LOG.warn("Did not process a record for doc type code " + type.getDocumentTypeCode() + " - " + type.getDocumentName());
//            }
//        }
    }
    
    public void testThis() {
        
    }

    /**
     * This method tests the {@link GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByDocumentClass(Class)} implementation
     */
    public void testGetGeneralLedgerInputTypeByDocumentClass() {
        // test for properly returned object
        Class doc_class_to_use = VALID_DOCUMENT.DOCUMENT_CLASS;
        GeneralLedgerInputType type = generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
        assertNotNull("Valid document class '" + doc_class_to_use + "' did not return valid GeneralLedgerInputType object", type);
        assertEquals("Input Type Code is incorrect", VALID_DOCUMENT.GENERAL_LEDGER_INPUT_TYPE_CODE_ACCT, type.getInputTypeCode());
        assertEquals("Document Type Name is incorrect", VALID_DOCUMENT.DOCUMENT_TYPE_NAME, type.getDocumentTypeName());

        // test for exception when null class is passed
        doc_class_to_use = null;
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using null document class");
        }
        catch (IllegalArgumentException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + IllegalArgumentException.class.getName() + "'");
        }

        // test for exception when non Document.class assignable class is passed
        doc_class_to_use = NON_DOCUMENT_ASSIGNABLE_CLASS;
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using a class no assignable to '" + doc_class_to_use.getName() + "'");
        }
        catch (IllegalArgumentException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + IllegalArgumentException.class.getName() + "'");
        }

        // test for exception when Document assignable class that doesn't exist in data dictionary is passed
        doc_class_to_use = DocumentBase.class;
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using invalid document class '" + doc_class_to_use.getName() + "'");
        }
        catch (UnknownDocumentTypeException e) {
            // desired result
        }
        catch (Exception e) {
            LOG.error("Illegal Error", e);
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + UnknownDocumentTypeException.class.getName() + "'");
        }

        // TODO: Implement a test for when a valid class is used but no record in the GeneralLedgerInputType table is found
        // test for exception when valid document class is passed but no record exists in GeneralLedgerInputType table
        doc_class_to_use = VALID_DOCUMENT.DOCUMENT_CLASS;
        type = generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
        SpringContext.getBean(BusinessObjectService.class).delete(type);
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(doc_class_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using invalid document class '" + doc_class_to_use.getName() + "'");
        }
        catch (UnknownGeneralLedgerInputTypeException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + UnknownGeneralLedgerInputTypeException.class.getName() + "'");
        }
    }

    /**
     * This method tests the {@link GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByDocumentName(String)} implementation
     */
    public void testGetGeneralLedgerInputTypeByDocumentName() {
        // test for properly returned object
        String doc_type_name_to_use = UNIT_OF_MEASURE_DOCUMENT_TYPE_NAME;
        GeneralLedgerInputType type = generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentName(doc_type_name_to_use);
        assertNotNull("Valid document type name '" + doc_type_name_to_use + "' did not return valid GeneralLedgerInputType object", type);
        assertEquals("Input Type Code is incorrect", UNIT_OF_MEASURE_INPUT_TYPE_CODE_ACCT, type.getInputTypeCode());

        // test for exception when empty code is passed
        doc_type_name_to_use = "";
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentName(doc_type_name_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using empty document type name");
        }
        catch (IllegalArgumentException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + IllegalArgumentException.class.getName() + "'");
        }

        // test for exception when improper code is passed
        doc_type_name_to_use = "this is wrong to pass";
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentName(doc_type_name_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using invalid document type name '" + doc_type_name_to_use + "'");
        }
        catch (UnknownGeneralLedgerInputTypeException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + UnknownGeneralLedgerInputTypeException.class.getName() + "'");
        }

    }

    /**
     * This method tests the {@link GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByInputTypeCode(String)} implementation
     */
    public void testGetGeneralLedgerInputTypeByInputTypeCode() {
        // test for properly returned object
        String doc_type_to_use = UNIT_OF_MEASURE_INPUT_TYPE_CODE_ACCT;
        GeneralLedgerInputType type = generalLedgerInputTypeService.getGeneralLedgerInputTypeByInputTypeCode(doc_type_to_use);
        assertNotNull("Valid input type code '" + doc_type_to_use + "' did not return valid GeneralLedgerInputType object", type);
        assertEquals("Document Type Name is incorrect", UNIT_OF_MEASURE_DOCUMENT_TYPE_NAME, type.getDocumentTypeName());

        // test for exception when empty code is passed
        doc_type_to_use = "";
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByInputTypeCode(doc_type_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using empty input type code");
        }
        catch (IllegalArgumentException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + IllegalArgumentException.class.getName() + "'");
        }

        // test for exception when improper code is passed
        doc_type_to_use = "this is wrong to pass";
        try {
            generalLedgerInputTypeService.getGeneralLedgerInputTypeByInputTypeCode(doc_type_to_use);
            fail("Exception should have been thrown when retrieving GeneralLedgerInputType object using invalid input type code '" + doc_type_to_use + "'");
        }
        catch (UnknownGeneralLedgerInputTypeException e) {
            // desired result
        }
        catch (Exception e) {
            fail("Exception thrown was '" + e.getClass().getName() + "' but expected '" + UnknownGeneralLedgerInputTypeException.class.getName() + "'");
        }

    }

}
