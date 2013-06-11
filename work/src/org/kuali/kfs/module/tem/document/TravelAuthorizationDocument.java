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
import java.sql.Date;
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
import org.kuali.kfs.module.tem.TemKeyConstants;
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
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationDocumentPaymentService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.service.AccountingLineService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "TEM_TRVL_AUTH_DOC_T")
public class TravelAuthorizationDocument extends TravelDocumentBase implements PaymentSource {

    protected static Logger LOG = Logger.getLogger(TravelAuthorizationDocument.class);

    private KualiDecimal perDiemAdjustment;

    // Emergency contact info section
    private String cellPhoneNumber;
    private String regionFamiliarity;
    private String citizenshipCountryCode;
    private Country citizenshipCountry;
    private List<TransportationModeDetail> transportationModes = new ArrayList<TransportationModeDetail>();
    private TravelAdvance travelAdvance;
    private TravelPayment advanceTravelPayment;
    private PaymentSourceWireTransfer wireTransfer;
    private List<TemSourceAccountingLine> advanceAccountingLines;
    private Integer nextAdvanceLineNumber  = new Integer(1);

    protected volatile static PersonService personService;
    protected volatile static TravelAuthorizationDocumentPaymentService travelAuthorizationDocumentPaymentService;
    protected volatile static ConfigurationService configurationService;
    protected volatile static PaymentSourceHelperService paymentSourceHelperService;
    protected volatile static OptionsService optionsService;

    /**
     * Creates a new instance of the Travel Request Document. Initializes the empty arrays as well as the line tracking numbers
     */
    public TravelAuthorizationDocument() {
        super();
        advanceAccountingLines = new ArrayList<TemSourceAccountingLine>();
        travelAdvance = new TravelAdvance();
        advanceTravelPayment = new TravelPayment();
        wireTransfer = new PaymentSourceWireTransfer();

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
        documentHeader.setDocumentNumber(documentID);
        BeanUtils.copyProperties(copyToDocument.getDocumentHeader(), documentHeader, new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});
        documentHeader.setOrganizationDocumentNumber(this.getDocumentHeader().getOrganizationDocumentNumber());
        copyToDocument.setDocumentHeader(documentHeader);

        copyToDocument.setTransportationModes(getTravelDocumentService().copyTransportationModeDetails(getTransportationModes(), documentID));
        copyToDocument.setPerDiemExpenses(getTravelDocumentService().copyPerDiemExpenses(getPerDiemExpenses(), documentID));
        copyToDocument.setSpecialCircumstances(getTravelDocumentService().copySpecialCircumstances(getSpecialCircumstances(), documentID));
        copyToDocument.setTravelerDetailId(null);
        copyToDocument.setTraveler(getTravelerService().copyTravelerDetail(getTraveler(), documentID));
        copyToDocument.setGroupTravelers(getTravelDocumentService().copyGroupTravelers(getGroupTravelers(), documentID));
        copyToDocument.setImportedExpenses(new ArrayList<ImportedExpense>());
        copyToDocument.setTravelAdvance(new TravelAdvance());

        copyToDocument.getNotes().clear();
        copyToDocument.getDocumentHeader().setDocumentDescription(getDocumentHeader().getDocumentDescription());
        copyToDocument.setTravelDocumentIdentifier(getTravelDocumentIdentifier());
        copyToDocument.setDocumentNumber(documentID);
        copyToDocument.setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());

        //reset to only include the encumbrance line
        List<TemSourceAccountingLine> newList = new ArrayList<TemSourceAccountingLine>();
        int sequence = 1;
        for (TemSourceAccountingLine line : copyToDocument.getEncumbranceSourceAccountingLines()){
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
        if (StringUtils.isBlank(citizenshipCountryCode)) {
            return null;
        }
        if (citizenshipCountry != null){
            if (StringUtils.equals(citizenshipCountryCode, citizenshipCountry.getCode())) {
                return citizenshipCountry;
            }
        }
        //re-update by the country code
        citizenshipCountry = SpringContext.getBean(CountryService.class).getCountry(citizenshipCountryCode);
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
     * @return the TravelAdvance associated with this document
     */
    public TravelAdvance getTravelAdvance() {
        return travelAdvance;
    }

    /**
     * Sets the TravelAdvance associated with this document
     * @param travelAdvance the Travel Advance for this document
     */
    public void setTravelAdvance(TravelAdvance travelAdvance) {
        this.travelAdvance = travelAdvance;
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
        setDefaultBankCode();
        setAdvanceTravelPayment(new TravelPayment());
        getAdvanceTravelPayment().setDocumentNumber(getDocumentNumber());
        getAdvanceTravelPayment().setDocumentationLocationCode(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE,
                getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE)));
        getAdvanceTravelPayment().setCheckStubText(getConfigurationService().getPropertyValueAsString(TemKeyConstants.MESSAGE_TA_ADVANCE_PAYMENT_HOLD_TEXT));
        if (allParametersForAdvanceSet()) {
            // we can initiate the travel advance accounting line
            TemSourceAccountingLine accountingLine = initiateAdvanceAccountingLine();
            addAdvanceAccountingLine(accountingLine);
        }
    }

    /**
     * Initiates the accounting line to go with the advance if all the parameters for the advance are set
     * @return the initiated accounting line
     */
    protected TemSourceAccountingLine initiateAdvanceAccountingLine() {
        try {
            TemSourceAccountingLine accountingLine = getAdvanceAccountingLineClass().newInstance();
            accountingLine.setDocumentNumber(getDocumentNumber());
            accountingLine.setFinancialDocumentLineTypeCode(TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE);
            accountingLine.setSequenceNumber(new Integer(1));
            accountingLine.setChartOfAccountsCode(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_CHART));
            accountingLine.setAccountNumber(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_ACCOUNT));
            accountingLine.setFinancialObjectCode(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_OBJECT_CODE));
            accountingLine.setCardType("EXPENSE");
            return accountingLine;
        }
        catch (InstantiationException ie) {
            LOG.error("Could not instantiate new advance accounting line of type: "+getAdvanceAccountingLineClass().getName());
            throw new RuntimeException("Could not instantiate new advance accounting line of type: "+getAdvanceAccountingLineClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            LOG.error("Illegal access attempting to instantiate advance accounting line of class "+getAdvanceAccountingLineClass().getName());
            throw new RuntimeException("Illegal access attempting to instantiate advance accounting line of class "+getAdvanceAccountingLineClass().getName(), iae);
        }
    }

    /**
     * Adds a new emergency contact line
     *
     * @param line
     */
    public void addEmergencyContactLine(TravelerDetailEmergencyContact line) {
        if (!ObjectUtils.isNull(getTraveler())) {
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
        return getDocumentHeader().getWorkflowDocument().isEnroute();
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
        if (postable instanceof AccountingLine) {
            final AccountingLine accountingLine = (AccountingLine)postable;
            if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
                customizeAdvanceExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
                return; // just leave the method, we're done
            }
        }
        customizeExpenseExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
    }

    /**
     * Customization for "normal" accounting lines - the accounting lines which will encumber
     * @param postable the general ledger pending entry source which is the source of the customized explicit entry
     * @param explicitEntry the explicit entry to customize
     */
    protected void customizeExpenseExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        // set the encumbrance update code Set to ENCUMB_UPDT_REFERENCE_DOCUMENT_CD ("R")
        explicitEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);

        // set the offset entry to Debit "D"
        explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        explicitEntry.setDocumentNumber(this.getDocumentNumber());

        String referenceDocumentNumber = this.getTravelDocumentIdentifier();
        if (ObjectUtils.isNotNull(referenceDocumentNumber)) {
            explicitEntry.setReferenceFinancialDocumentNumber(referenceDocumentNumber);
            explicitEntry.setReferenceFinancialDocumentTypeCode(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            explicitEntry.setReferenceFinancialSystemOriginationCode(TemConstants.ORIGIN_CODE);
        }

        String balanceType = getTravelEncumbranceService().getEncumbranceBalanceTypeByTripType(this);
        if (StringUtils.isNotEmpty(balanceType)) {
            explicitEntry.setFinancialBalanceTypeCode(balanceType);
        }
    }

    /**
     * Customization for accounting lines associated with travel advances
     * @param postable the general ledger pending entry source which is the source of the customized explicit entry
     * @param explicitEntry the explicit entry to customize
     */
    protected void customizeAdvanceExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT);
        //explicitEntry.setFinancialObjectTypeCode(getOptionsService().getCurrentYearOptions().getFinObjectTypeLiabilitiesCode());
        // TODO finish customizing
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean customized = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
        if (accountingLine instanceof AccountingLine) {
            final AccountingLine postable = (AccountingLine)accountingLine;
            if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(postable.getFinancialDocumentLineTypeCode())) {
                return customized && customizeAdvanceOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
            }
        }
        return customized && customizeExpenseOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
    }

    /**
     * Customizes offset GLPE's for "normal" accounting lines which are offsetting entries which are paying off expenses
     * @param accountingLine the general ledger pending entry source which acts as the source of the given offset entry to customize
     * @param explicitEntry the explicit GLPE which needs to be offset
     * @param offsetEntry the offset GLPE which is being customized
     * @return true if customization has completed successfully, false otherwise
     */
    public boolean customizeExpenseOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean customized = false;
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
            customized = true;
        }
        return customized;
    }

    /**
     * Customizes offset GLPE's for accounting lines associated with advances
     * @param accountingLine the general ledger pending entry source which acts as the source of the given offset entry to customize
     * @param explicitEntry the explicit GLPE which needs to be offset
     * @param offsetEntry the offset GLPE which is being customized
     * @return true if customization has completed successfully, false otherwise
     */
    public boolean customizeAdvanceOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        offsetEntry.setFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT);
        //offsetEntry.setFinancialObjectTypeCode(getOptionsService().getCurrentYearOptions().getFinObjectTypeLiabilitiesCode());
        // finish customizing
        return true;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        if (getTravelAdvance().isAtLeastPartiallyFilledIn() && getTravelAdvance().getTravelAdvanceRequested() != null) {
            // generate wire entries
            if (!StringUtils.isBlank(getAdvanceTravelPayment().getPaymentMethodCode())) {
                if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getAdvanceTravelPayment().getPaymentMethodCode()) && !getWireTransfer().isWireTransferFeeWaiverIndicator()) {
                    LOG.debug("generating wire charge gl pending entries.");

                    // retrieve wire charge
                    WireCharge wireCharge = getPaymentSourceHelperService().retrieveCurrentYearWireCharge();

                    // generate debits
                    GeneralLedgerPendingEntry chargeEntry = getPaymentSourceHelperService().processWireChargeDebitEntries(this, sequenceHelper, wireCharge);

                    // generate credits
                    getPaymentSourceHelperService().processWireChargeCreditEntries(this, sequenceHelper, wireCharge, chargeEntry);
                }

                // for wire or drafts generate bank offset entry (if enabled), for ACH and checks offset will be generated by PDP
                if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getAdvanceTravelPayment().getPaymentMethodCode()) || KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_DRAFT.equals(getAdvanceTravelPayment().getPaymentMethodCode())) {
                    getPaymentSourceHelperService().generateDocumentBankOffsetEntries(this, sequenceHelper, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT, getTravelAdvance().getTravelAdvanceRequested().negated());
                }
            }
            // generate entries for advance accounting lines
            for (TemSourceAccountingLine advanceAccountingLine : getAdvanceAccountingLines()) {
                generateGeneralLedgerPendingEntries(advanceAccountingLine, sequenceHelper);
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        if (DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {
            LOG.debug("New route status is " + statusChangeEvent.getNewRouteStatus());

            if (!(this instanceof TravelAuthorizationCloseDocument)) {
                // for some reason when it goes to final it never updates to the last status, updating TA status to OPEN REIMBURSEMENT
                updateAppDocStatus(TravelAuthorizationStatusCodeKeys.OPEN_REIMB);

                getTravelAuthorizationService().createCustomerInvoice(this);

                //If the hold new fiscal year encumbrance indicator is true and the trip end date is after the current fiscal year end date then mark all the gl pending entries
                //as 'H' (Hold) otherwise mark all the gl pending entries as 'A' (approved)
                if (getGeneralLedgerPendingEntries() != null && !getGeneralLedgerPendingEntries().isEmpty()) {
                    if(getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.HOLD_NEW_FISCAL_YEAR_ENCUMBRANCES_IND)) {
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

                if (shouldProcessAdvanceForDocument() && this.getAdvanceTravelPayment().isImmediatePaymentIndicator()) {
                    SpringContext.getBean(PaymentSourceExtractionService.class).extractSingleImmediatePayment(this);
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
     * @return the payment for advances on this document
     */
    public TravelPayment getAdvanceTravelPayment() {
        return advanceTravelPayment;
    }

    /**
     * Sets the payment information for advances on this document
     * @param advanceTravelPayment the payment information for advances on this document
     */
    public void setAdvanceTravelPayment(TravelPayment advanceTravelPayment) {
        this.advanceTravelPayment = advanceTravelPayment;
    }

    /**
     * @return the accounting lines associated with advances on this document
     */
    public List<TemSourceAccountingLine> getAdvanceAccountingLines() {
        return advanceAccountingLines;
    }

    /**
     * Sets accounting lines associated with advances on this document
     * @param advanceAccountingLines the travel advance accounting lines
     */
    public void setAdvanceAccountingLines(List<TemSourceAccountingLine> advanceAccountingLines) {
        this.advanceAccountingLines = advanceAccountingLines;
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in source accounting line using the value that has
     * been stored in the nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextSourceLineNumber variable for you.
     */
    public void addAdvanceAccountingLine(TemSourceAccountingLine line) {
        line.setSequenceNumber(this.getNextAdvanceLineNumber());
        this.advanceAccountingLines.add(line);
        this.nextAdvanceLineNumber = new Integer(getNextAdvanceLineNumber().intValue() + 1);
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause adding lines to break
     * if you add more than one item to the list.
     */
    public TemSourceAccountingLine getAdvanceAccountingLine(int index) {
        while (getSourceAccountingLines().size() <= index) {
            try {
                getSourceAccountingLines().add(getAdvanceAccountingLineClass().newInstance());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Unable to get class");
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get class");
            }
        }
        return getAdvanceAccountingLines().get(index);
    }

    /**
     * @return the class associated with advances accounting lines
     */
    protected Class<? extends TemSourceAccountingLine> getAdvanceAccountingLineClass() {
        return TemSourceAccountingLine.class;
    }

    /**
     * @return the next available accounting line number for advances accounting lines
     */
    public Integer getNextAdvanceLineNumber() {
        return this.nextAdvanceLineNumber;
    }

    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.SPECIAL_REQUEST)) {
            return requiresSpecialRequestReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.INTL_TRAVEL)) {
            return requiresInternationalTravelReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.RISK_MANAGEMENT)) {
            return requiresRiskManagementReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.TRVL_ADV_REQUESTED)) {
            return requiresTravelAdvanceReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.DIVISION_APPROVAL_REQUIRED)) {
            return requiresDivisionApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED)) {
            return requiresAccountApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW)) {
            return requiresTravelerApprovalRouting();
        }
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
            String initiator = this.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
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
        return (shouldProcessAdvanceForDocument() && KualiDecimal.ZERO.isLessThan(this.getTravelAdvance().getTravelAdvanceRequested()));
    }

    /**
     * This method checks to see if Risk Management needs to be routed
     *
     * @return
     */
    private boolean requiresRiskManagementReviewRouting() {
        // Right now this works just like International Travel Reviewer, but may change for next version
        if (ObjectUtils.isNotNull(this.getTripTypeCode()) && getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPES).contains(this.getTripTypeCode())) {
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
        if (!ObjectUtils.isNull(getTravelAdvance())) {
            getTravelAdvance().setTravelAdvancePolicy(false);
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
        String locationCode = getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE, getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE));
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
        return isTripGenerateEncumbrance()? TemConstants.ENCUMBRANCE : "";
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

    /**
     * Set the document number and the trip id on our travel advance
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#prepareForSave(org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        if (!ObjectUtils.isNull(getTravelAdvance())) {
            getTravelAdvance().setDocumentNumber(getDocumentNumber());
            getTravelAdvance().setTravelDocumentIdentifier(getTravelDocumentIdentifier());
            getWireTransfer().setDocumentNumber(getDocumentNumber());
            final String checkStubPrefix = getConfigurationService().getPropertyValueAsString(TemKeyConstants.MESSAGE_TA_ADVANCE_PAYMENT_CHECK_TEXT_PREFIX);
            getAdvanceTravelPayment().setCheckStubText(checkStubPrefix+" "+getDocumentHeader().getDocumentDescription());
            getAdvanceTravelPayment().setDueDate(getTravelAdvance().getDueDate());
        }
    }

    /**
     * Returns the WireTransfer associated with this travel authorization
     * @see org.kuali.kfs.sys.document.PaymentSource#getWireTransfer()
     */
    @Override
    public PaymentSourceWireTransfer getWireTransfer() {
        return wireTransfer;
    }

    /**
     * Sets the wire transfer for this travel authorization
     * @param wireTransfer the wire transfer for this travel authorization
     */
    public void setWireTransfer(PaymentSourceWireTransfer wireTransfer) {
        this.wireTransfer = wireTransfer;
    }

    @Override
    public PaymentGroup generatePaymentGroup(Date processRunDate) {
        if (shouldProcessAdvanceForDocument()) {
            return getTravelAuthorizationDocumentPaymentService().createPaymentGroupForAuthorization(this, processRunDate);
        }
        return null;
    }

    /**
     * @return true if this document seems to have an active advance which should therefore be processed, false otherwise
     */
    public boolean shouldProcessAdvanceForDocument() {
        return !ObjectUtils.isNull(getTravelAdvance()) && getTravelAdvance().isAtLeastPartiallyFilledIn();
    }

    /**
     * Sets the extraction date on the travel payment
     * @see org.kuali.kfs.sys.document.PaymentSource#markAsExtracted(java.sql.Date)
     */
    @Override
    public void markAsExtracted(Date extractionDate) {
        getAdvanceTravelPayment().setExtractDate(extractionDate);
    }

    /**
     * Sets the paid date on the travel payment
     * @see org.kuali.kfs.sys.document.PaymentSource#markAsPaid(java.sql.Date)
     */
    @Override
    public void markAsPaid(Date processDate) {
        getAdvanceTravelPayment().setPaidDate(processDate);
    }

    @Override
    public void cancelPayment(Date cancelDate) {
        getTravelAuthorizationDocumentPaymentService().cancelAuthorizationDocument(this, cancelDate);
    }

    /**
     * This involves rolling back the extracted and paid dates and setting the document to approved
     * @see org.kuali.kfs.sys.document.PaymentSource#resetFromExtraction()
     */
    @Override
    public void resetFromExtraction() {
        getAdvanceTravelPayment().setExtractDate(null);
        getAdvanceTravelPayment().setPaidDate(null);
    }

    /**
     * Returns the cancel date from the travel payment
     * @see org.kuali.kfs.sys.document.PaymentSource#getCancelDate()
     */
    @Override
    public Date getCancelDate() {
        return this.getAdvanceTravelPayment().getCancelDate();
    }

    /**
     * Returns the attachment code from the travel payment
     * @see org.kuali.kfs.sys.document.PaymentSource#hasAttachment()
     */
    @Override
    public boolean hasAttachment() {
        return this.getAdvanceTravelPayment().isAttachmentCode();
    }

    /**
     * Returns the payment method code associated with the travel payment for the advance
     * @see org.kuali.kfs.sys.document.PaymentSource#getPaymentMethodCode()
     */
    @Override
    public String getPaymentMethodCode() {
        return this.getAdvanceTravelPayment().getPaymentMethodCode();
    }

    /**
     * If the travel advance is partially filled in, returns the amount requested
     * @see org.kuali.kfs.sys.document.PaymentSource#getPaymentAmount()
     */
    @Override
    public KualiDecimal getPaymentAmount() {
        if (shouldProcessAdvanceForDocument()) {
            return this.getTravelAdvance().getTravelAdvanceRequested();
        }
        return KualiDecimal.ZERO;
    }

    /**
     * Returns the campus code of the initiator of this document
     * @see org.kuali.kfs.sys.document.PaymentSource#getCampusCode()
     */
    @Override
    public String getCampusCode() {
        final Person initiator = getPersonService().getPerson(getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        return initiator.getCampusCode();
    }

    /**
     * Returns "TACA"
     * @see org.kuali.kfs.sys.document.PaymentSource#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType() {
        return TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT;
    }

    @Override
    public String getImmediateExtractEMailFromAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getImmediateExtractEmailToAddresses() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean allParametersForAdvanceSet() {
        return (!StringUtils.isBlank(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_ACCOUNT, "")) &&
                !StringUtils.isBlank(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_CHART)) &&
                        !StringUtils.isBlank(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_OBJECT_CODE)));
    }

    /**
     * Propagates the amount from the advance to the travel payment and to the accounting line if the accounting line is read only
     */
    public void propagateAdvanceAmountIfNeeded() {
        if (!ObjectUtils.isNull(getTravelAdvance()) && getTravelAdvance().getTravelAdvanceRequested() != null) {
            if (!ObjectUtils.isNull(getAdvanceTravelPayment())) {
                getAdvanceTravelPayment().setCheckTotalAmount(getTravelAdvance().getTravelAdvanceRequested());
            }
            if (allParametersForAdvanceSet()) {
                getAdvanceAccountingLines().get(0).setAmount(getTravelAdvance().getTravelAdvanceRequested());
            }
            // let's also propogate the due date
            if (getTravelAdvance().getDueDate() != null && !ObjectUtils.isNull(getAdvanceTravelPayment())) {
                getAdvanceTravelPayment().setDueDate(getTravelAdvance().getDueDate());
            }
        }
    }

    /**
     * Generate events for advance accounting lines
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateSaveEvents()
     */
    @Override
    public List generateSaveEvents() {
        List events = super.generateSaveEvents();

        final List persistedAdvanceAccountingLines = getPersistedAdvanceAccountingLinesForComparison();
        final List currentAdvanceAccountingLines = getAdvanceAccountingLinesForComparison();

        final List advanceEvents = generateEvents(persistedAdvanceAccountingLines, currentAdvanceAccountingLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.ADVANCE_ACCOUNTING_LINES, this);
        events.addAll(advanceEvents);

        return events;
    }

    /**
     * This method gets the Source Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getAdvanceAccountingLinesForComparison() {
        return getAdvanceAccountingLines();
    }

    /**
     * This method gets the Persisted advance Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getPersistedAdvanceAccountingLinesForComparison() {
        return SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderIdAndLineType(getAdvanceAccountingLineClass(), getDocumentNumber(), TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE);
    }

    /**
     * @return the default implementation of the PersonService
     */
    protected static PersonService getPersonService() {
        if (personService == null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    /**
     * @return the default implementation of the ReimbursableDocumentPaymentService
     */
    protected static TravelAuthorizationDocumentPaymentService getTravelAuthorizationDocumentPaymentService() {
        if (travelAuthorizationDocumentPaymentService == null) {
            travelAuthorizationDocumentPaymentService = SpringContext.getBean(TravelAuthorizationDocumentPaymentService.class);
        }
        return travelAuthorizationDocumentPaymentService;
    }

    /**
     * @return the default implementation of the PaymentSourceHelperService
     */
    protected static PaymentSourceHelperService getPaymentSourceHelperService() {
        if (paymentSourceHelperService == null) {
            paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
        }
        return paymentSourceHelperService;
    }

    /**
     * @return the default implementation of the OptionsService
     */
    protected static OptionsService getOptionsService() {
        if (optionsService == null) {
            optionsService = SpringContext.getBean(OptionsService.class);
        }
        return optionsService;
    }
}
