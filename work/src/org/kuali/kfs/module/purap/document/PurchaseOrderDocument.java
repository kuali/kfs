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

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PaymentTermType;
import org.kuali.module.purap.bo.PurchaseOrderVendorChoice;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.RecurringPaymentFrequency;
import org.kuali.module.purap.bo.ShippingPaymentTerms;
import org.kuali.module.purap.bo.ShippingTitle;
import org.kuali.module.purap.bo.SourceDocumentReference;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.service.PurchaseOrderPostProcessorService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Purchase Order Document
 */
public class PurchaseOrderDocument extends PurchasingDocumentBase implements Copyable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocument.class);

    private Date purchaseOrderCreateDate;
    private Integer requisitionIdentifier;
    private String purchaseOrderVendorChoiceCode;
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;
    private String recurringPaymentFrequencyCode;
    private KualiDecimal recurringPaymentAmount;
    private Date recurringPaymentDate;
    private KualiDecimal initialPaymentAmount;
    private Date initialPaymentDate;
    private KualiDecimal finalPaymentAmount;
    private Date finalPaymentDate;
    private Date purchaseOrderInitialOpenDate;
    private Date purchaseOrderLastTransmitDate;
    private Date purchaseOrderQuoteDueDate;
    private String purchaseOrderQuoteTypeCode;
    private String purchaseOrderQuoteVendorNoteText;
    private boolean purchaseOrderConfirmedIndicator;
    private String purchaseOrderCommodityDescription;
    private Integer purchaseOrderPreviousIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorNumber; //not persisted in db
    private String alternateVendorName;
    private String statusChange;
    private String statusChangeNote;
    private boolean purchaseOrderCurrentIndicator;
    private boolean pendingActionIndicator;
    private Date purchaseOrderFirstTransmissionDate;
    
    private PurchaseOrderVendorChoice purchaseOrderVendorChoice;
    private PaymentTermType vendorPaymentTerms;
    private ShippingTitle vendorShippingTitle;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private RecurringPaymentFrequency recurringPaymentFrequency;
    
    //COLLECTIONS
    private List<PurchaseOrderVendorStipulation> purchaseOrderVendorStipulations;

    /**
	 * Default constructor.
	 */
	public PurchaseOrderDocument() {
        super();
        this.purchaseOrderVendorStipulations = new TypedArrayList( PurchaseOrderVendorStipulation.class );
    }

    public void populatePurchaseOrderFromRequisition(RequisitionDocument requisitionDocument) {
// TODO fix this (is this data correct?  is there a better way of doing this?
//        this.setPurchaseOrderCreateDate(requisitionDocument.getDocumentHeader().getWorkflowDocument().getCreateDate());
        this.setPurchaseOrderCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
        
        this.getDocumentHeader().setOrganizationDocumentNumber(requisitionDocument.getDocumentHeader().getOrganizationDocumentNumber());
        this.getDocumentHeader().setFinancialDocumentDescription(requisitionDocument.getDocumentHeader().getFinancialDocumentDescription());

        this.setPurchaseOrderBeginDate(requisitionDocument.getPurchaseOrderBeginDate());
        
        this.setBillingCityName(requisitionDocument.getBillingCityName());
        this.setBillingCountryCode(requisitionDocument.getBillingCountryCode());
        this.setBillingLine1Address(requisitionDocument.getBillingLine1Address());
        this.setBillingLine2Address(requisitionDocument.getBillingLine2Address());
        this.setBillingName(requisitionDocument.getBillingName());
        this.setBillingPhoneNumber(requisitionDocument.getBillingPhoneNumber());
        this.setBillingPostalCode(requisitionDocument.getBillingPostalCode());
        this.setBillingStateCode(requisitionDocument.getBillingStateCode());
        this.setContractManagerCode(requisitionDocument.getContractManagerCode());
        this.setPurchaseOrderCostSourceCode(requisitionDocument.getPurchaseOrderCostSourceCode());
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
        this.setPostingYear(requisitionDocument.getPostingYear());
        this.setPurchaseOrderEndDate(requisitionDocument.getPurchaseOrderEndDate());
        this.setChartOfAccountsCode(requisitionDocument.getChartOfAccountsCode());
        this.setFundingSourceCode(requisitionDocument.getFundingSourceCode());
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
        this.setVendorCityName(requisitionDocument.getVendorCityName());
        this.setVendorContract(requisitionDocument.getVendorContract());
        this.setVendorCountryCode(requisitionDocument.getVendorCountryCode());
        this.setVendorCustomerNumber(requisitionDocument.getVendorCustomerNumber());
        this.setVendorDetailAssignedIdentifier(requisitionDocument.getVendorDetailAssignedIdentifier());
        this.setVendorFaxNumber(requisitionDocument.getVendorFaxNumber());
        this.setVendorHeaderGeneratedIdentifier(requisitionDocument.getVendorHeaderGeneratedIdentifier());
        this.setVendorLine1Address(requisitionDocument.getVendorLine1Address());
        this.setVendorLine2Address(requisitionDocument.getVendorLine2Address());
        this.setVendorName(requisitionDocument.getVendorName());
        this.setVendorNoteText(requisitionDocument.getVendorNoteText());
        this.setVendorPhoneNumber(requisitionDocument.getVendorPhoneNumber());
        this.setVendorPostalCode(requisitionDocument.getVendorPostalCode());
        this.setVendorRestrictedIndicator(requisitionDocument.getVendorRestrictedIndicator());
        this.setVendorStateCode(requisitionDocument.getVendorStateCode());
        this.setExternalOrganizationB2bSupplierIdentifier(requisitionDocument.getExternalOrganizationB2bSupplierIdentifier());
        this.setRequisitionSourceCode(requisitionDocument.getRequisitionSourceCode());

        this.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
        //copy items from req to pending (which will copy the item's accounts and assets)
//        List items = new ArrayList();
//        for (Iterator iter = requisitionDocument.getItems().iterator(); iter.hasNext();) {
//          RequisitionItem reqItem = (RequisitionItem) iter.next();
//          items.add(new PurchaseOrderItem(reqItem, this));
//        }
//        this.setItems(items);
        
      // TODO Naser This is the code Naser is working on
 /*
        SourceDocumentReference sourceDocumentReference = new SourceDocumentReference();
        Integer ReqId = this.getRequisitionIdentifier();
        // The following code is assuming that any PO has one and only one requisition:
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.SOURCE_DOCUMENT_IDENTIFIER, this.getRequisitionIdentifier());
        List<SourceDocumentReference> sourceDocumentReferences = new ArrayList(SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(SourceDocumentReference.class,  
                fieldValues, PurapPropertyConstants.SOURCE_DOCUMENT_IDENTIFIER, true));
        if (sourceDocumentReferences.size()>= 1){
            Integer sourceDocumentReferenceGeneratedId = sourceDocumentReferences.get(0).getSourceDocumentReferenceGeneratedIdentifier();
            sourceDocumentReference.setSourceDocumentReferenceGeneratedIdentifier(sourceDocumentReferences.get(0).getSourceDocumentReferenceGeneratedIdentifier());
    }
        String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(this.getClass());
        String documentTypeCode = SpringServiceLocator.getDataDictionaryService().getDocumentTypeCodeByTypeName(documentTypeName);
        sourceDocumentReference.setSourceFinancialDocumentTypeCode(documentTypeCode);
       // sourceDocumentReference.setSourceFinancialDocumentTypeCode("PO");
        // This line is giving this error:
        
        //javax.servlet.ServletException: OJB operation; SQL []; ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
        // ; nested exception is java.sql.SQLException: ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
        //sourceDocumentReference.setSourceDocumentObjectIdentifier(this.getObjectId());
        sourceDocumentReference.setSourceDocumentObjectIdentifier("POObjectID");
        //sourceDocumentReferences = new TypedArrayList(SourceDocumentReference.class);
        sourceDocumentReferences.add(sourceDocumentReference);
        this.setSourceDocumentReferences(sourceDocumentReferences);
     */
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    /**
     * Perform logic needed to initiate PO Document
     */
    public void initiateDocument() {

    }

    public PurchaseOrderVendorStipulation getPurchaseOrderVendorStipulation(int index) {
        while (getPurchaseOrderVendorStipulations().size() <= index) {
            getPurchaseOrderVendorStipulations().add(new PurchaseOrderVendorStipulation());
        }
        return (PurchaseOrderVendorStipulation)purchaseOrderVendorStipulations.get(index);
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

        // additional processing
        PurchaseOrderPostProcessorService popp = 
            SpringServiceLocator.getPurchaseOrderService().convertDocTypeToService(getDocumentHeader().getWorkflowDocument().getDocumentType());
        // null if defined as empty string in map
        if (popp != null) {
            popp.handleRouteStatusChange(this);
        }
    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();


    }

    // SETTERS AND GETTERS
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

    public Date getPurchaseOrderCreateDate() {
        return purchaseOrderCreateDate;
    }

    public void setPurchaseOrderCreateDate(Date purchaseOrderCreateDate) {
        this.purchaseOrderCreateDate = purchaseOrderCreateDate;
    }

    public Date getPurchaseOrderInitialOpenDate() {
        return purchaseOrderInitialOpenDate;
    }

    public void setPurchaseOrderInitialOpenDate(Date purchaseOrderInitialOpenDate) {
        this.purchaseOrderInitialOpenDate = purchaseOrderInitialOpenDate;
    }

    public Date getPurchaseOrderLastTransmitDate() {
        return purchaseOrderLastTransmitDate;
    }

    public void setPurchaseOrderLastTransmitDate(Date purchaseOrderLastTransmitDate) {
        this.purchaseOrderLastTransmitDate = purchaseOrderLastTransmitDate;
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

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorShippingPaymentTermsCode() {
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
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


    public void setStatusChange(String statusChange) {
        this.statusChange = statusChange;
    }

    public String getStatusChangeNote() {
        return statusChangeNote;
    }

    public void setStatusChangeNote(String statusChangeNote) {
        this.statusChangeNote = statusChangeNote;
    }

    /**
     * Gets the pendingActionIndicator attribute. 
     * @return Returns the pendingActionIndicator.
     */
    public boolean isPendingActionIndicator() {
        return pendingActionIndicator;
    }

    /**
     * Sets the pendingActionIndicator attribute value.
     * @param pendingActionIndicator The pendingActionIndicator to set.
     */
    public void setPendingActionIndicator(boolean pendingActionIndicator) {
        this.pendingActionIndicator = pendingActionIndicator;
    }

    /**
     * Gets the purchaseOrderCurrentIndicator attribute. 
     * @return Returns the purchaseOrderCurrentIndicator.
     */
    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    /**
     * Sets the purchaseOrderCurrentIndicator attribute value.
     * @param purchaseOrderCurrentIndicator The purchaseOrderCurrentIndicator to set.
     */
    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }

    /**
     * Gets the purchaseOrderFirstTransmissionDate attribute. 
     * @return Returns the purchaseOrderFirstTransmissionDate.
     */
    public Date getPurchaseOrderFirstTransmissionDate() {
        return purchaseOrderFirstTransmissionDate;
    }

    /**
     * Sets the purchaseOrderFirstTransmissionDate attribute value.
     * @param purchaseOrderFirstTransmissionDate The purchaseOrderFirstTransmissionDate to set.
     */
    public void setPurchaseOrderFirstTransmissionDate(Date purchaseOrderFirstTransmissionDate) {
        this.purchaseOrderFirstTransmissionDate = purchaseOrderFirstTransmissionDate;
    }    
    
    /**
     * Gets the alternateVendorNumber attribute. 
     * @return Returns the alternateVendorNumber.
     */
    public String getAlternateVendorNumber() {
        String hdrGenId = "";
        String detAssgndId = "";
        String vendorNumber = "";
        if( this.alternateVendorHeaderGeneratedIdentifier != null ) {
            hdrGenId = this.alternateVendorHeaderGeneratedIdentifier.toString();
}
        if( this.alternateVendorDetailAssignedIdentifier != null ) {
            detAssgndId = this.alternateVendorDetailAssignedIdentifier.toString();
        }
        if (!StringUtils.isEmpty(hdrGenId) && !StringUtils.isEmpty(detAssgndId)) {
            vendorNumber = hdrGenId+"-"+detAssgndId;
        }
        return vendorNumber;
    }
    /**
     * Sets the alternateVendorNumber attribute value.
     * @param alternateVendorNumber The vendorNumber to set.
     */
    public void setAlternateVendorNumber(String vendorNumber) {
        if (! StringUtils.isEmpty(vendorNumber)) {
            int dashInd = vendorNumber.indexOf("-");
            if (vendorNumber.length() >= dashInd) {
                String vndrHdrGenId = vendorNumber.substring( 0, dashInd );
                String vndrDetailAssgnedId = vendorNumber.substring( dashInd + 1 );
                if (!StringUtils.isEmpty(vndrHdrGenId) && !StringUtils.isEmpty(vndrDetailAssgnedId)) {
                    this.alternateVendorHeaderGeneratedIdentifier = new Integer(vndrHdrGenId);
                    this.alternateVendorDetailAssignedIdentifier = new Integer(vndrDetailAssgnedId);
                }
            }
        } else {
            this.alternateVendorNumber = vendorNumber;
        }
    }
    
    /**
     * Convenience method to set alternate vendor fields based on a given VendorDetail.
     * 
     * @param vendorDetail
     */
    public void templateAlternateVendor(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }
    
        this.setAlternateVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + PurapConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setAlternateVendorName(vendorDetail.getVendorName());
    }
    
    @Override
    public void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
            super.afterInsert(persistenceBroker);
            
            //TODO Naser This is the code Naser is working on
            
            SourceDocumentReference sourceDocumentReference = new SourceDocumentReference();
            Integer ReqId = this.getRequisitionIdentifier();
            // The following code is assuming that any PO has one and only one requisition:
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.SOURCE_DOCUMENT_IDENTIFIER, this.getRequisitionIdentifier());
            List<SourceDocumentReference> sourceDocumentReferences = new ArrayList(SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(SourceDocumentReference.class,  
                    fieldValues, PurapPropertyConstants.SOURCE_DOCUMENT_IDENTIFIER, true));
            if (sourceDocumentReferences.size()>= 1){
                Integer sourceDocumentReferenceGeneratedId = sourceDocumentReferences.get(0).getSourceDocumentReferenceGeneratedIdentifier();
                sourceDocumentReference.setSourceDocumentReferenceGeneratedIdentifier(sourceDocumentReferences.get(0).getSourceDocumentReferenceGeneratedIdentifier());
}
            String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(this.getClass());
            String documentTypeCode = SpringServiceLocator.getDataDictionaryService().getDocumentTypeCodeByTypeName(documentTypeName);
            sourceDocumentReference.setSourceFinancialDocumentTypeCode(documentTypeCode);
           // sourceDocumentReference.setSourceFinancialDocumentTypeCode("PO");
            // This line is giving this error:
            //javax.servlet.ServletException: OJB operation; SQL []; ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
            // ; nested exception is java.sql.SQLException: ORA-01400: cannot insert NULL into ("KULDEV"."PUR_SRC_DOC_REF_T"."SRC_DOC_OBJ_ID")
            String objID = this.getObjectId();
            sourceDocumentReference.setSourceDocumentObjectIdentifier(this.getObjectId());
            
           // sourceDocumentReference.setSourceDocumentObjectIdentifier("POObjectID");
            //sourceDocumentReferences = new TypedArrayList(SourceDocumentReference.class);
            sourceDocumentReferences.add(sourceDocumentReference);
            this.setSourceDocumentReferences(sourceDocumentReferences);
    }

    public void toCopy(String docType) throws WorkflowException {
        TransactionalDocument newDoc = (TransactionalDocument) SpringServiceLocator.getDocumentService().getNewDocument(docType);
        newDoc.getDocumentHeader().setFinancialDocumentDescription(getDocumentHeader().getFinancialDocumentDescription());
        newDoc.getDocumentHeader().setOrganizationDocumentNumber(getDocumentHeader().getOrganizationDocumentNumber());

        try {
            ObjectUtils.setObjectPropertyDeep(this, PropertyConstants.DOCUMENT_NUMBER, documentNumber.getClass(), newDoc.getDocumentNumber());
        }
        catch (Exception e) {
            LOG.error("Unable to set document number property in copied document " + e.getMessage());
            throw new RuntimeException("Unable to set document number property in copied document " + e.getMessage());
        }
        
        // replace current documentHeader with new documentHeader
        setDocumentHeader(newDoc.getDocumentHeader());
        
    }        
            
}
