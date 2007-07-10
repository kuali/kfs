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

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.OrganizationParameter;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private PurapAccountingService purapAccountingService;
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;    
    }
    
    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
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
        Map criteria = new HashMap();
        criteria.put("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        if (clazz == PurchaseOrderView.class) {
            criteria.put("purchaseOrderCurrentIndicator", true);
        }
        List boList = (List) boService.findMatching(clazz, criteria);
        return boList;
    }

    /**
     * 
     * This method will add the below line items to the corresponding document based on
     * the item types specified in the "BELOW_THE_LINE_ITEMS" system parameter of the 
     * document.
     * 
     * @param document
     */
    public void addBelowLineItems(PurchasingAccountsPayableDocument document) {
        String[] itemTypes = getBelowTheLineForDocument(document);
        
        List<PurchasingApItem> existingItems = document.getItems();

        List<String> existingItemTypes = new ArrayList();
        for (PurchasingApItem existingItem : existingItems) {
            existingItemTypes.add(existingItem.getItemTypeCode());
        }
        
        Class itemClass = document.getItemClass();
        
        for (int i=0; i < itemTypes.length; i++) {
            int lastFound;
            if (!existingItemTypes.contains(itemTypes[i])) {
                try {
                    if (i > 0) {
                        lastFound = existingItemTypes.lastIndexOf(itemTypes[i-1]) + 1;
                    }
                    else {
                        lastFound = existingItemTypes.size();
                    }
                    PurchasingApItem newItem = (PurchasingApItem)itemClass.newInstance();                    
                    newItem.setItemTypeCode(itemTypes[i]);
                    newItem.refreshNonUpdateableReferences();
                    existingItems.add(lastFound, newItem);
                    existingItemTypes.add(itemTypes[i]);
                }
                catch (Exception e) {
                    //do something
                }
            }
        }
    }

    /**
     * This method get the Below the line item type codes from the parameters table
     * @param document
     * @return
     */
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document) {
        //Obtain a list of below the line items from system parameter
        String documentTypeClassName = document.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];
        //If it's a credit memo, we'll have to append the source of the credit memo
        //whether it's created from a Vendor, a PO or a PREQ.
        if (documentType.equals("CreditMemoDocument")) {
           
        }
        String securityGroup = (String)PurapConstants.ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP.get(documentType);
        String[] itemTypes = kualiConfigurationService.getApplicationParameterValues(securityGroup, PurapConstants.BELOW_THE_LINES_PARAMETER);
        return itemTypes;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.service.PurapService#getBelowTheLineByType(org.kuali.module.purap.document.PurchasingAccountsPayableDocument, org.kuali.module.purap.bo.ItemType)
     */
    public PurchasingApItem getBelowTheLineByType(PurchasingAccountsPayableDocument document, ItemType iT) {
        PurchasingApItem belowTheLineItem = null;
        for (PurchasingApItem item : (List<PurchasingApItem>)document.getItems()) {
            if(!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                if(StringUtils.equals(iT.getItemTypeCode(), item.getItemType().getItemTypeCode())) {
                    belowTheLineItem = item;
                    break;
                }
            }
        }
        return belowTheLineItem;
    }
    
    public List<SourceAccountingLine> generateSummary(List<PurchasingApItem> items) {
        return purapAccountingService.generateSummary(items);
    }
    
    /**
     * @see org.kuali.module.purap.service.PurapService#isDateAYearAfterToday(java.sql.Date)
     */
    public boolean isDateAYearBeforeToday(Date compareDate) {
        Date today = dateTimeService.getCurrentSqlDate();
        String todayString = today.toString();
        
        String todaysYear = todayString.substring(0,4);
        String restOfToday = todayString.substring(4);
        String lastYear = new Integer((new Integer(todaysYear)).intValue() - 1).toString();
        Date yearAgo = null;
        try {
            yearAgo = dateTimeService.convertToSqlDate(lastYear+restOfToday);
        }
        catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        int diffFromYearAgo = dateTimeService.dateDiff(compareDate, yearAgo, false);
        return (diffFromYearAgo > 0);
    }
    
    
    /*
     *    PURCHASING DOCUMENT METHODS
     * 
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org) {
        KualiDecimal purchaseOrderTotalLimit = SpringServiceLocator.getVendorService().getApoLimitFromContract(
                vendorContractGeneratedIdentifier, chart, org);
        if (ObjectUtils.isNull(purchaseOrderTotalLimit)) {
            // We didn't find the limit on the vendor contract, get it from the org parameter table.
            if ( ObjectUtils.isNull(chart) || ObjectUtils.isNull(org) ) {
                return null;
            }
            OrganizationParameter organizationParameter = new OrganizationParameter();
            organizationParameter.setChartOfAccountsCode(chart);
            organizationParameter.setOrganizationCode(org);
            Map orgParamKeys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(organizationParameter);
            organizationParameter = (OrganizationParameter) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(OrganizationParameter.class, orgParamKeys);
            purchaseOrderTotalLimit = (organizationParameter == null) ? null : organizationParameter.getOrganizationAutomaticPurchaseOrderLimit();
        }
        return purchaseOrderTotalLimit;
    }

    public boolean isDocumentStoppingAtRouteLevel(String documentNumber, String routeNodeName) {
        try {
            ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(Long.valueOf(documentNumber));
            reportCriteriaVO.setTargetNodeName(routeNodeName);
            return SpringServiceLocator.getWorkflowInfoService().documentWillHaveAtLeastOneActionRequest(
                    reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ});
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to test document id '" + documentNumber + "' for action requests at node name '" + routeNodeName + "'";
            LOG.error("isDocumentStoppingAtRouteLevel() " + errorMessage,e);
            throw new RuntimeException(errorMessage,e);
        }
    }
    
}
