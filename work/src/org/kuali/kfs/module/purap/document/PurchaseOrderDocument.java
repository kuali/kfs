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

package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.rice.kns.util.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.QuoteTypeDescriptions;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorChoice;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentFrequency;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MultiselectableDocSearchConversion;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.kfs.vnd.businessobject.ShippingPaymentTerms;
import org.kuali.kfs.vnd.businessobject.ShippingTitle;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentHeaderService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Purchase Order Document
 */
public class PurchaseOrderDocument extends PurchasingDocumentBase implements MultiselectableDocSearchConversion {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocument.class);

    private Timestamp purchaseOrderCreateTimestamp;
    private Integer requisitionIdentifier;
    private String purchaseOrderVendorChoiceCode;
    private String recurringPaymentFrequencyCode;
    private KualiDecimal recurringPaymentAmount;
    private Date recurringPaymentDate;
    private KualiDecimal initialPaymentAmount;
    private Date initialPaymentDate;
    private KualiDecimal finalPaymentAmount;
    private Date finalPaymentDate;
    private Timestamp purchaseOrderInitialOpenTimestamp;
    private Timestamp purchaseOrderLastTransmitTimestamp;
    private Date purchaseOrderQuoteDueDate;
    private String purchaseOrderQuoteTypeCode;
    private String purchaseOrderQuoteVendorNoteText;
    private boolean purchaseOrderConfirmedIndicator;
    private String purchaseOrderCommodityDescription;
    private Integer purchaseOrderPreviousIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private Integer newQuoteVendorHeaderGeneratedIdentifier;
    private Integer newQuoteVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    private boolean purchaseOrderCurrentIndicator = false;
    private boolean pendingActionIndicator = false;
    private Timestamp purchaseOrderFirstTransmissionTimestamp;
    private Integer contractManagerCode;
    private Date purchaseOrderQuoteInitializationDate;
    private Date purchaseOrderQuoteAwardedDate;
    private String assignedUserPrincipalId;
    
    // COLLECTIONS
    private List<PurchaseOrderVendorStipulation> purchaseOrderVendorStipulations;
    private List<PurchaseOrderVendorQuote> purchaseOrderVendorQuotes;
    
    // NOT PERSISTED IN DB
    private String statusChange;
    private String alternateVendorNumber;
    private String purchaseOrderRetransmissionMethodCode;
    private String retransmitHeader;
    private Integer purchaseOrderQuoteListIdentifier;
    private KualiDecimal internalPurchasingLimit;
    private boolean pendingSplit = false;           // Needed for authorization
    private boolean copyingNotesWhenSplitting;      // Check box on Split PO tab
    private boolean assigningSensitiveData = false; // whether the form is currently used for assigning sensitive data to the PO
    private List<PurchaseOrderSensitiveData> purchaseOrderSensitiveData;  
    private String assignedUserPrincipalName; // this serves as a temporary holder before validation is done
    
    // REFERENCE OBJECTS
    private PurchaseOrderVendorChoice purchaseOrderVendorChoice;
    private PaymentTermType vendorPaymentTerms;
    private ShippingTitle vendorShippingTitle;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private RecurringPaymentFrequency recurringPaymentFrequency;
    private ContractManager contractManager;
    //private Person assignedUser;

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";
    private static final String UNIVERSITY_FISCAL_YEAR_KEY = "univ_fiscal_year";
    private static final String VENDOR_IS_EMPLOYEE = "Employee Vendor";
    private static final String VENDOR_IS_FOREIGN = "Foreign Vendor";
    private static final String VENDOR_IS_FOREIGN_EMPLOYEE = "Foreign and Employee Vendor";
    
    /**
     * Default constructor.
     */
    public PurchaseOrderDocument() {
        super();
        this.purchaseOrderVendorStipulations = new TypedArrayList(PurchaseOrderVendorStipulation.class);
        this.purchaseOrderVendorQuotes = new TypedArrayList(PurchaseOrderVendorQuote.class);
    }

    public PurchasingDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(PurchaseOrderService.class);    
    }
    
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to add the criteria
     * specific to Purchase Order Document.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        if ( isPostingYearPrior() && 
             ( getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) || 
               getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CANCELLED) ||
               getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.VOID) ) )  {
               return false;            
        }
        else {
            return true;
        }
    }
    
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(PurchaseOrderDocument.class, PurapParameterConstants.PURAP_OVERRIDE_PO_DOC_TITLE)) {
            return getCustomDocumentTitle();
        }
        
        return this.buildDocumentTitle(super.getDocumentTitle());
    }

    /**
     * Returns a custom document title based on the workflow document title. 
     * Depending on what route level the document is currently in, various info may be added to the documents title.
     * 
     * @return - Customized document title text dependent upon route level.
     */
    private String getCustomDocumentTitle() {
        try {
            String poNumber = getPurapDocumentIdentifier().toString();
            String cmCode = getContractManagerCode().toString();
            String vendorName = StringUtils.trimToEmpty(getVendorName());
            String totalAmount = getTotalDollarAmount().toString();
            PurApAccountingLine accountingLine = getFirstAccount();
            String chartAcctCode = accountingLine != null ? accountingLine.getChartOfAccountsCode() : "";
            String accountNumber = accountingLine != null ? accountingLine.getAccountNumber() : "";
            String chartCode = getChartOfAccountsCode();
            String orgCode = getOrganizationCode();
            String deliveryCampus = getDeliveryCampus() != null ? getDeliveryCampus().getCampus().getCampusShortName() : "";
            String documentTitle = "";
         
            String[] nodeNames = getDocumentHeader().getWorkflowDocument().getNodeNames();
            String routeLevel = "";
            if (nodeNames.length == 1)
                routeLevel = nodeNames[0];
            
            if (getStatusCode().equals(PurchaseOrderStatuses.OPEN)) {
                documentTitle = super.getDocumentTitle();
            }
            else if (routeLevel.equals(NodeDetailEnum.BUDGET_OFFICE_REVIEW.getName()) || routeLevel.equals(NodeDetailEnum.CONTRACTS_AND_GRANTS_REVIEW.getName())) {
                // Budget & C&G approval levels
                documentTitle = "PO: " + poNumber + " Account Number: " + chartAcctCode + "-" + accountNumber + " Dept: " + chartCode + "-" + orgCode + " Delivery Campus: " + deliveryCampus;
            }
            else if (routeLevel.equals(NodeDetailEnum.VENDOR_TAX_REVIEW.getName())) {
                // Tax approval level
                documentTitle = "Vendor: " + vendorName + " PO: " + poNumber + " Account Number: " + chartCode + "-" + accountNumber + " Dept: " + chartCode + "-" + orgCode + " Delivery Campus: " + deliveryCampus;
            }
            else 
                documentTitle += "PO: " + poNumber + " Contract Manager: " + cmCode + " Vendor: " + vendorName + " Amount: " + totalAmount;
                        
            return documentTitle;
        }
        catch (WorkflowException e) {
            LOG.error("Error updating Purchase Order document: " + e.getMessage());
            throw new RuntimeException("Error updating Purchase Order document: " + e.getMessage());
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
      //NOTE: do not do anything with this method as it is used by routing etc!
        return super.getSourceAccountingLineClass();
    } 
    
    /**
     * Returns the first PO item's first accounting line (assuming the item list is sequentially ordered).
     * 
     * @return - The first accounting line of the first PO item.
     */
    private PurApAccountingLine getFirstAccount() {
        // loop through items, and pick the first item with non-empty accouting lines
        if (getItems() != null && !getItems().isEmpty()) {
            for (Iterator iter = getItems().iterator(); iter.hasNext();) {
                PurchaseOrderItem item = (PurchaseOrderItem)iter.next();
                if (item.isConsideredEntered() && item.getSourceAccountingLines() != null && !item.getSourceAccountingLines().isEmpty()) {
                    // accounting lines are not empty so pick the first account
                    PurApAccountingLine accountingLine = item.getSourceAccountingLine(0);
                    accountingLine.refreshNonUpdateableReferences();
                    return accountingLine;
                }
            }
        }
        return null;
    }
            
    public String getAssignedUserPrincipalId() {
        return assignedUserPrincipalId;
    }

    public void setAssignedUserPrincipalId(String assignedUserPrincipalId) {
        this.assignedUserPrincipalId = assignedUserPrincipalId;
    }

    public String getAssignedUserPrincipalName() {
        // init this field when PO is first loaded and assigned user exists in PO
        if (assignedUserPrincipalName == null && assignedUserPrincipalId != null) {
            // extra caution in case ref obj didn't get refreshed
            //if (assignedUser == null)
            //    this.refreshReferenceObject("assignedUser");
            Person assignedUser = SpringContext.getBean(PersonService.class).getPerson(assignedUserPrincipalId);
            this.assignedUserPrincipalName = assignedUser.getPrincipalName();
        }
        // otherwise return its current value directly
        return assignedUserPrincipalName;
    }

    public void setAssignedUserPrincipalName(String assignedUserPrincipalName) {
        this.assignedUserPrincipalName = assignedUserPrincipalName;           
        // each time this field changes we need to update the assigned user ID and ref obj to keep consistent
        // this code can be moved to where PO is saved and with validation too, which may be more appropriate
        Person assignedUser = null;
        if (assignedUserPrincipalName != null) 
            assignedUser = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(assignedUserPrincipalName);
        if (assignedUser != null) 
            assignedUserPrincipalId = assignedUser.getPrincipalId();        
        else
            assignedUserPrincipalId = null;        
    }

    /*
    public Person getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(Person assignedUser) {
        this.assignedUser = assignedUser;
    }
    */
    
    /*
    public String getAssignedUserName() {      
        // assignedUserPrincipalId is either null or returned from lookup (thus valid)
        if (StringUtils.isEmpty(assignedUserPrincipalId))
            return null;  
        Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(assignedUserPrincipalId);
        if (person == null)
            return null;
        return person.getName();
    }
    */

    public boolean getAssigningSensitiveData() {
        return assigningSensitiveData;
    }

    public void setAssigningSensitiveData(boolean assigningSensitiveData) {
        this.assigningSensitiveData = assigningSensitiveData;
    }

    public List<PurchaseOrderSensitiveData> getPurchaseOrderSensitiveData() {
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.PURAP_DOC_ID, getPurapDocumentIdentifier());
        return new ArrayList(SpringContext.getBean(BusinessObjectService.class).findMatching(PurchaseOrderSensitiveData.class, fieldValues));
    }

    public void setPurchaseOrderSensitiveData(List<PurchaseOrderSensitiveData> purchaseOrderSensitiveData) {
        this.purchaseOrderSensitiveData = purchaseOrderSensitiveData;
    }
    
    public ContractManager getContractManager() {
        if (ObjectUtils.isNull(contractManager))
            refreshReferenceObject(PurapPropertyConstants.CONTRACT_MANAGER);
        return contractManager;
    }

    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public Integer getContractManagerCode() {
        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        if (allowDeleteAwareCollection) {
            managedLists.add(this.getPurchaseOrderVendorQuotes());
        }
        return managedLists;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getOverrideWorkflowButtons()
     */
    @Override
    public Boolean getOverrideWorkflowButtons() {
        if (ObjectUtils.isNull(super.getOverrideWorkflowButtons())) {
            // should only be null on the first call... never after
            setOverrideWorkflowButtons(Boolean.TRUE);
        }
        return super.getOverrideWorkflowButtons();
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#customPrepareForSave()
     */
    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        super.customPrepareForSave(event);
        if (ObjectUtils.isNull(getPurapDocumentIdentifier())) {
            // need retrieve the next available PO id to save in GL entries (only do if purap id is null which should be on first
            // save)
            SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
            Long poSequenceNumber = sas.getNextAvailableSequenceNumber("PO_ID", this.getClass());
            setPurapDocumentIdentifier(poSequenceNumber.intValue());
        }

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = this.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            // Set quantities
            item.setItemOutstandingEncumberedQuantity(item.getItemQuantity());
            if (item.getItemInvoicedTotalQuantity() == null) {
                item.setItemInvoicedTotalQuantity(ZERO);
            }
            if (item.getItemInvoicedTotalAmount() == null) {
                item.setItemInvoicedTotalAmount(ZERO);
            }

            // Set amount
            item.setItemOutstandingEncumberedAmount(item.getTotalAmount() == null ? ZERO : item.getTotalAmount());

            List accounts = (List) item.getSourceAccountingLines();
            Collections.sort(accounts);

            for (Iterator iterator = accounts.iterator(); iterator.hasNext();) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) iterator.next();
                if (!account.isEmpty()) {
                    account.setItemAccountOutstandingEncumbranceAmount(account.getAmount());
                }
            }// endfor accounts
        }// endfor items

        this.setSourceAccountingLines(SpringContext.getBean(PurapAccountingService.class).generateSummaryWithNoZeroTotals(this.getItems()));
    }// end customPrepareForSave(KualiDocumentEvent)

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        String documentType = getDocumentHeader().getWorkflowDocument().getDocumentType();
        if ((documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT)) ||
            (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT))) {
            if (!getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                super.prepareForSave(event);
            }
            else {
                // if doc is FINAL, saving should not be creating GL entries
                setGeneralLedgerPendingEntries(new ArrayList());
            }
        }
    }

    /**
     * Sets default values for APO.
     */
    public void setDefaultValuesForAPO() {
        this.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);
        if (!RequisitionSources.B2B.equals(this.getRequisitionSourceCode())) {
            String paramName = PurapParameterConstants.DEFAULT_APO_VENDOR_CHOICE;
            String paramValue = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, paramName);
            this.setPurchaseOrderVendorChoiceCode(paramValue);
        }
    }

    /**
     * Populates this Purchase Order from the related Requisition Document.
     * 
     * @param requisitionDocument the Requisition Document from which field values are copied.
     */
    public void populatePurchaseOrderFromRequisition(RequisitionDocument requisitionDocument) {
        this.setPurchaseOrderCreateTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        this.getDocumentHeader().setOrganizationDocumentNumber(requisitionDocument.getDocumentHeader().getOrganizationDocumentNumber());
        this.getDocumentHeader().setDocumentDescription(requisitionDocument.getDocumentHeader().getDocumentDescription());
        this.getDocumentHeader().setExplanation(requisitionDocument.getDocumentHeader().getExplanation());

        this.setBillingName(requisitionDocument.getBillingName());
        this.setBillingLine1Address(requisitionDocument.getBillingLine1Address());
        this.setBillingLine2Address(requisitionDocument.getBillingLine2Address());
        this.setBillingCityName(requisitionDocument.getBillingCityName());
        this.setBillingStateCode(requisitionDocument.getBillingStateCode());
        this.setBillingPostalCode(requisitionDocument.getBillingPostalCode());
        this.setBillingCountryCode(requisitionDocument.getBillingCountryCode());
        this.setBillingPhoneNumber(requisitionDocument.getBillingPhoneNumber());
        
        this.setReceivingName(requisitionDocument.getReceivingName());
        this.setReceivingCityName(requisitionDocument.getReceivingCityName());
        this.setReceivingLine1Address(requisitionDocument.getReceivingLine1Address());
        this.setReceivingLine2Address(requisitionDocument.getReceivingLine2Address());
        this.setReceivingStateCode(requisitionDocument.getReceivingStateCode());
        this.setReceivingPostalCode(requisitionDocument.getReceivingPostalCode());
        this.setReceivingCountryCode(requisitionDocument.getReceivingCountryCode());
        this.setAddressToVendorIndicator(requisitionDocument.getAddressToVendorIndicator());

        this.setDeliveryBuildingCode(requisitionDocument.getDeliveryBuildingCode());
        this.setDeliveryBuildingRoomNumber(requisitionDocument.getDeliveryBuildingRoomNumber());
        this.setDeliveryBuildingName(requisitionDocument.getDeliveryBuildingName());
        this.setDeliveryCampusCode(requisitionDocument.getDeliveryCampusCode());
        this.setDeliveryCityName(requisitionDocument.getDeliveryCityName());
        this.setDeliveryCountryCode(requisitionDocument.getDeliveryCountryCode());
        this.setDeliveryInstructionText(requisitionDocument.getDeliveryInstructionText());
        this.setDeliveryBuildingLine1Address(requisitionDocument.getDeliveryBuildingLine1Address());
        this.setDeliveryBuildingLine2Address(requisitionDocument.getDeliveryBuildingLine2Address());
        this.setDeliveryPostalCode(requisitionDocument.getDeliveryPostalCode());
        this.setDeliveryRequiredDate(requisitionDocument.getDeliveryRequiredDate());
        this.setDeliveryRequiredDateReasonCode(requisitionDocument.getDeliveryRequiredDateReasonCode());
        this.setDeliveryStateCode(requisitionDocument.getDeliveryStateCode());
        this.setDeliveryToEmailAddress(requisitionDocument.getDeliveryToEmailAddress());
        this.setDeliveryToName(requisitionDocument.getDeliveryToName());
        this.setDeliveryToPhoneNumber(requisitionDocument.getDeliveryToPhoneNumber());
        this.setDeliveryBuildingOtherIndicator(requisitionDocument.isDeliveryBuildingOtherIndicator());
        
        this.setPurchaseOrderBeginDate(requisitionDocument.getPurchaseOrderBeginDate());
        this.setPurchaseOrderCostSourceCode(requisitionDocument.getPurchaseOrderCostSourceCode());
        this.setPostingYear(requisitionDocument.getPostingYear());
        this.setPurchaseOrderEndDate(requisitionDocument.getPurchaseOrderEndDate());
        this.setChartOfAccountsCode(requisitionDocument.getChartOfAccountsCode());
        this.setDocumentFundingSourceCode(requisitionDocument.getDocumentFundingSourceCode());
        this.setInstitutionContactEmailAddress(requisitionDocument.getInstitutionContactEmailAddress());
        this.setInstitutionContactName(requisitionDocument.getInstitutionContactName());
        this.setInstitutionContactPhoneNumber(requisitionDocument.getInstitutionContactPhoneNumber());
        this.setNonInstitutionFundAccountNumber(requisitionDocument.getNonInstitutionFundAccountNumber());
        this.setNonInstitutionFundChartOfAccountsCode(requisitionDocument.getNonInstitutionFundChartOfAccountsCode());
        this.setNonInstitutionFundOrgChartOfAccountsCode(requisitionDocument.getNonInstitutionFundOrgChartOfAccountsCode());
        this.setNonInstitutionFundOrganizationCode(requisitionDocument.getNonInstitutionFundOrganizationCode());
        this.setOrganizationCode(requisitionDocument.getOrganizationCode());
        this.setRecurringPaymentTypeCode(requisitionDocument.getRecurringPaymentTypeCode());
        this.setRequestorPersonEmailAddress(requisitionDocument.getRequestorPersonEmailAddress());
        this.setRequestorPersonName(requisitionDocument.getRequestorPersonName());
        this.setRequestorPersonPhoneNumber(requisitionDocument.getRequestorPersonPhoneNumber());
        this.setRequisitionIdentifier(requisitionDocument.getPurapDocumentIdentifier());
        this.setPurchaseOrderTotalLimit(requisitionDocument.getPurchaseOrderTotalLimit());
        this.setPurchaseOrderTransmissionMethodCode(requisitionDocument.getPurchaseOrderTransmissionMethodCode());
        this.setUseTaxIndicator( requisitionDocument.isUseTaxIndicator() );
        
        this.setVendorCityName(requisitionDocument.getVendorCityName());
        this.setVendorContractGeneratedIdentifier(requisitionDocument.getVendorContractGeneratedIdentifier());
        this.setVendorCountryCode(requisitionDocument.getVendorCountryCode());
        this.setVendorCustomerNumber(requisitionDocument.getVendorCustomerNumber());
        this.setVendorAttentionName(requisitionDocument.getVendorAttentionName());
        this.setVendorDetailAssignedIdentifier(requisitionDocument.getVendorDetailAssignedIdentifier());
        this.setVendorFaxNumber(requisitionDocument.getVendorFaxNumber());
        this.setVendorHeaderGeneratedIdentifier(requisitionDocument.getVendorHeaderGeneratedIdentifier());
        this.setVendorLine1Address(requisitionDocument.getVendorLine1Address());
        this.setVendorLine2Address(requisitionDocument.getVendorLine2Address());
        this.setVendorAddressInternationalProvinceName(requisitionDocument.getVendorAddressInternationalProvinceName());
        this.setVendorName(requisitionDocument.getVendorName());
        this.setVendorNoteText(requisitionDocument.getVendorNoteText());
        this.setVendorPhoneNumber(requisitionDocument.getVendorPhoneNumber());
        this.setVendorPostalCode(requisitionDocument.getVendorPostalCode());
        this.setVendorStateCode(requisitionDocument.getVendorStateCode());
        this.setVendorRestrictedIndicator(requisitionDocument.getVendorRestrictedIndicator());
        
        this.setExternalOrganizationB2bSupplierIdentifier(requisitionDocument.getExternalOrganizationB2bSupplierIdentifier());
        this.setRequisitionSourceCode(requisitionDocument.getRequisitionSourceCode());
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
        this.setReceivingDocumentRequiredIndicator(requisitionDocument.isReceivingDocumentRequiredIndicator());
        this.setPaymentRequestPositiveApprovalIndicator(requisitionDocument.isPaymentRequestPositiveApprovalIndicator());

        this.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);

        // Copy items from requisition (which will copy the item's accounts and capital assets)
        List<PurchaseOrderItem> items = new ArrayList();
        for (PurApItem reqItem : ((PurchasingAccountsPayableDocument)requisitionDocument).getItems()) {
            RequisitionCapitalAssetItem reqCamsItem = (RequisitionCapitalAssetItem)requisitionDocument.getPurchasingCapitalAssetItemByItemIdentifier(reqItem.getItemIdentifier().intValue());
            items.add(new PurchaseOrderItem((RequisitionItem)reqItem, this, reqCamsItem));
        }
        this.setItems(items);
        
        // Copy capital asset information that is directly off the document.
        this.setCapitalAssetSystemTypeCode(requisitionDocument.getCapitalAssetSystemTypeCode());
        this.setCapitalAssetSystemStateCode(requisitionDocument.getCapitalAssetSystemStateCode());
        for (CapitalAssetSystem capitalAssetSystem : requisitionDocument.getPurchasingCapitalAssetSystems()) {
            this.getPurchasingCapitalAssetSystems().add(new PurchaseOrderCapitalAssetSystem((RequisitionCapitalAssetSystem)capitalAssetSystem));
        }
        
        this.fixItemReferences();
    }

    /**
     * Returns the Vendor Stipulation at the specified index in this Purchase Order.
     * 
     * @param index the specified index.
     * @return the Vendor Stipulation at the specified index.
     */
    public PurchaseOrderVendorStipulation getPurchaseOrderVendorStipulation(int index) {
        while (getPurchaseOrderVendorStipulations().size() <= index) {
            getPurchaseOrderVendorStipulations().add(new PurchaseOrderVendorStipulation());
        }
        return (PurchaseOrderVendorStipulation) purchaseOrderVendorStipulations.get(index);
    }

    @Override
    public List<Long> getWorkflowEngineDocumentIdsToLock() {
        List<String> docIdStrings = new ArrayList<String>();
        docIdStrings.add(getDocumentNumber());
        
        List<PurchaseOrderView> relatedPoViews = getRelatedViews().getRelatedPurchaseOrderViews();
        for (PurchaseOrderView poView : relatedPoViews) {
            docIdStrings.add(poView.getDocumentNumber());
        }
        
        //  convert our easy to use List<String> to a Long[]
        List<Long> docIds = new ArrayList<Long>();
        for (int i = 0; i < docIdStrings.size(); i++) {
            docIds.add(new Long(docIdStrings.get(i)));
        }
        LOG.info("***** getWorkflowEngineDocumentIdsToLock(" + this.documentNumber + ") = '" + printList(docIds) + "'");
        return docIds;
    }
    
    // Only used for debugging in the getWorkflowEngineDocumentIdsToLock above
    private String printList(List<Long> docIds) {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < docIds.size(); i++) {
            sb.append(new Long(docIds.get(i)).toString() + ",");
        }
        return sb.append("]").toString();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");
        super.doRouteStatusChange(statusChangeEvent);
        String currentDocumentTypeName = this.getDocumentHeader().getWorkflowDocument().getDocumentType();
        // child classes need to call super, but we don't want to inherit the post-processing done by this PO class other than to the Split
        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT.equals(currentDocumentTypeName) || PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT.equals(currentDocumentTypeName)) { 
            try {
                // DOCUMENT PROCESSED
                if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                    SpringContext.getBean(PurchaseOrderService.class).completePurchaseOrder(this);
                }
                // DOCUMENT DISAPPROVED
                else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                    String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                    NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                    if (ObjectUtils.isNotNull(currentNode)) {
                        if (StringUtils.isNotBlank(currentNode.getDisapprovedStatusCode())) {
                            SpringContext.getBean(PurapService.class).updateStatus(this, currentNode.getDisapprovedStatusCode());
                            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
                            RequisitionDocument req = getPurApSourceDocumentIfPossible();
                            appSpecificRouteDocumentToUser(getDocumentHeader().getWorkflowDocument(), req.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId(), "Notification of Order Disapproval for Requisition " + req.getPurapDocumentIdentifier() + "(document id " + req.getDocumentNumber() + ")", "Requisition Routed By User");
                            return;
                        }
                    }
                    logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
                }
                // DOCUMENT CANCELED
                else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                    SpringContext.getBean(PurapService.class).updateStatus(this, PurchaseOrderStatuses.CANCELLED);
                    SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
                }
            }
            catch (WorkflowException e) {
                logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
            }
        }
    }

    /**
     * Returns the name of the current route node.
     * 
     * @param wd the current workflow document.
     * @return the name of the current route node.
     * @throws WorkflowException
     */
    private String getCurrentRouteNodeName(KualiWorkflowDocument wd) throws WorkflowException {
        String[] nodeNames = wd.getNodeNames();
        if ((nodeNames == null) || (nodeNames.length == 0)) {
            return null;
        }
        else {
            return nodeNames[0];
        }
    }

    /**
     * Sends FYI workflow request to the given user on this document.
     * 
     * @param workflowDocument the associated workflow document.
     * @param userNetworkId the network ID of the user to be sent to.
     * @param annotation the annotation notes contained in this document.
     * @param responsibility the responsibility specified in the request.
     * @throws WorkflowException
     */
    public void appSpecificRouteDocumentToUser(KualiWorkflowDocument workflowDocument, String userNetworkId, String annotation, String responsibility) throws WorkflowException {
        if (ObjectUtils.isNotNull(workflowDocument)) {
            String annotationNote = (ObjectUtils.isNull(annotation)) ? "" : annotation;
            String responsibilityNote = (ObjectUtils.isNull(responsibility)) ? "" : responsibility;
            String currentNodeName = getCurrentRouteNodeName(workflowDocument);
            KimPrincipal principal = SpringContext.getBean(IdentityManagementService.class).getPrincipalByPrincipalName(userNetworkId);
            workflowDocument.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, currentNodeName, annotationNote, principal.getPrincipalId(), responsibilityNote, true);
        }
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChangeDTO levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.doRouteLevelChange(levelChangeEvent);

        LOG.debug("handleRouteLevelChange() started");
        String newNodeName = levelChangeEvent.getNewNodeName();
        if (StringUtils.isNotBlank(newNodeName)) {
            ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(Long.valueOf(getDocumentNumber()));
            reportCriteriaDTO.setTargetNodeName(newNodeName);
            try {
                NodeDetails newNodeDetails = NodeDetailEnum.getNodeDetailEnumByName(newNodeName);
                if (ObjectUtils.isNotNull(newNodeDetails)) {
                    String newStatusCode = newNodeDetails.getAwaitingStatusCode();
                    if (StringUtils.isNotBlank(newStatusCode)) {
                        if (SpringContext.getBean(KualiWorkflowInfo.class).documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[] { KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_COMPLETE_REQ }, false)) {
                            // if an approve or complete request will be created then we need to set the status as awaiting for
                            // the new node
                            SpringContext.getBean(PurapService.class).updateStatus(this, newStatusCode);
                            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
                        }
                    }
                }
            }
            catch (WorkflowException e) {
                String errorMsg = "Workflow Error found checking actions requests on document with id " + getDocumentNumber() + ". *** WILL NOT UPDATE PURAP STATUS ***";
                LOG.warn(errorMsg, e);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#doActionTaken(org.kuali.rice.kew.clientapp.vo.ActionTakenEventDTO)
     */
    @Override
    public void doActionTaken(ActionTakenEventDTO event) {
        super.doActionTaken(event);
        // additional processing
    }

    /**
     * Gets the active items in this Purchase Order.
     * 
     * @return the list of all active items in this Purchase Order.
     */
    public List getItemsActiveOnly() {
        List returnList = new ArrayList();
        for (Iterator iter = getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) iter.next();
            if (item.isItemActiveIndicator()) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    /**
     * Gets the active items in this Purchase Order, and sets up the alternate amount for GL entry creation.
     * 
     * @return the list of all active items in this Purchase Order.
     */
    public List getItemsActiveOnlySetupAlternateAmount() {
        List returnList = new ArrayList();
        for (Iterator iter = getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) iter.next();
            if (item.isItemActiveIndicator()) {
                for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iterator.next();
                    account.setAlternateAmountForGLEntryCreation(account.getItemAccountOutstandingEncumbranceAmount());
                }
                returnList.add(item);
            }
        }
        return returnList;
    }

    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public String getAlternateVendorName() {
        return alternateVendorName;
    }

    public void setAlternateVendorName(String alternateVendorName) {
        this.alternateVendorName = alternateVendorName;
    }

    public KualiDecimal getFinalPaymentAmount() {
        return finalPaymentAmount;
    }

    public void setFinalPaymentAmount(KualiDecimal finalPaymentAmount) {
        this.finalPaymentAmount = finalPaymentAmount;
    }

    public Date getFinalPaymentDate() {
        return finalPaymentDate;
    }

    public void setFinalPaymentDate(Date finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public KualiDecimal getInitialPaymentAmount() {
        return initialPaymentAmount;
    }

    public void setInitialPaymentAmount(KualiDecimal initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
    }

    public Date getInitialPaymentDate() {
        return initialPaymentDate;
    }

    public void setInitialPaymentDate(Date initialPaymentDate) {
        this.initialPaymentDate = initialPaymentDate;
    }

    public String getPurchaseOrderCommodityDescription() {
        return purchaseOrderCommodityDescription;
    }

    public void setPurchaseOrderCommodityDescription(String purchaseOrderCommodityDescription) {
        this.purchaseOrderCommodityDescription = purchaseOrderCommodityDescription;
    }

    public boolean isPurchaseOrderConfirmedIndicator() {
        return purchaseOrderConfirmedIndicator;
    }

    public void setPurchaseOrderConfirmedIndicator(boolean purchaseOrderConfirmedIndicator) {
        this.purchaseOrderConfirmedIndicator = purchaseOrderConfirmedIndicator;
    }

    public Timestamp getPurchaseOrderCreateTimestamp() {
        return purchaseOrderCreateTimestamp;
    }

    public void setPurchaseOrderCreateTimestamp(Timestamp purchaseOrderCreateTimestamp) {
        this.purchaseOrderCreateTimestamp = purchaseOrderCreateTimestamp;
    }

    public Timestamp getPurchaseOrderInitialOpenTimestamp() {
        return purchaseOrderInitialOpenTimestamp;
    }

    public void setPurchaseOrderInitialOpenTimestamp(Timestamp purchaseOrderInitialOpenDate) {
        this.purchaseOrderInitialOpenTimestamp = purchaseOrderInitialOpenDate;
    }

    public Timestamp getPurchaseOrderLastTransmitTimestamp() {
        return purchaseOrderLastTransmitTimestamp;
    }

    public void setPurchaseOrderLastTransmitTimestamp(Timestamp PurchaseOrderLastTransmitTimestamp) {
        this.purchaseOrderLastTransmitTimestamp = PurchaseOrderLastTransmitTimestamp;
    }

    public Integer getPurchaseOrderPreviousIdentifier() {
        return purchaseOrderPreviousIdentifier;
    }

    public void setPurchaseOrderPreviousIdentifier(Integer purchaseOrderPreviousIdentifier) {
        this.purchaseOrderPreviousIdentifier = purchaseOrderPreviousIdentifier;
    }

    public Date getPurchaseOrderQuoteDueDate() {
        return purchaseOrderQuoteDueDate;
    }

    public void setPurchaseOrderQuoteDueDate(Date purchaseOrderQuoteDueDate) {
        this.purchaseOrderQuoteDueDate = purchaseOrderQuoteDueDate;
    }

    public String getPurchaseOrderQuoteTypeDescription() {
        String descript = purchaseOrderQuoteTypeCode;
        if (PurapConstants.QuoteTypes.COMPETITIVE.equals(purchaseOrderQuoteTypeCode)) {
            descript = QuoteTypeDescriptions.COMPETITIVE;
        }
        else if (PurapConstants.QuoteTypes.PRICE_CONFIRMATION.equals(purchaseOrderQuoteTypeCode)){
            descript = QuoteTypeDescriptions.PRICE_CONFIRMATION;
        }
        return descript;
    }

    public String getPurchaseOrderQuoteTypeCode() {
        return purchaseOrderQuoteTypeCode;
    }

    public void setPurchaseOrderQuoteTypeCode(String purchaseOrderQuoteTypeCode) {
        this.purchaseOrderQuoteTypeCode = purchaseOrderQuoteTypeCode;
    }

    public String getPurchaseOrderQuoteVendorNoteText() {
        return purchaseOrderQuoteVendorNoteText;
    }

    public void setPurchaseOrderQuoteVendorNoteText(String purchaseOrderQuoteVendorNoteText) {
        this.purchaseOrderQuoteVendorNoteText = purchaseOrderQuoteVendorNoteText;
    }

    public String getPurchaseOrderVendorChoiceCode() {
        return purchaseOrderVendorChoiceCode;
    }

    public void setPurchaseOrderVendorChoiceCode(String purchaseOrderVendorChoiceCode) {
        this.purchaseOrderVendorChoiceCode = purchaseOrderVendorChoiceCode;
    }

    public KualiDecimal getRecurringPaymentAmount() {
        return recurringPaymentAmount;
    }

    public void setRecurringPaymentAmount(KualiDecimal recurringPaymentAmount) {
        this.recurringPaymentAmount = recurringPaymentAmount;
    }

    public Date getRecurringPaymentDate() {
        return recurringPaymentDate;
    }

    public void setRecurringPaymentDate(Date recurringPaymentDate) {
        this.recurringPaymentDate = recurringPaymentDate;
    }

    public String getRecurringPaymentFrequencyCode() {
        return recurringPaymentFrequencyCode;
    }

    public void setRecurringPaymentFrequencyCode(String recurringPaymentFrequencyCode) {
        this.recurringPaymentFrequencyCode = recurringPaymentFrequencyCode;
    }

    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    public PurchaseOrderVendorChoice getPurchaseOrderVendorChoice() {
        return purchaseOrderVendorChoice;
    }

    public void setPurchaseOrderVendorChoice(PurchaseOrderVendorChoice purchaseOrderVendorChoice) {
        this.purchaseOrderVendorChoice = purchaseOrderVendorChoice;
    }

    public RecurringPaymentFrequency getRecurringPaymentFrequency() {
        return recurringPaymentFrequency;
    }

    public void setRecurringPaymentFrequency(RecurringPaymentFrequency recurringPaymentFrequency) {
        this.recurringPaymentFrequency = recurringPaymentFrequency;
    }

    public PaymentTermType getVendorPaymentTerms() {
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public ShippingTitle getVendorShippingTitle() {
        
        if( ObjectUtils.isNull(vendorShippingTitle) ){
            this.refreshReferenceObject("vendorShippingTitle");
        }

        return vendorShippingTitle;
    }

    public void setVendorShippingTitle(ShippingTitle vendorShippingTitle) {
        this.vendorShippingTitle = vendorShippingTitle;
    }

    public List getPurchaseOrderVendorStipulations() {
        return purchaseOrderVendorStipulations;
    }

    public String getStatusChange() {
        return statusChange;
    }

    public void setPurchaseOrderVendorStipulations(List purchaseOrderVendorStipulations) {
        this.purchaseOrderVendorStipulations = purchaseOrderVendorStipulations;
    }

    public List<PurchaseOrderVendorQuote> getPurchaseOrderVendorQuotes() {
        return purchaseOrderVendorQuotes;
    }

    public void setPurchaseOrderVendorQuotes(List<PurchaseOrderVendorQuote> purchaseOrderVendorQuotes) {
        this.purchaseOrderVendorQuotes = purchaseOrderVendorQuotes;
    }

    public PurchaseOrderVendorQuote getPurchaseOrderVendorQuote(int index) {
        while (getPurchaseOrderVendorQuotes().size() <= index) {
            getPurchaseOrderVendorQuotes().add(new PurchaseOrderVendorQuote());
        }
        return (PurchaseOrderVendorQuote) purchaseOrderVendorQuotes.get(index);
    }

    public void setStatusChange(String statusChange) {
        this.statusChange = statusChange;
    }

    public String getPurchaseOrderRetransmissionMethodCode() {
        return purchaseOrderRetransmissionMethodCode;
    }

    public void setPurchaseOrderRetransmissionMethodCode(String purchaseOrderRetransmissionMethodCode) {
        this.purchaseOrderRetransmissionMethodCode = purchaseOrderRetransmissionMethodCode;
    }

    public String getRetransmitHeader() {
        return retransmitHeader;
    }

    public void setRetransmitHeader(String retransmitHeader) {
        this.retransmitHeader = retransmitHeader;
    }

    public boolean isPendingActionIndicator() {
        return pendingActionIndicator;
    }

    public void setPendingActionIndicator(boolean pendingActionIndicator) {
        this.pendingActionIndicator = pendingActionIndicator;
    }

    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }
    
    public Timestamp getPurchaseOrderFirstTransmissionTimestamp() {
        return purchaseOrderFirstTransmissionTimestamp;
    }

    public void setPurchaseOrderFirstTransmissionTimestamp(Timestamp purchaseOrderFirstTransmissionTimestamp) {
        this.purchaseOrderFirstTransmissionTimestamp = purchaseOrderFirstTransmissionTimestamp;
    }

    /**
     * Gets the purchaseOrderQuoteAwardedDate attribute. 
     * @return Returns the purchaseOrderQuoteAwardedDate.
     */
    public Date getPurchaseOrderQuoteAwardedDate() {
        return purchaseOrderQuoteAwardedDate;
    }

    /**
     * Sets the purchaseOrderQuoteAwardedDate attribute value.
     * @param purchaseOrderQuoteAwardedDate The purchaseOrderQuoteAwardedDate to set.
     */
    public void setPurchaseOrderQuoteAwardedDate(Date purchaseOrderQuoteAwardedDate) {
        this.purchaseOrderQuoteAwardedDate = purchaseOrderQuoteAwardedDate;
    }

    /**
     * Gets the purchaseOrderQuoteInitializationDate attribute. 
     * @return Returns the purchaseOrderQuoteInitializationDate.
     */
    public Date getPurchaseOrderQuoteInitializationDate() {
        return purchaseOrderQuoteInitializationDate;
    }

    /**
     * Sets the purchaseOrderQuoteInitializationDate attribute value.
     * @param purchaseOrderQuoteInitializationDate The purchaseOrderQuoteInitializationDate to set.
     */
    public void setPurchaseOrderQuoteInitializationDate(Date purchaseOrderQuoteInitializationDate) {
        this.purchaseOrderQuoteInitializationDate = purchaseOrderQuoteInitializationDate;
    }

    /**
     * Gets the alternateVendorNumber attribute.
     * 
     * @return Returns the alternateVendorNumber.
     */
    public String getAlternateVendorNumber() {
        String hdrGenId = "";
        String detAssgndId = "";
        String vendorNumber = "";
        if (this.alternateVendorHeaderGeneratedIdentifier != null) {
            hdrGenId = this.alternateVendorHeaderGeneratedIdentifier.toString();
        }
        if (this.alternateVendorDetailAssignedIdentifier != null) {
            detAssgndId = this.alternateVendorDetailAssignedIdentifier.toString();
        }
        if (!StringUtils.isEmpty(hdrGenId) && !StringUtils.isEmpty(detAssgndId)) {
            vendorNumber = hdrGenId + "-" + detAssgndId;
        }
        return vendorNumber;
    }

    /**
     * Sets the alternateVendorNumber attribute value.
     * 
     * @param alternateVendorNumber The vendorNumber to set.
     */
    public void setAlternateVendorNumber(String vendorNumber) {
        if (!StringUtils.isEmpty(vendorNumber)) {
            int dashInd = vendorNumber.indexOf("-");
            if (vendorNumber.length() >= dashInd) {
                String vndrHdrGenId = vendorNumber.substring(0, dashInd);
                String vndrDetailAssgnedId = vendorNumber.substring(dashInd + 1);
                if (!StringUtils.isEmpty(vndrHdrGenId) && !StringUtils.isEmpty(vndrDetailAssgnedId)) {
                    this.alternateVendorHeaderGeneratedIdentifier = new Integer(vndrHdrGenId);
                    this.alternateVendorDetailAssignedIdentifier = new Integer(vndrDetailAssgnedId);
                }
            }
        }
        else {
            this.alternateVendorNumber = vendorNumber;
        }
    }

    /**
     * Sets alternate vendor fields based on a given VendorDetail.
     * 
     * @param vendorDetail the vendor detail used to set vendor fields.
     */
    public void templateAlternateVendor(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }
        this.setAlternateVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + VendorConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setAlternateVendorName(vendorDetail.getVendorName());
    }

    /**
     * Overriding this from the super class so that Note will use only the oldest PurchaseOrderDocument as the
     * documentBusinessObject.
     * 
     * @see org.kuali.rice.kns.document.Document#getDocumentBusinessObject()
     */
    @Override
    public PersistableBusinessObject getDocumentBusinessObject() {
        if (ObjectUtils.isNotNull(getPurapDocumentIdentifier()) && ((ObjectUtils.isNull(documentBusinessObject)) || ObjectUtils.isNull(((PurchaseOrderDocument) documentBusinessObject).getPurapDocumentIdentifier()))) {
            refreshDocumentBusinessObject();
        }
        else if (ObjectUtils.isNull(getPurapDocumentIdentifier()) && ObjectUtils.isNull(documentBusinessObject)) {
            // needed to keep populate happy
            documentBusinessObject = new PurchaseOrderDocument();
        }
        return documentBusinessObject;
    }

    public void refreshDocumentBusinessObject() {
        documentBusinessObject = SpringContext.getBean(PurchaseOrderService.class).getOldestPurchaseOrder(this, (PurchaseOrderDocument) this.documentBusinessObject);
    }

    public void setDocumentBusinessObject(PurchaseOrderDocument po) {
        documentBusinessObject = po;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PurchaseOrderItem.class;
    }

    @Override
    public Class getItemUseTaxClass() {
        return PurchaseOrderItemUseTax.class;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public RequisitionDocument getPurApSourceDocumentIfPossible() {
        RequisitionDocument sourceDoc = null;
        if (ObjectUtils.isNotNull(getRequisitionIdentifier())) {
            sourceDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(getRequisitionIdentifier());
        }
        return sourceDoc;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(RequisitionDocument.class);
    }

    public Integer getNewQuoteVendorDetailAssignedIdentifier() {
        return newQuoteVendorDetailAssignedIdentifier;
    }

    public void setNewQuoteVendorDetailAssignedIdentifier(Integer newQuoteVendorDetailAssignedIdentifier) {
        this.newQuoteVendorDetailAssignedIdentifier = newQuoteVendorDetailAssignedIdentifier;
    }

    public Integer getNewQuoteVendorHeaderGeneratedIdentifier() {
        return newQuoteVendorHeaderGeneratedIdentifier;
    }

    public void setNewQuoteVendorHeaderGeneratedIdentifier(Integer newQuoteVendorHeaderGeneratedIdentifier) {
        this.newQuoteVendorHeaderGeneratedIdentifier = newQuoteVendorHeaderGeneratedIdentifier;
    }

    public Integer getPurchaseOrderQuoteListIdentifier() {
        return purchaseOrderQuoteListIdentifier;
    }

    public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
        this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
    }

    /**
     * Returns true if a vendor has been awarded for this Purchase Order.
     * 
     * @return true if a vendor has been awarded for this Purchase Order.
     */
    public boolean isPurchaseOrderAwarded() {
        return (getAwardedVendorQuote() != null);
    }

    /**
     * Returns the quote from the awarded vendor.
     * 
     * @return the quote from the awarded vendor.
     */
    public PurchaseOrderVendorQuote getAwardedVendorQuote() {
        for (PurchaseOrderVendorQuote vendorQuote : purchaseOrderVendorQuotes) {
            if (vendorQuote.getPurchaseOrderQuoteAwardTimestamp() != null) {
                return vendorQuote;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        // return total without inactive and with below the line
        return getTotalDollarAmount(false, true);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getTotalDollarAmountAboveLineItems()
     */
    @Override
    public KualiDecimal getTotalDollarAmountAboveLineItems() {
        return getTotalDollarAmount(false, false);
    }

    /**
     * Gets the total dollar amount for this Purchase Order.
     * 
     * @param includeInactive indicates whether inactive items shall be included.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount for this Purchase Order.
     */
    public KualiDecimal getTotalDollarAmount(boolean includeInactive, boolean includeBelowTheLine) {
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurApItem item : (List<PurApItem>) getItems()) {
            
            if (item.getPurapDocument() == null) {
                item.setPurapDocument(this);
            }
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && (includeInactive || PurApItemUtils.checkItemActive(item))) {
                KualiDecimal totalAmount = item.getTotalAmount();
                KualiDecimal itemTotal = (totalAmount != null) ? totalAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getTotalPreTaxDollarAmount()
     */
    @Override
    public KualiDecimal getTotalPreTaxDollarAmount() {
        // return total without inactive and with below the line
        return getTotalPreTaxDollarAmount(false, true);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getTotalPreTaxDollarAmountAboveLineItems()
     */
    @Override
    public KualiDecimal getTotalPreTaxDollarAmountAboveLineItems() {
        return getTotalPreTaxDollarAmount(false, false);
    }

    /**
     * Gets the pre tax total dollar amount for this Purchase Order.
     * 
     * @param includeInactive indicates whether inactive items shall be included.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount for this Purchase Order.
     */
    public KualiDecimal getTotalPreTaxDollarAmount(boolean includeInactive, boolean includeBelowTheLine) {
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && (includeInactive || item.isItemActiveIndicator())) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    
    @Override
    public KualiDecimal getTotalTaxAmount() {
        // return total without inactive and with below the line
        return getTotalTaxAmount(false, true);
    }

    @Override
    public KualiDecimal getTotalTaxAmountAboveLineItems() {
        return getTotalTaxAmount(false, false);
    }

    /**
     * Gets the tax total amount for this Purchase Order.
     * 
     * @param includeInactive indicates whether inactive items shall be included.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount for this Purchase Order.
     */
    public KualiDecimal getTotalTaxAmount(boolean includeInactive, boolean includeBelowTheLine) {
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && (includeInactive || item.isItemActiveIndicator())) {
                KualiDecimal taxAmount = item.getItemTaxAmount();
                KualiDecimal itemTotal = (taxAmount != null) ? taxAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    /**
     * Returns true if this Purchase Order contains unpaid items in the Payment Request or Credit Memo.
     * 
     * @return true if this Purchase Order contains unpaid items in the Payment Request or Credit Memo.
     */
    public boolean getContainsUnpaidPaymentRequestsOrCreditMemos() {
        if (getRelatedViews().getRelatedPaymentRequestViews() != null) {
            for (PaymentRequestView element : getRelatedViews().getRelatedPaymentRequestViews()) {
                // If the PREQ is neither cancelled nor voided, check whether the PREQ has been paid.
                // If it has not been paid, then this method will return true.
                if (!PurapConstants.PaymentRequestStatuses.CANCELLED_STATUSES.contains(element.getStatusCode())) {
                    if (element.getPaymentPaidTimestamp() == null) {
                        return true;
                    }
                }
            }// endfor
        }
        if (getRelatedViews().getRelatedCreditMemoViews() != null) {
            for (CreditMemoView element : getRelatedViews().getRelatedCreditMemoViews()) {
                // If the CM is cancelled, check whether the CM has been paid.
                // If it has not been paid, then this method will return true.
                if (!CreditMemoStatuses.CANCELLED_STATUSES.contains(element.getCreditMemoStatusCode())) {
                    if (element.getCreditMemoPaidTimestamp() == null) {
                        return true;
                    }
                }
            }// endfor
        }
        return false;
    }
    
    public boolean getAdditionalChargesExist() {
        List<PurchaseOrderItem> items = this.getItems();
        for( PurchaseOrderItem item : items ) {
            if ((item != null) &&
                (item.getItemType() != null) && 
                (item.getItemType().isAdditionalChargeIndicator()) && 
                (item.getExtendedPrice() != null) && 
                (!KualiDecimal.ZERO.equals(item.getExtendedPrice()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used for routing only.
     * 
     * @deprecated
     */
    public String getContractManagerName() {
        return "";
    }

    /**
     * Used for routing only.
     * 
     * @deprecated
     */
    public void setContractManagerName(String contractManagerName) {
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

    public KualiDecimal getInternalPurchasingLimit() {
        //FIXME need the following because at places this field remains null because contract manager is not refreshed and null

        if (internalPurchasingLimit == null) {
            setInternalPurchasingLimit(SpringContext.getBean(PurchaseOrderService.class).getInternalPurchasingDollarLimit(this));
        }
        return internalPurchasingLimit;
    }

    public void setInternalPurchasingLimit(KualiDecimal internalPurchasingLimit) {
        this.internalPurchasingLimit = internalPurchasingLimit;
    }
    
    public boolean isPendingSplit() {
        return pendingSplit;
    }

    public void setPendingSplit(boolean pendingSplit) {
        this.pendingSplit = pendingSplit;
    }
    
    public boolean isCopyingNotesWhenSplitting() {
        return copyingNotesWhenSplitting;
    }

    public void setCopyingNotesWhenSplitting(boolean copyingNotesWhenSplitting) {
        this.copyingNotesWhenSplitting = copyingNotesWhenSplitting;
    }

    /**
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry, getPurapDocumentIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

        // don't think i should have to override this, but default isn't getting the right PO doc
        explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_DOCUMENT);
    }

    @Override
    public Class getPurchasingCapitalAssetItemClass() {
        return PurchaseOrderCapitalAssetItem.class;
    }

    @Override
    public Class getPurchasingCapitalAssetSystemClass() {
        return PurchaseOrderCapitalAssetSystem.class;
    }
    
    /**
     * Validates whether we can indeed close the PO. Return false and give error if 
     * the outstanding encumbrance amount of the trade in item is less than 0.
     * 
     * @param po
     * @return
     */
    public boolean canClosePOForTradeIn () {
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>)getItems()) {
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE) && item.getItemOutstandingEncumberedAmount().isLessThan(new KualiDecimal(0))) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TRADE_IN_OUTSTANDING_ENCUMBERED_AMOUNT_NEGATIVE, "amend the PO");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Provides answers to the following splits:
     * RequiresContractManagementReview
     * RequiresBudgetReview
     * VendorIsEmployeeOrNonResidentAlien
     * TransmissionMethodIsPrint
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.CONTRACT_MANAGEMENT_REVIEW_REQUIRED)) return isContractManagementReviewRequired();
        if (nodeName.equals(PurapWorkflowConstants.AWARD_REVIEW_REQUIRED)) return isAwardReviewRequired();
        if (nodeName.equals(PurapWorkflowConstants.BUDGET_REVIEW_REQUIRED)) return isBudgetReviewRequired();
        if (nodeName.equals(PurapWorkflowConstants.VENDOR_IS_EMPLOYEE_OR_NON_RESIDENT_ALIEN)) return isVendorEmployeeOrNonResidentAlien();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }
    
    private boolean isContractManagementReviewRequired() {
        KualiDecimal internalPurchasingLimit = SpringContext.getBean(PurchaseOrderService.class).getInternalPurchasingDollarLimit(this);
        return ((ObjectUtils.isNull(internalPurchasingLimit)) || (internalPurchasingLimit.compareTo(this.getTotalDollarAmount()) < 0));

    }

    private boolean isAwardReviewRequired() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        boolean objectCodeAllowed = true;
        
        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                
                objectCodeAllowed = isObjectCodeAllowedForAwardRouting(accountingLine, parameterService);
                // We should return true as soon as we have at least one objectCodeAllowed=true so that the PO will stop at Award
                // level.
                if (objectCodeAllowed) {
                    return objectCodeAllowed;
                }

            }
        }
        return objectCodeAllowed;        
    }
    
    private boolean isObjectCodeAllowedForAwardRouting(PurApAccountingLine accountingLine, ParameterService parameterService) {
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        // make sure object code is active
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            return false;
        }

        String chartCode = accountingLine.getChartOfAccountsCode();
        // check object level is in permitted list for award routing
        boolean objectCodeAllowed = parameterService.getParameterEvaluator(PurchaseOrderDocument.class, PurapParameterConstants.CG_ROUTE_OBJECT_LEVELS_BY_CHART, PurapParameterConstants.NO_CG_ROUTE_OBJECT_LEVELS_BY_CHART, chartCode, accountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluationSucceeds();

        if (!objectCodeAllowed) {
            // If the object level is not permitting for award routing, then we need to also
            // check object code is in permitted list for award routing
            objectCodeAllowed = parameterService.getParameterEvaluator(PurchaseOrderDocument.class, PurapParameterConstants.CG_ROUTE_OBJECT_CODES_BY_CHART, PurapParameterConstants.NO_CG_ROUTE_OBJECT_CODES_BY_CHART, chartCode, accountingLine.getFinancialObjectCode()).evaluationSucceeds();
        }
        return objectCodeAllowed;
    }
    
    private boolean isBudgetReviewRequired() {
        boolean alwaysRoutes = true;
        String documentHeaderId = null;
        String currentXpathExpression = null;
        String documentFiscalYearString = this.getPostingYear().toString();

        // if document's fiscal year is less than or equal to the current fiscal year
        if (SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().compareTo(Integer.valueOf(documentFiscalYearString)) >= 0) {
            // get list of sufficientfundItems
            List<SufficientFundsItem> fundsItems = SpringContext.getBean(SufficientFundsService.class).checkSufficientFunds(getPendingLedgerEntriesForSufficientFundsChecking());
                for (SufficientFundsItem fundsItem : fundsItems) {
                    if (this.getChartOfAccountsCode().equalsIgnoreCase(fundsItem.getAccount().getChartOfAccountsCode())) {
                    LOG.debug("Chart code of rule extension matches chart code of at least one Sufficient Funds Item");
                    return true;
                }
            }
        }

        return false;
    }


    private boolean isVendorEmployeeOrNonResidentAlien() {
        String vendorHeaderGeneratedId = this.getVendorHeaderGeneratedIdentifier().toString();
        if (StringUtils.isBlank(vendorHeaderGeneratedId)) {
            // no vendor header id so can't check for proper tax routing
            return false;
        }
        VendorService vendorService = SpringContext.getBean(VendorService.class);
        boolean routeDocumentAsEmployeeVendor = vendorService.isVendorInstitutionEmployee(Integer.valueOf(vendorHeaderGeneratedId));
        boolean routeDocumentAsForeignVendor = vendorService.isVendorForeign(Integer.valueOf(vendorHeaderGeneratedId));
        if ((!routeDocumentAsEmployeeVendor) && (!routeDocumentAsForeignVendor)) {
            // no need to route
            return false;
        }

        return true;
    }
    
    public List<Account> getAccountsForAwardRouting() {
        List<Account> accounts = new ArrayList<Account>();
        
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                if (isObjectCodeAllowedForAwardRouting(accountingLine, parameterService)) {
                    if (ObjectUtils.isNull(accountingLine.getAccount())) {
                        accountingLine.refreshReferenceObject("account");
                    }
                    if (accountingLine.getAccount() != null && !accounts.contains(accountingLine.getAccount())) {
                        accounts.add(accountingLine.getAccount());
                    }
                }
            }
        }
        return accounts;
    }
    
    public DocSearchCriteriaDTO convertSelections(DocSearchCriteriaDTO searchCriteria) {

        for (SearchAttributeCriteriaComponent comp : searchCriteria.getSearchableAttributes()) {  
            if (comp.getLookupableFieldType().equals(Field.MULTISELECT)) {
                List<String> values = comp.getValues();
                List<String> newVals = new ArrayList<String>();
                if (values.contains("INCOMPLETE")) {
                    for (String str : PurchaseOrderStatuses.INCOMPLETE_STATUSES)
                        newVals.add(str);
                } if (values.contains("COMPLETE")) {
                    for (String str : PurchaseOrderStatuses.COMPLETE_STATUSES)
                        newVals.add(str);
                } 
                
                for (String str : values) {
                    newVals.add(str);
                }
                
                comp.setValues(newVals);
            }
        }
        return searchCriteria;
    }
    
    /**
     * @return the purchase order current indicator
     */
    public boolean getPurchaseOrderCurrentIndicatorForSearching() {
        return purchaseOrderCurrentIndicator;
    }
    
    public String getDocumentTitleForResult() throws WorkflowException{
        return SpringContext.getBean(KualiWorkflowInfo.class).getDocType(this.getDocumentHeader().getWorkflowDocument().getDocumentType()).getDocTypeLabel();
    }
    
    /**
     * Checks whether the purchase order needs a warning to be displayed, i.e. it never has been opened.
     * @return true if the purchase order needs a warning; false otherwise.
     */
    public boolean getNeedWarning() {
        return getPurchaseOrderInitialOpenTimestamp() == null;
    }

}
