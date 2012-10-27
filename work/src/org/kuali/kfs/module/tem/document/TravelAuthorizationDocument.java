/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "TEM_TRVL_AUTH_DOC_T")
public class TravelAuthorizationDocument extends TravelDocumentBase {
    
    protected static Logger LOG = Logger.getLogger(TravelAuthorizationDocument.class);
    
    private KualiDecimal perDiemAdjustment;

    // Emergency contact info section
    private String cellPhoneNumber;
    private String regionFamiliarity;
    private String citizenshipCountryCode;
    private Country citizenshipCountry;
    private List<TransportationModeDetail> transportationModes = new ArrayList<TransportationModeDetail>();

    private String ojbConcreteClass;

    /**
     * Creates a new instance of the Travel Request Document. Initializes the empty arrays as well as the line tracking numbers
     */
    public TravelAuthorizationDocument() {
        super();
        setOjbConcreteClass(getClass().getName());
    }

    public String getOjbConcreteClass() {
        return ojbConcreteClass;
    }

    public void setOjbConcreteClass(final String ojbConcreteClass) {
        this.ojbConcreteClass = ojbConcreteClass;
    }

    public TravelAuthorizationAmendmentDocument toCopyTAA() throws WorkflowException {
        TravelAuthorizationAmendmentDocument doc = (TravelAuthorizationAmendmentDocument) SpringContext.getBean(DocumentService.class).getNewDocument(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
        toCopyTravelAuthorizationDocument(doc);       
        
        doc.getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);
        
        return doc;
    }

    public TravelAuthorizationCloseDocument toCopyTAC() throws WorkflowException {
        TravelAuthorizationCloseDocument doc = (TravelAuthorizationCloseDocument) SpringContext.getBean(DocumentService.class).getNewDocument(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
        toCopyTravelAuthorizationDocument(doc);
        
        return doc;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#updateAppDocStatus(java.lang.String)
     */
    @Override
    public boolean updateAppDocStatus(String status) {
        boolean updated = false;

        //using the parent's update app doc status logic
        updated = super.updateAppDocStatus(status);
        
        if (!updated && (status.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD) 
                || status.equals(TravelAuthorizationStatusCodeKeys.OPEN_REIMB)
                || status.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)
                || status.equals(TravelAuthorizationStatusCodeKeys.CANCELLED)
                || status.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)
                || status.equals(TravelAuthorizationStatusCodeKeys.CLOSED))){
            setAppDocStatus(status);
            updated = saveAppDocStatus();
        }
        return updated;
    }

    /**
     * 
     * @param doc
     * @param documentID
     */
    private void toCopyTravelAuthorizationDocument(TravelAuthorizationDocument copyToDocument) {
        String documentID = copyToDocument.getDocumentNumber();

        //copy over all possible elements from this self to TravelAuthorizationDocument except document header 
        BeanUtils.copyProperties(this, copyToDocument, new String[]{KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME});
        
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        BeanUtils.copyProperties(copyToDocument.getDocumentHeader(), documentHeader);
        documentHeader.setOrganizationDocumentNumber(this.getDocumentHeader().getOrganizationDocumentNumber());
        copyToDocument.setDocumentHeader(documentHeader);
        
        copyToDocument.setTransportationModes(getTravelDocumentService().copyTransportationModeDetails(getTransportationModes(), documentID));
        copyToDocument.setPerDiemExpenses(getTravelDocumentService().copyPerDiemExpenses(getPerDiemExpenses(), documentID));
        copyToDocument.setSpecialCircumstances(getTravelDocumentService().copySpecialCircumstances(getSpecialCircumstances(), documentID));
        copyToDocument.setTravelerDetailId(null);
        copyToDocument.setTraveler(getTravelerService().copyTravelerDetail(getTraveler(), documentID));
        copyToDocument.setGroupTravelers(getTravelDocumentService().copyGroupTravelers(getGroupTravelers(), documentID));               
        copyToDocument.setTravelAdvances(getTravelDocumentService().copyTravelAdvances(getTravelAdvances(), documentID));
        copyToDocument.setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(getActualExpenses(), documentID));
        copyToDocument.setImportedExpenses(new ArrayList<ImportedExpense>());
        
        copyToDocument.getDocumentHeader().getBoNotes().clear();
        copyToDocument.getBoNotes().clear();
        copyToDocument.getDocumentHeader().setDocumentDescription(getDocumentHeader().getDocumentDescription());
        copyToDocument.setTravelDocumentIdentifier(getTravelDocumentIdentifier());
        copyToDocument.setDocumentNumber(documentID);
        copyToDocument.setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());
        
        //reset to only include the encumbrance line
        List<TemSourceAccountingLine> newList = new ArrayList<TemSourceAccountingLine>();
        int sequence = 1;
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)copyToDocument.getEncumbranceSourceAccountingLines()){
            line.setSequenceNumber(new Integer(sequence));
            sequence++;
            newList.add(line);
        }
        copyToDocument.setSourceAccountingLines(newList);
    }
    
    /**
     * Gets the perDiemAdjustment attribute.
     * 
     * @return Returns the perDiemAdjustment.
     */
    @Override
    @Column(name = "PER_DIEM_ADJ", precision = 19, scale = 2)
    public KualiDecimal getPerDiemAdjustment() {
        return perDiemAdjustment == null?KualiDecimal.ZERO:perDiemAdjustment;
    }

    /**
     * Sets the perDiemAdjustment attribute value.
     * 
     * @param perDiemAdjustment The perDiemAdjustment to set.
     */
    @Override
    public void setPerDiemAdjustment(KualiDecimal perDiemAdjustment) {
        this.perDiemAdjustment = perDiemAdjustment == null?KualiDecimal.ZERO:perDiemAdjustment;
    }

    /**
     * This method returns the traveler's cell phone number
     * 
     * @return cell phone of traveler
     */
    @Column(name = "CELL_PH_NUM", length = 10)
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    /**
     * This method sets the cell phone of the traveler
     * 
     * @param cellPhoneNumber
     */
    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }


    /**
     * This method gets the traveler's familiarity with the region
     * 
     * @return traveler's regional familiarity
     */
    @Column(name = "rgn_famil")
    public String getRegionFamiliarity() {
        return regionFamiliarity;
    }

    /**
     * This method sets the traveler's familiarity with the region
     * 
     * @param regionFamiliarity
     */
    public void setRegionFamiliarity(String regionFamiliarity) {
        this.regionFamiliarity = regionFamiliarity;
    }

    /**
     * This method returns the citizenship country code for the traveler
     * 
     * @return the traveler's citizenship
     */
    @Column(name = "CTZN_CNTRY_CD")
    public String getCitizenshipCountryCode() {
        return citizenshipCountryCode;
    }

    /**
     * This method sets the traveler's citizenship country
     * 
     * @param citizenshipCountryCode
     */
    public void setCitizenshipCountryCode(String citizenshipCountryCode) {
        this.citizenshipCountryCode = citizenshipCountryCode;
    }

    /**
     * This method returns the country that the traveler is a citizen of
     * 
     * @return country the traveler is a citizen of
     */
    @Transient
    public Country getCitizenshipCountry() {
        citizenshipCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(citizenshipCountryCode, citizenshipCountry);
        return citizenshipCountry;
    }

    /**
     * This method sets the country the traveler is a citizen of Should only be used during OJB population
     * 
     * @param citizenshipCountry
     */
    public void setCitizenshipCountry(Country citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    @Override
    @Transient
    public List<TransportationModeDetail> getTransportationModes() {
        return transportationModes;
    }

    /**
     * This method sets the transportation modes
     * 
     * @param transportationModes
     */
    @Override
    public void setTransportationModes(List<TransportationModeDetail> transportationModes) {
        this.transportationModes = transportationModes;
    }

    /**
     * Helper method to add a transportation mode detail to a Travel Request
     * 
     * @param transportationModeDetail
     */
    public void addTransportationMode(TransportationModeDetail transportationModeDetail) {
        transportationModeDetail.setDocumentNumber(this.documentNumber);
        this.transportationModes.add(transportationModeDetail);
    }


    /**
     * This method populates the list of transportation modes from an array of strings
     * 
     * @param selectedTransportationModes
     */
    @Transient
    public void setTransportationModeCodes(List<String> selectedTransportationModes) {
        // now we need to lookup the corresponding transportation mode and add it
        for (String string : selectedTransportationModes) {

            // now we need to determine if this mode is already stored as a detail object

            TransportationModeDetail detail = new TransportationModeDetail();
            detail.setTransportationModeCode(string);
            detail.setDocumentNumber(this.documentNumber);

            if (!transportationModes.contains(detail)) {
                this.addTransportationMode(detail);
            }
        }

        // now for removed items
        if (selectedTransportationModes.size() != this.transportationModes.size()) {

            // need to figure out which items need to be removed from the transportation modes array

            for (ListIterator<TransportationModeDetail> iter = transportationModes.listIterator(); iter.hasNext();) {
                TransportationModeDetail detail = iter.next();
                if (!selectedTransportationModes.contains(detail.getTransportationModeCode())) {
                    // we need to remove this item from collection (and OJB should manage the rest
                    iter.remove();
                }
            }
        }
    }

    /**
     * The toCopy method is forcing me to put in this method so it doesn't choke when trying to copy over
     */
    @Transient
    public List<String> getTransportationModeCodes() {
        List<String> codes = new ArrayList<String>();
        for (TransportationModeDetail mode : transportationModes) {
            codes.add(mode.getTransportationModeCode());
        }
        return codes;
    }

    /**
     * This method adds a new travel expense line to the managed collection
     * 
     * @param travel expense line
     */
    public void addActualExpenseLine(ActualExpense line) {
        line.setDocumentLineNumber(getActualExpenses().size() + 1);
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ActualExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
        getActualExpenses().add(line);
        logErrors();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();
        setAppDocStatus(TravelAuthorizationStatusCodeKeys.IN_PROCESS);
        
        //always default trip begin/date
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        calendar.add(Calendar.DAY_OF_MONTH, 2);
        setTripEnd(new Timestamp(calendar.getTimeInMillis()));

    }

    /**
     * Adds a new emergency contact line
     * 
     * @param line
     */
    public void addEmergencyContactLine(TravelerDetailEmergencyContact line) {
        if (getTraveler() != null) {
            line.setFinancialDocumentLineNumber(getTraveler().getEmergencyContacts().size() + 1);
            line.setDocumentNumber(this.documentNumber);       
            line.setTravelerDetailId(getTraveler().getId());
            getTraveler().getEmergencyContacts().add(line);
        }
    }
    
    public void addPerDiemExpensesLine(PerDiemExpense line) {
        line.setDocumentNumber(this.documentNumber);
        this.perDiemExpenses.add(line);
    }

    /**
     * Determines if this document should be able to return to the fiscal officer node again. This can happen if the user has rights
     * to reroute and also if the document is already ENROUTE.
     * 
     * @return true if the document is currently enroute and reroutable
     */
    @Override
    public boolean canReturn() {
        return getDocumentHeader().getWorkflowDocument().stateIsEnroute();
    }

    @Override
    @Transient
    public KualiDecimal getEncumbranceTotal() {
        TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.PER_DIEM);
        KualiDecimal encTotal = service.getAllExpenseTotal(this, false);
        
        service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.ACTUAL);
        encTotal = service.getAllExpenseTotal(this, false).add(encTotal);

        if (ObjectUtils.isNotNull(this.perDiemAdjustment) && perDiemAdjustment.isPositive()) {
            encTotal = encTotal.subtract(this.perDiemAdjustment);
        }
        
        return encTotal;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {

        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

        // set the encumbrance update code Set to ENCUMB_UPDT_REFERENCE_DOCUMENT_CD ("R")
        explicitEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        explicitEntry.setDocumentNumber(this.getDocumentNumber());

        String referenceDocumentNumber = this.getTravelDocumentIdentifier();
        if (ObjectUtils.isNotNull(referenceDocumentNumber)) {
            explicitEntry.setReferenceFinancialDocumentNumber(referenceDocumentNumber);
            explicitEntry.setReferenceFinancialDocumentTypeCode(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            explicitEntry.setReferenceFinancialSystemOriginationCode(TemConstants.ORIGIN_CODE);
        }
        // set the offset entry to Debit "D"
        explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

        this.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = this.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            String balanceType = tripType.getEncumbranceBalanceType();
            explicitEntry.setFinancialBalanceTypeCode(balanceType);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {

        boolean customized = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);

        // set the encumbrance update code
        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);

        // set the offset entry to Credit "C"
        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        offsetEntry.setDocumentNumber(this.getDocumentNumber());

        String referenceDocumentNumber = this.getTravelDocumentIdentifier();
        if (ObjectUtils.isNotNull(referenceDocumentNumber)) {
            offsetEntry.setReferenceFinancialDocumentNumber(referenceDocumentNumber);
            offsetEntry.setReferenceFinancialDocumentTypeCode(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            offsetEntry.setReferenceFinancialSystemOriginationCode(TemConstants.ORIGIN_CODE);
        }
        
        String balanceType = getTravelEncumbranceService().getEncumbranceBalanceTypeByTripType(this);
        if (StringUtils.isNotEmpty(balanceType)) {
            offsetEntry.setFinancialBalanceTypeCode(balanceType);
            customized &= true;
        }
        return customized;
    }

    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            LOG.debug("New route status is " + statusChangeEvent.getNewRouteStatus());
                        
            if (!(this instanceof TravelAuthorizationCloseDocument)) {
                // for some reason when it goes to final it never updates to the last status, updating TA status to OPEN REIMBURSEMENT
                updateAppDocStatus(TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
                
                getTravelAuthorizationService().createTravelAdvanceDVDocument(this);
                getTravelAuthorizationService().createCustomerInvoice(this);
                
                //If the hold new fiscal year encumbrance indicator is true and the trip end date is after the current fiscal year end date then mark all the gl pending entries 
                //as 'H' (Hold) otherwise mark all the gl pending entries as 'A' (approved)
                if (getGeneralLedgerPendingEntries() != null && !getGeneralLedgerPendingEntries().isEmpty()) {
                    if(getParameterService().getIndicatorParameter(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.HOLD_NEW_FY_ENCUMBRANCES_IND)) {
                        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
                        java.util.Date endDate = universityDateService.getLastDateOfFiscalYear(universityDateService.getCurrentFiscalYear());
                        if (ObjectUtils.isNotNull(getTripEnd()) && getTripEnd().after(endDate)) {
                            for(GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                                glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.HOLD);
                            }
                        }
                    } else {
                        for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                            glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                        }
                    }
                    SpringContext.getBean(BusinessObjectService.class).save(getGeneralLedgerPendingEntries());
                }
            }
        }
    }

    /**
     * NOTE: need to find out all reference to TA's source accounting lines
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLines()
     */
    @Override
    public List getSourceAccountingLines() {
        return super.getSourceAccountingLines();
    }
    
    /**
     * Get all of the encumbrance source accounting lines (for estimated expenses) - do not include any import
     * expense lines 
     * 
     * @return
     */
    public List<TemSourceAccountingLine> getEncumbranceSourceAccountingLines() {
        List<TemSourceAccountingLine> encumbranceLines = new ArrayList<TemSourceAccountingLine>();
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>) getSourceAccountingLines()){
            if (TemConstants.ENCUMBRANCE.equals(line.getCardType())){
                encumbranceLines.add(line);
            }
        }
        return encumbranceLines;
    }

    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.SPECIAL_REQUEST))
            return requiresSpecialRequestReviewRouting();
        if (nodeName.equals(TemWorkflowConstants.INTL_TRAVEL))
            return requiresInternationalTravelReviewRouting();
        if (nodeName.equals(TemWorkflowConstants.RISK_MANAGEMENT))
            return requiresRiskManagementReviewRouting();
        if (nodeName.equals(TemWorkflowConstants.TRVL_ADV_REQUESTED))
            return requiresTravelAdvanceReviewRouting();
        if (nodeName.equals(TemWorkflowConstants.DIVISION_APPROVAL_REQUIRED))
            return requiresDivisionApprovalRouting();
        if (nodeName.equals(TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED))
            return requiresAccountApprovalRouting();
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW))
            return requiresTravelerApprovalRouting();
        if (nodeName.equals(TemWorkflowConstants.SEPARATION_OF_DUTIES)) {
            return requiresSeparationOfDutiesRouting();
        }
       throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * Traveler approval is required if it requires review for travel advance
     * 
     * @return
     */
    private boolean requiresTravelerApprovalRouting() {
        boolean routeToTraveler = false;
        
        //If there's travel advances, route to traveler if necessary
        if (requiresTravelAdvanceReviewRouting()){
            String initiator = this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
            String travelerID = this.getTraveler().getPrincipalId();

            //traveler must accept policy, if initiator is arranger, the traveler will have to accept later.
            routeToTraveler = travelerID != null && !initiator.equals(travelerID);
        }
        return routeToTraveler;
    }

    /**
     * 
     * @return
     */
    private boolean requiresTravelAdvanceReviewRouting() {
        if (this.getTravelAdvances().size() > 0) {
            KualiDecimal total = KualiDecimal.ZERO;
            for (TravelAdvance advance : this.getTravelAdvances()) {
                total = total.add(advance.getTravelAdvanceRequested());
            }
            if (total.isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }   
    
    /**
     * This method checks to see if Risk Management needs to be routed
     * 
     * @return
     */
    private boolean requiresRiskManagementReviewRouting() {
        // Right now this works just like International Travel Reviewer, but may change for next version
        if (ObjectUtils.isNotNull(this.getTripTypeCode()) && getParameterService().getParameterValues(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPE_CODES).contains(this.getTripTypeCode())) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#setTemProfile(org.kuali.kfs.module.tem.businessobject.TEMProfile)
     */
    @Override
    public void setTemProfile(TEMProfile temProfile) {
        super.setTemProfile(temProfile);
        if (getTravelAdvances() != null && getTravelAdvances().size() > 0){
            for (TravelAdvance advance : getTravelAdvances()){
                advance.setTravelAdvancePolicy(false);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#logErrors()
     */
    @Override
    public void logErrors() {
        super.logErrors();
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        //m2 removed this one managedLists.add(getPerDiemExpenses());
        managedLists.add(getTransportationModes());

        return managedLists;
    }  
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
      
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);

        //override the check stub text
        disbursementVoucherDocument.setDisbVchrCheckStubText("Travel Advance for " + getTravelDocumentIdentifier() + " " + getTraveler().getLastName() + " - " + getPrimaryDestinationName() + " - " + getTripBegin());
        //set the payment method from the advance
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(getTravelAdvances().get(0).getPaymentMethod());
        
        final String paymentReasonCode = getParameterService().getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);
        final String paymentLocationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(paymentLocationCode);
                
        final String advancePaymentChartCode = getParameterService().getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_CHART_CODE);
        final String advancePaymentAccountNumber = getParameterService().getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR);
        final String advancePaymentObjectCode = getParameterService().getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE);

        // set accounting (this should have been bypassed in the super class)
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (TravelAdvance advance : getTravelAdvances()) {
            if (StringUtils.isBlank(advance.getArInvoiceDocNumber())) {
                SourceAccountingLine accountingLine = new SourceAccountingLine();
    
                //if the parameter fields are empty, use that of the advance
                accountingLine.setChartOfAccountsCode(StringUtils.defaultIfEmpty(advancePaymentChartCode, advance.getAcct().getChartOfAccountsCode()));
                accountingLine.setAccountNumber(StringUtils.defaultIfEmpty(advancePaymentAccountNumber, advance.getAccountNumber()));
                accountingLine.setFinancialObjectCode(StringUtils.defaultIfEmpty(advancePaymentObjectCode, StringUtils.defaultString(advance.getFinancialObjectCode())));
                //accountingLine.setFinancialSubObjectCode(StringUtils.defaultString(advance.getFinancialSubObjectCode()));
                //accountingLine.setSubAccountNumber(StringUtils.defaultString(advance.getSubAccountNumber()));
                accountingLine.setAmount(advance.getTravelAdvanceRequested());
                accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
                accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
    
                disbursementVoucherDocument.addSourceAccountingLine(accountingLine);
                totalAmount = totalAmount.add(advance.getTravelAdvanceRequested());
                
                //in case of multiple unprocessed advance, this becomes an issue what the due date and payment method will be set to
                // this is now setting to the latest advance's information
                if (advance.getDueDate() != null) {
                    disbursementVoucherDocument.setDisbursementVoucherDueDate(advance.getDueDate());
                }
                disbursementVoucherDocument.setDisbVchrPaymentMethodCode(advance.getPaymentMethod());
            }
        }
        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
    }
    
    @Override
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReimbursableTotal()
     * This method is used for accounting line validation.
     */
    @Override
    public KualiDecimal getReimbursableTotal() {
        return getEncumbranceTotal();        
    }
    
    @Override
    public String getReportPurpose() {
        return getTripDescription();
    }

    @Override
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateVendorPayment(disbursementVoucherDocument);
        String locationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripBegin());
        String endDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripEnd());
        String checkStubText = this.getTravelDocumentIdentifier() + ", " + this.getPrimaryDestinationName() + ", " + startDate + " - " + endDate;
        
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getExpenseTypeCode()
     */
    @Override
    public String getExpenseTypeCode() {
        return TemConstants.ENCUMBRANCE;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#hasCustomDVDistribution()
     */
    @Override
    public boolean hasCustomDVDistribution(){
       return true;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getDisapprovedAppDocStatusMap()
     */
    @Override
    public Map<String, String> getDisapprovedAppDocStatusMap() {
        return TravelAuthorizationStatusCodeKeys.getDisapprovedAppDocStatusMap();
    }
   
}
