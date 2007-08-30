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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.OrganizationParameter;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

public class PurapServiceImpl implements PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private PurapAccountingService purapAccountingService;
    private UniversityDateService universityDateService;
    
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
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    /**
     * This method updates the status and status history for a purap document, passing in a note
     * for the status history.
     */
    public boolean updateStatusAndStatusHistory(PurchasingAccountsPayableDocument document,String statusToSet) {
        LOG.debug("updateStatusAndStatusHistory(): entered method.");
        boolean success = true;
        if ( ObjectUtils.isNotNull(document) && ObjectUtils.isNotNull(statusToSet) ) {
            success &= this.updateStatusHistory(document, statusToSet);
            success &= this.updateStatus(document, statusToSet);
            return success;
        }
        else {
            return false;
        }
    }

    /**
     * This method updates the status for a purap document.
     */
    public boolean updateStatus(PurchasingAccountsPayableDocument document,String newStatus) {
        LOG.debug("updateStatus(): entered method.");       
        if ( ObjectUtils.isNotNull(document) || ObjectUtils.isNotNull(newStatus) ) {
            String oldStatus = document.getStatusCode();
            document.setStatusCode(newStatus);
            LOG.debug("Status of document #"+document.getDocumentNumber()+" has been changed from "+
                        oldStatus+" to "+newStatus);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * This method updates the status history for a purap document.
     * 
     * @param document              The document whose status history needs to be updated.
     * @param newStatus             The new status code in String form
     * @return                      True on success.
     */
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document, String newStatus) {
        LOG.debug("updateStatusHistory(): entered method.");       
        if ( ObjectUtils.isNotNull(document) || ObjectUtils.isNotNull(newStatus) ) {
            String oldStatus = document.getStatusCode();       
            document.addToStatusHistories( oldStatus, newStatus );      
            LOG.debug("StatusHistory record for document #"+document.getDocumentNumber()+" has added to show change from "
                    +oldStatus+" to "+newStatus);
            return true;
        }
        else {
            return false;
        }
    }

    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        Map criteria = new HashMap();
        criteria.put("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        if (clazz == PurchaseOrderView.class) {
            criteria.put("purchaseOrderCurrentIndicator", true);
        }
        List boList = (List) businessObjectService.findMatching(clazz, criteria);
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

    public Map<SourceAccountingLine, List<PurchasingApItem>> generateSummaryWithItems(List<PurchasingApItem> items) {
        return purapAccountingService.generateSummaryWithItems(items);
    }
    
    /**
     * @see org.kuali.module.purap.service.PurapService#isDateAYearAfterToday(java.sql.Date)
     */
    public boolean isDateAYearBeforeToday(Date compareDate) {
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.YEAR,-1);
        Date yearAgo = new Date(calendar.getTimeInMillis());        
        int diffFromYearAgo = dateTimeService.dateDiff(compareDate, yearAgo, false);
        return (diffFromYearAgo > 0);
    }    
    
    /*
     *    PURCHASING DOCUMENT METHODS
     * 
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org) {
        KualiDecimal purchaseOrderTotalLimit = SpringContext.getBean(VendorService.class).getApoLimitFromContract(
                vendorContractGeneratedIdentifier, chart, org);
        if (ObjectUtils.isNull(purchaseOrderTotalLimit)) {
            // We didn't find the limit on the vendor contract, get it from the org parameter table.
            if ( ObjectUtils.isNull(chart) || ObjectUtils.isNull(org) ) {
                return null;
            }
            OrganizationParameter organizationParameter = new OrganizationParameter();
            organizationParameter.setChartOfAccountsCode(chart);
            organizationParameter.setOrganizationCode(org);
            Map orgParamKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(organizationParameter);
            organizationParameter = (OrganizationParameter) businessObjectService.findByPrimaryKey(OrganizationParameter.class, orgParamKeys);
            purchaseOrderTotalLimit = (organizationParameter == null) ? null : organizationParameter.getOrganizationAutomaticPurchaseOrderLimit();
        }
        return purchaseOrderTotalLimit;
    }

    private boolean allowEncumberNextFiscalYear() {
        LOG.debug("allowEncumberNextFiscalYear() started");

        java.util.Date today = dateTimeService.getCurrentDate();
        java.util.Date closingDate = universityDateService.getLastDateOfFiscalYear(universityDateService.getCurrentFiscalYear());
        Integer allowEncumberNext = new Integer(kualiConfigurationService.getApplicationParameterValue(PurapRuleConstants.PURAP_ADMIN_GROUP, PurapRuleConstants.ALLOW_ENCUMBER_NEXT_YEAR_DAYS));
        int diffTodayClosing = dateTimeService.dateDiff(today, closingDate, false);

        if (ObjectUtils.isNotNull(closingDate) && ObjectUtils.isNotNull(today) && ObjectUtils.isNotNull(allowEncumberNext)) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            LOG.debug("allowEncumberNextFiscalYear() today = " + format.format(today.getTime()) + "; encumber next FY range = " + allowEncumberNext + " - " + format.format(closingDate.getTime()));

            if (allowEncumberNext.intValue() >= diffTodayClosing && diffTodayClosing >= KualiDecimal.ZERO.intValue()) {
                LOG.debug("allowEncumberNextFiscalYear() encumber next FY allowed; return true.");
                return true;
            }
        }
        LOG.debug("allowEncumberNextFiscalYear() encumber next FY not allowed; return false.");
        return false;
    }

    public List<Integer> getAllowedFiscalYears() {
        List allowedYears = new ArrayList();
        Integer currentFY = universityDateService.getCurrentFiscalYear();
        allowedYears.add(currentFY);
        if (allowEncumberNextFiscalYear()) {
            allowedYears.add(currentFY + 1);
        }
        return allowedYears;
    }
    
    /**
     * 
     * This method returns true if full entry mode has ended for this document
     * @param preqDocument
     * @return a boolean
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument) {
        //for now just return true if not in one of the first few states
        boolean value = false;
        if(purapDocument instanceof PaymentRequestDocument) {
            value = PurapConstants.PaymentRequestStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        } else if(purapDocument instanceof CreditMemoDocument) {
            value = PurapConstants.CreditMemoStatuses.STATUS_ORDER.isFullDocumentEntryCompleted(purapDocument.getStatusCode());
        }
        return value;
    }
    
    public void performLogicForFullEntryCompleted(PurchasingAccountsPayableDocument purapDocument) {
        //TODO: move logic from various parts of the app to here
        if (purapDocument instanceof RequisitionDocument) {
            /* not sure if this can be used or not?  The fact that the REQ is editable by anyone while it's In Process
             * but only Content Approvers can edit the doc in Content Level does that leave this method as holding too many
             * if-else cases?
             */
        }
        // below code preferable to run in post processing
        else if (purapDocument instanceof PaymentRequestDocument) {
            // change PREQ accounts from percents to dollars
            // TODO Chris - put code here for document to change percents into dollars and any related functions
            // do GL entries for PREQ creation
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesCreatePreq((PaymentRequestDocument)purapDocument);
            // route potential 'Close PO Document' if checkbox was checked
        }
        // below code preferable to run in post processing
        else if (purapDocument instanceof CreditMemoDocument) {
            // change CM accounts from percents to dollars
            // TODO Chris - put code here for document to change percents into dollars and any related functions
            // do GL entries for CM creation
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesCreateCreditMemo((CreditMemoDocument)purapDocument);
            // route potential 'Re-Open PO Document' if PO criteria meets requirements from EPIC business rules
        }
        else {
            throw new RuntimeException("Attempted to perform full entry logic for unhandled document type '" + purapDocument.getClass().getName() + "'");
        }
    }
    
    public Object performLogicWithFakedUserSession(String requiredUniversalUserPersonUserId, LogicToRunAsFakeUser logicToRun, Object... objects) throws UserNotFoundException, WorkflowException, Exception {
        if (StringUtils.isBlank(requiredUniversalUserPersonUserId)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with a blank user person id: '" + requiredUniversalUserPersonUserId + "'");
        }
        if (ObjectUtils.isNull(logicToRun)) {
            throw new RuntimeException("Attempted to perform logic with a fake user session with no logic to run");
        }
        UserSession actualUserSession = GlobalVariables.getUserSession();
        try {
            GlobalVariables.setUserSession(new UserSession(requiredUniversalUserPersonUserId));
            return logicToRun.runLogic(objects);
        }
        finally {
            GlobalVariables.setUserSession(actualUserSession);
        }
    }
}
