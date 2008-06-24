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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.ValidateCapitalAssetsForAutomaticPurchaseOrderEvent;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;



/**
 * Implementation of RequisitionService
 */
@Transactional
public class RequisitionServiceImpl implements RequisitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private KualiRuleService ruleService;
    private ParameterService parameterService;
    private PurapService purapService;
    private RequisitionDao requisitionDao;
    private UniversityDateService universityDateService;


    /**
     * @see org.kuali.kfs.module.purap.document.service.RequisitionService#saveDocumentWithoutValidation(org.kuali.kfs.module.purap.document.RequisitionDocument)
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
     * @see org.kuali.kfs.module.purap.document.service.RequisitionService#getRequisitionById(java.lang.Integer)
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
     * @see org.kuali.kfs.module.purap.document.service.RequisitionService#isAutomaticPurchaseOrderAllowed(org.kuali.kfs.module.purap.document.RequisitionDocument)
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

//TODO RELEASE 3 - Discounts and trade-ins
//          if (EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE.equals(item.getItemType().getCode()) || EpicConstants.ITEM_TYPE_TRADE_IN_CODE.equals(item.getItemType().getCode())) {
//              if ((item.getUnitPrice() != null) && ((zero.compareTo(item.getUnitPrice())) != 0)) {
//                  // discount or trade-in item has unit price that is not empty or zero
//                  return "Requisition contains a " + item.getItemType().getDescription() + " item, so it does not qualify as an APO.";
//              }
//          }
//TODO RELEASE 3 - Capital Asset Codes
//          if (!PurapConstants.RequisitionSources.B2B.equals(requisitionSource)) {
//              for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
//                  RequisitionAccount account = (RequisitionAccount) iterator.next();
//                  ObjectCode code =  objectCodeService.getByPrimaryId(requisition.getPostingYear(), account.getChartOfAccountsCode(), account.getFinancialObjectCode());
//                  if (EpicConstants.FIS_CAPITAL_ASSET_OBJECT_LEVEL_CODE.equals(code.getFinancialObjectLevelCode())) {
//                      return "Standard requisition with a capital asset object code.";
//                  }
//              }//endfor accounts
//          }

        }// endfor items

// TODO RELEASE 3 - need to implement validateCapitalASsetNumbersForAPO
//    if( !validateCapitalAssetNumbersForAPO( requisition ) ) {
//        return "Requisition did not pass CAMS validation.";
//    }

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
        
        if (requisition.isPostingYearNext() && !purapService.isTodayWithinApoAllowedRange()) {
            return "Requisition is set to encumber next fiscal year and approval is not within APO allowed date range.";
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

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
