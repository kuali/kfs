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

package org.kuali.kfs.module.purap.document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.DefaultPrincipalAddress;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseRequisitionItemUseTax;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.businessobject.options.RequisitionStatusValuesFinder;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Document class for the Requisition.
 */
public class RequisitionDocument extends PurchasingDocumentBase implements Copyable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDocument.class);

    protected String requisitionOrganizationReference1Text;
    protected String requisitionOrganizationReference2Text;
    protected String requisitionOrganizationReference3Text;
    protected String alternate1VendorName;
    protected String alternate2VendorName;
    protected String alternate3VendorName;
    protected String alternate4VendorName;
    protected String alternate5VendorName;
    protected KualiDecimal organizationAutomaticPurchaseOrderLimit;
    protected List reqStatusList;

    // non-persistent property used for controlling validation for accounting lines when doc is request for blanket approve.
    protected boolean isBlanketApproveRequest = false;

    /**
     * Default constructor.
     */
    public RequisitionDocument() {
        super();
    }

    @Override
    public PurchasingDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(RequisitionService.class);
    }

    /**
     * Provides answers to the following splits:
     * AmountRequiresSeparationOfDutiesReview
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.HAS_ACCOUNTING_LINES)) {
            return !isMissingAccountingLines();
        }
        if (nodeName.equals(PurapWorkflowConstants.AMOUNT_REQUIRES_SEPARATION_OF_DUTIES_REVIEW_SPLIT)) {
            return isSeparationOfDutiesReviewRequired();
        }
        return super.answerSplitNodeQuestion(nodeName);
    }

    protected boolean isMissingAccountingLines() {
        for (Iterator iterator = getItems().iterator(); iterator.hasNext();) {
            RequisitionItem item = (RequisitionItem) iterator.next();
            if (item.isConsideredEntered() && item.isAccountListEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected boolean isSeparationOfDutiesReviewRequired() {
        try {
            Set<Person> priorApprovers = this.getAllPriorApprovers();

            boolean noPriorApprover = (priorApprovers.size() == 0);

            // if there are more than 0 prior approvers which means there had been at least another approver than the current approver
            // then no need for separation of duties
            if (priorApprovers.size() > 0) {
                return false;
            }

            //If there was no prior approver, then we have to check the amounts to determine whether to route to separation of duties,
            //as mentioned below.
            if (noPriorApprover) {
                ParameterService parameterService = SpringContext.getBean(ParameterService.class);
                KualiDecimal maxAllowedAmount = new KualiDecimal(parameterService.getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.SEPARATION_OF_DUTIES_DOLLAR_AMOUNT));
                // if app param amount is greater than or equal to documentTotalAmount... no need for separation of duties
                KualiDecimal totalAmount = getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount();
                if (ObjectUtils.isNotNull(maxAllowedAmount) && ObjectUtils.isNotNull(totalAmount) && (maxAllowedAmount.compareTo(totalAmount) >= 0)) {
                    return false;
                }
                else {
                    return true;
                }
            }

        }
        catch (WorkflowException we) {
            LOG.error("Exception while attempting to retrieve all prior approvers from workflow: ", we);
        }

        return false;

    }

    public Set<Person> getAllPriorApprovers() throws WorkflowException {
        PersonService personService = KimApiServiceLocator.getPersonService();
         List<ActionTaken> actionsTaken = getDocumentHeader().getWorkflowDocument().getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();

        for (ActionTaken actionTaken : actionsTaken) {
            if (KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken().getCode())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to add the criteria
     * specific to Requisition Document.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        if ( isPostingYearPrior() &&
             ( getApplicationDocumentStatus().equals(PurapConstants.RequisitionStatuses.APPDOC_CLOSED) ||
                     getApplicationDocumentStatus().equals(PurapConstants.RequisitionStatuses.APPDOC_CANCELLED) ) )  {
               return false;
        }
        else {
            return true;
        }
    }

    /**
     * Performs logic needed to initiate Requisition Document.
     */
    public void initiateDocument() throws WorkflowException {
        this.setupAccountDistributionMethod();
        this.setRequisitionSourceCode(PurapConstants.RequisitionSources.STANDARD_ORDER);
        updateAndSaveAppDocStatus(PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS);
        this.setPurchaseOrderCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        this.setPurchaseOrderTransmissionMethodCode(determinePurchaseOrderTransmissionMethod());
        this.setDocumentFundingSourceCode(SpringContext.getBean(ParameterService.class).getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.DEFAULT_FUNDING_SOURCE));
        this.setUseTaxIndicator(SpringContext.getBean(PurchasingService.class).getDefaultUseTaxIndicatorValue(this));

        Person currentUser = GlobalVariables.getUserSession().getPerson();
        ChartOrgHolder purapChartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, PurapConstants.PURAP_NAMESPACE);
        if (ObjectUtils.isNotNull(purapChartOrg)) {
            this.setChartOfAccountsCode(purapChartOrg.getChartOfAccountsCode());
            this.setOrganizationCode(purapChartOrg.getOrganizationCode());
        }
        this.setDeliveryCampusCode(currentUser.getCampusCode());
        this.setDeliveryToName(currentUser.getName());
        this.setDeliveryToEmailAddress(currentUser.getEmailAddressUnmasked());
        this.setDeliveryToPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(currentUser.getPhoneNumber()));
        this.setRequestorPersonName(currentUser.getName());
        this.setRequestorPersonEmailAddress(currentUser.getEmailAddressUnmasked());
        this.setRequestorPersonPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(currentUser.getPhoneNumber()));

        DefaultPrincipalAddress defaultPrincipalAddress = new DefaultPrincipalAddress(currentUser.getPrincipalId());
        Map addressKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(defaultPrincipalAddress);
        defaultPrincipalAddress = (DefaultPrincipalAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DefaultPrincipalAddress.class, addressKeys);
        if (ObjectUtils.isNotNull(defaultPrincipalAddress) && ObjectUtils.isNotNull(defaultPrincipalAddress.getBuilding())) {
            if (defaultPrincipalAddress.getBuilding().isActive()) {
                this.setDeliveryCampusCode(defaultPrincipalAddress.getCampusCode());
                this.templateBuildingToDeliveryAddress(defaultPrincipalAddress.getBuilding());
                this.setDeliveryBuildingRoomNumber(defaultPrincipalAddress.getBuildingRoomNumber());
            }
            else {
                //since building is now inactive, delete default building record
                SpringContext.getBean(BusinessObjectService.class).delete(defaultPrincipalAddress);
            }
        }

        // set the APO limit
        this.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));

        // populate billing address
        BillingAddress billingAddress = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(BillingAddress.class, getDeliveryCampusCode());
        this.templateBillingAddress(billingAddress);

        // populate receiving address with the default one for the chart/org
        loadReceivingAddress();

        // Load Requistion Statuses
        RequisitionStatusValuesFinder requisitionStatusValuesFinder = new RequisitionStatusValuesFinder();
        reqStatusList = requisitionStatusValuesFinder.getKeyValues();


        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.refreshNonUpdateableReferences();
    }

    public void templateBuildingToDeliveryAddress(Building building) {
        if (ObjectUtils.isNotNull(building)) {
            setDeliveryBuildingCode(building.getBuildingCode());
            setDeliveryBuildingName(building.getBuildingName());
            setDeliveryBuildingLine1Address(building.getBuildingStreetAddress());
            setDeliveryCityName(building.getBuildingAddressCityName());
            setDeliveryStateCode(building.getBuildingAddressStateCode());
            setDeliveryPostalCode(building.getBuildingAddressZipCode());
            setDeliveryCountryCode(building.getBuildingAddressCountryCode());
        }
    }

    /**
     * Determines what PO transmission method to use.
     *
     * @return the PO PO transmission method to use.
     */
    protected String determinePurchaseOrderTransmissionMethod() {

        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.PURAP_DEFAULT_PO_TRANSMISSION_CODE);
    }

    /**
     * Checks whether copying of this document should be allowed. Copying is not allowed if this is a B2B requistion, and more than
     * a set number of days have passed since the document's creation.
     *
     * @return True if copying of this requisition is allowed.
     * @see org.kuali.rice.kns.document.Document#getAllowsCopy()
     */
    @Override
    public boolean getAllowsCopy() {
        boolean allowsCopy = super.getAllowsCopy();
        if (PurapConstants.RequisitionSources.B2B.equals(getRequisitionSourceCode())) {
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            Calendar c = Calendar.getInstance();

            // The allowed copy date is the document creation date plus a set number of days.
            DateTime createDate = getDocumentHeader().getWorkflowDocument().getDateCreated();
            c.setTime(createDate.toDate());
            String allowedCopyDays = SpringContext.getBean(ParameterService.class).getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.B2B_ALLOW_COPY_DAYS);
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
     * @see org.kuali.rice.kns.document.Document#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, ValidationException {
        super.toCopy();

        // Clear related views
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(null);
        this.setRelatedViews(null);

        Person currentUser = GlobalVariables.getUserSession().getPerson();
        ChartOrgHolder purapChartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, PurapConstants.PURAP_NAMESPACE);
        this.setPurapDocumentIdentifier(null);

        // Set req status to INPR.
        //for app doc status
        updateAndSaveAppDocStatus(PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS);

        // Set fields from the user.
        if (ObjectUtils.isNotNull(purapChartOrg)) {
        this.setChartOfAccountsCode(purapChartOrg.getChartOfAccountsCode());
        this.setOrganizationCode(purapChartOrg.getOrganizationCode());
        }
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
            this.setVendorContractGeneratedIdentifier(null);
        }
        if (!activeContract) {
            this.setVendorContractGeneratedIdentifier(null);
        }

        // These fields should not be set in this method; force to be null
        this.setOrganizationAutomaticPurchaseOrderLimit(null);
        this.setPurchaseOrderAutomaticIndicator(false);

        for (Iterator iter = this.getItems().iterator(); iter.hasNext();) {
            RequisitionItem item = (RequisitionItem) iter.next();
            item.setPurapDocumentIdentifier(null);
            item.setItemIdentifier(null);

            for (Iterator acctIter = item.getSourceAccountingLines().iterator(); acctIter.hasNext();) {
                RequisitionAccount account = (RequisitionAccount) acctIter.next();
                account.setAccountIdentifier(null);
                account.setItemIdentifier(null);
                account.setObjectId(null);
                account.setVersionNumber(null);
            }
        }

        if (!PurapConstants.RequisitionSources.B2B.equals(this.getRequisitionSourceCode())) {
            SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        }
        this.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(this.getVendorContractGeneratedIdentifier(), this.getChartOfAccountsCode(), this.getOrganizationCode()));
        clearCapitalAssetFields();
        SpringContext.getBean(PurapService.class).clearTax(this, this.isUseTaxIndicator());

        this.refreshNonUpdateableReferences();
    }

    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        List<String> docIdStrings = new ArrayList<String>();
        docIdStrings.add(getDocumentNumber());

        //  PROCESSED
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            // creates a new PO but no way to know what the docID will be ahead of time
        }

        return docIdStrings;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");
        super.doRouteStatusChange(statusChangeEvent);
        try {
            // DOCUMENT PROCESSED
            if (this.getDocumentHeader().getWorkflowDocument().isProcessed()) {
                String newRequisitionStatus = PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN;
                if (SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(this)) {
                    newRequisitionStatus = PurapConstants.RequisitionStatuses.APPDOC_CLOSED;
                    SpringContext.getBean(PurchaseOrderService.class).createAutomaticPurchaseOrderDocument(this);
                }
                // for app doc status
                String reqStatus = PurapConstants.RequisitionStatuses.getRequistionAppDocStatuses().get(newRequisitionStatus);
                updateAndSaveAppDocStatus(reqStatus);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().isDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                String disapprovalStatus = RequisitionStatuses.getRequistionAppDocStatuses().get(nodeName);

                if (StringUtils.isNotBlank(disapprovalStatus)) {
                    updateAndSaveAppDocStatus(disapprovalStatus);
                }else{
                    logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
                }
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().isCanceled()) {
                String reqStatus = RequisitionStatuses.getRequistionAppDocStatuses().get(RequisitionStatuses.APPDOC_CANCELLED);
                updateAndSaveAppDocStatus(reqStatus);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
        LOG.debug("doRouteStatusChange() ending");
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChange change) {
        LOG.debug("handleRouteLevelChange() started");
        super.doRouteLevelChange(change);
/*
  FIXME: Remove this code
        try {
            String newNodeName = change.getNewNodeName();
            if (StringUtils.isNotBlank(newNodeName)) {
                ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(Long.valueOf(getDocumentNumber()));
                reportCriteriaDTO.setTargetNodeName(newNodeName);
                if (SpringContext.getBean(KualiWorkflowInfo.class).documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[] { KewApiConstants.ACTION_REQUEST_APPROVE_REQ, KewApiConstants.ACTION_REQUEST_COMPLETE_REQ }, false)) {
                    NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(newNodeName);
                    if (ObjectUtils.isNotNull(currentNode)) {
                        if (StringUtils.isNotBlank(currentNode.getAwaitingStatusCode())) {
                            updateStatusAndSave(currentNode.getAwaitingStatusCode());
                        }
                    }
                }
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Document with id " + getDocumentNumber() + " will not stop in route node '" + newNodeName + "'");
                    }
                }
            }
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Error found checking actions requests on document with id " + getDocumentNumber() + ". *** WILL NOT UPDATE PURAP STATUS ***";
            LOG.warn(errorMsg, e);
        }
        */
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
      //NOTE: do not do anything with this method as it is used by routing etc!
        return super.getSourceAccountingLineClass();
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
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return RequisitionItem.class;
    }

    @Override
    public Class getItemUseTaxClass() {
        return PurchaseRequisitionItemUseTax.class;
    }

    /**
     * Returns null as requistion has no source document.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * Returns null as requistion has no source document.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.rice.kns.document.Document#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String title = "";
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(RequisitionDocument.class, PurapParameterConstants.PURAP_OVERRIDE_REQ_DOC_TITLE)) {
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
    protected String getFirstChartAccount() {
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
        return this.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate();
    }

    public String getUrl() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
    }

    /**
     * This is a "do nothing" version of the method - it just won't create GLPEs
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    @Override
    public Class getPurchasingCapitalAssetItemClass() {
        return RequisitionCapitalAssetItem.class;
    }

    @Override
    public Class getPurchasingCapitalAssetSystemClass() {
        return RequisitionCapitalAssetSystem.class;
    }

    @Override
    public boolean shouldGiveErrorForEmptyAccountsProration() {
        //to be removed
        //for app doc status
        //remove   isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW) kfsmi - 4592
        if (isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_CONTENT_REVIEW) ||
                getApplicationDocumentStatus().equals(PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS)) {
            return false;
        }
        return true;
    }

    public Date getCreateDateForResult() {
        return this.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate();
    }

    /**
     * Gets the isBlanketApproveRequest attribute.
     * @return Returns the isBlanketApproveRequest.
     */
    public boolean isBlanketApproveRequest() {
        return isBlanketApproveRequest;
    }

    /**
     * Sets the isBlanketApproveRequest attribute value.
     * @param isBlanketApproveRequest The isBlanketApproveRequest to set.
     */
    public void setBlanketApproveRequest(boolean isBlanketApproveRequest) {
        this.isBlanketApproveRequest = isBlanketApproveRequest;
    }

    /**
     * retrieves the system parameter value for account distribution method and determines
     * if the drop-down box on the form should be read only or not.  Sets the default
     * value for account distribution method property on the document.
     */
    public void setupAccountDistributionMethod() {
        String defaultDistributionMethod = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.DISTRIBUTION_METHOD_FOR_ACCOUNTING_LINES);
        String defaultDistributionMethodCode = PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE;

        if (PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE.equalsIgnoreCase(defaultDistributionMethod) || PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(defaultDistributionMethod)) {
            defaultDistributionMethodCode = defaultDistributionMethod;
        }
        else {
            if (PurapConstants.AccountDistributionMethodCodes.BOTH_WITH_DEFAULT_PROPORTIONAL_CODE.equalsIgnoreCase(defaultDistributionMethod)) {
                defaultDistributionMethodCode = PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE;
            }
            else if (PurapConstants.AccountDistributionMethodCodes.BOTH_WITH_DEFAULT_SEQUENTIAL_CODE.equalsIgnoreCase(defaultDistributionMethod)){
                defaultDistributionMethodCode = PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE;
                }
                else {
                    new RuntimeException("Error in reading system parameter values for DISTRIBUTION_METHOD_FOR_ACCOUNTING_LINES");
                }
        }
        setAccountDistributionMethod(defaultDistributionMethodCode);
    }

}

