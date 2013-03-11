/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of RequisitionService
 */
@Transactional
public class RequisitionServiceImpl implements RequisitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private KualiRuleService ruleService;
    private ConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private PersonService personService;
    private PostalCodeValidationService postalCodeValidationService;
    private PurapService purapService;
    private RequisitionDao requisitionDao;
    private UniversityDateService universityDateService;
    private VendorService vendorService;
 
    public PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingCapitalAssetItem camsItem = new RequisitionCapitalAssetItem();
        camsItem.setItemIdentifier(purapItem.getItemIdentifier());
        // If the system type is INDIVIDUAL then for each of the capital asset items, we need a system attached to it.
        if (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS)) {
            CapitalAssetSystem resultSystem = new RequisitionCapitalAssetSystem();
            camsItem.setPurchasingCapitalAssetSystem(resultSystem);
        }
        camsItem.setPurchasingDocument(purDoc);

        return camsItem;
    }
    
    public CapitalAssetSystem createCapitalAssetSystem() {
        CapitalAssetSystem resultSystem = new RequisitionCapitalAssetSystem();
        return resultSystem;
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
                requisition.addNote(apoNote);
                documentService.saveDocumentNotes(requisition);
              }
            catch (Exception e) {
                throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("isAutomaticPurchaseOrderAllowed() return false; " + note);
            }
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
    protected String checkAutomaticPurchaseOrderRules(RequisitionDocument requisition) {
        String requisitionSource = requisition.getRequisitionSourceCode();
        KualiDecimal reqTotal = requisition.getTotalDollarAmount();
        KualiDecimal apoLimit = purapService.getApoLimit(requisition.getVendorContractGeneratedIdentifier(), requisition.getChartOfAccountsCode(), requisition.getOrganizationCode());
        requisition.setOrganizationAutomaticPurchaseOrderLimit(apoLimit);

        if (LOG.isDebugEnabled()) {
            LOG.debug("isAPO() reqId = " + requisition.getPurapDocumentIdentifier() + "; apoLimit = " + apoLimit + "; reqTotal = " + reqTotal);
        }
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("isAPO() vendor #" + requisition.getVendorHeaderGeneratedIdentifier() + "-" + requisition.getVendorDetailAssignedIdentifier());
        }
        if (requisition.getVendorHeaderGeneratedIdentifier() == null || requisition.getVendorDetailAssignedIdentifier() == null) {
            return "Vendor was not selected from the vendor database.";
        }
        else {
            VendorDetail vendorDetail = vendorService.getVendorDetail(requisition.getVendorHeaderGeneratedIdentifier(), requisition.getVendorDetailAssignedIdentifier());
            if (vendorDetail == null) {
                return "Error retrieving vendor from the database.";
            }
            if ( StringUtils.isBlank(requisition.getVendorLine1Address()) || 
                 StringUtils.isBlank(requisition.getVendorCityName()) ||
                 StringUtils.isBlank(requisition.getVendorCountryCode())) {
                return "Requisition does not have all of the vendor address fields that are required for Purchase Order.";
            }
            requisition.setVendorRestrictedIndicator(vendorDetail.getVendorRestrictedIndicator());
            if (requisition.getVendorRestrictedIndicator() != null && requisition.getVendorRestrictedIndicator()) {
                return "Selected vendor is marked as restricted.";
            }
            if (vendorDetail.isVendorDebarred()) {
                return "Selected vendor is marked as a debarred vendor";
            }
            requisition.setVendorDetail(vendorDetail);

            if ((!PurapConstants.RequisitionSources.B2B.equals(requisitionSource)) && ObjectUtils.isNull(requisition.getVendorContractGeneratedIdentifier())) {
               Person initiator = getPersonService().getPerson(requisition.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
                VendorContract b2bContract = vendorService.getVendorB2BContract(vendorDetail, initiator.getCampusCode());
                if (b2bContract != null) {
                    return "Standard requisition with no contract selected but a B2B contract exists for the selected vendor.";
                }
            }
            
            //vendor contract expirated date validation....KFSMI-8502
            // if the vendor is selected through vendor contract is selected 
            if (StringUtils.isNotBlank(requisition.getVendorContractName())) {
                if (vendorService.isVendorContractExpired(requisition, requisition.getVendorContractGeneratedIdentifier(), vendorDetail)) {
                    return "Contracted Vendor used where the contract end date is expired.";
                }
            }
        }
        
        //if vendor address isn't complete, no APO
        if (StringUtils.isBlank(requisition.getVendorLine1Address()) ||
                StringUtils.isBlank(requisition.getVendorCityName()) ||
                StringUtils.isBlank(requisition.getVendorCountryCode()) ||
                !postalCodeValidationService.validateAddress(requisition.getVendorCountryCode(), requisition.getVendorStateCode(), requisition.getVendorPostalCode(), "", "")) {
            return "Requistion does not contain a complete vendor address";
        }

        // These are needed for commodity codes. They are put in here so that
        // we don't have to loop through items too many times.
        String purchaseOrderRequiresCommodityCode = parameterService.getParameterValueAsString(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
        boolean commodityCodeRequired = purchaseOrderRequiresCommodityCode.equals("Y");
        
        for (Iterator iter = requisition.getItems().iterator(); iter.hasNext();) {
            RequisitionItem item = (RequisitionItem) iter.next();
            if (item.isItemRestrictedIndicator()) {
                return "Requisition contains an item that is marked as restricted.";
            }
            
            //We only need to check the commodity codes if this is an above the line item.
            if (item.getItemType().isLineItemIndicator()) {
                String commodityCodesReason = "";
                List<VendorCommodityCode> vendorCommodityCodes = commodityCodeRequired ? requisition.getVendorDetail().getVendorCommodities() : null;
                commodityCodesReason = checkAPORulesPerItemForCommodityCodes(item, vendorCommodityCodes, commodityCodeRequired);
                if (StringUtils.isNotBlank(commodityCodesReason)) {
                    return commodityCodesReason;
                }
            }
            
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE.equals(item.getItemType().getItemTypeCode()) || PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(item.getItemType().getItemTypeCode())) {
                if ((item.getItemUnitPrice() != null) && ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice())) != 0)) {
                    // discount or trade-in item has unit price that is not empty or zero
                    return "Requisition contains a " + item.getItemType().getItemTypeDescription() + " item, so it does not qualify as an APO.";
                }
            }
            
            if (!PurapConstants.RequisitionSources.B2B.equals(requisitionSource)) {
                for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                    if (capitalAssetBuilderModuleService.doesAccountingLineFailAutomaticPurchaseOrderRules(accountingLine)) {
                        return "Requisition contains accounting line with capital object level";
                    }
                }
            }

        }// endfor items

        if (capitalAssetBuilderModuleService.doesDocumentFailAutomaticPurchaseOrderRules(requisition)) {
            return "Requisition contains capital asset items.";
        }

        if (StringUtils.isNotEmpty(requisition.getRecurringPaymentTypeCode())) {
            return "Payment type is marked as recurring.";
        }

        if ((requisition.getPurchaseOrderTotalLimit() != null) && (KualiDecimal.ZERO.compareTo(requisition.getPurchaseOrderTotalLimit()) != 0)) {
            LOG.debug("isAPO() po total limit is not null and not equal to zero; return false.");
            return "The 'PO not to exceed' amount has been entered.";
        }

        if (StringUtils.isNotEmpty(requisition.getAlternate1VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate2VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate3VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate4VendorName()) || StringUtils.isNotEmpty(requisition.getAlternate5VendorName())) {
            LOG.debug("isAPO() alternate vendor name exists; return false.");
            return "Requisition contains additional suggested vendor names.";
        }
        
        if (requisition.isPostingYearNext() && !purapService.isTodayWithinApoAllowedRange()) {
            return "Requisition is set to encumber next fiscal year and approval is not within APO allowed date range.";
        }

        return "";
    }
    
    /**
     * Checks the APO rules for Commodity Codes. 
     * The rules are as follow:
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
     * @param purItem
     * @param vendorCommodityCodes
     * @param commodityCodeRequired
     * @return
     */
    protected String checkAPORulesPerItemForCommodityCodes(RequisitionItem purItem, List<VendorCommodityCode>vendorCommodityCodes, boolean commodityCodeRequired) {
        // If the commodity code is blank on any line item and a commodity code is required,
        // then the system should use the default commodity code for the vendor
        if (purItem.getCommodityCode() == null && commodityCodeRequired) {
            for (VendorCommodityCode vcc : vendorCommodityCodes) {
                if (vcc.isCommodityDefaultIndicator()) {
                    purItem.setCommodityCode(vcc.getCommodityCode());
                    purItem.setPurchasingCommodityCode(vcc.getPurchasingCommodityCode());
                }
            }
        }
        if (purItem.getCommodityCode() == null) {
            // If there is not a default commodity code for the vendor then the requisition is not eligible to become an APO.
            if (commodityCodeRequired)
                return "There are missing commodity code(s).";
        }
        else if (!purItem.getCommodityCode().isActive()) {
            return "Requisition contains inactive commodity codes.";
        }
        else if (purItem.getCommodityCode().isRestrictedItemsIndicator()) {
            return "Requisition contains an item with a restricted commodity code.";
        }
        return "";
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.RequisitionService#getRequisitionsAwaitingContractManagerAssignment()
     */
    public List<RequisitionDocument> getRequisitionsAwaitingContractManagerAssignment() {
        List<RequisitionDocument> unassignedRequisitions = new ArrayList<RequisitionDocument>();
        List<RequisitionDocument> requisitions = new ArrayList<RequisitionDocument>();

        List<String> documentsNumbersAwaitingContractManagerAssignment = getDocumentsNumbersAwaitingContractManagerAssignment();
        
        for (String documentNumber : documentsNumbersAwaitingContractManagerAssignment) {
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            requisitions = (List<RequisitionDocument>) businessObjectService.findMatching(RequisitionDocument.class, fieldValues);

            for (RequisitionDocument req : requisitions) {
                unassignedRequisitions.add(req);
            }
        }
        
        return unassignedRequisitions;   
    }

    /**
     * Gets a list of strings of document numbers from workflow documents where  
     * document status = 'P', 'F'  and document type = 'REQS'.  If appDocStatus status 
     * of 'Awaiting Contract Manager Assignment' then the document number is added to the list
     * 
     * NOTE: simplify using DocSearch lookup with AppDocStatus
     * 
     * @return list of documentNumbers to retrieve requisitions.
     */
    protected List<String> getDocumentsNumbersAwaitingContractManagerAssignment() {
        List<String> requisitionDocumentNumbers = new ArrayList<String>();
             
        DocumentSearchCriteria.Builder documentSearchCriteriaDTO = DocumentSearchCriteria.Builder.create();
        //Search for status of P and F and "Awaiting Contract Manager Assignment" application document status      
        documentSearchCriteriaDTO.setDocumentStatuses(Arrays.asList(DocumentStatus.PROCESSED, DocumentStatus.FINAL));
        documentSearchCriteriaDTO.setDocumentTypeName(PurapConstants.REQUISITION_DOCUMENT_TYPE);
        documentSearchCriteriaDTO.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
        documentSearchCriteriaDTO.setSaveName(null);
        
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(null, documentSearchCriteriaDTO.build());

        String documentHeaderId = null;

        for (DocumentSearchResult result : results.getSearchResults()) {
            requisitionDocumentNumbers.add(result.getDocument().getDocumentId());            
        }
        
        return requisitionDocumentNumbers;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.RequisitionService#getCountOfRequisitionsAwaitingContractManagerAssignment()
     */
    public int getCountOfRequisitionsAwaitingContractManagerAssignment() {
        List<RequisitionDocument> unassignedRequisitions = getRequisitionsAwaitingContractManagerAssignment();
        if (ObjectUtils.isNotNull(unassignedRequisitions)) {
            return unassignedRequisitions.size();
        }
        else {
            return 0;
        }
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

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

}
