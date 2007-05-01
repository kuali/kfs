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

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.OrganizationParameter;
import org.kuali.module.purap.dao.RequisitionDao;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RequisitionServiceImpl implements RequisitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private RequisitionDao requisitionDao;
    private VendorService vendorService;

    public void save(RequisitionDocument requisitionDocument) {
        requisitionDao.save(requisitionDocument);
    }
    
    public RequisitionDocument getRequisitionById(Integer id) {
        return requisitionDao.getRequisitionById(id);
    }
    
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org) {
        if ( ObjectUtils.isNull(chart) || ObjectUtils.isNull(org) ) {
            return null;
        }

        KualiDecimal purchaseOrderTotalLimit = SpringServiceLocator.getVendorService().getApoLimitFromContract(
          vendorContractGeneratedIdentifier, chart, org);
        
        if ( ObjectUtils.isNull(purchaseOrderTotalLimit)) {
        // We didn't find the limit on the vendor contract, get it from the org parameter table.
            OrganizationParameter organizationParameter = new OrganizationParameter();
            organizationParameter.setChartOfAccountsCode(chart);
            organizationParameter.setOrganizationCode(org);
            Map orgParamKeys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(organizationParameter);
            organizationParameter = (OrganizationParameter) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(OrganizationParameter.class, orgParamKeys);
            purchaseOrderTotalLimit = organizationParameter.getOrganizationAutomaticPurchaseOrderLimit();
        }

        return purchaseOrderTotalLimit;
    }
    
//    public void disapproveRequisition(String docHeaderId, String level, String networkId) {
//        if(RoutingService.REQ_CONTENT_NODE_NAME.equalsIgnoreCase(level)){
//            //disapproved content
//            changeRequisitionStatus(docHeaderId, EpicConstants.REQ_STAT_DAPRVD_CONTENT, networkId);
//        }
//        else if(RoutingService.REQ_SUB_ACCT_NODE_NAME.equalsIgnoreCase(level)){
//            //disapproved subaccount
//            changeRequisitionStatus(docHeaderId, EpicConstants.REQ_STAT_DAPRVD_SUB_ACCT, networkId);
//        }
//        else if(RoutingService.REQ_FISCAL_OFFICER_NODE_NAME.equalsIgnoreCase(level)){
//            //disapproved fiscal
//            changeRequisitionStatus(docHeaderId, EpicConstants.REQ_STAT_DAPRVD_FISCAL, networkId);
//        }
//        else if(RoutingService.REQ_CHART_ORG_NODE_NAME.equalsIgnoreCase(level)){
//            //disapprove base hierarchy
//            changeRequisitionStatus(docHeaderId, EpicConstants.REQ_STAT_DAPRVD_CHART, networkId);
//        }
//        else if(RoutingService.REQ_SOD_NODE_NAME.equalsIgnoreCase(level)){
//            changeRequisitionStatus(docHeaderId, EpicConstants.REQ_STAT_DAPRVD_SEP_OF_DUTY, networkId);
//        }
//    }
//        
//   
//    public boolean failsSeparationOfDuties(String docHeaderId, String networkId, String approverId, int approvals) {
//        LOG.debug("failsSeparationOfDuties() ");
//        boolean fails = false;
//        Requisition r = requisitionService.getRequisitionByDocumentId(docHeaderId);
//        BigDecimal totalCost = r.getTotalCost();
//        LOG.debug("failsSeparationOfDuties() - approvals = " + approvals);
//        LOG.debug("failsSeparationOfDuties() - total cost = " + totalCost);
//        LOG.debug("failsSeparationOfDuties() - ApproverID = " + networkId);
//        LOG.debug("failsSeparationOfDuties() - InitiatorID = " + approverId);
//        if((totalCost.compareTo(new BigDecimal(10000.00)) == 1) && (networkId.equals(approverId)) && (approvals <= 1)){
//          return true;
//        }
//        LOG.debug("failsSeparationOfDuties() return value: " + fails);
//        return fails;
//    }
//
    
    public boolean isAutomaticPurchaseOrderAllowed(RequisitionDocument requisition) {
      LOG.debug("isAutomaticPurchaseOrderAllowed() started");
      
      /*
       * The private checkAutomaticPurchaseOrderRules method contains rules to check 
       * if a requisition can become an APO (Automatic Purchase Order).  The method 
       * returns a string containing the reason why this method should return false.  
       * Save the reason as a note on the requisition.
       */
      String note = checkAutomaticPurchaseOrderRules(requisition);
      if (StringUtils.isNotEmpty(note)) {
        note = "Requisition did not become an APO because: " + note;
// TODO create new note on REQ with "note" that contains reason REQ didn't become an APO
//        DocumentNote docNote = new DocumentNote(docHeaderId, note, u);
//        //This has to changed because I'm changing requisitionService.saveRequisitionDocumentNote KULAPP 1293
//        //requisitionService.saveRequisitionDocumentNote(r.getId(), docNote, u);
//        requisitionService.saveRequisitionDocumentNote(docNote);
        LOG.debug("isAutomaticPurchaseOrderAllowed() return false; " + note);
        return false;
      }

      LOG.debug("isAutomaticPurchaseOrderAllowed() You made it!  Your REQ can become an APO; return true.");
      return true;
    }
    
    private String checkAutomaticPurchaseOrderRules(RequisitionDocument requisition) {
        String requisitionSource = requisition.getRequisitionSourceCode();
        KualiDecimal reqTotal = requisition.getTotalDollarAmount();
        KualiDecimal apoLimit = getApoLimit(requisition.getVendorContractGeneratedIdentifier(),
          requisition.getChartOfAccountsCode(), requisition.getOrganizationCode());
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

        if (reqTotal.compareTo(KFSConstants.ZERO) <= 0) {
            return "Requisition total is not greater than zero.";
        }

// TODO finish item logic (david and chris)
//      Collection items = requisition.getItems();
//      Iterator i = items.iterator();
//      while (i.hasNext()) {
//        RequisitionItem item = (RequisitionItem)i.next();
//        if (item.getRestricted().booleanValue()) {
//          return "Requisition contains an item that is marked as restricted.";
//        }
//        if (EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE.equals(item.getItemType().getCode()) ||
//                EpicConstants.ITEM_TYPE_TRADE_IN_CODE.equals(item.getItemType().getCode())) {
//          if ( (item.getUnitPrice() != null) && ((zero.compareTo(item.getUnitPrice())) != 0) ) {
//            // discount or trade-in item has unit price that is not empty or zero
//            return "Requisition contains a " + item.getItemType().getDescription() + " item, so it does not qualify as an APO.";
//          }
//        }
//        if (!EpicConstants.REQ_SOURCE_B2B.equals(requisitionSource)) {
//          Collection accounts = item.getAccounts();
//          Iterator a = accounts.iterator();
//          while (a.hasNext()) {
//            RequisitionAccount account = (RequisitionAccount)a.next();
//            ObjectCode code = chartOfAccountsService.getObjectCode(account.getFinancialChartOfAccountsCode(), 
//                account.getFinancialObjectCode(), requisition.getPurchaseOrderEncumbranceFiscalYear());
//            if (EpicConstants.FIS_CAPITAL_ASSET_OBJECT_LEVEL_CODE.equals(code.getFinancialObjectLevelCode())) {
//              return "Standard requisition with a capital asset object code.";
//            }
//          }//endwhile accounts
//        }
//      }//endwhile items

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

//TODO fix this
//            Boolean vendorRestricted = vendorDetail.getVendorRestrictedIndicator();
//            if (vendorRestricted != requisition.getVendorRestrictedIndicator()) {
//                // restricted status of vendor has changed; save new status to requisition 
//                requisition.setVendorRestrictedIndicator(vendorDetail.getVendorRestrictedIndicator());
//                save(requisition);
//            }
            if (requisition.getVendorRestrictedIndicator()) {
                return "Selected vendor is marked as restricted.";
            }

            if ((!PurapConstants.RequisitionSources.B2B.equals(requisitionSource)) && (requisition.getVendorContractGeneratedIdentifier() == null)) {
                // TODO check what campus we should be using:  REQ initiator?  doc initiator?  delivery campus?
                VendorContract b2bContract = vendorService.getVendorB2BContract(vendorDetail, requisition.getDeliveryCampusCode());
                if (b2bContract != null) {
                    return "Standard requisition with no contract selected but a B2B contract exists for the selected vendorequisition.";
                }
            }
        }

        if (StringUtils.isNotEmpty(requisition.getRecurringPaymentTypeCode())) {
            return "Payment type is marked as recurring.";
        }

        if ((requisition.getPurchaseOrderTotalLimit() != null) && (KFSConstants.ZERO.compareTo(requisition.getPurchaseOrderTotalLimit()) != 0)) {
            LOG.debug("isAPO() po total limit is not null or not equal to zero; return false.");
            return "The 'PO not to exceed' amount has been entered.";
        }

        if (StringUtils.isNotEmpty(requisition.getAlternate1VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate2VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate3VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate4VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate5VendorName())) {
            LOG.debug("isAPO() alternate vendor name exists; return false.");
            return "Requisition contains alternate vendor names.";
        }

        // TODO need to implement validateCapitalASsetNumbersForAPO
//        if( !validateCapitalAssetNumbersForAPO( requisition ) ) {
//            return "Requisition did not pass CAMS validation.";
//        }

// TODO finish converting logic
        Calendar today = dateTimeService.getCurrentCalendar(); 
//      Calendar allowApoDate = chartOfAccountsService.getDateRangeWithClosingDate(-appSettingService.getInt("ALLOW_APO_NEXT_FY_DAYS").intValue());
//      Integer currentFY = chartOfAccountsService.getFiscalPeriodForToday().getUniversityFiscalYear();
//      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//      LOG.debug("isApo() req FY = " + requisition.getPurchaseOrderEncumbranceFiscalYear() + " and currentFY = " + currentFY);
//      LOG.debug("isApo() today = " + sdf.format(today.getTime()) + " and allowApoDate = " + sdf.format(allowApoDate.getTime()));
//
//      if (requisition.getPurchaseOrderEncumbranceFiscalYear().compareTo(currentFY) > 0 &&
//          today.before(allowApoDate)) {
//        return "Requisition is set to encumber next fiscal year and approval is not within APO allowed date range.";
//      }

        return "";
    }
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;    
    }

    public void setRequisitionDao(RequisitionDao requisitionDao) {
        this.requisitionDao = requisitionDao;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;    
    }

}

