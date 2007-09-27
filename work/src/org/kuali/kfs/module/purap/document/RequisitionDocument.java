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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ConciseXmlDocumentConversionService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurapUser;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.bo.RequisitionStatusHistory;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.PhoneNumberService;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
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

	/**
	 * Default constructor.
	 */
	public RequisitionDocument() {
        super();
    }

    //TODO uncomment once WorkflowXmlRequisitionDocument is filled in with fields
//    @Override
//    protected Document getDocumentRepresentationForSerialization() {
//        return SpringContext.getBean(ConciseXmlDocumentConversionService.class).getDocumentForSerialization(this);
//    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }
   
    /**
     * Perform logic needed to initiate Requisition Document
     */
    public void initiateDocument() {
        this.setRequisitionSourceCode( PurapConstants.RequisitionSources.STANDARD_ORDER );
        this.setStatusCode( PurapConstants.RequisitionStatuses.IN_PROCESS );
        this.setPurchaseOrderCostSourceCode( PurapConstants.POCostSources.ESTIMATE );
        this.setPurchaseOrderTransmissionMethodCode( determinePurchaseOrderTransmissionMethod() );

        // set the default funding source
        this.setFundingSourceCode(SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE, PurapConstants.Components.REQUISITION, PurapConstants.DEFAULT_FUNDING_SOURCE));

        PurapUser currentUser = (PurapUser)GlobalVariables.getUserSession().getUniversalUser().getModuleUser( PurapUser.MODULE_ID );
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganizationCode());
        this.setDeliveryCampusCode(currentUser.getUniversalUser().getCampusCode());
        this.setRequestorPersonName(currentUser.getUniversalUser().getPersonName());
        this.setRequestorPersonEmailAddress(currentUser.getUniversalUser().getPersonEmailAddress());
        this.setRequestorPersonPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(currentUser.getUniversalUser().getPersonLocalPhoneNumber()));
        
        // set the APO limit
        this.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setBillingCampusCode(this.getDeliveryCampusCode());
        Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
        billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);
        this.templateBillingAddress(billingAddress);

        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.refreshNonUpdateableReferences();
    }

    /**
     * This method determines what PO transmission method to use.
     *  
     * @return
     */
    private String determinePurchaseOrderTransmissionMethod(){
        //KULPURAP-826: Return a value based on a sys param. Perhaps later change it to more dynamic logic
        return SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE, PurapConstants.Components.REQUISITION, 
                PurapParameterConstants.PURAP_DEFAULT_PO_TRANSMISSION_CODE);
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
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);            
            Calendar c = Calendar.getInstance();
            DocumentHeader dh = this.getDocumentHeader();
            KualiWorkflowDocument wd = dh.getWorkflowDocument();
                       
            // The allowed copy date is the document creation date plus a set number of days.                      
            Date createDate = wd.getCreateDate();
            c.setTime(createDate);
            String allowedCopyDays = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE, PurapConstants.Components.REQUISITION, PurapConstants.B2_B_ALLOW_COPY_DAYS);
            c.add(Calendar.DATE, Integer.parseInt(allowedCopyDays));
            Date allowedCopyDate = c.getTime();

            Date currentDate = dateTimeService.getCurrentDate();
                       
            // Return true if the current time is before the allowed copy date.
            allowsCopy = (dateTimeService.dateDiff( currentDate, allowedCopyDate, false ) > 0);
        }
        return allowsCopy;
    }

    /**
     * Perform logic needed to copy Requisition Document
     */
    @Override
    public void toCopy() throws WorkflowException, ValidationException {
        // Need to clear this identifier before copy so that related documents appear to be none
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(null);
        
        super.toCopy();

        PurapUser currentUser = (PurapUser)GlobalVariables.getUserSession().getUniversalUser().getModuleUser( PurapUser.MODULE_ID );

        this.setPurapDocumentIdentifier(null);

        // Set req status to INPR.
        this.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);

        // Set fields from the user.
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganizationCode());

        this.setPostingYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        boolean activeVendor = true;
        boolean activeContract = true;

        Date today = SpringContext.getBean(DateTimeService.class).getCurrentDate();

        VendorContract vendorContract = new VendorContract();
        vendorContract.setVendorContractGeneratedIdentifier(this.getVendorContractGeneratedIdentifier());
        Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(vendorContract);
        vendorContract = (VendorContract) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(VendorContract.class, keys);
        if (!(vendorContract != null && 
                today.after(vendorContract.getVendorContractBeginningDate()) && 
                today.before(vendorContract.getVendorContractEndDate()))) {
            activeContract = false;
        }

        VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(this.getVendorHeaderGeneratedIdentifier(), this.getVendorDetailAssignedIdentifier());
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
        
        // Fill the BO Notes with an empty List.
        this.setBoNotes(new ArrayList());
      
        for (Iterator iter = this.getItems().iterator(); iter.hasNext();) {
            RequisitionItem item = (RequisitionItem) iter.next();
            item.setPurapDocumentIdentifier(null);
            item.setItemIdentifier(null);
            for (Iterator acctIter = item.getSourceAccountingLines().iterator(); acctIter.hasNext();) {
                RequisitionAccount account = (RequisitionAccount) acctIter.next();
                account.setAccountIdentifier(null);
                account.setItemIdentifier(null);
            }
        }
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);

        this.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));
      
        this.refreshNonUpdateableReferences();
	}

    private void updateStatusAndStatusHistoryAndSave(String statusCode) {
        SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, statusCode);
        SpringContext.getBean(RequisitionService.class).saveDocumentWithoutValidation(this);
    }
    
	/**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();
        try {
            // DOCUMENT PROCESSED
            if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                String newRequisitionStatus = PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN;
                if (SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(this)) {
                    newRequisitionStatus = PurapConstants.RequisitionStatuses.CLOSED;
                    SpringContext.getBean(PurchaseOrderService.class).createAutomaticPurchaseOrderDocument(this);
                }
                updateStatusAndStatusHistoryAndSave(newRequisitionStatus);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    if (StringUtils.isNotBlank(currentNode.getDisapprovedStatusCode())) {
                        updateStatusAndStatusHistoryAndSave(currentNode.getDisapprovedStatusCode());
                        return;
                    }
                }
                // TODO PURAP/delyea - what to do in a disapproval where no status to set exists?
                logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                updateStatusAndStatusHistoryAndSave(RequisitionStatuses.CANCELLED);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
        LOG.debug("handleRouteStatusChange() ending");
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO change) {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange(change);
        try {
            String newNodeName = change.getNewNodeName();
            if (StringUtils.isNotBlank(newNodeName)) {
                ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(Long.valueOf(getDocumentNumber()));
                reportCriteriaVO.setTargetNodeName(newNodeName);
                if (SpringContext.getBean(KualiWorkflowInfo.class).documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[] { EdenConstants.ACTION_REQUEST_APPROVE_REQ, EdenConstants.ACTION_REQUEST_COMPLETE_REQ })) {
                    NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(newNodeName);
                    if (ObjectUtils.isNotNull(currentNode)) {
                        if (StringUtils.isNotBlank(currentNode.getAwaitingStatusCode())) {
                            updateStatusAndStatusHistoryAndSave(currentNode.getAwaitingStatusCode());
                        }
                    }
                }
                else {
                    LOG.debug("Document with id " + getDocumentNumber() + " will not stop in route node '" + newNodeName + "'");
                }
            }
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Error found checking actions requests on document with id " + getDocumentNumber() + ". *** WILL NOT UPDATE PURAP STATUS ***";
            LOG.warn(errorMsg, e);
        }
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus ) {
        RequisitionStatusHistory rsh = new RequisitionStatusHistory( oldStatus, newStatus );
        this.getStatusHistories().add( rsh );
    }

    // SETTERS AND GETTERS
    public String getVendorPaymentTermsCode() {
        if (ObjectUtils.isNotNull(getVendorDetail())) {
            return getVendorDetail().getVendorPaymentTermsCode();
        }
        return "";
    }

    public String getVendorShippingPaymentTermsCode() {
        if (ObjectUtils.isNotNull(getVendorDetail())) {
            return getVendorDetail().getVendorShippingPaymentTermsCode();
        }
        return "";
    }

    public String getVendorShippingTitleCode() {
        if (ObjectUtils.isNotNull(getVendorDetail())) {
            return getVendorDetail().getVendorShippingTitleCode();
        }
        return "";
    }

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
    public List<RequisitionView> getRelatedRequisitionViews() {
        return null;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return RequisitionItem.class;
    }
    
    /**
     * METHOD WILL RETURN NULL AS REQUISITION HAS NO SOURCE DOCUMENT
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * METHOD WILL RETURN NULL AS REQUISITION HAS NO SOURCE DOCUMENT
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        // TODO Auto-generated method stub
//        return RequisitionAccount.class;
//    }

    /**
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String title = "";
        String specificTitle = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE,PurapConstants.Components.REQUISITION, PurapParameterConstants.PURAP_OVERRIDE_REQ_DOC_TITLE);
        if (StringUtils.equalsIgnoreCase(specificTitle,Boolean.TRUE.toString())) {
            String docIdStr = "";
            if ( (this.getPurapDocumentIdentifier() != null) && (StringUtils.isNotBlank(this.getPurapDocumentIdentifier().toString())) ) {
                docIdStr = "Requisition: " + this.getPurapDocumentIdentifier().toString();
            }
            String chartAcct = this.getFirstChartAccount();
            String chartAcctStr = (chartAcct == null ? "" : " - Account Number:  "+chartAcct);
            title = docIdStr+chartAcctStr;
        }
        else {
            title = super.getDocumentTitle();
        }
        return title;
    }
    
    /**
     * This method gives this requisition's Chart/Account of the first accounting line from the
     * first item.
     * 
     * @return  The first Chart and Account, or an empty string if there is none.
     */
    private String getFirstChartAccount() {
        String chartAcct = null;
        RequisitionItem item = (RequisitionItem)this.getItem(0);
        if (ObjectUtils.isNotNull(item)) {
            PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
            if (ObjectUtils.isNotNull(accountLine) && 
                    ObjectUtils.isNotNull(accountLine.getChartOfAccountsCode()) && 
                    ObjectUtils.isNotNull(accountLine.getAccountNumber())) {
                chartAcct = accountLine.getChartOfAccountsCode()+"-"+accountLine.getAccountNumber();
            }
        }
        return chartAcct;
    }

    public Date getCreateDate(){
        return this.getDocumentHeader().getWorkflowDocument().getCreateDate();
    }
	
	public String getUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
    }
    
    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }
               
}

