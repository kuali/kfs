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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.dao.RequisitionDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.rule.event.ValidateCapitalAssetsForAutomaticPurchaseOrderEvent;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.vendor.bo.VendorCommodityCode;
import org.kuali.module.vendor.bo.VendorDetail;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;



/**
 * Implementation of RequisitionService
 */
@Transactional
public class RequisitionServiceImpl implements RequisitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PurapService purapService;
    private RequisitionDao requisitionDao;
    private KualiRuleService ruleService;
    private ParameterService parameterService;


    /**
     * @see org.kuali.module.purap.service.RequisitionService#saveDocumentWithoutValidation(org.kuali.module.purap.document.RequisitionDocument)
     */
    public void saveDocumentWithoutValidation(RequisitionDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.module.purap.service.RequisitionService#getRequisitionById(java.lang.Integer)
     */
    public RequisitionDocument getRequisitionById(Integer id) {
        String documentNumber = requisitionDao.getDocumentNumberForRequisitionId(id);
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                RequisitionDocument doc = (RequisitionDocument) documentService.getByDocumentHeaderId(documentNumber);

                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting requisition document from document service";
                LOG.error("getRequisitionById() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }

        return null;
    }

    /**
     * @see org.kuali.module.purap.service.RequisitionService#isAutomaticPurchaseOrderAllowed(org.kuali.module.purap.document.RequisitionDocument)
     */
    public boolean isAutomaticPurchaseOrderAllowed(RequisitionDocument requisition) {
        LOG.debug("isAutomaticPurchaseOrderAllowed() started");

        /*
         * The private checkAutomaticPurchaseOrderRules method contains rules to check if a requisition can become an APO (Automatic
         * Purchase Order). The method returns a string containing the reason why this method should return false. Save the reason
         * as a note on the requisition.
         */
        String note = checkAutomaticPurchaseOrderRules(requisition);
        if (StringUtils.isNotEmpty(note)) {
            note = PurapConstants.REQ_REASON_NOT_APO + note;
            try {
                Note apoNote = documentService.createNoteFromDocument(requisition, note);
                documentService.addNoteToDocument(requisition, apoNote);
            }
            catch (Exception e) {
                throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
            }
            LOG.debug("isAutomaticPurchaseOrderAllowed() return false; " + note);

            return false;
        }

        LOG.debug("isAutomaticPurchaseOrderAllowed() You made it!  Your REQ can become an APO; return true.");

        return true;
    }

    /**
     * Checks the rule for Automatic Purchase Order eligibility of the requisition and return a String containing the reason why the
     * requisition was not eligible to become an APO if it was not eligible, or return an empty String if the requisition is
     * eligible to become an APO
     * 
     * @param requisition the requisition document to be checked for APO eligibility.
     * @return String containing the reason why the requisition was not eligible to become an APO if it was not eligible, or an
     *         empty String if the requisition is eligible to become an APO.
     */
    private String checkAutomaticPurchaseOrderRules(RequisitionDocument requisition) {
        String requisitionSource = requisition.getRequisitionSourceCode();
        KualiDecimal reqTotal = requisition.getTotalDollarAmount();
        KualiDecimal apoLimit = purapService.getApoLimit(requisition.getVendorContractGeneratedIdentifier(), requisition.getChartOfAccountsCode(), requisition.getOrganizationCode());
        requisition.setOrganizationAutomaticPurchaseOrderLimit(apoLimit);

        LOG.debug("isAPO() reqId = " + requisition.getPurapDocumentIdentifier() + "; apoLimit = " + apoLimit + "; reqTotal = " + reqTotal);
        if (apoLimit == null) {

            return "APO limit is empty.";
        }
        else {
            if (reqTotal.compareTo(apoLimit) == 1) {

                return "Requisition total is greater than the APO limit.";
            }
        }

        if (reqTotal.compareTo(KualiDecimal.ZERO) <= 0) {

            return "Requisition total is not greater than zero.";
        }

        LOG.debug("isAPO() vendor #" + requisition.getVendorHeaderGeneratedIdentifier() + "-" + requisition.getVendorDetailAssignedIdentifier());
        if (requisition.getVendorHeaderGeneratedIdentifier() == null || requisition.getVendorDetailAssignedIdentifier() == null) {

            return "Vendor was not selected from the vendor database.";
        }
        else {
            VendorDetail vendorDetail = new VendorDetail();
            vendorDetail.setVendorHeaderGeneratedIdentifier(requisition.getVendorHeaderGeneratedIdentifier());
            vendorDetail.setVendorDetailAssignedIdentifier(requisition.getVendorDetailAssignedIdentifier());
            vendorDetail = (VendorDetail) businessObjectService.retrieve(vendorDetail);
            if (vendorDetail == null) {

                return "Error retrieving vendor from the database.";
            }

            requisition.setVendorRestrictedIndicator(vendorDetail.getVendorRestrictedIndicator());
            if (requisition.getVendorRestrictedIndicator() != null && requisition.getVendorRestrictedIndicator()) {

                return "Selected vendor is marked as restricted.";
            }
            requisition.setVendorDetail(vendorDetail);
        }
        
        //These are needed for commodity codes. They are put in here so that
        //we don't have to loop through items too many times.
        String purchaseOrderRequiresCommodityCode = parameterService.getParameterValue(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
        
        for (Iterator iter = requisition.getItems().iterator(); iter.hasNext();) {
            RequisitionItem item = (RequisitionItem) iter.next();
            if (item.isItemRestrictedIndicator()) {

                return "Requisition contains an item that is marked as restricted.";
            }
            
            String commodityCodesReason = "";
            if (purchaseOrderRequiresCommodityCode.equals("Y")) {
                List<VendorCommodityCode> vendorCommodityCodes = requisition.getVendorDetail().getVendorCommodities();
                commodityCodesReason = checkAPORulesPerItemForCommodityCodes(item, vendorCommodityCodes);
            }
            if (StringUtils.isNotBlank(commodityCodesReason)) {
                return commodityCodesReason;    
            }
        }// endfor items

        if (StringUtils.isNotEmpty(requisition.getRecurringPaymentTypeCode())) {

            return "Payment type is marked as recurring.";
        }

        if ((requisition.getPurchaseOrderTotalLimit() != null) && (KualiDecimal.ZERO.compareTo(requisition.getPurchaseOrderTotalLimit()) != 0)) {
            LOG.debug("isAPO() po total limit is not null and not equal to zero; return false.");

            return "The 'PO not to exceed' amount has been entered.";
        }

        if (StringUtils.isNotEmpty(requisition.getAlternate1VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate2VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate3VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate4VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate5VendorName())) {
            LOG.debug("isAPO() alternate vendor name exists; return false.");

            return "Requisition contains alternate vendor names.";
        }
        
        if (!ruleService.applyRules(new ValidateCapitalAssetsForAutomaticPurchaseOrderEvent("", requisition))) {
            
            return "Requisition has failed Capital Asset rules.";
        }
        
        return "";
    }
    
    /**
     * Checks the APO rules for Commodity Codes. 
     * The rules are as follow :
     * 1. If an institution does not require a commodity code on a requisition but 
     *    does require a commodity code on a purchase order:
     *    a. If the requisition qualifies for an APO and the commodity code is blank
     *       on any line item then the system should use the default commodity code
     *       for the vendor.
     *    b. If there is not a default commodity code for the vendor then the
     *       requisition is not eligible to become an APO.
     * 2. The commodity codes where the restricted indicator is Y should disallow
     *    the requisition from becoming an APO.
     * 3. If the commodity code is Inactive when the requisition is finally approved 
     *    do not allow the requisition to become an APO.
     *    
     * @param requisition
     * @return
     */
    private String checkAPORulesPerItemForCommodityCodes(RequisitionItem purItem, List<VendorCommodityCode>vendorCommodityCodes) {
        // If the commodity code is blank on any line item, then the system should use
        // the default commodity code for the vendor
        if (purItem.getCommodityCode() == null) {
            for (VendorCommodityCode vcc : vendorCommodityCodes) {
                if (vcc.isCommodityDefaultIndicator()) {
                    purItem.setCommodityCode(vcc.getCommodityCode());
                }
            }
            // If there is not a default commodity code for the vendor then the requisition
            // is not eligible to become an APO.
            if (purItem.getCommodityCode() == null) {
                return "there are missing commodity code(s).";
            }
        }
        if (!purItem.getCommodityCode().isActive()) {
            return "Requisition contains inactive commodity codes.";
        }
        else if (purItem.getCommodityCode().isRestrictedItemsIndicator()) {
            return "Requisition contains an item that is marked as restricted.";
        }
        return "";
    }
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setRequisitionDao(RequisitionDao requisitionDao) {
        this.requisitionDao = requisitionDao;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    
    public KualiRuleService getRuleService() {
        return ruleService;
    }

    public void setRuleService(KualiRuleService ruleService) {
        this.ruleService = ruleService;
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
