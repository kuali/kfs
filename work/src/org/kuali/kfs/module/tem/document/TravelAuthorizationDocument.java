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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_AWARD;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_DIV;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_FISCAL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_INTL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_ORG;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_RISK;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_SPCL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_SUB;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_TRVL_MGR;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_AWARD;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_DIV;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_FISCAL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_INTL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_ORG;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_RISK;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_SPCL;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_SUB;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.DAPRVD_TRVL;
import static org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
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
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.util.TemObjectUtils;
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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

@Entity
@Table(name = "TEM_TRVL_AUTH_DOC_T")
public class TravelAuthorizationDocument extends TravelDocumentBase {
    
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
        String documentID = doc.getDocumentNumber();

        toCopyTravelDocument(doc, documentID);       
        
        doc.getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);
        
        return doc;
    }

    public TravelAuthorizationCloseDocument toCopyTAC() throws WorkflowException {
        TravelAuthorizationCloseDocument doc = (TravelAuthorizationCloseDocument) SpringContext.getBean(DocumentService.class).getNewDocument(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
        String documentID = doc.getDocumentNumber();

        toCopyTravelDocument(doc, documentID);
        
        return doc;
    }

    private void toCopyTravelDocument(TravelDocument doc, String documentID) {
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        
        try {
            BeanUtils.copyProperties(documentHeader, doc.getDocumentHeader());
        }
        catch (IllegalAccessException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (InvocationTargetException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        
        try {
            BeanUtils.copyProperties(doc, this);
        }
        catch (IllegalAccessException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (InvocationTargetException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        /*
         * Replace all list objects with copies and update the copy's document number to the doc number of the TAA
         */
        doc.setTransportationModes(getTravelDocumentService().copyTransportationModeDetails(this.getTransportationModes(), documentID));
        doc.setPerDiemExpenses(getTravelDocumentService().copyPerDiemExpenses(this.getPerDiemExpenses(), documentID));
        doc.setSpecialCircumstances(getTravelDocumentService().copySpecialCircumstances(this.getSpecialCircumstances(), documentID));
        doc.getTraveler().setEmergencyContacts(getTravelDocumentService().copyTravelerDetailEmergencyContact(this.getTraveler().getEmergencyContacts(), documentID));
        doc.setGroupTravelers(getTravelDocumentService().copyGroupTravelers(this.getGroupTravelers(), documentID));               
        doc.setTravelAdvances(getTravelDocumentService().copyTravelAdvances(this.getTravelAdvances(), documentID));
        doc.setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(this.getActualExpenses(), documentID));
        doc.setImportedExpenses(new ArrayList<ImportedExpense>());
        
        doc.setDocumentHeader(documentHeader);
        doc.getDocumentHeader().getBoNotes().clear();
        doc.setTravelDocumentIdentifier(getTravelDocumentIdentifier());
        doc.setDocumentNumber(documentID);
        doc.getDocumentHeader().setDocumentDescription(this.getDocumentHeader().getDocumentDescription());
        doc.setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());
        List<TemSourceAccountingLine> newList = new ArrayList<TemSourceAccountingLine>();
        int sequence = 1;
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)doc.getSourceAccountingLines()){
            if (!line.getCardType().equals(TemConstants.CARD_TYPE_CTS)){
                line.setSequenceNumber(new Integer(sequence));
                sequence++;
                newList.add(line);
            }
        }
        doc.setSourceAccountingLines(newList);
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
     * This method populates specific items when the document is first initiated
     */
    @Override
    public void initiateDocument() {
        // due date
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        calendar.add(Calendar.DAY_OF_MONTH, 2);
        setTripEnd(new Timestamp(calendar.getTimeInMillis()));

        updateAppDocStatus(TravelAuthorizationStatusCodeKeys.IN_PROCESS);

        getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);

        if (this.getTraveler() == null) {
            this.setTraveler(new TravelerDetail());
            this.getTraveler().setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        }

        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!getTravelDocumentService().isTravelArranger(currentUser)) {
            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if (temProfile != null) {
                setTemProfile(temProfile);
            }
        }
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
            offsetEntry.setReferenceFinancialDocumentTypeCode(TRAVEL_AUTHORIZATION_DOCUMENT);
            offsetEntry.setReferenceFinancialSystemOriginationCode(TemConstants.ORIGIN_CODE);
        }

        // need to pull the Balance Type from the trip type
        this.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = this.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            // check to make sure they're the same
            String balanceType = tripType.getEncumbranceBalanceType();
            offsetEntry.setFinancialBalanceTypeCode(balanceType);
            customized = true;
        }

        return customized;
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
            explicitEntry.setReferenceFinancialDocumentTypeCode(TRAVEL_AUTHORIZATION_DOCUMENT);
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
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        debug("Handling route status change");

        debug("route status is ", statusChangeEvent.getNewRouteStatus());

        // in this case the status has already been updated and we need to update the internal status code
        String currStatus = getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
        if (ObjectUtils.isNotNull(currStatus)) {
            updateAppDocStatus(currStatus);
        }
        if (KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            // first we need to see where we were so we can change the app doc status
            String currAppDocStatus = getAppDocStatus();
            if (currAppDocStatus.equals(AWAIT_FISCAL)) {
                updateAppDocStatus(DAPRVD_FISCAL);
            }
            if (currAppDocStatus.equals(AWAIT_ORG)) {
                updateAppDocStatus(DAPRVD_ORG);
            }
            if (currAppDocStatus.equals(AWAIT_DIV)) {
                updateAppDocStatus(DAPRVD_DIV);
            }
            if (currAppDocStatus.equals(AWAIT_INTL)) {
                updateAppDocStatus(DAPRVD_INTL);
            }
            if (currAppDocStatus.equals(AWAIT_RISK)) {
                updateAppDocStatus(DAPRVD_RISK);
            }
            if (currAppDocStatus.equals(AWAIT_SUB)) {
                updateAppDocStatus(DAPRVD_SUB);
            }
            if (currAppDocStatus.equals(AWAIT_AWARD)) {
                updateAppDocStatus(DAPRVD_AWARD);
            }
            if (currAppDocStatus.equals(AWAIT_SPCL)) {
                updateAppDocStatus(DAPRVD_SPCL);
            }
            if (currAppDocStatus.equals(AWAIT_TRVL_MGR)) {
                updateAppDocStatus(DAPRVD_TRVL);
            }
        }
        
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            debug("New route status is ", statusChangeEvent.getNewRouteStatus());
                        
            // for some reason when it goes to final it never updates to the last status
            updateAppDocStatus(TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
            
            if (!(this instanceof TravelAuthorizationCloseDocument)) {
                if (!(KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getOldRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getOldRouteStatus()))) {
                    getTravelAuthorizationService().createDVARDocument(this);
                    getTravelAuthorizationService().createCustomerInvoice(this);
                    
                    //If the hold new fiscal year encumbrance indicator is true and the trip end date
                    //is after the current fiscal year end date then mark all the gl pending entries 
                    //as 'H' (Hold) otherwise mark all the gl pending entries as 'A' (approved)
                    if (getGeneralLedgerPendingEntries() != null && !getGeneralLedgerPendingEntries().isEmpty()) {
                    	if(getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.HOLD_NEW_FY_ENCUMBRANCES_IND)) {
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

            if (this instanceof TravelAuthorizationAmendmentDocument) {
                Map<String, List<Document>> relatedDocs = null;
                try {
                    relatedDocs = getTravelDocumentService().getDocumentsRelatedTo(this);            
                }
                catch (WorkflowException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                
                if (relatedDocs != null) {
                    List<Document> taDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
                    List<Document> taaDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

                    if (taDocs == null) {
                        taDocs = new ArrayList<Document>();
                    }

                    if (taaDocs != null) {
                        taDocs.addAll(taaDocs);
                    }

                    for (int i = 0; i < taDocs.size(); i++) {
                        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument) taDocs.get(i);
                        if (!taDocument.getDocumentNumber().equals(this.getDocumentNumber())) {
                            taDocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION);
                        }
                    }
                }

                getTravelDocumentService().adjustEncumbranceForAmendment(this);
            }
        }
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

    private boolean requiresTravelerApprovalRouting() {
      //If there's travel advances, route to traveler if necessary
        if (requiresTravelAdvanceReviewRouting()){
            String initiator = this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();

            String travelerID = this.getTraveler().getPrincipalId();
            boolean routeToTraveler = false;
            
            if (travelerID != null){
                //traveler must accept policy, if initiator is arranger, the traveler will have to accept later.
                return !initiator.equals(travelerID);
            }
        }
        return false;
    }

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
        if (ObjectUtils.isNotNull(this.getTripTypeCode()) && getParameterService().getParameterValues(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.INTERNATIONAL_TRIP_TYPE_CODES).contains(this.getTripTypeCode())) {
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        super.logErrors();
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
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
        // TODO Auto-generated method stub
        //super.populateDisbursementVoucherFields(disbursementVoucherDocument, document);
        String reasonCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE,TravelReimbursementParameters.DEFAULT_REFUND_PAYMENT_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(reasonCode);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_VENDOR);
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(this.getTravelDocumentIdentifier());
        String locationCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        Calendar dueDate = Calendar.getInstance();
        dueDate.add(Calendar.DATE, 1);
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(dueDate.getTimeInMillis()));
        
        disbursementVoucherDocument.setDisbVchrCheckStubText(this.getDocumentTitle() != null ? this.getDocumentTitle() : "");               
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription("Generated for TA doc: " + this.getTravelDocumentIdentifier());
        getTravelDocumentService().trimFinancialSystemDocumentHeader(disbursementVoucherDocument.getDocumentHeader());
        
        for (Object accountingLineObj : this.getSourceAccountingLines()) {
            SourceAccountingLine sourceccountingLine=(SourceAccountingLine)accountingLineObj;
            SourceAccountingLine accountingLine = new SourceAccountingLine();
              
            accountingLine.setChartOfAccountsCode(sourceccountingLine.getChartOfAccountsCode());
            accountingLine.setAccountNumber(sourceccountingLine.getAccountNumber());
            if (StringUtils.isNotBlank(sourceccountingLine.getFinancialObjectCode())) {
                accountingLine.setFinancialObjectCode(sourceccountingLine.getFinancialObjectCode());
            }

            if (StringUtils.isNotBlank(sourceccountingLine.getFinancialSubObjectCode())) {
                accountingLine.setFinancialSubObjectCode(sourceccountingLine.getFinancialSubObjectCode());
            }

            if (StringUtils.isNotBlank(sourceccountingLine.getSubAccountNumber())) {
                accountingLine.setSubAccountNumber(sourceccountingLine.getSubAccountNumber());
            }

            accountingLine.setAmount(sourceccountingLine.getAmount());
            accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
            accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());

            disbursementVoucherDocument.addSourceAccountingLine(accountingLine);
        }        
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
        String locationCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
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
}
