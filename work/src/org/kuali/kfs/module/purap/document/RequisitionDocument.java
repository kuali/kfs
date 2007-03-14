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

package org.kuali.module.purap.document;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.Copyable;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.bo.SourceDocumentReference;
import org.kuali.module.purap.bo.VendorContract;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.service.PhoneNumberService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Requisition Document
 */
public class RequisitionDocument extends PurchasingDocumentBase implements Copyable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDocument.class);

	private String requisitionOrganizationReference1Text;
	private String requisitionOrganizationReference2Text;
	private String requisitionOrganizationReference3Text;
	private String alternate1VendorName;
	private String alternate2VendorName;
	private String alternate3VendorName;
	private String alternate4VendorName;
	private String alternate5VendorName;
	private KualiDecimal organizationAutomaticPurchaseOrderLimit;

    private PhoneNumberService phoneNumberService;

	/**
	 * Default constructor.
	 */
	public RequisitionDocument() {
        super();
        /*
        SourceDocumentReference sourceDocumentReference = new SourceDocumentReference();
        
        sourceDocumentReference.setSourceDocumentIdentifier(this.getIdentifier());
        String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(this.getClass());
        String documentTypeCode = SpringServiceLocator.getDataDictionaryService().getDocumentTypeCodeByTypeName(documentTypeName);
        sourceDocumentReference.setSourceFinancialDocumentTypeCode(documentTypeCode);
        */
        // This line is giving this error:
        /*
        javax.servlet.ServletException: OJB operation; SQL []; ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
        ; nested exception is java.sql.SQLException: ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
        */
        //sourceDocumentReference.setSourceDocumentObjectIdentifier(this.getObjectId());
        /*
        String ObjID = this.getObjectId();
        sourceDocumentReference.setSourceDocumentObjectIdentifier("objectID");
        sourceDocumentReferences = new TypedArrayList(SourceDocumentReference.class);
        sourceDocumentReferences.add(sourceDocumentReference);

      */
        
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    /**
     * Perform logic needed to initiate Requisition Document
     */
    public void initiateDocument() {

        this.setRequisitionSourceCode( PurapConstants.RequisitionSources.STANDARD_ORDER );
        this.setStatusCode( PurapConstants.RequisitionStatuses.IN_PROCESS );
        this.setPurchaseOrderCostSourceCode( PurapConstants.POCostSources.ESTIMATE );
        this.setPurchaseOrderTransmissionMethodCode( PurapConstants.POTransmissionMethods.FAX );

        // set the default funding source
        this.setFundingSourceCode(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("PurapAdminGroup","PURAP.REQUISITION_DEFAULT_FUNDING_SOURCE"));

        ChartUser currentUser = (ChartUser)GlobalVariables.getUserSession().getUniversalUser().getModuleUser( ChartUser.MODULE_ID );
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganizationCode());
        this.setDeliveryCampusCode(currentUser.getUniversalUser().getCampusCode());
        this.setRequestorPersonName(currentUser.getUniversalUser().getPersonName());
        this.setRequestorPersonEmailAddress(currentUser.getUniversalUser().getPersonEmailAddress());
        this.phoneNumberService = SpringServiceLocator.getPhoneNumberService();
        this.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(currentUser.getUniversalUser().getPersonLocalPhoneNumber()));
        
        // set the APO limit
        this.setOrganizationAutomaticPurchaseOrderLimit(SpringServiceLocator.getRequisitionService().getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setBillingCampusCode(this.getDeliveryCampusCode());
        Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(billingAddress);
        billingAddress = (BillingAddress) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BillingAddress.class, keys);
        this.templateBillingAddress(billingAddress);

// TODO  WAIT ON ITEM LOGIC  (CHRIS AND DAVID SHOULD FIX THIS HERE)
//        // add new item for freight
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_FREIGHT_CODE);
//
//        // add new item for s&h
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_SHIP_AND_HAND_CODE);
//
//        // add new item for full order discount
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE);
//
//        // add new item for trade in
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_TRADE_IN_CODE);

//        Integer numRows = applicationSettingService.getInt("DEFAULT_ITEM_ROWS");
//        for (int i = 0; i < numRows.intValue(); i++) {
//            Integer nextLineNum = r.getNextItemLineNumber();
//            ItemType iT = getItemType();
//            addNewItem(r, iT, nextLineNum);
//        }

        this.refreshAllReferences();
    }

    /**
     * A method in which to do checks as to whether copying of this document should be allowed.
     * Copying is not to be allowed if this is a B2B req. and more than a set number of days
     * have passed since the document's creation.
     * 
     * @return  True if copying of this requisition is allowed.
     * @see     org.kuali.core.document.Document#getAllowsCopy()
     */
    @Override
    public boolean getAllowsCopy() {
        boolean allowsCopy = super.getAllowsCopy();
        if (this.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            String allowedCopyDays = (new Integer(PurapConstants.REQ_B2B_ALLOW_COPY_DAYS)).toString();

            Calendar c = Calendar.getInstance();
            DocumentHeader dh = this.getDocumentHeader();
            KualiWorkflowDocument wd = dh.getWorkflowDocument();
            Timestamp createDate = (Timestamp) wd.getCreateDate();
            c.setTime(createDate);
            c.set(Calendar.HOUR, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            c.add(Calendar.DATE, Integer.parseInt(allowedCopyDays));
            // The allowed copy date is the document creation date plus a set number of days.
            Timestamp allowedCopyDate = new Timestamp(c.getTime().getTime());

            Calendar c2 = Calendar.getInstance();
            c2.setTime(SpringServiceLocator.getDateTimeService().getCurrentDate());
            c2.set(Calendar.HOUR, 11);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.set(Calendar.MILLISECOND, 59);
            c2.set(Calendar.AM_PM, Calendar.PM);
            Timestamp testTime = new Timestamp(c2.getTime().getTime());

            // Return true if the current time is before the allowed copy date.
            allowsCopy = (testTime.compareTo(allowedCopyDate) <= 0);
        }
        return allowsCopy;
    }

    /**
     * Perform logic needed to copy Requisition Document
     */
    @Override
    public void toCopy() throws WorkflowException, ValidationException {
        super.toCopy();

        ChartUser currentUser = (ChartUser)GlobalVariables.getUserSession().getUniversalUser().getModuleUser( ChartUser.MODULE_ID );

        this.setPurapDocumentIdentifier(null);

        // Set req status to INPR.
        this.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);

        // Set fields from the user.
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganizationCode());

        this.setPostingYear(SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear());

        boolean activeVendor = true;
        boolean activeContract = true;

        Date today = SpringServiceLocator.getDateTimeService().getCurrentDate();

        VendorContract vendorContract = new VendorContract();
        vendorContract.setVendorContractGeneratedIdentifier(this.getVendorContractGeneratedIdentifier());
        Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(vendorContract);
        vendorContract = (VendorContract) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(VendorContract.class, keys);
        if (!(vendorContract != null && 
                today.after(vendorContract.getVendorContractBeginningDate()) && 
                today.before(vendorContract.getVendorContractEndDate()))) {
            activeContract = false;
        }

        VendorDetail vendorDetail = SpringServiceLocator.getVendorService().getVendorDetail(this.getVendorHeaderGeneratedIdentifier(), this.getVendorDetailAssignedIdentifier());
        if (!(vendorDetail != null && vendorDetail.isActiveIndicator())) {
            activeVendor = false;
        }

        // B2B - only copy if contract and vendor are both active (throw separate errors to print to screen)
        if (this.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            if (!activeContract) {
                throw new ValidationException(PurapKeyConstants.ERROR_REQ_COPY_EXPIRED_CONTRACT);
            }
            if (!activeVendor) {
                throw new ValidationException(PurapKeyConstants.ERROR_REQ_COPY_INACTIVE_VENDOR);
            }
        }

        if (!activeVendor) {
            this.setVendorHeaderGeneratedIdentifier(null);
            this.setVendorDetailAssignedIdentifier(null);
            this.setVendorContractGeneratedIdentifier(null);
        }
        if (!activeContract) {
            this.setVendorContractGeneratedIdentifier(null);
        }

        // These fields should not be set in this method; force to be null
        this.setVendorNoteText(null);
        this.setContractManagerCode(null);
        this.setInstitutionContactName(null);
        this.setInstitutionContactPhoneNumber(null);
        this.setInstitutionContactEmailAddress(null);
        this.setOrganizationAutomaticPurchaseOrderLimit(null);
        this.setPurchaseOrderAutomaticIndicator(false);
        this.setStatusHistories(null);
      
        //TODO WAIT ON ITEM LOGIC (CHRIS AND DAVID SHOULD FIX THIS HERE)
        //TODO what about id in items?  do we need to null them out?

      //Trade In and Discount Items are only available for B2B. If the Requisition
      //doesn't currently contain trade in and discount, we should add them in 
      //here (KULAPP 1715)
//      if (! EpicConstants.REQ_SOURCE_B2B.equals(req.getSource().getCode())) {
//        boolean containsFullOrderDiscount = req.containsFullOrderDiscount();
//        //If the po has not contained full order discount item, create and
//        //add the full order discount item to it.
//        if (! containsFullOrderDiscount) {
//          ItemType iT = (ItemType)(referenceService.getCode("ItemType",EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE));
//          RequisitionItem discountItem = new RequisitionItem(iT, EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_LINE_NUMBER, req);
//          newReq.getItems().add(discountItem);
//        }
//          
//        boolean containsTradeIn = req.containsTradeIn();
//        //If the po has not contained trade in item, create and
//        //add the trade in item to it.
//        if (! containsTradeIn) {
//          ItemType iT = (ItemType)(referenceService.getCode("ItemType",EpicConstants.ITEM_TYPE_TRADE_IN_CODE));
//          RequisitionItem tradeInItem = new RequisitionItem(iT, EpicConstants.ITEM_TYPE_TRADE_IN_LINE_NUMBER, req);
//          newReq.getItems().add(tradeInItem);
//        }
//      }

        this.setOrganizationAutomaticPurchaseOrderLimit(SpringServiceLocator.getRequisitionService().getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));
      
        this.refreshAllReferences();
	}

	/**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

        // DOCUMENT PROCESSED
        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            String newRequisitionStatus = PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN;
            if (SpringServiceLocator.getRequisitionService().isAutomaticPurchaseOrderAllowed(this)) {
                newRequisitionStatus = PurapConstants.RequisitionStatuses.CLOSED;
                PurchaseOrderDocument poDocument = SpringServiceLocator.getPurchaseOrderService().createPurchaseOrderDocument(this);
                try {
                    poDocument = (PurchaseOrderDocument)SpringServiceLocator.getDocumentService().routeDocument(poDocument, null, null);
                }
                catch (WorkflowException e) {
                    LOG.error("Error routing PO document: " + e.getMessage());
                    throw new RuntimeException("Error routing PO document: " + e.getMessage());
                }
            }
            SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(this, newRequisitionStatus);
            SpringServiceLocator.getRequisitionService().save(this);
        }
        // DOCUMENT DISAPPROVED
        else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            // TODO set REQ status to disapproved - based on route level
//            SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(this, ??);
//            SpringServiceLocator.getRequisitionService().save(this);
        }
        // DOCUMENT CANCELED
        else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(this, PurapConstants.RequisitionStatuses.CANCELLED);
            SpringServiceLocator.getRequisitionService().save(this);
        }


    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();

        //on level change alter EPIC status of document - possibly clear & reset attributes
//        DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(levelChangeEvent.getRouteHeaderId());
//
//        if (RoutingService.REQ_CONTENT_NODE_NAME.equalsIgnoreCase(levelChangeEvent.getNewNodeName())) {
//            getRequisitionPostProcessorService().changeRequisitionStatus(routeHeader.getRouteHeaderId(), EpicConstants.REQ_STAT_AWAIT_CONTENT_APRVL, getLastUserId(routeHeader));
//        }
//        if (RoutingService.REQ_SUB_ACCT_NODE_NAME.equalsIgnoreCase(levelChangeEvent.getNewNodeName())) {
//            getRequisitionPostProcessorService().changeRequisitionStatus(routeHeader.getRouteHeaderId(), EpicConstants.REQ_STAT_AWAIT_SUB_ACCT_APRVL, getLastUserId(routeHeader));
//        }
//        if (RoutingService.REQ_FISCAL_OFFICER_NODE_NAME.equalsIgnoreCase(levelChangeEvent.getNewNodeName())) {
//            getRequisitionPostProcessorService().changeRequisitionStatus(routeHeader.getRouteHeaderId(), EpicConstants.REQ_STAT_AWAIT_FISCAL_APRVL, getLastUserId(routeHeader));
//        }
//        if (RoutingService.REQ_CHART_ORG_NODE_NAME.equalsIgnoreCase(levelChangeEvent.getNewNodeName())) {
//            getRequisitionPostProcessorService().changeRequisitionStatus(routeHeader.getRouteHeaderId(), EpicConstants.REQ_STAT_AWAIT_CHART_APRVL, getLastUserId(routeHeader));          
//        }
//        if (RoutingService.REQ_SOD_NODE_NAME.equalsIgnoreCase(levelChangeEvent.getNewNodeName())){
//            getRequisitionPostProcessorService().changeRequisitionStatus(routeHeader.getRouteHeaderId(), EpicConstants.REQ_STAT_AWAIT_SEP_OF_DUTY_APRVL, getLastUserId(routeHeader));
//            
//            //test for separation of duties, if fail send to exception routing
//            String initiatorId = routeHeader.getInitiatorUser().getAuthenticationUserId().getAuthenticationId();
//            int numberOfApprovals = 0;
//            String approver = null;
//            for (Iterator iter = routeHeader.getActionsTaken().iterator(); iter.hasNext();) {
//                ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
//                if(actionTaken.getActionTaken().equals(EdenConstants.ACTION_TAKEN_APPROVED_CD)){
//                    numberOfApprovals++;
//                    approver = actionTaken.getWorkflowUser().getAuthenticationUserId().getAuthenticationId();
//                }
//            }
//            
//            if(getRequisitionPostProcessorService().failsSeparationOfDuties(routeHeader.getRouteHeaderId(), initiatorId, approver, numberOfApprovals)){
//                WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiatorId), routeHeader.getRouteHeaderId());
//                document.clearAttributeContent();
//                document.addAttributeDefinition(new SeparationDefinition("fails"));
//                document.saveRoutingData();
//            }
//            
//        }

    }

    // SETTERS AND GETTERS
	/**
	 * Gets the requisitionOrganizationReference1Text attribute.
	 * 
	 * @return Returns the requisitionOrganizationReference1Text
	 * 
	 */
	public String getRequisitionOrganizationReference1Text() { 
		return requisitionOrganizationReference1Text;
	}

	/**
	 * Sets the requisitionOrganizationReference1Text attribute.
	 * 
	 * @param requisitionOrganizationReference1Text The requisitionOrganizationReference1Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference1Text(String requisitionOrganizationReference1Text) {
		this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
	}


	/**
	 * Gets the requisitionOrganizationReference2Text attribute.
	 * 
	 * @return Returns the requisitionOrganizationReference2Text
	 * 
	 */
	public String getRequisitionOrganizationReference2Text() { 
		return requisitionOrganizationReference2Text;
	}

	/**
	 * Sets the requisitionOrganizationReference2Text attribute.
	 * 
	 * @param requisitionOrganizationReference2Text The requisitionOrganizationReference2Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference2Text(String requisitionOrganizationReference2Text) {
		this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
	}


	/**
	 * Gets the requisitionOrganizationReference3Text attribute.
	 * 
	 * @return Returns the requisitionOrganizationReference3Text
	 * 
	 */
	public String getRequisitionOrganizationReference3Text() { 
		return requisitionOrganizationReference3Text;
	}

	/**
	 * Sets the requisitionOrganizationReference3Text attribute.
	 * 
	 * @param requisitionOrganizationReference3Text The requisitionOrganizationReference3Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference3Text(String requisitionOrganizationReference3Text) {
		this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
	}



	/**
	 * Gets the alternate1VendorName attribute.
	 * 
	 * @return Returns the alternate1VendorName
	 * 
	 */
	public String getAlternate1VendorName() { 
		return alternate1VendorName;
	}

	/**
	 * Sets the alternate1VendorName attribute.
	 * 
	 * @param alternate1VendorName The alternate1VendorName to set.
	 * 
	 */
	public void setAlternate1VendorName(String alternate1VendorName) {
		this.alternate1VendorName = alternate1VendorName;
	}


	/**
	 * Gets the alternate2VendorName attribute.
	 * 
	 * @return Returns the alternate2VendorName
	 * 
	 */
	public String getAlternate2VendorName() { 
		return alternate2VendorName;
	}

	/**
	 * Sets the alternate2VendorName attribute.
	 * 
	 * @param alternate2VendorName The alternate2VendorName to set.
	 * 
	 */
	public void setAlternate2VendorName(String alternate2VendorName) {
		this.alternate2VendorName = alternate2VendorName;
	}


	/**
	 * Gets the alternate3VendorName attribute.
	 * 
	 * @return Returns the alternate3VendorName
	 * 
	 */
	public String getAlternate3VendorName() { 
		return alternate3VendorName;
	}

	/**
	 * Sets the alternate3VendorName attribute.
	 * 
	 * @param alternate3VendorName The alternate3VendorName to set.
	 * 
	 */
	public void setAlternate3VendorName(String alternate3VendorName) {
		this.alternate3VendorName = alternate3VendorName;
	}


	/**
	 * Gets the alternate4VendorName attribute.
	 * 
	 * @return Returns the alternate4VendorName
	 * 
	 */
	public String getAlternate4VendorName() { 
		return alternate4VendorName;
	}

	/**
	 * Sets the alternate4VendorName attribute.
	 * 
	 * @param alternate4VendorName The alternate4VendorName to set.
	 * 
	 */
	public void setAlternate4VendorName(String alternate4VendorName) {
		this.alternate4VendorName = alternate4VendorName;
	}


	/**
	 * Gets the alternate5VendorName attribute.
	 * 
	 * @return Returns the alternate5VendorName
	 * 
	 */
	public String getAlternate5VendorName() { 
		return alternate5VendorName;
	}

	/**
	 * Sets the alternate5VendorName attribute.
	 * 
	 * @param alternate5VendorName The alternate5VendorName to set.
	 * 
	 */
	public void setAlternate5VendorName(String alternate5VendorName) {
		this.alternate5VendorName = alternate5VendorName;
	}


	/**
	 * Gets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @return Returns the organizationAutomaticPurchaseOrderLimit
	 * 
	 */
	public KualiDecimal getOrganizationAutomaticPurchaseOrderLimit() { 
		return organizationAutomaticPurchaseOrderLimit;
	}

	/**
	 * Sets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @param organizationAutomaticPurchaseOrderLimit The organizationAutomaticPurchaseOrderLimit to set.
	 * 
	 */
	public void setOrganizationAutomaticPurchaseOrderLimit(KualiDecimal organizationAutomaticPurchaseOrderLimit) {
		this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
	}
	
    @Override
    public void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
            super.afterInsert(persistenceBroker);
            SourceDocumentReference sourceDocumentReference = new SourceDocumentReference();
            
            sourceDocumentReference.setSourceDocumentIdentifier(this.getPurapDocumentIdentifier());
            String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(this.getClass());
            String documentTypeCode = SpringServiceLocator.getDataDictionaryService().getDocumentTypeCodeByTypeName(documentTypeName);
            sourceDocumentReference.setSourceFinancialDocumentTypeCode(documentTypeCode);
            // This line is giving this error:
            /*
            javax.servlet.ServletException: OJB operation; SQL []; ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
            ; nested exception is java.sql.SQLException: ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
            */
            String ObjID = this.getObjectId();
            sourceDocumentReference.setSourceDocumentObjectIdentifier(this.getObjectId());
            //String ObjID = this.getObjectId();
           // sourceDocumentReference.setSourceDocumentObjectIdentifier("objectID");
            sourceDocumentReferences = new TypedArrayList(SourceDocumentReference.class);
            sourceDocumentReferences.add(sourceDocumentReference);
           // String ObjID = this.getObjectId();
            
     }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return RequisitionItem.class;
    }   
    
}

