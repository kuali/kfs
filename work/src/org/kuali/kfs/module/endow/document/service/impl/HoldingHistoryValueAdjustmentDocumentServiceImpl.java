/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
