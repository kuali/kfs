/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.bo.Note;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapService;

public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    /**
     * This method updates the status and status history for a purap document.
     */
    public boolean updateStatusAndStatusHistory(PurchasingAccountsPayableDocument document,String statusToSet) {
        return updateStatusAndStatusHistory( document, statusToSet, null );
    }
    
    /**
     * This method updates the status and status history for a purap document, passing in a note
     * for the status history.
     */
    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document,
            String statusToSet, Note statusHistoryNote ) {
        LOG.debug("updateStatusAndStatusHistory(): entered method.");
        
        boolean success = false;
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(statusToSet) ) {
            return success;
        }

        success &= this.updateStatusHistory(document, statusToSet, statusHistoryNote);
        success = this.updateStatus(document, statusToSet);

        LOG.debug("updateStatusAndStatusHistory(): leaving method.");
        return success;
    }

    /**
     * This method updates the status for a purap document.
     */
    public boolean updateStatus(PurchasingAccountsPayableDocument document,String newStatus) {
        LOG.debug("updateStatus(): entered method.");
        
        boolean success = false;
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(newStatus) ) {
            return success;
        }
        
        String oldStatus = document.getStatusCode();

        document.setStatusCode(newStatus);
        
        success = true;
        if (success) {
            LOG.debug("Status of document #"+document.getDocumentNumber()+" has been changed from "+
               oldStatus+" to "+newStatus);
        }
        
        LOG.debug("updateStatus(): leaving method.");
        return success;
    }

    /**
     * This method updates the status history for a purap document.
     */
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document, String newStatus) {      
        return updateStatusHistory( document, newStatus, null );
    }
    
    /**
     * This method updates the status history for a purap document and includes an optional BO
     * annotation for the entry.
     * 
     * @param document              The document whose status history needs to be updated.
     * @param newStatus             The new status code in String form
     * @param statusHistoryNote     An optional BO annotation
     * @return                      True on success.
     */
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document, 
            String newStatus, Note statusHistoryNote ) {
        LOG.debug("updateStatusHistory(): entered method.");
        boolean success = false;       
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(newStatus) ) {
            return success;
        }
        String oldStatus = document.getStatusCode();       
        document.addToStatusHistories( oldStatus, newStatus, statusHistoryNote );      

        success = true;
        if (success) {
            LOG.debug("StatusHistory of document #"+document.getDocumentNumber()+" has been changed from "
                    +oldStatus+" to "+newStatus);
        }
        LOG.debug("updateStatusHistory(): leaving method.");
        return success;
    }

    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
        Map<String, Integer> criteria = new HashMap();
        criteria.put("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        List boList = (List) boService.findMatching(clazz, criteria);
        return boList;
    }

}
