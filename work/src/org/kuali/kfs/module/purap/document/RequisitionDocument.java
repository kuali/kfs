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
import org.kuali.kfs.service.GeneralLedgerPendingEntryGenerationProcess;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.CapitalAssetSystemType;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurapUser;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
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
 * Document class for the Requisition.
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
    private String capitalAssetSystemTypeCode;
    
    private CapitalAssetSystemType capitalAssetSystemType;
    
    private final static String REQUESITION_GL_POSTING_HELPER_BEAN_ID = "kfsDoNothingGeneralLedgerPostingHelper";
    
    /**
     * Default constructor.
     */
    public RequisitionDocument() {
        super();
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    /**
     * Performs logic needed to initiate Requisition Document.
     */
    public void initiateDocument() {
        this.setRequisitionSourceCode(PurapConstants.RequisitionSources.STANDARD_ORDER);
        this.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);
        this.setPurchaseOrderCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        this.setPurchaseOrderTransmissionMethodCode(determinePurchaseOrderTransmissionMethod());

        // set the default funding source
        this.setFundingSourceCode(SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), PurapConstants.DEFAULT_FUNDING_SOURCE));

        PurapUser currentUser = (PurapUser) GlobalVariables.getUserSession().getUniversalUser().getModuleUser(PurapUser.MODULE_ID);
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganizationCode());
        this.setDeliveryCampusCode(currentUser.getUniversalUser().getCampusCode());
        this.setRequestorPersonName(currentUser.getUniversalUser().getPersonName());
        this.setRequestorPersonEmailAddress(currentUser.getUniversalUser().getPersonEmailAddress());
        this.setRequestorPersonPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(currentUser.getUniversalUser().getPersonLocalPhoneNumber()));

        // set the APO limit
        this.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));

        // populate billing address
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setBillingCampusCode(this.getDeliveryCampusCode());
        Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
        billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);
        this.templateBillingAddress(billingAddress);
        
        // populate receiving address with the default one for the chart/org
        loadReceivingAddress();
        
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.refreshNonUpdateableReferences();
    }

    /**
     * Determines what PO transmission method to use.
     * 
     * @return the PO PO transmission method to use.
     */
    private String determinePurchaseOrderTransmissionMethod() {
        // KULPURAP-826: Return a value based on a sys param. Perhaps later change it to more dynamic logic
        return SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), PurapParameterConstants.PURAP_DEFAULT_PO_TRANSMISSION_CODE);
    }

    /**
     * Checks whether copying of this document should be allowed. Copying is not allowed if this is a B2B requistion, and more than
     * a set number of days have passed since the document's creation.
     * 
     * @return True if copying of this requisition is allowed.
     * @see org.kuali.core.document.Document#getAllowsCopy()
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
            String allowedCopyDays = SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), PurapConstants.B2_B_ALLOW_COPY_DAYS);
            c.add(Calendar.DATE, Integer.parseInt(allowedCopyDays));
            Date allowedCopyDate = c.getTime();
            Date currentDate = dateTimeService.getCurrentDate();

            // Return true if the current time is before the allowed copy date.
            allowsCopy = (dateTimeService.dateDiff(currentDate, allowedCopyDate, false) > 0);
        }
        return allowsCopy;
    }

    /**
     * Performs logic needed to copy Requisition Document.
     * 
     * @see org.kuali.core.document.Document#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, ValidationException {
        // Need to clear this identifier before copy so that related documents appear to be none
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(null);
        super.toCopy();
        PurapUser currentUser = (PurapUser) GlobalVariables.getUserSession().getUniversalUser().getModuleUser(PurapUser.MODULE_ID);
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
        if (!(vendorContract != null && today.after(vendorContract.getVendorContractBeginningDate()) && today.before(vendorContract.getVendorContractEndDate()))) {
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
        this.setOrganizationAutomaticPurchaseOrderLimit(null);
        this.setPurchaseOrderAutomaticIndicator(false);

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

    /**
     * Updates status of this document and saves it.
     * 
     * @param statusCode the status code of the current status.
     */
    private void updateStatusAndSave(String statusCode) {
        SpringContext.getBean(PurapService.class).updateStatus(this, statusCode);
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
                updateStatusAndSave(newRequisitionStatus);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    if (StringUtils.isNotBlank(currentNode.getDisapprovedStatusCode())) {
                        updateStatusAndSave(currentNode.getDisapprovedStatusCode());
                        return;
                    }
                }
                logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                updateStatusAndSave(RequisitionStatuses.CANCELLED);
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
                            updateStatusAndSave(currentNode.getAwaitingStatusCode());
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

    public String getVendorPaymentTermsCode() {
        if (getVendorContract() != null) {
            return getVendorContract().getVendorPaymentTermsCode();
        }
        else if (getVendorDetail() != null) {
            return getVendorDetail().getVendorPaymentTermsCode();
        }
        return "";
    }

    public String getVendorShippingPaymentTermsCode() {
        if (getVendorContract() != null) {
            return getVendorContract().getVendorShippingPaymentTermsCode();
        }
        else if (getVendorDetail() != null) {
            return getVendorDetail().getVendorShippingPaymentTermsCode();
        }
        return "";
    }

    public String getVendorShippingTitleCode() {
        if (getVendorContract() != null) {
            return getVendorContract().getVendorShippingTitleCode();
        }
        else if (getVendorDetail() != null) {
            return getVendorDetail().getVendorShippingTitleCode();
        }
        return "";
    }

    public String getRequisitionOrganizationReference1Text() {
        return requisitionOrganizationReference1Text;
    }

    public void setRequisitionOrganizationReference1Text(String requisitionOrganizationReference1Text) {
        this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
    }

    public String getRequisitionOrganizationReference2Text() {
        return requisitionOrganizationReference2Text;
    }

    public void setRequisitionOrganizationReference2Text(String requisitionOrganizationReference2Text) {
        this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
    }

    public String getRequisitionOrganizationReference3Text() {
        return requisitionOrganizationReference3Text;
    }

    public void setRequisitionOrganizationReference3Text(String requisitionOrganizationReference3Text) {
        this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
    }

    public String getAlternate1VendorName() {
        return alternate1VendorName;
    }

    public void setAlternate1VendorName(String alternate1VendorName) {
        this.alternate1VendorName = alternate1VendorName;
    }

    public String getAlternate2VendorName() {
        return alternate2VendorName;
    }

    public void setAlternate2VendorName(String alternate2VendorName) {
        this.alternate2VendorName = alternate2VendorName;
    }

    public String getAlternate3VendorName() {
        return alternate3VendorName;
    }

    public void setAlternate3VendorName(String alternate3VendorName) {
        this.alternate3VendorName = alternate3VendorName;
    }

    public String getAlternate4VendorName() {
        return alternate4VendorName;
    }

    public void setAlternate4VendorName(String alternate4VendorName) {
        this.alternate4VendorName = alternate4VendorName;
    }

    public String getAlternate5VendorName() {
        return alternate5VendorName;
    }

    public void setAlternate5VendorName(String alternate5VendorName) {
        this.alternate5VendorName = alternate5VendorName;
    }

    public KualiDecimal getOrganizationAutomaticPurchaseOrderLimit() {
        return organizationAutomaticPurchaseOrderLimit;
    }

    public void setOrganizationAutomaticPurchaseOrderLimit(KualiDecimal organizationAutomaticPurchaseOrderLimit) {
        this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
    }

    /**
     * Gets the capitalAssetSystemTypeCode attribute. 
     * @return Returns the capitalAssetSystemTypeCode.
     */
    public String getCapitalAssetSystemTypeCode() {
        return capitalAssetSystemTypeCode;
    }

    /**
     * Sets the capitalAssetSystemTypeCode attribute value.
     * @param capitalAssetSystemTypeCode The capitalAssetSystemTypeCode to set.
     */
    public void setCapitalAssetSystemTypeCode(String capitalAssetSystemTypeCode) {
        this.capitalAssetSystemTypeCode = capitalAssetSystemTypeCode;
    }

    /**
     * Gets the capitalAssetSystemType attribute. 
     * @return Returns the capitalAssetSystemType.
     */
    public CapitalAssetSystemType getCapitalAssetSystemType() {
        return capitalAssetSystemType;
    }

    /**
     * Sets the capitalAssetSystemType attribute value.
     * @param capitalAssetSystemType The capitalAssetSystemType to set.
     * @deprecated
     */
    public void setCapitalAssetSystemType(CapitalAssetSystemType capitalAssetSystemType) {
        this.capitalAssetSystemType = capitalAssetSystemType;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return RequisitionItem.class;
    }

    /**
     * Returns null as requistion has no source document.
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * Returns null as requistion has no source document.
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String title = "";
        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(getClass(), PurapParameterConstants.PURAP_OVERRIDE_REQ_DOC_TITLE)) {
            String docIdStr = "";
            if ((this.getPurapDocumentIdentifier() != null) && (StringUtils.isNotBlank(this.getPurapDocumentIdentifier().toString()))) {
                docIdStr = "Requisition: " + this.getPurapDocumentIdentifier().toString();
            }
            String chartAcct = this.getFirstChartAccount();
            String chartAcctStr = (chartAcct == null ? "" : " - Account Number:  " + chartAcct);
            title = docIdStr + chartAcctStr;
        }
        else {
            title = super.getDocumentTitle();
        }
        return title;
    }

    /**
     * Gets this requisition's Chart/Account of the first accounting line from the first item.
     * 
     * @return The first Chart and Account, or an empty string if there is none.
     */
    private String getFirstChartAccount() {
        String chartAcct = null;
        RequisitionItem item = (RequisitionItem) this.getItem(0);
        if (ObjectUtils.isNotNull(item)) {
            if (item.getSourceAccountingLines().size() > 0) {
                PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
                if (ObjectUtils.isNotNull(accountLine) && ObjectUtils.isNotNull(accountLine.getChartOfAccountsCode()) && ObjectUtils.isNotNull(accountLine.getAccountNumber())) {
                    chartAcct = accountLine.getChartOfAccountsCode() + "-" + accountLine.getAccountNumber();
                }
            }
        }
        return chartAcct;
    }

    public Date getCreateDate() {
        return this.getDocumentHeader().getWorkflowDocument().getCreateDate();
    }

    public String getUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
    }

    /**
     * Used for routing only.
     * 
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * Used for routing only.
     * 
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }
    
    /**
     * Returns an instance of org.kuali.module.purap.service.impl.RequistionGeneralLedgerPostingHelperImpl, which will not generate GL pending entries for this document 
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostingHelper()
     */
    public GeneralLedgerPendingEntryGenerationProcess getGeneralLedgerPostingHelper() {
        Map<String, GeneralLedgerPendingEntryGenerationProcess> glPostingHelpers = SpringContext.getBeansOfType(GeneralLedgerPendingEntryGenerationProcess.class);
        return glPostingHelpers.get(RequisitionDocument.REQUESITION_GL_POSTING_HELPER_BEAN_ID);
    }
}
