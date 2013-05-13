/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class provides service for Security maintenance
 */
public class HoldingHistoryValueAdjustmentDocumentServiceImpl implements HoldingHistoryValueAdjustmentDocumentService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService#getHoldingHistoryValueAdjustmentDocument(String)
     */
    public Collection<HoldingHistoryValueAdjustmentDocument> getHoldingHistoryValueAdjustmentDocument(String transactionPosted) {

        Collection<HoldingHistoryValueAdjustmentDocument> holdingHistoryValueAdjustmentDocument = new ArrayList();
        
        if (StringUtils.isNotBlank(transactionPosted)) {
            Map criteria = new HashMap();
            
            criteria.put("transactionPosted", transactionPosted);            
            holdingHistoryValueAdjustmentDocument = businessObjectService.findMatching(HoldingHistoryValueAdjustmentDocument.class, criteria);
        }
        
        return holdingHistoryValueAdjustmentDocument;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService#saveHoldingHistory(HoldingHistoryValueAdjustmentDocument)
     */
    public boolean saveHoldingHistory(HoldingHistoryValueAdjustmentDocument holdingHistoryValueAdjustmentDocument) {
       boolean success = true;
       
       try {
           businessObjectService.save(holdingHistoryValueAdjustmentDocument);
       }
       catch (Exception ex) {
           success = false;
       }
       
       return success;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService#saveHoldingHistory#getHoldingHistoryValueAdjustmentDocumentByDocumentNumber(String)
     */
    public Collection<HoldingHistoryValueAdjustmentDocument> getHoldingHistoryValueAdjustmentDocumentByDocumentNumber(String documentNumber) {    
        Collection<HoldingHistoryValueAdjustmentDocument> holdingHistoryValueAdjustmentDocument = new ArrayList();

        if (StringUtils.isNotBlank(documentNumber)) {
            Map criteria = new HashMap();
            
            criteria.put("documentNumber", documentNumber);            
            holdingHistoryValueAdjustmentDocument = businessObjectService.findMatching(HoldingHistoryValueAdjustmentDocument.class, criteria); // .findMatching(HoldingHistoryValueAdjustmentDocument.class, criteria);
        }
        
        return holdingHistoryValueAdjustmentDocument;
    }
    
    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
