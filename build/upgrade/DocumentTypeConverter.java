
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class...
 */
public class DocumentTypeConverter {

    /**
     * This method...
     * 
     * @param args
     */
    public static void main(String[] args) {
        Set<String> alreadySaved = new HashSet<String>();
        Map<String, DocumentEntry> entryMap = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries();
        for (String key : entryMap.keySet()) {
            DocumentEntry docEntry = entryMap.get(key);
            String docTypeCode = docEntry.getDocumentTypeCode();
            Map<String, String> primaryKeys = new HashMap<String, String>();
            primaryKeys.put("documentTypeCode", docTypeCode);
            DocumentType type = (DocumentType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, primaryKeys);
            if (ObjectUtils.isNotNull(type)) {
                GeneralLedgerInputType inputType = new GeneralLedgerInputType();
                inputType.setInputTypeCode(type.getDocumentTypeCode());
                inputType.setDocumentTypeActiveIndicator(type.isDocumentTypeActiveIndicator());
                inputType.setDocumentTypeName(docEntry.getDocumentTypeName());
                boolean scrubberOffset = false;
                for (org.kuali.rice.kns.bo.DocumentTypeAttribute docTypeAttr : type.getDocumentTypeAttributes()) {
                    if (KFSConstants.DocumentTypeAttributes.TRANSACTION_SCRUBBER_OFFSET_INDICATOR_ATTRIBUTE_KEY.equals(docTypeAttr.getKey())) {
                        scrubberOffset = KFSConstants.DocumentTypeAttributes.INDICATOR_ATTRIBUTE_TRUE_VALUE.equals(docTypeAttr.getValue());
                    }
                }
                inputType.setTransactionScrubberOffsetGenerationIndicator(scrubberOffset);
                if (!alreadySaved.contains(type.getDocumentTypeCode())) {
                    LOG.warn("Saving GL Input Type for key " + key + " and value " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
                    SpringContext.getBean(BusinessObjectService.class).save(inputType);
                    alreadySaved.add(type.getDocumentTypeCode());
                }
            }
            else {
                LOG.warn("Could not find valid document type table record for " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
            }
        }
        Map<String, BusinessObjectEntry> boEntryMap = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntries();
        for (String key : boEntryMap.keySet()) {
            BusinessObjectEntry boEntry = boEntryMap.get(key);
            MaintenanceDocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(boEntry.getBusinessObjectClass());
            if (ObjectUtils.isNotNull(docEntry)) {
                String docTypeCode = docEntry.getDocumentTypeCode();
                Map<String, String> primaryKeys = new HashMap<String, String>();
                primaryKeys.put("documentTypeCode", docTypeCode);
                DocumentType type = (DocumentType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, primaryKeys);
                if (ObjectUtils.isNotNull(type)) {
                    GeneralLedgerInputType inputType = new GeneralLedgerInputType();
                    inputType.setInputTypeCode(type.getDocumentTypeCode());
                    inputType.setDocumentTypeActiveIndicator(type.isDocumentTypeActiveIndicator());
                    inputType.setDocumentTypeName(docEntry.getDocumentTypeName());
                    boolean scrubberOffset = false;
                    for (org.kuali.rice.kns.bo.DocumentTypeAttribute docTypeAttr : type.getDocumentTypeAttributes()) {
                        if (KFSConstants.DocumentTypeAttributes.TRANSACTION_SCRUBBER_OFFSET_INDICATOR_ATTRIBUTE_KEY.equals(docTypeAttr.getKey())) {
                            scrubberOffset = KFSConstants.DocumentTypeAttributes.INDICATOR_ATTRIBUTE_TRUE_VALUE.equals(docTypeAttr.getValue());
                        }
                    }
                    inputType.setTransactionScrubberOffsetGenerationIndicator(scrubberOffset);
                    if (!alreadySaved.contains(type.getDocumentTypeCode())) {
                        LOG.warn("Saving GL Input Type for key " + key + " and value " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
                        SpringContext.getBean(BusinessObjectService.class).save(inputType);
                        alreadySaved.add(type.getDocumentTypeCode());
                    }
                }
                else {
                    LOG.warn("Could not find valid document type table record for " + docEntry.getDocumentTypeCode() + "--" + docEntry.getDocumentTypeName());
                }
            }
        }
        Collection docTypes = SpringContext.getBean(BusinessObjectService.class).findAll(DocumentType.class);
        for (Iterator iterator = docTypes.iterator(); iterator.hasNext();) {
            DocumentType type = (DocumentType) iterator.next();
            if (!alreadySaved.contains(type.getDocumentTypeCode())) {
                LOG.warn("Did not process a record for doc type code " + type.getDocumentTypeCode() + " - " + type.getDocumentName());
            }
        }
    }

}
