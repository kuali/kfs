/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document;

import static org.kuali.kfs.module.tem.TemKeyConstants.AGENCY_SITES_URL;
import static org.kuali.kfs.module.tem.TemKeyConstants.ENABLE_AGENCY_SITES_URL;
import static org.kuali.kfs.module.tem.TemKeyConstants.PASS_TRIP_ID_TO_AGENCY_SITES;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.ExpenseType;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ClassOfService;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEncumbranceService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelDocumentNotificationService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.module.tem.util.GroupTravelerComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Abstract Travel Document Base
 */
public abstract class TravelDocumentBase extends AccountingDocumentBase implements TravelDocument, Copyable {

    protected static Logger LOG = Logger.getLogger(TravelDocumentBase.class);

    private TripType tripType;
    private String tripTypeCode;
    private Timestamp tripBegin;
    private Timestamp tripEnd;
    private String tripDescription;
    private Boolean primaryDestinationIndicator = false;
    private Integer primaryDestinationId;
    private PrimaryDestination primaryDestination;
    private String primaryDestinationName;
    private String primaryDestinationCountryState;
    private String primaryDestinationCounty;
    private KualiDecimal rate;
    private KualiDecimal expenseLimit;
    private String mealWithoutLodgingReason;
    private String dummyAppDocStatus;
    private String financialDocumentBankCode;

    // Traveler section
    private Integer temProfileId;
    private TEMProfile temProfile;
    private Integer travelerDetailId;
    private TravelerDetail traveler;
    private Bank bank;

    protected List<SpecialCircumstances> specialCircumstances = new ArrayList<SpecialCircumstances>();
    protected List<GroupTraveler> groupTravelers = new ArrayList<GroupTraveler>();
    protected List<TravelAdvance> travelAdvances = new ArrayList<TravelAdvance>();
    protected List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
    protected List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();
    protected List<ImportedExpense> importedExpenses = new ArrayList<ImportedExpense>();
    protected List<HistoricalTravelExpense> historicalTravelExpenses = new ArrayList<HistoricalTravelExpense>();

    protected String travelDocumentIdentifier;
    protected Integer travelDocumentLinkIdentifier;
    private Boolean delinquentTRException = false;

    @Transient
    private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();

    @Transient
    private Map<String, String> disabledProperties;

    @Transient
    private Boolean taxSelectable = Boolean.TRUE;

    protected TravelDocumentBase() {
        super();
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TravelDisbursementService getTravelDisbursementService() {
        return SpringContext.getBean(TravelDisbursementService.class);
    }

    protected TravelEncumbranceService getTravelEncumbranceService() {
        return SpringContext.getBean(TravelEncumbranceService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    protected TEMRoleService getTemRoleService() {
        return SpringContext.getBean(TEMRoleService.class);
    }

    protected TemProfileService getTemProfileService() {
        return SpringContext.getBean(TemProfileService.class);
    }

    @Override
    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }

    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }

    protected TravelExpenseService getTravelExpenseService() {
        return SpringContext.getBean(TravelExpenseService.class);
    }

    protected TravelerService getTravelerService() {
        return SpringContext.getBean(TravelerService.class);
    }

    @Override
    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return SpringContext.getBean(SequenceAccessorService.class);
    }

    protected WorkflowDocumentService getWorkflowDocumentService() {
        return SpringContext.getBean(WorkflowDocumentService.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getAccountingDistributionService()
     */
    @Override
    public AccountingDistributionService getAccountingDistributionService(){
        return SpringContext.getBean(AccountingDistributionService.class);
    }

    /**
     * This method updates both the internal travel document status value and the app doc status
     * in the document header of workflow
     *
     * NOTE: force to update the app doc status when workflow state  is F, P or D
     *
     * @param status
     */
    @Override
    public boolean updateAppDocStatus(String status) {
        boolean updated = false;
        WorkflowDocument workflow = getDocumentHeader().getWorkflowDocument();
        //final, processed and dispproved will always need to update the app doc status
        if ((workflow.isFinal() || workflow.isProcessed()) || workflow.isDisapproved()){
            setAppDocStatus(status);
            updated = saveAppDocStatus();
        }
        return updated;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getAppDocStatus()
     */
    @Override
    public String getAppDocStatus() {
        String status = getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
        return StringUtils.defaultIfEmpty(status, TemConstants.TRAVEL_DOC_APP_DOC_STATUS_INIT);
    }

    /**
     * Update application doc status if it is different from the current stats
     *
     * @param status
     */
    final public void setAppDocStatus(String status) {
        // get current workflow status and compare to status change
        String currStatus = getAppDocStatus();
        if (StringUtils.isBlank(currStatus) || !status.equalsIgnoreCase(currStatus)) {
            LOG.debug("NEW status is [" + status + "] was {" + currStatus + "}");
            getDocumentHeader().getWorkflowDocument().setApplicationDocumentStatus(status);
        }
    }

    /**
     * Save the current document header with the updated app doc status
     *
     * @return
     */
    final public boolean saveAppDocStatus() {
        boolean saved = false;
        try {
            getWorkflowDocumentService().save(getDocumentHeader().getWorkflowDocument(), null);
            saved = true;
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return saved;
    }

    /**
     * Gets the dummyAppDocStatus attribute.
     * @return Returns the dummyAppDocStatus.
     */
    public String getDummyAppDocStatus() {
        return dummyAppDocStatus;
    }

    /**
     * Sets the dummyAppDocStatus attribute value.
     * @param dummyAppDocStatus The dummyAppDocStatus to set.
     */
    public void setDummyAppDocStatus(String dummyAppDocStatus) {
        this.dummyAppDocStatus = dummyAppDocStatus;
    }

    /**
     * Gets the primaryDestinationIndicator attribute.
     * @return Returns the primaryDestinationIndicator.
     */
    @Override
    public Boolean getPrimaryDestinationIndicator() {
        return primaryDestinationIndicator;
    }

    /**
     * Sets the primaryDestinationIndicator attribute value.
     * @param primaryDestinationIndicator The primaryDestinationIndicator to set.
     */
    @Override
    public void setPrimaryDestinationIndicator(Boolean primaryDestinationIndicator) {
        this.primaryDestinationIndicator = primaryDestinationIndicator;
    }

    @Column(name = "TRIP_DESC")
    public String getTripDescription() {
        return tripDescription;
    }
    @Override
    public Integer getPrimaryDestinationId() {
        return primaryDestinationId;
    }

    @Override
    public void setPrimaryDestinationId(Integer primaryDestinationId) {
        this.primaryDestinationId = primaryDestinationId;

    }

    @Override
    public PrimaryDestination getPrimaryDestination() {
        if (primaryDestination ==  null){
            primaryDestination = new PrimaryDestination();
        }
        return primaryDestination;
    }

    @Override
    public void setPrimaryDestination(PrimaryDestination primaryDestination) {
        this.primaryDestination = primaryDestination;
        if (primaryDestination !=  null){
            this.setPrimaryDestinationCountryState(primaryDestination.getCountryState());
            this.setPrimaryDestinationCounty(primaryDestination.getCounty());
            this.setPrimaryDestinationName(primaryDestination.getPrimaryDestinationName());

        }
        else{
            this.setPrimaryDestinationCountryState(null);
            this.setPrimaryDestinationCounty(null);
            this.setPrimaryDestinationName(null);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPrimaryDestinationName()
     */
    @Override
    public String getPrimaryDestinationName() {
        return primaryDestinationName;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPrimaryDestinationName(java.lang.String)
     */
    @Override
    public void setPrimaryDestinationName(String primaryDestinationName) {
        this.primaryDestinationName = primaryDestinationName;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPrimaryDestinationCountryState()
     */
    @Override
    public String getPrimaryDestinationCountryState() {
        return primaryDestinationCountryState;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPrimaryDestinationCountryState(java.lang.String)
     */
    @Override
    public void setPrimaryDestinationCountryState(String primaryDestinationCountryState) {
        if (primaryDestinationCountryState != null) {
            primaryDestinationCountryState = primaryDestinationCountryState.toUpperCase();
        }

        this.primaryDestinationCountryState = primaryDestinationCountryState;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPrimaryDestinationCounty()
     */
    @Override
    public String getPrimaryDestinationCounty() {
        return primaryDestinationCounty;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPrimaryDestinationCounty(java.lang.String)
     */
    @Override
    public void setPrimaryDestinationCounty(String primaryDestinationCounty) {
        this.primaryDestinationCounty = primaryDestinationCounty;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTripDescription(java.lang.String)
     */
    @Override
    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTripType()
     */
    @Override
    @ManyToOne
    @JoinColumn(name = "TRIP_TYP_CD")
    public TripType getTripType() {
        return tripType;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTripType(org.kuali.kfs.module.tem.businessobject.TripType)
     */
    @Override
    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTripTypeCode()
     */
    @Override
    @Column(name = "TRIP_TYP_CD", length = 3)
    public String getTripTypeCode() {
        return tripTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTripTypeCode(java.lang.String)
     */
    @Override
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTravelDocumentIdentifier()
     */
    @Override
    @Column(name = "TRVL_ID")
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTravelDocumentIdentifier(java.lang.String)
     */
    @Override
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTripBegin()
     */
    @Override
    @Column(name = "TRIP_BGN_DT")
    public Timestamp getTripBegin() {
        return tripBegin;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTripBegin(java.sql.Timestamp)
     */
    @Override
    public void setTripBegin(Timestamp tripBegin) {
        this.tripBegin = tripBegin;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTripEnd()
     */
    @Override
    @Column(name = "TRIP_END_DT")
    public Timestamp getTripEnd() {
        return tripEnd;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTripEnd(java.sql.Timestamp)
     */
    @Override
    public void setTripEnd(Timestamp tripEnd) {
        this.tripEnd = tripEnd;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#canReturn()
     */
    @Override
    public boolean canReturn() {
        return getDocumentHeader().getWorkflowDocument().isEnroute();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTravelDocumentLinkIdentifier()
     */
    @Override
    public Integer getTravelDocumentLinkIdentifier() {
        return travelDocumentLinkIdentifier;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTravelDocumentLinkIdentifier(java.lang.Integer)
     */
    @Override
    public void setTravelDocumentLinkIdentifier(Integer travelDocumentLinkIdentifier) {
        this.travelDocumentLinkIdentifier = travelDocumentLinkIdentifier;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTraveler()
     */
    @Override
    @ManyToOne
    @JoinColumn(name = "traveler_dtl_id")
    public TravelerDetail getTraveler() {
        return traveler;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTraveler(org.kuali.kfs.module.tem.businessobject.TravelerDetail)
     */
    @Override
    public void setTraveler(TravelerDetail traveler) {
        this.traveler = traveler;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTravelerDetailId()
     */
    @Override
    @Column(name = "traveler_dtl_id")
    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTravelerDetailId(java.lang.Integer)
     */
    @Override
    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getExpenseLimit()
     */
    @Override
    @Column(name = "expenseLimit", precision = 19, scale = 2)
    public KualiDecimal getExpenseLimit() {
        return expenseLimit;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setExpenseLimit(org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void setExpenseLimit(KualiDecimal expenseLimit) {
        this.expenseLimit = expenseLimit;
    }

    /**
     * Sets the propertyChangeListener attribute value.
     *
     * @param propertyChangeListener The propertyChangeListener to set.
     */
    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }

    /**
     * Gets the propertyChangeListeners attribute.
     *
     * @return Returns the propertyChangeListenerDetailId.
     */
    public List<PropertyChangeListener> getPropertyChangeListeners() {
        return this.propertyChangeListeners;
    }

    /**
     * Notify listeners that an event occurred
     *
     * @param event the {@link PropertyChangeEvent}
     */
    protected void notifyChangeListeners(final PropertyChangeEvent event) {
        for (final PropertyChangeListener listener : getPropertyChangeListeners()) {
            listener.propertyChange(event);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setSpecialCircumstances(java.util.List)
     */
    @Override
    public void setSpecialCircumstances(final List<SpecialCircumstances> specialCircumstances) {
        this.specialCircumstances = specialCircumstances;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getSpecialCircumstances()
     */
    @Override
    public List<SpecialCircumstances> getSpecialCircumstances() {
        return this.specialCircumstances;
    }

    /**
     * Here we need to generate the travel request number from the SequenceAccessorService as well as set any status for the
     * document
     *
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // Set the identifier and org doc number before anything is done in the super class
        if (ObjectUtils.isNull(getTravelDocumentIdentifier())) {
            // need retrieve the next available TR id to save in GL entries (only do if travel request id is null which should be on
            // first
            // save)
            SequenceAccessorService sas = getSequenceAccessorService();
            Long trSequenceNumber = sas.getNextAvailableSequenceNumber("TRVL_ID_SEQ", this.getClass());

            String docId = "T-";
            if(this instanceof TravelEntertainmentDocument) {
            	docId = "E-";
            } else if (this instanceof TravelRelocationDocument) {
            	docId = "M-";
            }

            setTravelDocumentIdentifier(docId + trSequenceNumber.toString());
            this.getDocumentHeader().setOrganizationDocumentNumber(getTravelDocumentIdentifier());
        }

        //always generate the description in case the traveler changes from the last time it was generated.
        getDocumentHeader().setDocumentDescription(generateDescription());

        super.prepareForSave(event);
    }

    /**
     * This method generates a description based on the following information: principal trip begin date (mm/dd/yyy) primary
     * destination and truncate at 40 characters
     *
     * @return a newly generated description
     */
    protected String generateDescription() {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        PersonService ps = SpringContext.getBean(PersonService.class);

        Person person = ps.getPerson(getTraveler().getPrincipalId());

        this.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);

        AccountsReceivableCustomer customer = getTraveler().getCustomer();
        if (person != null) {
            sb.append(person.getLastName() + ", " + person.getFirstName() + " " + person.getMiddleName() + " ");
        }
        else if (customer != null) {
            sb.append(customer.getCustomerName() + " ");
        }
        else {
            sb.append(getTraveler().getFirstName() + " " + getTraveler().getLastName() + " ");
        }

        if(this.getTripBegin() != null) {
            sb.append(format.format(this.getTripBegin()) + " ");
        }

        if(this.getPrimaryDestination() != null && StringUtils.isNotEmpty(this.getPrimaryDestination().getPrimaryDestinationName())) {
        	sb.append(this.getPrimaryDestination().getPrimaryDestinationName());
        } else  {
            if (this.getPrimaryDestinationName() != null) {
                sb.append(this.getPrimaryDestinationName());
            }
        }

        String tempStr = sb.toString();

        if (getDelinquentAction() != null) {
            tempStr = "(Delinquent) " + tempStr;
        }

        if (tempStr.length() > 40) {
            tempStr = tempStr.substring(0, 39);
        }

        return tempStr;
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        cleanTravelDocument();

        // Copy Trip Detail Estimates, Traveler, Emergency Contacts, Transportation Modes, and Group Travelers
        setTransportationModes(getTravelDocumentService().copyTransportationModeDetails(getTransportationModes(), getDocumentNumber()));
        setPerDiemExpenses(getTravelDocumentService().copyPerDiemExpenses(getPerDiemExpenses(), getDocumentNumber()));
        setSpecialCircumstances(getTravelDocumentService().copySpecialCircumstances(this.getSpecialCircumstances(), getDocumentNumber()));
        setTraveler(getTravelerService().copyTravelerDetail(getTraveler(), getDocumentNumber()));
        setGroupTravelers(getTravelDocumentService().copyGroupTravelers(getGroupTravelers(), getDocumentNumber()));
        setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(getActualExpenses(), getDocumentNumber()));
        setImportedExpenses(new ArrayList<ImportedExpense>());

        // Cleanup Travel Advances and notes
        setTravelAdvances(new ArrayList<TravelAdvance>());
        setNotes(new ArrayList<Note>());

        //set it to in-progress so it will be updated
        setAppDocStatus(TemConstants.TravelStatusCodeKeys.IN_PROCESS);
    }

    /**
     * Cleans the Travel Document Identifier, and the notes
     */
    protected void cleanTravelDocument() {
        this.travelDocumentIdentifier = null;
        this.travelerDetailId = null;
        this.getTraveler().setId(null);
        this.getDocumentHeader().setOrganizationDocumentNumber("");
        this.getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);
    }

    /**
     *
     * @return
     */
    @Transient
    public boolean isValidExpenses(){
        if(this.actualExpenses == null){
            return true;
        }

        int counter = 0;
        for(ActualExpense actualExpense: this.actualExpenses){
            if (actualExpense.getExpenseDetails().size() > 0){
                KualiDecimal detailAmount = actualExpense.getTotalDetailExpenseAmount();
                GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
                GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.ACTUAL_EXPENSES + "[" + counter + "]");
                if(detailAmount.isGreaterThan(actualExpense.getExpenseAmount()) && !actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.MILEAGE)){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DETAIL_AMOUNT_EXCEED, detailAmount.toString(), actualExpense.getExpenseAmount().toString());
                    return false;
                }
                GlobalVariables.getMessageMap().clearErrorPath();
            }
            counter++;
        }

        return true;
    }

    /**
     *
     * @return
     */
    @Transient
    public KualiDecimal getMealsAndIncidentalsGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMealsAndIncidentalsGrandTotal(this);
    }

    /**
     *
     * @return
     */
    @Transient
    public KualiDecimal getLodgingGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getLodgingGrandTotal(this);
    }

    /**
     *
     * @return
     */
    @Transient
    public KualiDecimal getMileageTotalGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMileageTotalGrandTotal(this);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getDailyTotalGrandTotal()
     */
    @Override
    @Transient
    public KualiDecimal getDailyTotalGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getDailyTotalGrandTotal(this);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getDocumentGrandTotal()
     */
    @Override
    @Transient
    public KualiDecimal getDocumentGrandTotal() {
        KualiDecimal total  = KualiDecimal.ZERO;
        for (ExpenseType expense : EnumSet.allOf(ExpenseType.class)){
            total = getTravelExpenseService().getExpenseServiceByType(expense).getAllExpenseTotal(this, true).add(total);
        }
        return total;
    }

    /**
     *
     * @return
     */
    @Transient
    public Integer getMilesGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMilesGrandTotal(this);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPerDiemExpenses()
     */
    @Override
    @OneToMany(mappedBy="documentNumber")
    public List<PerDiemExpense> getPerDiemExpenses() {
        return perDiemExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPerDiemExpenses(java.util.List)
     */
    @Override
    public void setPerDiemExpenses(List<PerDiemExpense> perDiemExpenses) {
        this.perDiemExpenses = perDiemExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getActualExpenses()
     */
    @Override
    @OneToMany(mappedBy="documentNumber")
    public List<ActualExpense> getActualExpenses() {
        return actualExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setActualExpenses(java.util.List)
     */
    @Override
    public void setActualExpenses(List<ActualExpense> actualExpenses) {
        this.actualExpenses = actualExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#enableExpenseTypeSpecificFields(java.util.List)
     */
    @Override
    @Transient
    public void enableExpenseTypeSpecificFields(final List<ActualExpense> actualExpenses){
        for(ActualExpense actualExpense: actualExpenses){
            actualExpense.enableExpenseTypeSpecificFields();
        }
    }

    /**
     * Returns the pending expense amount after summing up detail rows expense amount
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTotalPendingAmount(java.util.List, java.lang.Long)
     */
    @Override
    @Transient
    public KualiDecimal getTotalPendingAmount(ActualExpense actualExpense){
        KualiDecimal expenseAmount = actualExpense.getExpenseAmount();
        return expenseAmount.subtract(actualExpense.getTotalDetailExpenseAmount());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getParentExpenseAmount(java.util.List, java.lang.Long)
     */
    @Override
    @Transient
    public KualiDecimal getParentExpenseAmount(List<ActualExpense> actualExpenses, Long id){

        for(ActualExpense actualExpense: actualExpenses){
            if(actualExpense.getId().equals(id)){
                return actualExpense.getExpenseAmount();
            }
        }

        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getParentExpenseRecord(java.util.List, java.lang.Long)
     */
    @Override
    @Transient
    public ActualExpense getParentExpenseRecord(List<ActualExpense> actualExpenses, Long id){

        for(ActualExpense actualExpense: actualExpenses){
            if(actualExpense.getId().equals(id)){
                return actualExpense;
            }
        }

        return null;
    }

    /**
     * Totals up all the other expenses. Needs to multiply the expenseAmount by the currencyRate because the currency
     * could be from another country other than US. If there is no currencyRate, we assume it's US even if the country isn't.
     *
     * @return {@link KualiDecimal} with the total
     */
    @Override
    @Transient
    public KualiDecimal getActualExpensesTotal() {
        KualiDecimal retval = KualiDecimal.ZERO;

        LOG.debug("Getting other expense total");

        if(actualExpenses != null){
            for (final ActualExpense expense : actualExpenses) {
                final KualiDecimal expenseAmount = expense.getExpenseAmount().multiply(expense.getCurrencyRate());

                LOG.debug("Expense amount gotten is " + expenseAmount);
                retval = retval.add(expenseAmount);
            }
        }

        LOG.debug("Returning otherExpense Total " + retval);
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#addActualExpense(org.kuali.kfs.module.tem.businessobject.ActualExpense)
     */
    @Override
    @Transient
    public void addActualExpense(final ActualExpense line) {
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ActualExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
        line.enableExpenseTypeSpecificFields();
        getActualExpenses().add(line);
    }

    @Override
    @Transient
    public void addExpense(TEMExpense line) {
        final String sequenceName = line.getSequenceName();
        // Because all expense types use the same sequence, it doesn't matter which class grabs the sequence
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ImportedExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);

        if (line instanceof ActualExpense){
            getActualExpenses().add((ActualExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
            ((ActualExpense)line).enableExpenseTypeSpecificFields();
        }
        else{
            getImportedExpenses().add((ImportedExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#addExpenseDetail(org.kuali.kfs.module.tem.businessobject.TEMExpense, java.lang.Integer)
     */
    @Override
    @Transient
    public void addExpenseDetail(TEMExpense line, Integer index) {
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ImportedExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));

        if (line instanceof ActualExpense){
            getActualExpenses().get(index).addExpenseDetails(line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
        }
        else{
            getImportedExpenses().get(index).addExpenseDetails(line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));
        }
    }

    /**
     * Adds a new other expense line
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocument#removeActualExpense(java.lang.Integer)
     */
    @Override
    @Transient
    public void removeActualExpense(final Integer index) {
        final ActualExpense line = getActualExpenses().remove(index.intValue());
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, line, null));

        //Remove detail lines which are associated with parentId
        int nextIndex = -1;
        while((nextIndex = getNextDetailIndex(line.getId())) !=-1){
            final ActualExpense detailLine = getActualExpenses().remove(nextIndex);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, detailLine, null));
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#removeExpense(org.kuali.kfs.module.tem.businessobject.TEMExpense, java.lang.Integer)
     */
    @Override
    @Transient
    public void removeExpense(TEMExpense expense, Integer index) {
        TEMExpense line = null;
        if (expense instanceof ActualExpense){
            line = getActualExpenses().remove(index.intValue());
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, line, null));
        }
        else{
            line = getImportedExpenses().remove(index.intValue());
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, line, null));
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#removeExpenseDetail(org.kuali.kfs.module.tem.businessobject.TEMExpense, java.lang.Integer)
     */
    @Override
    @Transient
    public void removeExpenseDetail(TEMExpense expense, Integer index) {
        final TEMExpense line = expense.getExpenseDetails().remove(index.intValue());
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.EXPENSES_DETAILS, line, null));
    }

    /**
     *
     * @param id
     * @return
     */
    @Transient
    private int getNextDetailIndex(Long id){
        int index = 0;
        for(ActualExpense detailLine: getActualExpenses()){
            if(ObjectUtils.isNotNull(detailLine.getExpenseParentId()) && detailLine.getExpenseParentId().equals(id)){
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Disbursement voucher fields for reimbursable
     *
     * @param disbursementVoucherDocument
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        getTravelDisbursementService().populateReimbursableDisbursementVoucherFields(disbursementVoucherDocument, this);
    }

    /**
     *
     * @param reqsDoc
     * @param document
     */
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
    }

    /**
     *
     * @return
     */
    @Transient
    public List<Map<String, KualiDecimal>> getPerDiemExpenseTotals() {
        return getTravelDocumentService().calculateDailyTotals(this.perDiemExpenses);
    }

    /**
     *
     * @return
     */
    public KualiDecimal getRate() {
        return rate;
    }

    /**
     *
     * @param rate
     */
    public void setRate(KualiDecimal rate) {
        this.rate = rate;
    }

    /**
     * Gets the disabledProperties attribute.
     * @return Returns the disabledProperties.
     */
    public Map<String, String> getDisabledProperties() {
        if (disabledProperties == null){
            disabledProperties = new HashMap<String, String>();
        }
        return disabledProperties;
    }

    /**
     * Sets the disabledProperties attribute value.
     * @param disabledProperties The disabledProperties to set.
     */
    public void setDisabledProperties(Map<String, String> disabledProperties) {
        this.disabledProperties = disabledProperties;
    }

    /**
     * Gets the profileId attribute.
     * @return Returns the profileId.
     */
    @Override
    public Integer getProfileId() {
        return temProfileId;
    }

    /**
     * Sets the profileId attribute value, looks up the profile and populates the TravelerDetail based on the profile.
     * @param profileId The profileId to set.
     */
    @Override
    public void setProfileId(Integer profileId) {
        this.temProfileId = profileId;
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, profileId);
        setTemProfile(getBusinessObjectService().findByPrimaryKey(TEMProfile.class, primaryKeys));
    }

    /**
     * Gets the temProfileId attribute.
     * @return Returns the temProfileId.
     */
    @Override
    public Integer getTemProfileId() {
        return temProfileId;
    }

    /**
     * Sets the temProfileId attribute value.
     * @param temProfileId The temProfileId to set.
     */
    @Override
    public void setTemProfileId(Integer temProfileId) {
        this.temProfileId = temProfileId;
    }

    /**
     * Gets the temProfile attribute.
     * @return Returns the temProfile.
     */
    @Override
    public TEMProfile getTemProfile() {
        return temProfile;
    }

    /**
     * Sets the temProfile attribute value and populates the TravelerDetail from the TemProfile.
     * @param temProfile The temProfile to set.
     */
    @Override
    public void setTemProfile(TEMProfile temProfile) {
        this.temProfile = temProfile;
        if(temProfile != null){
            getTravelerService().populateTEMProfile(temProfile);
            if (temProfile.getTravelerType() == null){
                Map<String, Object> fieldValues = new HashMap<String, Object>();
                fieldValues.put("code", temProfile.getTravelerTypeCode());
                List<TravelerType> types = (List<TravelerType>) getBusinessObjectService().findMatching(TravelerType.class, fieldValues);
                temProfile.setTravelerType(types.get(0));
                setTemProfileId(temProfile.getProfileId());
            }

            if (traveler != null) {
                traveler.setDocumentNumber(this.documentNumber);
                getTravelerService().convertTEMProfileToTravelerDetail(temProfile,(traveler == null?new TravelerDetail():traveler));
            }
        }
    }

    /**
     *
     * This method sets up the traveler from the TravelerDetail if it exists,
     * otherwise it populates the TravelerDetail from the TemProfile.
     * @param temProfileId
     * @param traveler
     */
    public void configureTraveler(Integer temProfileId, TravelerDetail traveler) {

        if (traveler != null && traveler.getId() != null) {
            // There's a traveler, which means it needs to copy the traveler, rather than
            // setting it up from the profile, which is why setProfileId() is not called here.
            this.temProfileId = temProfileId;
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, temProfileId);
            this.temProfile = getBusinessObjectService().findByPrimaryKey(TEMProfile.class, primaryKeys);

            this.traveler = getTravelerService().copyTravelerDetail(traveler, this.documentNumber);
        }
        else {
            setProfileId(temProfileId);
        }
    }

    /**
     *
     * This method wraps {@link #checkTravelerFieldForChanges(String, String, String, String)} for looping purposes.
     * @param o1
     * @param o2
     * @param propertyName
     * @param noteText
     * @param fieldLabel
     * @return
     */
    private String checkTravelerFieldForChanges(Object o1, Object o2, String propertyName, String noteText, String fieldLabel){
        String profileFieldValue = (String) ObjectUtils.getPropertyValue(o1, propertyName);
        String travelerDetailFieldValue = (String) ObjectUtils.getPropertyValue(o2, propertyName);

        return checkTravelerFieldForChanges(profileFieldValue != null ? profileFieldValue.trim() : "", travelerDetailFieldValue != null ? travelerDetailFieldValue.trim() : "", noteText, fieldLabel);
    }

    /**
     *
     * This method compares profile and traveler field values using {@link StringValueComparator#compare(Object, Object)} and returns the formatted noteText accordingly
     * @param profileFieldValue
     * @param travelerDetailFieldValue
     * @param noteText
     * @param fieldLabel
     * @return
     */
    private String checkTravelerFieldForChanges(String profileFieldValue, String travelerDetailFieldValue, String noteText, String fieldLabel) {
        if(StringValueComparator.getInstance().compare(profileFieldValue, travelerDetailFieldValue) == 0){
            return noteText;
        }
        return noteText += fieldLabel + ", " ;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getDelinquentAction()
     */
    @Override
    public String getDelinquentAction(){
        if(tripEnd != null){
            Collection<String> delinquentRules = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.NUMBER_OF_DAYS_DELINQUENT);
            String action = null;

            if(delinquentRules != null){
                for(String rule : delinquentRules){
                    String[] arg = rule.split("=");
                    if(arg != null && arg.length == 2){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(arg[1]) * -1);

                        if(tripEnd.before(cal.getTime())){
                            if(action != null){
                                if(TemPropertyConstants.delinquentActionsRank().get(action) < TemPropertyConstants.delinquentActionsRank().get(arg[0])){
                                    action = arg[0];
                                }
                            }else{
                                action = arg[0];
                            }
                        }
                    }
                }
            }

            return action;
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#canDisplayAgencySitesUrl()
     */
    @Override
    public boolean canDisplayAgencySitesUrl(){
        String value = getConfigurationService().getPropertyValueAsString(ENABLE_AGENCY_SITES_URL);

        if(value== null || value.length() ==0 || !value.equalsIgnoreCase("Y")){
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getAgencySitesUrl()
     */
    @Override
    public String getAgencySitesUrl(){
        return getConfigurationService().getPropertyValueAsString(AGENCY_SITES_URL);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#canPassTripIdToAgencySites()
     */
    @Override
    public boolean canPassTripIdToAgencySites(){
        String value = getConfigurationService().getPropertyValueAsString(PASS_TRIP_ID_TO_AGENCY_SITES);

        if(value== null || value.length() ==0 || !value.equalsIgnoreCase("Y")){
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getImportedExpenses());
        managedLists.add(getActualExpenses());
        managedLists.add(getPerDiemExpenses());

        return managedLists;
    }

    /**
     * Get all Actual expenses with details
     *
     * If expenses have details, use that information instead of the original expense
     *
     * @return
     */
    public List<TEMExpense> getAllActualExpensesWithDetails(){
        List<TEMExpense> expenseList = new ArrayList<TEMExpense>();
        if (ObjectUtils.isNotNull(getActualExpenses())) {
            for (ActualExpense ae : getActualExpenses()) {
                if (ae.getExpenseDetails().isEmpty()) {
                    expenseList.addAll(ae.getExpenseDetails());
                } else {
                    expenseList.add(ae);
                }
            }
        }
        return expenseList;
    }

    /**
     *
     * @return
     */
    public Boolean getDelinquentTRException() {
        return delinquentTRException != null ? delinquentTRException : false;
    }

    /**
     *
     * @param delinquentTRException
     */
    public void setDelinquentTRException(Boolean delinquentTRException) {
        this.delinquentTRException = delinquentTRException;
    }

    /**
     *
     * @param argTaxSelectable
     */
    public void setTaxSelectable(Boolean argTaxSelectable){
        this.taxSelectable = argTaxSelectable;
    }

    /**
     *
     * @return
     */
    public Boolean getTaxSelectable(){
        return taxSelectable;
    }

    /**
     *
     * @return
     */
    public Boolean isTaxSelectable(){
        return taxSelectable;
    }

    /**
     *
     * @return
     */
    protected boolean requiresAccountApprovalRouting() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingLine line : ((List<AccountingLine>) this.getSourceAccountingLines())) {
            total = total.add(line.getAmount());
        }
        if (total.isGreaterThan(KualiDecimal.ZERO)) {
            return true;
        }

        return false;
    }

    /**
     *
     * @return
     */
    protected boolean requiresDivisionApprovalRouting() {
        if (getTravelDocumentService().getTotalAuthorizedEncumbrance(this).isGreaterEqual(new KualiDecimal(getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.CUMULATIVE_REIMBURSABLE_AMOUNT_WITHOUT_DIVISION_APPROVAL)))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    protected boolean requiresInternationalTravelReviewRouting() {
        if (ObjectUtils.isNotNull(this.getTripTypeCode()) && getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPES).contains(this.getTripTypeCode())) {
            return true;
        }
        return false;
    }

    /**
     * Tax Manager approval is required if any actual expenses or its detail is taxable
     *
     * @return
     */
    protected boolean requiresTaxManagerApprovalRouting() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (TEMExpense line : getAllActualExpensesWithDetails()) {
            if(line.getTaxable()){
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    protected boolean requiresSeparationOfDutiesRouting(){
        String code = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.SEPARATION_OF_DUTIES_ROUTING_CHOICE);

        if (code.equals(TemConstants.SEP_OF_DUTIES_FO)){
            if (!requiresAccountApprovalRouting()){
                return false;
            }

            if (getTraveler().getPrincipalId() != null){
                return getTravelDocumentService().isResponsibleForAccountsOn(this, this.getTraveler().getPrincipalId());
            }
        }
        else if (code.equals(TemConstants.SEP_OF_DUTIES_DR)){
            if (!requiresDivisionApprovalRouting()){
                return false;
            }

            if (getTraveler().getPrincipalId() != null){
                RoleService service = SpringContext.getBean(RoleService.class);
                List<String> principalIds = (List<String>) service.getRoleMemberPrincipalIds(TemConstants.PARAM_NAMESPACE, TemConstants.TEMRoleNames.DIVISION_REVIEWER, null);
                for (String id : principalIds){
                    if (id.equals(getTraveler().getPrincipalId())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check all actual expenses and details
     *
     * @return
     */
    protected boolean requiresSpecialRequestReviewRouting() {
        for (TEMExpense expense: getAllActualExpensesWithDetails()) {
            if (checkActualExpenseSpecialRequest(expense)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param expense
     * @return
     */
    private boolean checkActualExpenseSpecialRequest(TEMExpense expense) {

        //check class of service for this expense
        if (StringUtils.isNotEmpty(expense.getClassOfServiceCode())){
            Map<String, String> searchMap = new HashMap<String, String>();
            searchMap.put(KRADPropertyConstants.CODE, expense.getClassOfServiceCode());
            ClassOfService classOfService = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ClassOfService.class, searchMap);
            if (classOfService.isApprovalRequired()) {
                return true;
            }
        }

        //check if the expense type code requires special request
        TemTravelExpenseTypeCode expenseTypeCode = expense.getTravelExpenseTypeCode();
        if (expenseTypeCode.getSpecialRequestRequired() != null && expenseTypeCode.getSpecialRequestRequired()) {
            return true;
        }

        if (expense.isRentalCar() && expense.getRentalCarInsurance()){
            return true;
        }

        return false;
    }

    /**
     * Gets the groupTravelers attribute.
     *
     * @return Returns the groupTravelers.
     */
    @Override
    public List<GroupTraveler> getGroupTravelers() {
        Collections.sort(groupTravelers, new GroupTravelerComparator());
        return groupTravelers;
    }

    /**
     * Sets the groupTravelers attribute value.
     *
     * @param groupTravelers The groupTravelers to set.
     */
    @Override
    public void setGroupTravelers(List<GroupTraveler> groupTravelers) {
        this.groupTravelers = groupTravelers;
    }

    /**
     * This method adds a new group traveler line to the travel doc
     *
     * @param group traveler line
     */
    @Override
    public void addGroupTravelerLine(GroupTraveler line) {
        line.setFinancialDocumentLineNumber(this.groupTravelers.size() + 1);
        line.setDocumentNumber(this.documentNumber);
        this.groupTravelers.add(line);
    }

    /**
     * Gets the travelAdvances attribute.
     *
     * @return Returns the travelAdvances.
     */
    @Override
    public List<TravelAdvance> getTravelAdvances() {
        return travelAdvances;
    }

    /**
     * Sets the travelAdvances attribute value.
     *
     * @param travelAdvances The travelAdvances to set.
     */
    @Override
    public void setTravelAdvances(List<TravelAdvance> travelAdvances) {
        this.travelAdvances = travelAdvances;
    }

    /**
     * This method adds a new travel advance line
     *
     * @param line
     */
    public void addTravelAdvanceLine(TravelAdvance line) {
        line.setFinancialDocumentLineNumber(this.travelAdvances.size()+1);
        line.setDocumentNumber(this.documentNumber);
        this.travelAdvances.add(line);
    }

    /**
     * Given the <code>financialObjectCode</code>, determine the total of the {@link SourceAccountingLine} instances with that
     * <code>financialObjectCode</code>
     *
     * @param financialObjectCode to search for total on
     * @return @{link KualiDecimal} with total value for {@link AccountingLines} with <code>finanncialObjectCode</code>
     */
    @Override
    public KualiDecimal getTotalFor(final String financialObjectCode) {
        KualiDecimal retval = KualiDecimal.ZERO;

        LOG.debug("Getting total for " + financialObjectCode);

        for (final AccountingLine line : (List<AccountingLine>) getSourceAccountingLines()) {
            try {
                LOG.debug("Comparing "+ financialObjectCode+ " to "+ line.getObjectCode().getCode());
                if (line.getObjectCode().getCode().equals(financialObjectCode)) {
                    retval = retval.add(line.getAmount());
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getNonReimbursableTotal()
     */
    @Override
    public KualiDecimal getNonReimbursableTotal() {
        KualiDecimal total  = KualiDecimal.ZERO;
        for (ExpenseType expense : EnumSet.allOf(ExpenseType.class)){
            total = getTravelExpenseService().getExpenseServiceByType(expense).getNonReimbursableExpenseTotal(this).add(total);
        }
        return total;
    }

    /**
     * This method returns total expense amount minus the non-reimbursable
     *
     * @return
     */
    @Override
    public KualiDecimal getApprovedAmount() {
        KualiDecimal total  = KualiDecimal.ZERO;
        for (ExpenseType expense : EnumSet.allOf(ExpenseType.class)){
            total = getTravelExpenseService().getExpenseServiceByType(expense).getAllExpenseTotal(this, false).add(total);
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getMealWithoutLodgingReason()
     */
    @Override
    public String getMealWithoutLodgingReason() {
        return mealWithoutLodgingReason;
    }

    /**
     *
     * @param mealWithoutLodgingReason
     */
    public void setMealWithoutLodgingReason(String mealWithoutLodgingReason) {
        this.mealWithoutLodgingReason = mealWithoutLodgingReason;
    }

    /**
     *
     * @return
     */
    public boolean isMealsWithoutLodging(){
        if (perDiemExpenses != null){
            for(PerDiemExpense pde : perDiemExpenses){
                if (checkMealWithoutLodging(pde)) {
                    return true;
                }
            }
        }

        if (actualExpenses != null){
            for(ActualExpense actualExpense : actualExpenses){
                if (checkMealWithoutLodging(actualExpense)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *
     * @param pde
     * @return
     */
    public boolean checkMealWithoutLodging(PerDiemExpense pde) {
        return pde.getMealsTotal().isGreaterThan(KualiDecimal.ZERO) && pde.getLodgingTotal().isLessEqual(KualiDecimal.ZERO);
    }

    /**
     *
     * @param actualExpense
     * @return
     */
    public boolean checkMealWithoutLodging(ActualExpense actualExpense) {
        if (actualExpense.isHostedMeal()) {
            if (actualExpense.getExpenseParentId() != null) {
                ActualExpense parent = getParentExpenseRecord(getActualExpenses(), actualExpense.getExpenseParentId());
                if (!parent.getLodgingIndicator() && !parent.getLodgingAllowanceIndicator()) {
                    return true;
                }
            }
            else {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the bank code which payments made to reimburse this document will be drawn from
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the bank code which payments made to reimburse this document will be drawn from
     * @param financialDocumentBankCode the bank code which payments made to reimburse this document will be drawn from
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }

    /**
     * @return the full bank business object for the bank from which payments made to reimburse this document will be drawn
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the full bank business object for the bank from which payments made to reimburse this document will be drawn
     * @param bank the full bank business object for the bank from which payments made to reimburse this document will be drawn
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Sets the bank code for a new document to the setup default for the TEM document.
     */
    public void setDefaultBankCode() {
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(this.getClass());
        if (defaultBank != null) {
            this.financialDocumentBankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTransportationModes()
     */
    @Override
    public List<TransportationModeDetail> getTransportationModes() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setTransportationModes(java.util.List)
     */
    @Override
    public void setTransportationModes(List<TransportationModeDetail> transportationModes) {
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getImportedExpenses()
     */
    @Override
    public List<ImportedExpense> getImportedExpenses() {
        return importedExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setImportedExpenses(java.util.List)
     */
    @Override
    public void setImportedExpenses(List<ImportedExpense> importedExpenses) {
        this.importedExpenses = importedExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getCTSTotal()
     */
    @Override
    public KualiDecimal getCTSTotal() {
        KualiDecimal lessCtsCharges = getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCTS).getAllExpenseTotal(this, false);
        return lessCtsCharges;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getCorporateCardTotal()
     */
    @Override
    public KualiDecimal getCorporateCardTotal() {
        KualiDecimal lessCorpCardCharges = getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCorpCard).getAllExpenseTotal(this, false);
        return lessCorpCardCharges;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getHistoricalTravelExpenses()
     */
    @Override
    public List<HistoricalTravelExpense> getHistoricalTravelExpenses() {
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        Map<String,String> fieldValues = new HashMap<String, String>();
        fieldValues.put(TemPropertyConstants.TRIP_ID,this.getTravelDocumentIdentifier());
        historicalTravelExpenses = (List<HistoricalTravelExpense>) service.findMatchingOrderBy(HistoricalTravelExpense.class, fieldValues, TemPropertyConstants.TRANSACTION_POSTING_DATE, true);
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_AGENCY);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.AGENCY_STAGING_DATA);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_STAGING_DATA);
            historicalTravelExpense.getCreditCardAgency().refreshReferenceObject(TemPropertyConstants.TRAVEL_CARD_TYPE);
        }
        return historicalTravelExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setHistoricalTravelExpenses(java.util.List)
     */
    @Override
    public void setHistoricalTravelExpenses(List<HistoricalTravelExpense> historicalTravelExpenses) {
        this.historicalTravelExpenses = historicalTravelExpenses;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getGeneralLedgerPendingEntrySourceDetails()
     */
    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (getSourceAccountingLines() != null) {
            for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)getSourceAccountingLines()){
                //we are generating source accounting lines on document's expense type code
                // could be either OUT OF POCKET  or ENCUMBRANCE
                if (line.getCardType().equals(getExpenseTypeCode())){
                    accountingLines.add(line);
                }
            }
        }
        return accountingLines;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#initiateDocument()
     */
    @Override
    public void initiateDocument() {

        //pre filled descriptions
        getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);

        //set traveler to employee if null
        if (this.getTraveler() == null) {
            this.setTraveler(new TravelerDetail());
            this.getTraveler().setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        }

        //set profile to current user
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!getTemRoleService().isTravelArranger(currentUser)) {
            TEMProfile temProfile = getTemProfileService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if (temProfile != null) {
                setTemProfile(temProfile);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getEncumbranceTotal()
     */
    @Override
    public KualiDecimal getEncumbranceTotal() {
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        this.refreshNonUpdateableReferences();

        SpringContext.getBean(TravelDocumentNotificationService.class).sendNotificationOnChange(this, statusChangeEvent);
        super.doRouteStatusChange(statusChangeEvent);

        if (DocumentStatus.FINAL.getCode().equals(statusChangeEvent.getNewRouteStatus()) || DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {
            //Some docs come here twice.  if the imported expenses for this doc are reconciled, don't process again.
            boolean processImports = true;
            if (this.getHistoricalTravelExpenses() != null
                    && this.getHistoricalTravelExpenses().size() > 0){
                for (HistoricalTravelExpense historicalTravelExpense : this.getHistoricalTravelExpenses()){
                    if (historicalTravelExpense.getDocumentNumber() != null
                            && historicalTravelExpense.getDocumentNumber().equals(this.getDocumentNumber())
                            && historicalTravelExpense.getReconciled().equals(TemConstants.ReconciledCodes.RECONCILED)){
                        processImports = false;
                        break;
                    }
                }
            }
            if (processImports){
                getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCTS).updateExpense(this);
                getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCorpCard).updateExpense(this);
            }
        }

        LOG.debug("Handling Route Status changing to [" + statusChangeEvent.getNewRouteStatus() + "]");

        //Update internal app doc status status
        String currStatus = getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
        if (ObjectUtils.isNotNull(currStatus)) {
            updateAppDocStatus(currStatus);
        }

        //for disapproval cases
        if (DocumentStatus.DISAPPROVED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {
            updateAppDocStatus(getDisapprovedAppDocStatusMap().get(getAppDocStatus()));
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCTS).processExpense(this);
        getTravelExpenseService().getExpenseServiceByType(ExpenseType.importedCorpCard).processExpense(this);
        return true;
    }

    @Override
    public String getDocumentTypeName(){
        return this.getDataDictionaryEntry().getDocumentTypeName();
    }

    public boolean canPayDVToVendor() {
        return (getDocumentHeader() != null && !(getDocumentHeader().getWorkflowDocument().isCanceled() || getDocumentHeader().getWorkflowDocument().isInitiated() || getDocumentHeader().getWorkflowDocument().isException() || getDocumentHeader().getWorkflowDocument().isDisapproved() || getDocumentHeader().getWorkflowDocument().isSaved()));
    }

    public boolean canCreateREQSForVendor() {
        return (getDocumentHeader() != null && !(getDocumentHeader().getWorkflowDocument().isCanceled() || getDocumentHeader().getWorkflowDocument().isInitiated() || getDocumentHeader().getWorkflowDocument().isException() || getDocumentHeader().getWorkflowDocument().isDisapproved() || getDocumentHeader().getWorkflowDocument().isSaved()));
    }

    @Override
    public NoteType getNoteType() {
        return NoteType.BUSINESS_OBJECT;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#populateVendorPayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument) {
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription("Created by " + this.getDocumentTypeName() + " document" + (this.getTravelDocumentIdentifier() == null?".":": " + this.getTravelDocumentIdentifier()));
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(this.getTravelDocumentIdentifier());
        String reasonCode = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.VENDOR_PAYMENT_REASON_CODE);

        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(reasonCode);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(KFSConstants.PaymentPayeeTypes.VENDOR);
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(this.getTravelDocumentIdentifier());

        Calendar dueDate = Calendar.getInstance();
        dueDate.add(Calendar.DATE, 1);
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(dueDate.getTimeInMillis()));

        Person initiator = SpringContext.getBean(PersonService.class).getPerson(this.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }

        disbursementVoucherDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReimbursableSourceAccountingLines()
     */
    @Override
    public List<SourceAccountingLine> getReimbursableSourceAccountingLines() {
        List<SourceAccountingLine> reimbursableLines = new ArrayList<SourceAccountingLine>();
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>) getSourceAccountingLines()){
            if (TemConstants.ACTUAL_EXPENSE.equals(line.getCardType())){
                reimbursableLines.add(line);
            }
        }
        return reimbursableLines;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getDefaultAccountingLineCardAgencyType()
     */
    @Override
    public String getDefaultAccountingLineCardAgencyType(){
        return getExpenseTypeCode();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#hasCustomDVDistribution()
     */
    @Override
    public boolean hasCustomDVDistribution(){
       return false;
    }

    /**
     * Check trip type to determine if it should be generating encumbrance / dis-encumbrance
     *
     * @return
     */
    public boolean isTripGenerateEncumbrance(){
        return getTripType() != null && getTripType().isGenerateEncumbrance();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#isSpecialCircumstancesDefaultOpen()
     */
    @Override
    public boolean isSpecialCircumstancesDefaultOpen(){
        boolean isOpen = false;
        if (expenseLimit != null){
            isOpen = true;
        }else{
            //open if any of the checkbox is selected (but not for text)
            for (SpecialCircumstances circumstance : getSpecialCircumstances()){
                if (circumstance.getResponse()){
                    isOpen = true;
                    break;
                }
            }
        }
        return isOpen;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#isSpecialCircumstancesDefaultOpen()
     */
    @Override
    public boolean isEmergencyContactDefaultOpen(){
        boolean isOpen = false;
        Collection<String> internationalTrips = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPES);
        if (internationalTrips.contains(getTripTypeCode())){
            isOpen  = traveler.getEmergencyContacts().isEmpty();
        }
        return isOpen;
    }

    /**
     * Should be defined per each doc type
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getDisapprovedAppDocStatusMap()
     */
    @Override
    public abstract Map<String, String> getDisapprovedAppDocStatusMap();

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#isTravelAuthorizationDoc()
     */
    @Override
    public boolean isTravelAuthorizationDoc(){
        return TravelDocTypes.getAuthorizationDocTypes().contains(getDocumentTypeName());
    }

}
