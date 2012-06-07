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
package org.kuali.kfs.module.tem.document.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.DAILY_TOTAL;
import static org.kuali.kfs.module.tem.TemConstants.LODGING_TOTAL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.MEALS_AND_INC_TOTAL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.MILEAGE_TOTAL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.ENABLE_PER_DIEM_CATEGORIES;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.HOSTED_MEAL_EXPENSE_TYPES;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.warn;
import static org.kuali.rice.kns.util.GlobalVariables.getMessageList;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherDocumentationLocation;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelEntertainmentParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.MileageRateObjCode;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstancesQuestion;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Travel Service Implementation
 */
public class TravelDocumentServiceImpl implements TravelDocumentService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelDocumentServiceImpl.class);
    
    protected DataDictionaryService dataDictionaryService;
    protected DocumentService documentService;
    protected BusinessObjectService businessObjectService;
    protected TravelDocumentDao travelDocumentDao;
    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;
    protected TravelerService travelerService;
    protected AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    protected RoleService roleService;
    protected WorkflowDocumentService workflowDocumentService;
    private KualiRuleService kualiRuleService;
    private StateService stateService;
    private PersistenceStructureService persistenceStructureService;
    protected UniversityDateService universityDateService;
    
    /**
     * Creates and populates an individual per diem item.
     * 
     * @param perDiemId is the id for the referenced {@link PerDiem} object that gets attached
     * @return date of the item
     */
    protected PerDiemExpense createPerDiemItem(final TravelDocument document, final PerDiem perDiem, final Timestamp ts, final boolean prorated) {
        final PerDiemExpense retval = newPerDiemExpense();
        retval.setPerDiem(perDiem);        
        retval.setPerDiemId(perDiem.getId());
        retval.refreshPerDiem();
        retval.setProrated(prorated);
        retval.setMileageDate(ts);        
        retval.setPrimaryDestination(retval.getPerDiem().getPrimaryDestination());
        retval.setCountryState(retval.getPerDiem().getCountryState());
        retval.setCounty(retval.getPerDiem().getCounty());
        retval.setBreakfastValue(new KualiDecimal(retval.getPerDiem().getBreakfast()));
        retval.setLunchValue(new KualiDecimal(retval.getPerDiem().getLunch()));
        retval.setDinnerValue(new KualiDecimal(retval.getPerDiem().getDinner()));
        retval.setIncidentalsValue(retval.getPerDiem().getIncidentals());        
        if(retval.isProrated()){            
            Integer perDiemPercent = this.calculateProratePercentage(retval, document.getTripType().getPerDiemCalcMethod(), document.getTripEnd());                      
            retval.setDinnerValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(retval.getDinnerValue(), perDiemPercent));                   
            retval.setLunchValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(retval.getLunchValue(), perDiemPercent));                   
            retval.setBreakfastValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(retval.getBreakfastValue(), perDiemPercent));            
            retval.setIncidentalsValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(retval.getIncidentalsValue(), perDiemPercent));
        }
        retval.setLodging(retval.getPerDiem().getLodging());
        
        String showPerDiemBreakdown = parameterService.getParameterValue(PARAM_NAMESPACE, TravelAuthorizationParameters.PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_TA_PER_DIEM_AMOUNT_EDIT_IND);
        List<String> perDiemCats = getParameterService().getParameterValues(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, ENABLE_PER_DIEM_CATEGORIES);
               
        if (showPerDiemBreakdown == null || !showPerDiemBreakdown.equals(KFSConstants.ParameterValues.YES)) {
            if (showPerDiem(perDiemCats, TemConstants.LODGING)) {
                retval.setBreakfast(false);
            }
            
            if (showPerDiem(perDiemCats, TemConstants.MILEAGE)) {
                retval.setLunch(false);
            }
            
            if (showPerDiem(perDiemCats, TemConstants.PER_DIEM)) {
                retval.setDinner(false);
            }
        }

        return retval;
    }

    /**
     * returns a new instance of a PerDiemExpense turned into a service call so that we can provide our own instance during testing
     */
    protected PerDiemExpense newPerDiemExpense() {
        return new PerDiemExpense();
    }

    /**
     * Creates a date range for iterating over
     * 
     * @param start of the date range
     * @param end of the date range
     * @return Collection for iterating
     */
    protected Collection<Timestamp> dateRange(final Timestamp start, final Timestamp end) {
        final Collection<Timestamp> retval = new ArrayList<Timestamp>();

        if (start != null && end != null) {
            final Calendar cal = getDateTimeService().getCurrentCalendar();
            cal.setTime(start);

            for (; !cal.getTime().after(end) || DateUtils.isSameDay(cal.getTime(), end); cal.add(Calendar.DATE, 1)) {
                if (DateUtils.isSameDay(cal.getTime(), end)) {
                    retval.add(new Timestamp(end.getTime()));
                }
                else {
                    retval.add(new Timestamp(cal.getTime().getTime()));
                }
            }
        }

        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#updatePerDiemItemsFor(String, List, Integer, Timestamp, Timestamp)
     */
    // updatePerDiemItemsFor(final TravelDocument document, final Date start, final Date end)
    public void updatePerDiemItemsFor(final TravelDocument document, final List<PerDiemExpense> perDiemExpenseList, final Integer perDiemId, final Timestamp start, final Timestamp end) {       
        // Check for changes on trip begin and trip end.
        // This is necessary to prevent duplication of per diem creation due to timestamp changes.
        boolean datesChanged = false;
        if (perDiemExpenseList != null && !perDiemExpenseList.isEmpty()) {
            Timestamp tempStart = perDiemExpenseList.get(0).getMileageDate();
            Timestamp tempEnd = perDiemExpenseList.get(0).getMileageDate();
            
            if (perDiemExpenseList.size() > 1) {
                tempEnd = perDiemExpenseList.get(perDiemExpenseList.size()-1).getMileageDate();
            }
            
            if (!(tempStart.equals(start) && tempEnd.equals(end))) {
                // the perDiemExpenseList will be cleared once we recreate the table, but we need it for carrying over mileage rates
                datesChanged = true;
            }
        }
        
        List<PerDiem> perDiemList = new ArrayList<PerDiem>();
        Map<String,Object> fieldValues = new HashMap<String, Object>();
        // Gather all primary destination info
        fieldValues.put(TemPropertyConstants.PER_DIEM_NAME, document.getPrimaryDestinationName());
        fieldValues.put(TemPropertyConstants.PER_DIEM_COUNTRY_STATE, document.getPrimaryDestinationCountryState());
        fieldValues.put(TemPropertyConstants.PER_DIEM_COUNTY, document.getPrimaryDestinationCounty());
        fieldValues.put(TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE, document.getTripTypeCode());
        fieldValues.put("active", "Y");
              
        // find a valid per diem for each date.  If per diem is null, make it a custom per diem.
        for (final Timestamp someDate : dateRange(start, end)) {            
            fieldValues.put("date",someDate);
            PerDiem perDiem = getTravelDocumentDao().findPerDiem(fieldValues);
            if (perDiem == null){
                perDiem = new PerDiem();
                perDiem.setId(TemConstants.CUSTOM_PER_DIEM_ID);
                perDiem.setCountryState(document.getPrimaryDestinationCountryState());
                perDiem.setCounty(document.getPrimaryDestinationCounty());
                perDiem.setPrimaryDestination(document.getPrimaryDestinationName());
                perDiem.setTripType(document.getTripType());
                perDiem.setTripTypeCode(document.getTripTypeCode());
            }
            perDiemList.add(perDiem);
        }
        
        final Map<Timestamp, PerDiemExpense> perDiemMapped = new HashMap<Timestamp, PerDiemExpense>();

        int diffStartDays = 0;
        if (perDiemExpenseList.size() > 0 && perDiemExpenseList.get(0).getMileageDate() != null && !datesChanged) {
            diffStartDays = dateTimeService.dateDiff(start, perDiemExpenseList.get(0).getMileageDate(), false);
        }
     
        Calendar endCal = Calendar.getInstance();

        if (end != null) {
            endCal.setTime(end);
            if (!datesChanged) {
                for (final PerDiemExpense perDiemItem : perDiemExpenseList) {
                    if (diffStartDays != 0) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(perDiemItem.getMileageDate());
                        cal.add(Calendar.DATE, -diffStartDays);
                        perDiemItem.setMileageDate(new Timestamp(cal.getTimeInMillis()));
                    }
    
                    if (perDiemItem.getMileageDate() != null) {
                        Calendar currCal = Calendar.getInstance();
                        currCal.setTime(perDiemItem.getMileageDate());
                        if (!endCal.before(currCal)) {
                            perDiemMapped.put(perDiemItem.getMileageDate(), perDiemItem);
                        }
                    }
                }
            }

            debug("Iterating over date range from ", start, " to ", end);
            int counter = 0;
            for (final Timestamp someDate : dateRange(start, end)) {
                // Check if a per diem entry exists for this date
                if (!perDiemMapped.containsKey(someDate)) {
                    boolean prorated = !DateUtils.isSameDay(start, end) && (DateUtils.isSameDay(someDate, start) || DateUtils.isSameDay(someDate, end));
                    PerDiemExpense perDiemExpense = createPerDiemItem(document,perDiemList.get(counter), someDate, prorated);
                    if (counter < perDiemExpenseList.size()) {
                        // copy mileage data over from the original perdiem expense
                        perDiemExpense = setupMileageRate(perDiemExpense, perDiemExpenseList.get(counter));
                    }
                    else {
                        perDiemExpense = setupMileageRate(perDiemExpense, null);
                    }
                    perDiemExpense.setDocumentNumber(document.getDocumentNumber());
                    perDiemMapped.put(someDate, perDiemExpense);
                }
                counter++;
            }
        }
        
        // Sort the dates and recreate the collection
        perDiemExpenseList.clear();
        for (final Timestamp someDate : new TreeSet<Timestamp>(perDiemMapped.keySet())) {
            debug("Adding ", perDiemMapped.get(someDate), " to perdiem list");
            perDiemExpenseList.add(perDiemMapped.get(someDate));
        }
    }

    /**
     * 
     * This method populates the MileageRate on the rebuilt PerDiemExpense. It will attempt to use the old PerDiemExpense's values if possible.
     * @param newExpense
     * @param oldExpense
     * @return
     */
    protected PerDiemExpense setupMileageRate(PerDiemExpense newExpense, PerDiemExpense oldExpense) {
        try {
            Date searchDate = getDateTimeService().convertToSqlDate(newExpense.getMileageDate());
            List<KeyLabelPair> mileageRates = getMileageRateKeyValues(searchDate);
            if (ObjectUtils.isNotNull(mileageRates)) {
                for (KeyLabelPair pair : mileageRates) {
                    Integer key = (Integer) pair.getKey();
                    if (ObjectUtils.isNotNull(oldExpense) && oldExpense.getMileageRateId().intValue() == key.intValue()) {
                        // use the same mileage rate as before
                        newExpense.setMileageRateId(oldExpense.getMileageRateId());
                        newExpense.setMiles(oldExpense.getMiles());
                        break;
                    }
                }
                if (ObjectUtils.isNull(newExpense.getMileageRateId())) {
                    // mileage rate is different than it was before the change, use the first element in the list
                    newExpense.setMileageRateId((Integer) mileageRates.get(0).getKey());
                }
            }
        }
        catch (ParseException ex) {
            error("Unable to convert timestamp to sql date. Unable to populate PerDiemExpense with MileageRate.");
            ex.printStackTrace();
        }
        return newExpense;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getMileageRateKeyValues(java.sql.Date)
     */
    @Override
    public List getMileageRateKeyValues(Date searchDate) {
        final List keyValues = new ArrayList();
        TravelDocument document = (TravelDocument) ((TravelFormBase)GlobalVariables.getKualiForm()).getDocument();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        
        if(document.getTripTypeCode() != null){
            fieldValues.put(TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE, document.getTripTypeCode());
        }

        String documentType = SpringContext.getBean(TravelDocumentService.class).getDocumentType(document);        
        
        if (documentType != null) {
            fieldValues.put("documentType", documentType);
        }
        
        fieldValues.put(TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD, document.getTraveler().getTravelerTypeCode());
        fieldValues.put("active", "Y");
               
        final Collection<MileageRateObjCode> bos = SpringContext.getBean(BusinessObjectService.class).findMatching(MileageRateObjCode.class,fieldValues);
               
        for (final MileageRateObjCode typ : bos) {
            typ.refreshNonUpdateableReferences();
            final Date fromDate = typ.getMileageRate().getActiveFromDate();
            final Date toDate = typ.getMileageRate().getActiveToDate();
            if(ObjectUtils.isNull(searchDate)) {
                //just add them all
                keyValues.add(new KeyLabelPair(typ.getId(), typ.getMileageRate().getName()));
            } else if((fromDate.equals(searchDate) || fromDate.before(searchDate)) && (toDate.equals(searchDate) || toDate.after(searchDate))) {
                if (typ.getMileageRate() != null && typ.getMileageRate().isActive()) {
                    keyValues.add(new KeyLabelPair(typ.getMileageRateId(), typ.getMileageRate().getCodeAndRate())); 
                } 
            }            
        } 
        
        //sort by label
        Comparator<KeyLabelPair> labelComparator = new Comparator<KeyLabelPair>() {
            @Override
            public int compare(KeyLabelPair o1, KeyLabelPair o2) {
                try{
                    return o1.getLabel().compareTo(o2.getLabel());
                }catch (NullPointerException e){
                    return -1;
                }
            }
        };

        Collections.sort(keyValues, labelComparator);

        return keyValues;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyDownPerDiemExpense(int, java.util.List)
     */
    public void copyDownPerDiemExpense(int copyIndex, List<PerDiemExpense> perDiemExpenses) {

        PerDiemExpense lineToCopy = perDiemExpenses.get(copyIndex);
        List<PerDiemExpense> tempPerDiemExpenses = new ArrayList<PerDiemExpense>();

        if (copyIndex < perDiemExpenses.size()) {
            for (int i = 0; i < perDiemExpenses.size(); i++) {
                PerDiemExpense perDiemExpense = new PerDiemExpense();
                if (perDiemExpenses != null && i < copyIndex) {
                    // copy over from the old list
                    perDiemExpense = perDiemExpenses.get(i);
                }
                else if (i > copyIndex) {
                    perDiemExpense = this.copyPerDiemExpense(lineToCopy);
                    perDiemExpense.setMileageDate(perDiemExpenses.get(i).getMileageDate());
                }
                else {
                    perDiemExpense = lineToCopy;
                }

                tempPerDiemExpenses.add(perDiemExpense);

            }
        }

        perDiemExpenses.clear();
        perDiemExpenses.addAll(tempPerDiemExpenses);
    }

    /**
     * Get DV, TA, TAA, TAC, TR, and AV documents related to the given <code>document</code>. travel document either have a travel
     * document number or they have the value of the <code>document</code> in their organization doc ids.
     * 
     * @param document {@link TravelDocument} to get other document instances related to
     * @return A {@link Map} of {@link Document} instances where the key is the document type name
     */
    public Map<String, List<Document>> getDocumentsRelatedTo(final TravelDocument document) throws WorkflowException {
        return getDocumentsRelatedTo(document.getDocumentNumber());
    }

    /**
     * Get DV, TA, TAA, TAC, TR, and AV documents related to the given <code>travelDocumentIdentifier</code>. travel document either
     * have a TEM document number or they have the value of the <code>travelDocumentIdentifier</code> in their organization doc
     * ids.
     * 
     * @param travelDocumentIdentifier integer that links all travel related documents together
     * @return A {@link Map} of {@link Document} instances where the key is the document type name
     */
    public Map<String, List<Document>> getDocumentsRelatedTo(final String documentNumber) throws WorkflowException {
        final Map<String, List<Document>> retval = new HashMap<String, List<Document>>();

        Set<String> documentNumbers = accountingDocumentRelationshipService.getAllRelatedDocumentNumbers(documentNumber);
        if (!documentNumbers.isEmpty()) {
            for (String documentHeaderId : documentNumbers) {
                try{
                    Document doc = documentService.getByDocumentHeaderId(documentHeaderId);
                    if (doc != null) {
                        Class<? extends Document> clazz = doc.getClass();

                        if (clazz != null) {
                            String docTypeName = getDataDictionaryService().getDocumentTypeNameByClass(clazz);

                            List<Document> docs = retval.get(docTypeName);
                            if (docs == null) {
                                docs = new ArrayList<Document>();
                            }
                            docs.add(doc);

                            retval.put(docTypeName, docs);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                
            }
        }

        return retval;
    }

    protected boolean isTravelDocument(final Class documentClass) {
        return documentClass.getPackage().getName().contains("org.kuali.kfs.module.tem");
    }

    /**
     * @param docMap contains {@link List} instances of documents mapped by doc type name
     * @param clazz is the {@link Class} of documents to lookup
     * @param headerIds is a list of constrained header ids belonging to documents to search for
     */
    @Deprecated
    protected void addDocuments(final Map<String, List<Document>> docMap, final Class<? extends Document> clazz, final List<String> headerIds) throws WorkflowException {
        if (headerIds.size() == 0) {
            return;
        }

        final String docTypeName = getDataDictionaryService().getDocumentTypeNameByClass(clazz);
        final List<Document> results = getDocumentService().getDocumentsByListOfDocumentHeaderIds(clazz, headerIds);
        debug("Found ", results.size(), " documents with ids in ", headerIds);
        docMap.put(docTypeName, results);
    }


    /**
     * @param docMap contains {@link List} instances of documents mapped by doc type name
     * @param clazz is the {@link Class} of documents to lookup
     * @param headerIds is a list of constrained header ids belonging to documents to search for
     */
    @Deprecated
    protected void addDocuments(final Map<String, List<Document>> docMap, final Class<? extends Document> clazz, final String travelDocumentIdentifier) throws WorkflowException {
        final String docTypeName = getDataDictionaryService().getDocumentTypeNameByClass(clazz);
        final List<Document> results = (List<Document>) find(clazz, travelDocumentIdentifier);
        filterResults(clazz, results);
        debug("Found ", results.size(), " documents with travel id ", travelDocumentIdentifier);
        docMap.put(docTypeName, results);
    }

    /**
     * Make sure that the elements returned are of the right class.
     * 
     * @param clazz
     * @param results
     */
    protected void filterResults(Class<? extends Document> clazz, List<Document> results) {
        Iterator it = results.iterator();
        while (it.hasNext()) {
            if (!it.next().getClass().getName().equals(clazz.getName())) {
                it.remove();
            }
        }
    }

    protected Collection<DocumentHeader> getHeadersWith(final Integer orgDocId) throws WorkflowException {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("organizationDocumentNumber", "" + orgDocId);
        return getBusinessObjectService().findMatching(FinancialSystemDocumentHeader.class, criteria);
    }

    public List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType) {
        List<SpecialCircumstances> retval = new ArrayList<SpecialCircumstances>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("active", true);      
        
        // add specialCircumstances with specific documentType SpecialCircumstancesQuestion
        criteria.put("documentType", documentType);       
        retval.addAll(buildSpecialCircumstances(documentNumber, criteria));
        
        return retval;
    }

    private List<SpecialCircumstances> buildSpecialCircumstances(String documentNumber, Map<String, Object> criteria) {
        List<SpecialCircumstances> retval = new ArrayList<SpecialCircumstances>();
        
        Collection<SpecialCircumstancesQuestion> questions = getBusinessObjectService().findMatching(SpecialCircumstancesQuestion.class, criteria);
        for (SpecialCircumstancesQuestion question : questions) {
            SpecialCircumstances spc = new SpecialCircumstances();
            spc.setDocumentNumber(documentNumber);
            spc.setQuestionId(question.getId());
            spc.setQuestion(question);
            retval.add(spc);
        }
        
        return retval;
    }

    public <T> List<T> find(final Class<T> travelDocumentClass, final String travelDocumentNumber) throws WorkflowException {
        final List ids = getTravelDocumentDao().findDocumentNumbers(travelDocumentClass, travelDocumentNumber);

        if (ids.size() > 0) {
            return (List<T>) getDocumentService().getDocumentsByListOfDocumentHeaderIds(travelDocumentClass, ids);
        }

        return new ArrayList();
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocFYIRecipient(org.kuali.rice.kns.document.Document)
     */
    public void addAdHocFYIRecipient(final Document document) {
        addAdHocFYIRecipient(document, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocFYIRecipient(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    public void addAdHocFYIRecipient(final Document document, String initiatorUserId) {
        addAdHocRecipient(document, initiatorUserId, KEWConstants.ACTION_REQUEST_FYI_REQ);
    }    

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocRecipient(Document, String, String)
     */
    public void addAdHocRecipient(Document document, String initiatorUserId, String actionRequested) {
        List<AdHocRoutePerson> adHocRoutePersons = document.getAdHocRoutePersons();
        List<String> adHocRoutePersonIds = new ArrayList<String>();
        if(!adHocRoutePersons.isEmpty()){
            for(AdHocRoutePerson ahrp : adHocRoutePersons){
                adHocRoutePersonIds.add(ahrp.getId());
            }
        }
        
        // Add adhoc for initiator      
        if(!adHocRoutePersonIds.contains(initiatorUserId)){
            if(initiatorUserId != null){
                Person finSysUser = SpringContext.getBean(PersonService.class).getPerson(initiatorUserId);
                    if(finSysUser != null){
                    AdHocRoutePerson recipient = buildAdHocRecipient(finSysUser.getPrincipalName(), actionRequested);       
                    DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(document);    
                    if (documentAuthorizer.canReceiveAdHoc(document, finSysUser, actionRequested)) {               
                        adHocRoutePersons.add(recipient);
                    }
                }else{
                    LOG.warn("finSysUser is null.");
                }
            }else{
                LOG.warn("initiatorUserId is null.");
            }
        }
    }

    /**
     * This method builds the AdHoc Route Person
     * 
     * @param userId
     * @return
     */
    protected AdHocRoutePerson buildAdHocRecipient(String userId, String actionRequested) {
        AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
        adHocRoutePerson.setActionRequested(actionRequested);
        adHocRoutePerson.setId(userId);
        return adHocRoutePerson;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#calculateDailyTotals(java.util.List)
     */
    @Override
    public List<Map<String, KualiDecimal>> calculateDailyTotals(List<PerDiemExpense> perDiemExpenses) {
        List<Map<String, KualiDecimal>> tripTotals = new ArrayList<Map<String, KualiDecimal>>();

        for (PerDiemExpense perDiemExpense : perDiemExpenses){
            Map<String, KualiDecimal> dailyTotal = calculateDailyTotal(perDiemExpense);
            tripTotals.add(dailyTotal);
        }

        return tripTotals;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#calculateDailyTotal(org.kuali.kfs.module.tem.businessobject.PerDiemExpense,
     *      boolean)
     */
    @Override
    public Map<String, KualiDecimal> calculateDailyTotal(PerDiemExpense perDiemExpense) {
        Map<String, KualiDecimal> dailyTotals = new HashMap<String, KualiDecimal>();

        dailyTotals.put(MILEAGE_TOTAL_ATTRIBUTE, perDiemExpense.getMileageTotal());
        dailyTotals.put(LODGING_TOTAL_ATTRIBUTE, perDiemExpense.getLodgingTotal());
        dailyTotals.put(MEALS_AND_INC_TOTAL_ATTRIBUTE, perDiemExpense.getMealsAndIncidentals());             
        dailyTotals.put(DAILY_TOTAL, perDiemExpense.getDailyTotal());

        return dailyTotals;
    }

    public void routeToFiscalOfficer(final TravelDocument document, final String noteText) throws WorkflowException, Exception {
        // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'

        final Note newNote = getDocumentService().createNoteFromDocument(document, noteText);
        getDocumentService().addNoteToDocument(document, newNote);

        document.getDocumentHeader().getWorkflowDocument().returnToPreviousNode(noteText, TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED);

        addAdHocFYIRecipient(document, document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId());

        getDocumentService().saveDocument(document);
    }

    /**
     * 
     * This method calculates the prorate percentage value based on perDiemCalcMethod (P/Q)
     * @param expense
     * @param perDiemCalcMethod
     * @return
     */
    public Integer calculateProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd) {
        Integer perDiemPercent = 100;
        String perDiemPercentage = null;
        
        if (perDiemExpense.isProrated()) {
            if (perDiemCalcMethod != null && perDiemCalcMethod.equals(TemConstants.PERCENTAGE)) {
                try {
                    perDiemPercentage = parameterService.getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE);
                    perDiemPercent = Integer.parseInt(perDiemPercentage);                    
                }
                catch (Exception e1) {
                    error("Failed to process prorate percentage for FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE parameter.", e1);
                }
            }
            else {
                perDiemPercent = calculatePerDiemPercentageFromTimestamp(perDiemExpense, tripEnd);
            }
        }
        
        return perDiemPercent;
    }    

    public Integer calculatePerDiemPercentageFromTimestamp(PerDiemExpense perDiemExpense, Timestamp tripEnd) {
        if (perDiemExpense.getMileageDate() != null) {
            try {
                List<String> quarterTimes = parameterService.getParameterValues(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TravelParameters.QUARTER_DAY_TIME_TABLE);

                // Take date and compare to the quadrant specified.
                Calendar prorateDate = new GregorianCalendar();
                prorateDate.setTime(perDiemExpense.getMileageDate());

                int quadrantIndex = 4;
                for (String qT : quarterTimes) {
                    String[] indexTime = qT.split("=");
                    String[] hourMinute = indexTime[1].split(":");

                    Calendar qtCal = new GregorianCalendar();
                    qtCal.setTime(perDiemExpense.getMileageDate());
                    qtCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourMinute[0]));
                    qtCal.set(Calendar.MINUTE, Integer.parseInt(hourMinute[1]));
                            
                    if (prorateDate.equals(qtCal) || prorateDate.before(qtCal)) {
                        quadrantIndex = Integer.parseInt(indexTime[0]);
                        break;
                    }
                }
                            
                // Prorate on trip begin. (12:01 AM arrival = 100%, 11:59 PM arrival = 25%)
                if (!DateUtils.isSameDay(prorateDate.getTime(), tripEnd)) {
                    return 100 - ((quadrantIndex - 1) * TemConstants.QUADRANT_PERCENT_VALUE);
                }
                else { // Prorate on trip end. (12:01 AM departure = 25%, 11:59 PM arrival = 100%).
                    return quadrantIndex * TemConstants.QUADRANT_PERCENT_VALUE;
                }
            }
            catch (IllegalArgumentException e2) {
                error("IllegalArgumentException.", e2);
            }
        }
        
        return 100;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#transferPerDiemMileage(org.kuali.kfs.module.tem.businessobject.PerDiemMileage)
     */
    public PerDiemExpense copyPerDiemExpense(PerDiemExpense perDiemExpense) {
        final PerDiemExpense retval = new PerDiemExpense();
        retval.setDocumentNumber(perDiemExpense.getDocumentNumber());

        retval.setCountryState(perDiemExpense.getCountryState());
        retval.setCounty(perDiemExpense.getCounty());
        retval.setPrimaryDestination(perDiemExpense.getPrimaryDestination());
        retval.setMileageDate(perDiemExpense.getMileageDate());
        retval.setMiles(perDiemExpense.getMiles());
        retval.setAccommodationTypeCode(perDiemExpense.getAccommodationTypeCode());
        retval.setAccommodationName(perDiemExpense.getAccommodationName());
        retval.setAccommodationPhoneNum(perDiemExpense.getAccommodationPhoneNum());
        retval.setAccommodationAddress(perDiemExpense.getAccommodationAddress());

        debug("Setting mileage rate on new Per Diem Object to ", perDiemExpense.getMileageRateId());
        retval.setMileageRateId(perDiemExpense.getMileageRateId());
        retval.refreshReferenceObject("mileageRate");

        debug("Got mileage ", retval.getMileageRate());
        retval.setPerDiemId(perDiemExpense.getPerDiemId());     

        if (retval.getMiles() == null) {
            retval.setMiles(0);
        }

        if (perDiemExpense.getLodging() == null || perDiemExpense.getLodging().isNegative()) {
            retval.setLodging(KualiDecimal.ZERO);
        }
        else {
            retval.setLodging(perDiemExpense.getLodging());
        }

        retval.setPersonal(perDiemExpense.getPersonal());
        retval.setBreakfast(perDiemExpense.getBreakfast());
        retval.setLunch(perDiemExpense.getLunch());
        retval.setDinner(perDiemExpense.getDinner());
        
        retval.setBreakfastValue(perDiemExpense.getBreakfastValue());
        retval.setLunchValue(perDiemExpense.getLunchValue());
        retval.setDinnerValue(perDiemExpense.getDinnerValue());
        retval.setIncidentalsValue(perDiemExpense.getIncidentalsValue());

        debug("estimated meals and incidentals ", retval.getMealsAndIncidentals());

        return retval;
    }

    @Override
    /**
     * Calculates Mileage and returns total mileage amount
     * @param ActualExpense actualExpense
     */
    public KualiDecimal calculateMileage(ActualExpense actualExpense) {
        KualiDecimal mileageTotal = KualiDecimal.ZERO;

        if (ObjectUtils.isNotNull(actualExpense.getTravelExpenseTypeCodeCode()) && actualExpense.isMileage()) {
            mileageTotal = actualExpense.getMileageTotal();
        }

        return mileageTotal;
    }

    /**
     * Calculates Expense Amount total
     * 
     * @param List<ActualExpense> actualExpenses
     */
    public KualiDecimal calculateExpenseAmountTotalForMileage(final List<ActualExpense> actualExpenses) {
        KualiDecimal total = KualiDecimal.ZERO;

        for (final ActualExpense actualExpense : actualExpenses) {
            if (actualExpense.getMileageIndicator()) {
                actualExpense.setExpenseAmount(calculateMileage(actualExpense));
                total = total.add(actualExpense.getExpenseAmount());
                actualExpense.setConvertedAmount(actualExpense.getExpenseAmount().multiply(actualExpense.getCurrencyRate()));
            }

            // Check to see if it is detail record. if detail record, get parent record.
            if (ObjectUtils.isNotNull(actualExpense.getExpenseParentId())) {
                final ActualExpense parentActualExpense = getParentActualExpense(actualExpenses, actualExpense.getExpenseParentId());

                // If expense type for parent record is mileage, add detail expense amount to the parent expense amount
                if (parentActualExpense.getMileageIndicator()) {
                    parentActualExpense.setExpenseAmount(getExpenseAmountTotalFromDetail(actualExpense));
                    // total = total.add(parentActualExpense.getExpenseAmount());
                    parentActualExpense.setConvertedAmount(parentActualExpense.getExpenseAmount().multiply(parentActualExpense.getCurrencyRate()));
                }

            }
        }
        return total;
    }

    /**
     * This method returns the total expense amount from detail records
     * 
     * @param actualExpenses
     * @param expenseId
     * @return
     */
    public KualiDecimal getExpenseAmountTotalFromDetail(ActualExpense actualExpense) {
        KualiDecimal expenseAmount = KualiDecimal.ZERO;
        for (TEMExpense detail : actualExpense.getExpenseDetails()) {
            expenseAmount = expenseAmount.add(detail.getExpenseAmount());
        }
        return expenseAmount;
    }

    protected ActualExpense getParentActualExpense(final List<ActualExpense> actualExpenses, Long expenseId) {
        if (ObjectUtils.isNotNull(actualExpenses) && ObjectUtils.isNotNull(expenseId)) {

            for (final ActualExpense actualExpense : actualExpenses) {

                if (actualExpense.getId().equals(expenseId)) {
                    return actualExpense;
                }

            }
        }

        return null;
    }

    /**
     * 
     */
    public void handleNewActualExpense(final ActualExpense newActualExpenseLine) {
        if (newActualExpenseLine.getExpenseAmount() != null) {
            final KualiDecimal rate = newActualExpenseLine.getCurrencyRate();
            final KualiDecimal amount = newActualExpenseLine.getExpenseAmount();

            newActualExpenseLine.setConvertedAmount(amount.multiply(rate));
            debug("Set converted amount for ", newActualExpenseLine, " to ", newActualExpenseLine.getConvertedAmount());

            if (isHostedMeal(newActualExpenseLine)) {
                getMessageList().add(TemKeyConstants.MESSAGE_HOSTED_MEAL_ADDED,
                        new SimpleDateFormat("MM/dd/yyyy").format(newActualExpenseLine.getExpenseDate()),
                        newActualExpenseLine.getTravelExpenseTypeCode().getName());
                newActualExpenseLine.setNonReimbursable(true);
            }
        }
    }

    /**
     * Determines if an object with an expense type is that of a "hosted" meal. In TEM a hosted meal is a meal that has been
     * provided by a hosting institution and cannot be taken as a reimbursement. Uses the HOSTED_MEAL_EXPENSE_TYPES system parameter
     * to check the expense type against
     * 
     * @param havingExpenseType has an expense type to check for meal hosting
     * @return true if the expense is a hosted meal or not
     */
    public boolean isHostedMeal(final ExpenseTypeAware havingExpenseType) {
        if (ObjectUtils.isNull(havingExpenseType) || ObjectUtils.isNull(havingExpenseType.getTravelExpenseTypeCode())) {
            return false;
        }

        final String code = havingExpenseType.getTravelExpenseTypeCode().getCode();
        final String hostedCodes = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, HOSTED_MEAL_EXPENSE_TYPES);

        for (final String hostedSet : hostedCodes.split(";")) {
            final String[] codesForMeal = (hostedSet.contains("=") ? StringUtils.substringAfter(hostedSet, "=") : hostedSet).split(",");
            if (codesForMeal == null) {
                if (hostedSet.equalsIgnoreCase(code)) {
                    return true;
                }
            }
            else {
                for (final String hostedCode : codesForMeal) {
                    if (hostedCode.equalsIgnoreCase(code)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    /**
     * This method creates GLPE to disencumber the funds that had already been encumbered.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    public void disencumberFunds(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            // Find outstanding balances
            // lookup glpe's by trip id           
            TravelAuthorizationDocument taDocument = null;
            
            if (document instanceof TravelAuthorizationDocument) {
                taDocument = (TravelAuthorizationDocument) document;
            }
            else {
                try {
                    taDocument = (TravelAuthorizationDocument) find(TravelAuthorizationDocument.class, document.getTravelDocumentIdentifier()).get(0);
                }
                catch (WorkflowException we) {
                    warn("Unable to find ", TravelAuthorizationDocument.class.getSimpleName(), " related to ", document.getTravelDocumentIdentifier());
                }
            }

            getGeneralLedgerPendingEntryService().delete(taDocument.getDocumentNumber());

            int counter = taDocument.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            // Get encumbrance for the document
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", taDocument.getTravelDocumentIdentifier());

            final Iterator<Encumbrance> encumbrance_it = getEncumbranceService().findOpenEncumbrance(criteria);
            while (encumbrance_it.hasNext()) {
                liquidateEncumbrance((Encumbrance) encumbrance_it.next(), sequenceHelper, taDocument);
            }
        }

    }

    @Override
    public void updateEncumbranceObjectCode(TravelDocument taDoc, SourceAccountingLine line) {
        // Accounting Line default the Encumbrance Object Code based on trip type
        if (ObjectUtils.isNotNull(taDoc.getTripType())) {
            // set object code based on trip type
            line.setFinancialObjectCode(taDoc.getTripType().getEncumbranceObjCode());
        }
        else {
            // default object code here
            line.setFinancialObjectCode("");
        }
    }

    /**
     * This method removes all remaining encumbrance balance to bring the outstanding balance to zero.
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     */
    public void liquidateEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        GeneralLedgerPendingEntry pendingEntry = null;
        GeneralLedgerPendingEntry offsetEntry = null;

        pendingEntry = this.setupPendingEntry(encumbrance, sequenceHelper, document);
        sequenceHelper.increment();
        offsetEntry = this.setupOffsetEntry(encumbrance, sequenceHelper, document, pendingEntry);
        sequenceHelper.increment();

        if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isGreaterThan(KualiDecimal.ZERO)) {
            final KualiDecimal amount = encumbrance.getAccountLineEncumbranceOutstandingAmount();
            pendingEntry.setTransactionLedgerEntryAmount(amount);
            offsetEntry.setTransactionLedgerEntryAmount(amount);
            document.addPendingEntry(pendingEntry);
            document.addPendingEntry(offsetEntry);
        }
    } 

    /**
     * This method creates the pending entry based on the document and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    public GeneralLedgerPendingEntry setupPendingEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        final GeneralLedgerPendingEntrySourceDetail sourceDetail = convertTo(encumbrance);
        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();

        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }
        getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        pendingEntry.setFinancialBalanceTypeCode(balanceType);
        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        pendingEntry.setReferenceFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        pendingEntry.setReferenceFinancialSystemOriginationCode(encumbrance.getOriginCode());

        return pendingEntry;
    }


    /**
     * This method creates the offset entry based on the pending entry, document, and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param pendingEntry The pending entry that will accompany the offset entry.
     * @return offsetEntry The completed offset entry.
     */
    public GeneralLedgerPendingEntry setupOffsetEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {
        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }

        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        offsetEntry.setFinancialBalanceTypeCode(balanceType);
        offsetEntry.setReferenceFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        offsetEntry.setReferenceFinancialSystemOriginationCode(encumbrance.getOriginCode());

        return offsetEntry;
    }    

    /**
     * This method is used for creating a TAC document.
     * 
     * @param taDocument The originating document.
     */
    public void adjustEncumbranceForClose(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", document.getTravelDocumentIdentifier());

            final Map<String, Encumbrance> encumbranceMap = new HashMap<String, Encumbrance>();
            final Iterator<Encumbrance> encumbrance_it = getEncumbranceService().findOpenEncumbrance(criteria);

            // Create encumbrance map based on account numbers
            while (encumbrance_it.hasNext()) {
                Encumbrance encumbrance = (Encumbrance) encumbrance_it.next();
                StringBuffer key = new StringBuffer();
                key.append(encumbrance.getAccountNumber());
                key.append(encumbrance.getSubAccountNumber());
                key.append(encumbrance.getObjectCode());
                key.append(encumbrance.getSubObjectCode());
                key.append(encumbrance.getDocumentNumber());
                encumbranceMap.put(key.toString(), encumbrance);
            }

          //Get rid of all pending entries relating to encumbrance.
            processRelatedDocuments(document);

            int counter = document.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            if (!encumbranceMap.isEmpty()) {
                //Loop trough and create a glpe to close out the remaining outstanding encumbrance.
                for (final Encumbrance encumbrance : encumbranceMap.values()) {
                    liquidateEncumbrance(encumbrance, sequenceHelper, document);
                }
            }
        }
    }

    /**
     * Find All pending approved TA, TAA, TR glpe's. Make sure they are not offsets and not the current doc (this will be
     * previous document) Rather than deal with the complicated math of taking the old document's glpe's into account, just
     * remove them so they will never be picked up by the jobs and placed into encumbrance.
     * 
     * @param taDocument
     *          The document being processed.  Should only be a TAA or TAC.
     *          
     * @param encumbranceMap
     */
    public void processRelatedDocuments(TravelDocument document) {      
        try {
            Map<String, List<Document>> relatedDocs = getDocumentsRelatedTo(document);
            List<Document> taDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            List<Document> taaDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

            if(taDocs == null){
                taDocs = new ArrayList<Document>();
            }
            
            if(taaDocs != null){
                taDocs.addAll(taaDocs);
            }
            
            for (final Document tempDocument : taDocs) {
                if (!document.getDocumentNumber().equals(tempDocument.getDocumentNumber())) {
                    /*
                     * New for M3 - Skip glpe's created for imported expenses.
                     */
                    for (GeneralLedgerPendingEntry glpe :document.getGeneralLedgerPendingEntries()){
                        if (glpe != null && glpe.getOrganizationReferenceId() != null && !glpe.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                            getBusinessObjectService().delete(glpe);
                        }
                    }
                }
            }
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }    

    /**
     * This method adjusts the encumbrance for a TAA document.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    public void adjustEncumbranceForAmendment(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", document.getTravelDocumentIdentifier());

            int counter = document.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            final Map<String, Encumbrance> encumbranceMap = new HashMap<String, Encumbrance>();
            final Iterator<Encumbrance> encumbrance_it = getEncumbranceService().findOpenEncumbrance(criteria);

            // Create encumbrance map based on account numbers
            while (encumbrance_it.hasNext()) {
                Encumbrance encumbrance = (Encumbrance) encumbrance_it.next();
                StringBuffer key = new StringBuffer();
                key.append(encumbrance.getAccountNumber());
                key.append(encumbrance.getSubAccountNumber());
                key.append(encumbrance.getObjectCode());
                key.append(encumbrance.getSubObjectCode());
                key.append(encumbrance.getDocumentNumber());
                encumbranceMap.put(key.toString(), encumbrance);
            }

            processRelatedDocuments(document);

            /*
             * Adjust current encumbrances with the new amounts If new pending entry is found in encumbrance map, create a pending
             * entry to balance the difference by either crediting or debiting. If not found just continue on to be processed as
             * normal.
             */
            Iterator<GeneralLedgerPendingEntry> pendingEntriesIterator = document.getGeneralLedgerPendingEntries().iterator();
            while (pendingEntriesIterator.hasNext()) {
                GeneralLedgerPendingEntry pendingEntry = pendingEntriesIterator.next();
                /*
                 * New for M3 - Skip glpe's created for imported expenses.
                 */
                if (!pendingEntry.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                    StringBuffer key = new StringBuffer();
                    key.append(pendingEntry.getAccountNumber());
                    key.append(pendingEntry.getSubAccountNumber());
                    key.append(pendingEntry.getFinancialObjectCode());
                    key.append(pendingEntry.getFinancialSubObjectCode());
                    key.append(pendingEntry.getReferenceFinancialDocumentNumber());
                    Encumbrance encumbrance = encumbranceMap.get(key.toString());
                    /*
                     * If encumbrance found, find and calculate difference. If the difference is zero don't add to new list of glpe's If
                     * encumbrance is not found and glpe is not an offset glpe, add it and it's offset to the new list
                     */
                    if (encumbrance != null) {
                        KualiDecimal difference = encumbrance.getAccountLineEncumbranceOutstandingAmount().subtract(pendingEntry.getTransactionLedgerEntryAmount());
                        if (difference.isGreaterThan(KualiDecimal.ZERO)) {
                            if (!pendingEntry.isTransactionEntryOffsetIndicator()) {
                                pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                            }
                            else {
                                pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                            }
                            pendingEntry.setTransactionLedgerEntryAmount(difference);
                            pendingEntriesIterator.next().setTransactionLedgerEntryAmount(difference);
                        }
                        else if (difference.isLessEqual(KualiDecimal.ZERO)) {
                            difference = difference.multiply(new KualiDecimal(-1));
                            pendingEntry.setTransactionLedgerEntryAmount(difference);
                            pendingEntriesIterator.next().setTransactionLedgerEntryAmount(difference);
                        }
                        /*
                         * else{ pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD);
                         * pendingEntriesIterator.next().setFinancialDocumentApprovedCode(KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD); }
                         */

                    }
                }
            }

            /*
             * Loop through and remove encumbrances from map. This is done here because there is a possibility of pending entries
             * with the same account number.
             */
            for (GeneralLedgerPendingEntry pendingEntry : document.getGeneralLedgerPendingEntries()) {
                if (!pendingEntry.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                    if (!pendingEntry.isTransactionEntryOffsetIndicator()) {
                        StringBuffer key = new StringBuffer();
                        key.append(pendingEntry.getAccountNumber());
                        key.append(pendingEntry.getSubAccountNumber());
                        key.append(pendingEntry.getFinancialObjectCode());
                        key.append(pendingEntry.getFinancialSubObjectCode());
                        key.append(pendingEntry.getReferenceFinancialDocumentNumber());
                        encumbranceMap.remove(key.toString());
                    }
                }
            }

            /*
             * Find any remaining encumbrances that no longer should exist in the new TAA.
             */
            if (!encumbranceMap.isEmpty()) {
                for (final Encumbrance encumbrance : encumbranceMap.values()) {
                    liquidateEncumbrance(encumbrance, sequenceHelper, document);
                }
            }

        }
    }

    /**
     * This method sets up the pending and offset entries and adds them to the document
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to
     * @param tempList A temporary list to hold all the values in.
     */
    protected void adjustEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, List<GeneralLedgerPendingEntry> tempList) {
        GeneralLedgerPendingEntry pendingEntry = null;
        GeneralLedgerPendingEntry offsetEntry = null;

        pendingEntry = this.setupPendingEntry(encumbrance, sequenceHelper, document);
        sequenceHelper.increment();
        offsetEntry = this.setupOffsetEntry(encumbrance, sequenceHelper, document, pendingEntry);
        sequenceHelper.increment();

        KualiDecimal amount = null;
        if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isGreaterThan(KualiDecimal.ZERO)) {
            amount = encumbrance.getAccountLineEncumbranceOutstandingAmount();
            pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isLessThan(KualiDecimal.ZERO)) {
            amount = encumbrance.getAccountLineEncumbranceOutstandingAmount().multiply(new KualiDecimal(-1));

        }
        if (amount != null) {
            pendingEntry.setTransactionLedgerEntryAmount(amount);
            offsetEntry.setTransactionLedgerEntryAmount(amount);
            tempList.add(pendingEntry);
            tempList.add(offsetEntry);
        }
    }
    
    public boolean isTravelArranger(final Person user, final String primaryDepartmentCode) {
    	boolean checkProfileAssignedRole = checkPersonRole(user, TemConstants.TEM_ASSIGNED_PROFILE_ARRANGER, TemConstants.PARAM_NAMESPACE);
        if(!checkProfileAssignedRole) {
            boolean checkOrgRole = checkOrganizationRole(user, TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER, TemConstants.PARAM_NAMESPACE, primaryDepartmentCode);
            return checkOrgRole;
        }
        return checkProfileAssignedRole;
    }
    
    public boolean isTravelManager(final Person user) {
        return checkPersonRole(user, TemConstants.TRAVEL_MANAGER, KFSConstants.ParameterNamespaces.FINANCIAL);
    }

    public boolean isFiscalOfficer(final Person user) {
        return checkPersonRole(user, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME, KFSConstants.ParameterNamespaces.KFS);
    }
       
    public boolean checkPersonRole(final Person user, String role, String parameterNamespace){
        try{
            final String arrangerRoleId = roleService.getRoleIdByName(parameterNamespace, role);
            
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(arrangerRoleId);
            return roleService.principalHasRole(user.getPrincipalId(), roleIds, null);
        } catch(NullPointerException e){
            LOG.error("NPE.", e);
        }
        
        return false;   
    }
    
    public boolean checkOrganizationRole(final Person user, String role, String parameterNamespace, String primaryDepartmentCode){
        try{
        	final String arrangerRoleId = roleService.getRoleIdByName(parameterNamespace, role);
            
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(arrangerRoleId);
            
            AttributeSet qualification = null;
        	String chartOfAccounts, organizationCode = null;
            if (StringUtils.isNotEmpty(primaryDepartmentCode)) {
	            String[] split = primaryDepartmentCode.split("-");
	            if(split != null){ 
	                chartOfAccounts = split[0];
	                qualification = new AttributeSet();
	                qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccounts);
	                qualification.put("performQualifierMatch", "True");
		            
		            if(split.length == 2){                     
		            	organizationCode = split[1];
		
		            	qualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
		            }
	            }
            }
            
            if(roleService.principalHasRole(user.getPrincipalId(), roleIds, qualification)) {
            	return true;
            }
        }catch(NullPointerException e){
            LOG.error("NPE.", e);
        }
        
        return false;   
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#convertTo(Encumbrance)
     */
    public GeneralLedgerPendingEntrySourceDetail convertTo(final Encumbrance encumbrance) {
        return new AccountingLineBase() {
            /**
             * @return Returns the chartOfAccountsCode.
             */
            public String getChartOfAccountsCode() {
                return encumbrance.getChartOfAccountsCode();
            }

            /**
             * @return Returns the accountNumber.
             */
            public String getAccountNumber() {
                return encumbrance.getAccountNumber();
            }

            /**
             * @return Returns the account.
             */
            public Account getAccount() {
                return encumbrance.getAccount();
            }

            /**
             * @return Returns the documentNumber.
             */
            public String getDocumentNumber() {
                return encumbrance.getDocumentNumber();
            }

            /**
             * @return Returns the financialObjectCode.
             */
            public String getFinancialObjectCode() {
                return encumbrance.getObjectCode();
            }

            /**
             * @return Returns the objectCode.
             */
            public ObjectCode getObjectCode() {
                return encumbrance.getFinancialObject();
            }

            /**
             * @return Returns the referenceNumber.
             */
            public String getReferenceNumber() {
                return encumbrance.getDocumentNumber();
            }

            /**
             * @return Returns the subAccountNumber.
             */
            public String getSubAccountNumber() {
                return encumbrance.getSubAccountNumber();
            }

            /**
             * @return Returns the financialSubObjectCode.
             */
            public String getFinancialSubObjectCode() {
                return encumbrance.getSubObjectCode();
            }

            /**
             * @return Returns the financialDocumentLineDescription.
             */
            public String getFinancialDocumentLineDescription() {
                return encumbrance.getTransactionEncumbranceDescription();
            }

            /**
             * @return Returns the amount.
             */
            public KualiDecimal getAmount() {
                return encumbrance.getAccountLineEncumbranceOutstandingAmount();
            }

            /**
             * @return Returns the postingYear.
             */
            public Integer getPostingYear() {
                return encumbrance.getUniversityFiscalYear();
            }

            /**
             * @return Returns the balanceTypeCode.
             */
            public String getBalanceTypeCode() {
                return encumbrance.getBalanceTypeCode();
            }

            /**
             * @return Returns the documentTypeCode.
             */
            public String getFinancialDocumentTypeCode() {
                return encumbrance.getDocumentTypeCode();
            }

            /**
             * @return Returns the originCode.
             */
            public String getFinancialSystemOriginationCode() {
                return encumbrance.getOriginCode();
            }

            /**
             * @return Returns the originCode.
             */
            public String getReferenceFinancialDocumentTypeCode() {
                return encumbrance.getDocumentTypeCode();
            }

            /**
             * @return Returns the originCode.
             */
            public String getReferenceFinancialSystemOriginationCode() {
                return encumbrance.getOriginCode();
            }

            /**
             * @return Returns the originCode.
             */
            public String getReferenceFinancialDocumentNumber() {
                return encumbrance.getDocumentNumber();
            }

        };
    }
    
    /**
     * Digs up a message from the {@link ConfigurationService} by key
     */
    public String getMessageFrom(final String messageType, String... args) {
        String strTemp = getConfigurationService().getPropertyString(messageType);
        for(int i=0;i<args.length;i++){
            strTemp = strTemp.replaceAll("\\{"+i+"\\}", args[i]);
        }
        return strTemp;
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDateTimeService(final DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setTravelDocumentDao(final TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    protected TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    protected EncumbranceService getEncumbranceService() {
        return SpringContext.getBean(EncumbranceService.class);
    }

    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

	/**
	 * Gets the roleService attribute. 
	 * @return Returns the roleService.
	 */
	public RoleService getRoleService() {
		return roleService;
	}

	/**
	 * Sets the roleService attribute value.
	 * @param roleService The roleService to set.
	 */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

    
    protected KualiConfigurationService getConfigurationService() {
        return SpringContext.getBean(KualiConfigurationService.class);
    }

    /**
     * is this document in an open for reimbursement workflow state?
     * 
     * @param reqForm
     * @return
     */
    public boolean isOpen(TravelDocument document) {
        return document.getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
    }

    /**
     * is this document in a final workflow state
     * 
     * @param reqForm
     * @return
     */
    public boolean isFinal(TravelDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().stateIsFinal();
    }
    
    public boolean isUnsuccessful(TravelDocument document) {
        String status = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();
        List<String> unsuccessful = KEWConstants.DOCUMENT_STATUS_PARENT_TYPES.get(KEWConstants.DOCUMENT_STATUS_PARENT_TYPE_UNSUCCESSFUL);
        for (String tempStatus : unsuccessful){
            if (status.equals(tempStatus)){
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * is this document in a processed workflow state?
     * 
     * @param reqForm
     * @return
     */
    public boolean isProcessed(TravelDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().stateIsProcessed();
    }

    @Override
    public KualiDecimal getAmountDueFromInvoice(String documentNumber, KualiDecimal requestedAmount) {
        try {
            AccountsReceivableCustomerInvoice doc = (AccountsReceivableCustomerInvoice) documentService.getByDocumentHeaderId(documentNumber);
            return doc.getOpenAmount();
        }
        catch (Exception ex) {
            // TODO Auto-generated catch block
            //.printStackTrace();
        }
        
        return requestedAmount;
    }
    
    /**
     * Find the current travel authorization.  This includes any amendments.
     * @param trDocument
     * @return
     * @throws WorkflowException
     */
    public TravelDocument findCurrentTravelAuthorization(TravelDocument document) throws WorkflowException{
        Map<String, List<Document>> relatedDocuments = getDocumentsRelatedTo(document);
        List<Document> taDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        List<Document> taaDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
        List<Document> tacDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
        
        //If TAC exists, it will always be the most current travel auth doc
        if (tacDocs != null && tacDocs.size() > 0){
            return (TravelAuthorizationDocument) tacDocs.get(0);
        }
        //find the TAA with the correct status
        else if (taaDocs != null && taaDocs.size() > 0){
            for (Document tempDocument : taaDocs){
                //Find the doc that is the open to perform actions against.
                if ((isFinal((TravelAuthorizationDocument)tempDocument)
                        || isProcessed((TravelAuthorizationDocument)tempDocument))
                        && isOpen((TravelAuthorizationDocument)tempDocument)){
                    return (TravelAuthorizationDocument) tempDocument;
                }
            }           
        }
        //return TA doc if no amendments exist
        else{
            if(taDocs == null || taDocs.isEmpty()){
                List<TravelAuthorizationDocument> tempTaDocs = find(TravelAuthorizationDocument.class, document.getTravelDocumentIdentifier());
                taDocs = new ArrayList<Document>();
                taDocs.addAll(tempTaDocs);
            }
            
            if(taDocs != null && !taDocs.isEmpty()){
                return (TravelAuthorizationDocument) taDocs.get(0);
            }
        }
        
        return null;
    }

    @Override
    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document) {
        KualiDecimal trTotal = KualiDecimal.ZERO;
        List<Document> trDocs = null;
        try {
            trDocs = getDocumentsRelatedTo(document).get(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        if(trDocs != null && trDocs.size() > 0) {
            for(Document trDoc: trDocs) {
                List<AccountingLine> lines = ((TravelReimbursementDocument)trDoc).getSourceAccountingLines();
                for(AccountingLine line: lines) {
                    trTotal = trTotal.add(line.getAmount());
                }
                
            } 
        }
        if (document.getDocumentHeader().getWorkflowDocument().getDocumentType().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)){
            List<AccountingLine> lines = document.getSourceAccountingLines();
            for(AccountingLine line: lines) {
                trTotal = trTotal.add(line.getAmount());
            }
        }
        
        return trTotal;
    }

    @Override
    public KualiDecimal getTotalAuthorizedEncumbrance(TravelDocument document) {
        KualiDecimal taTotal = KualiDecimal.ZERO;
        TravelAuthorizationDocument taDoc = null;
        try {
            taDoc = (TravelAuthorizationDocument) findCurrentTravelAuthorization(document);
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        if(taDoc != null) {
            List<AccountingLine> lines = taDoc.getSourceAccountingLines();
            for(AccountingLine line: lines) {
                taTotal = taTotal.add(line.getAmount());
            }
        }
        return taTotal;
        
    } 
    
    /**
     * Determines if the user is a fiscal officer on {@link Account} instances tied to the {@link TravelAuthorizationDocument}
     * instance
     *
     * @param authorization to check for fiscal officer status on
     * @param principalId is a Person that might be a fiscal officer on account
     * @return if the <code>user</code> is a fiscal officer on accounts tied to the {@link TravelAuthorizationDocument}
     */
    public boolean isResponsibleForAccountsOn(final TravelDocument document, String principalId) {
        final List<String> accounts = findAccountsResponsibleFor(document.getSourceAccountingLines(), principalId);
        return (accounts != null && accounts.size() > 0);
    }

    /**
     * Looks up accounts from {@link List} of {@link SourceAccountingLine} instances to determine if {@link Person} <code>user</code>
     * is a fiscal officer on any of those
     * 
     * @param lines or {@link List} of {@link SourceAccountingLine} instances 
     * @param principalId is a Person that might be a fiscal officer on accounts in <code>lines</code>
     * @return a {@link List} of account numbers the {@link Person} is a fiscal officer on
     */
    protected List<String> findAccountsResponsibleFor(final List<SourceAccountingLine> lines, String principalId) {
        final Set<String> retval = new HashSet<String>();
        for (final AccountingLine line : lines) {
            // COA Account getAccountFiscalOfficerUser() is generating PersistenceBrokerException when we lookup an invalid account number.
            try {
                if (line != null && line.getAccount() != null) {
                    Person accountFiscalOfficerUser = line.getAccount().getAccountFiscalOfficerUser();
                    if (accountFiscalOfficerUser != null && accountFiscalOfficerUser.getPrincipalId().equals(principalId)) {
                        retval.add(line.getAccountNumber());
                    }
                }
            } catch (PersistenceBrokerException ex){
                ex.printStackTrace();
            }
        }
        return new ArrayList(retval);
    }
    
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document) {
        disbursementVoucherDocument.setRefundIndicator(true);
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(document.getTravelDocumentIdentifier());
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);        
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
        
        try {
            disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle("Disbursement Voucher - " + document.getDocumentHeader().getDocumentDescription());
        }
        catch (WorkflowException ex) {
            error("cannot set title for DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
            throw new RuntimeException("Error setting DV title: " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }
        disbursementVoucherDocument.initiateDocument();
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        disbursementVoucherDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());

        // This type needs to be Customer "C", do not change otherwise we will change the configuration
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_CUSTOMER); 
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(document.getTraveler().getPrincipalId());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(document.getTraveler().getFirstName() + " " + document.getTraveler().getLastName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);

       // disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(document.getTraveler().getCustomerAddressIdentifier().toString());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(document.getTraveler().getStreetAddressLine1());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(document.getTraveler().getStreetAddressLine2());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(document.getTraveler().getCityName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(document.getTraveler().getStateCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(document.getTraveler().getZipCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(document.getTraveler().getCountryCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(travelerService.isEmployee(document.getTraveler()));

        disbursementVoucherDocument.setDisbVchrPaymentMethodCode("P");
        
        String advancePaymentChartCode = parameterService.getParameterValue(PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_CHART_CODE);
        String advancePaymentAccountNumber = parameterService.getParameterValue(PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR);
        String advancePaymentObjectCode = parameterService.getParameterValue(PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE);

        // set accounting
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (Object accountingLineObj : document.getSourceAccountingLines()) {
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

            totalAmount = totalAmount.add(sourceccountingLine.getAmount()); //TODO: verify this.
        }

        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
    }
    
    public DisbursementVoucherDocument createDVReimbursementDocument(TravelDocumentBase document){
        String principalName = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
        GlobalVariables.setUserSession(new UserSession(principalName));

        DisbursementVoucherDocument disbursementVoucherDocument = null;
        try {
            disbursementVoucherDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            error("Error creating new disbursement voucher document: ", e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }
        document.populateDisbursementVoucherFields(disbursementVoucherDocument);
        
        final Map<String, TypedArrayList> oldErrors = new LinkedHashMap<String, TypedArrayList>();
        oldErrors.putAll(GlobalVariables.getMessageMap().getErrorMessages());

        // always save DV
        try {
            disbursementVoucherDocument.prepareForSave();
            businessObjectService.save(disbursementVoucherDocument);
        }catch(Exception e){
            // if we can't save DV, need to stop processing
            error("cannot save DV ", disbursementVoucherDocument.getDocumentNumber(), e);
            throw new RuntimeException("cannot save DV " + disbursementVoucherDocument.getDocumentNumber(), e);           
        }
        
        Note taDvNote = null;
        try {
            taDvNote = getDocumentService().createNoteFromDocument(disbursementVoucherDocument, "system generated note by document # " + document.getTravelDocumentIdentifier());
            getDocumentService().addNoteToDocument(disbursementVoucherDocument, taDvNote);

            boolean rulePassed = getKualiRuleService().applyRules(new AttributedRouteDocumentEvent("", disbursementVoucherDocument));

            if (rulePassed && !(TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())
            		|| TemConstants.DisbursementVoucherPaymentMethods.FOREIGN_DRAFT_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode()))) {
            	
            	KualiWorkflowDocument originalWorkflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();
                
            	try {
                    // original initiator may not have permission to blanket approve the DV
                    GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                    
                    KualiWorkflowDocument newWorkflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(disbursementVoucherDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                    newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                    
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
                
                    String annotation = "Blanket Approved by system in relation to Travel Auth Document: " + document.getDocumentNumber();
                	getWorkflowDocumentService().blanketApprove(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation, null); 
            	}
                finally {
                    GlobalVariables.setUserSession(new UserSession(principalName));
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
                }
            	
                final String noteText = String.format("DV Document %s was system generated and blanket approved", disbursementVoucherDocument.getDocumentNumber());                
            	final Note noteToAdd = getDocumentService().createNoteFromDocument(document, noteText);
            	getDocumentService().addNoteToDocument(document, noteToAdd);
            }
            else {
                businessObjectService.save(disbursementVoucherDocument);
                String annotation = "Saved by system in relation to Document: " + document.getDocumentNumber();
                getWorkflowDocumentService().save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation);

                final String noteText = String.format("DV Document %s is saved in the initiator's action list", disbursementVoucherDocument.getDocumentNumber());
                final Note noteToAdd = getDocumentService().createNoteFromDocument(document, noteText);
                getDocumentService().addNoteToDocument(document, noteToAdd);
                addAdHocFYIRecipient(disbursementVoucherDocument, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            }
        }
        catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        GlobalVariables.getMessageMap().clearErrorMessages();
        GlobalVariables.getMessageMap().getErrorMessages().putAll(oldErrors);
        return disbursementVoucherDocument;
    }
    
    /**
     * This method checks to see if the type code is for a non-employee
     * 
     * @param travelerTypeCode
     */
    public boolean checkNonEmployeeTravelerTypeCode(String travelerTypeCode) {
        boolean foundCode = false;
        if (getParameterService().getParameterValues(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, NON_EMPLOYEE_TRAVELER_TYPE_CODES).contains(travelerTypeCode)) {
            foundCode = true;
        }
        return foundCode;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getAllStates(java.lang.String)
     */
    public String getAllStates(final String countryCode) {

        final List<State> codes = getStateService().findAllStates(countryCode);

        final StringBuffer sb = new StringBuffer();
        sb.append(";");
        for (final State state : codes) {
            if (state.isActive()) {
                sb.append(state.getPostalStateCode()).append(";");
            }
        }

        return sb.toString();
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyGroupTravelers(java.util.List, java.lang.String)
     */
    public List<GroupTraveler> copyGroupTravelers(List<GroupTraveler> groupTravelers, String documentNumber) {
        List<GroupTraveler> newGroupTravelers = new ArrayList<GroupTraveler>();
        if (groupTravelers != null) {
            for (GroupTraveler groupTraveler : groupTravelers) {
                GroupTraveler newGroupTraveler = new GroupTraveler();
                try {
                    BeanUtils.copyProperties(newGroupTraveler, groupTraveler);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                newGroupTraveler.setDocumentNumber(documentNumber);
                newGroupTraveler.setVersionNumber(new Long(1));
                newGroupTraveler.setObjectId(null);
                newGroupTraveler.setId(null);
                newGroupTravelers.add(newGroupTraveler);
            }
        }
        return newGroupTravelers;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyActualExpenses(java.util.List, java.lang.String)
     */
    public List<? extends TEMExpense> copyActualExpenses(List<? extends TEMExpense> actualExpenses, String documentNumber) {
        List<ActualExpense> newActualExpenses = new ArrayList<ActualExpense>();
                         
        if (actualExpenses != null) {
            for (TEMExpense expense : actualExpenses) {  
                ActualExpense actualExpense = (ActualExpense)expense;
                ActualExpense newActualExpense = new ActualExpense();
                boolean nullCheck = false;
                if (actualExpense.getExpenseDate() == null) {
                    nullCheck = true;
                    actualExpense.setExpenseDate(new Date(0));
                }
                try {
                    BeanUtils.copyProperties(newActualExpense, actualExpense);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                if (nullCheck) {
                    actualExpense.setExpenseDate(null);
                    newActualExpense.setExpenseDate(null);
                }
                
                List<TEMExpense> newDetails = (List<TEMExpense>) this.copyActualExpenses(actualExpense.getExpenseDetails(), documentNumber);
                newActualExpense.setExpenseDetails(newDetails);
                newActualExpense.setDocumentNumber(documentNumber);
                newActualExpense.setVersionNumber(new Long(1));
                newActualExpense.setId(null);
                newActualExpense.setObjectId(null);
                //newActualExpense.setExpenseParentId(actualExpense.getExpenseParentId());
                newActualExpenses.add(newActualExpense);                
            }
        }
        return newActualExpenses;
    }
    
    private Long getNextActualExpenseId(){
        return SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber(TemConstants.TEM_ACTUAL_EXPENSE_SEQ_NAME);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyPerDiemExpenses(java.util.List, java.lang.String)
     */
    public List<PerDiemExpense> copyPerDiemExpenses(List<PerDiemExpense> perDiemExpenses, String documentNumber) {
        List<PerDiemExpense> newPerDiemExpenses = new ArrayList<PerDiemExpense>();
        if (perDiemExpenses != null) {
            for (PerDiemExpense expense : perDiemExpenses){
                PerDiemExpense newExpense = new PerDiemExpense();
                try {
                    BeanUtils.copyProperties(newExpense, expense);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                newExpense.setBreakfastValue(expense.getBreakfastValue());
                newExpense.setLunchValue(expense.getLunchValue());
                newExpense.setDinnerValue(expense.getDinnerValue());
                newExpense.setIncidentalsValue(expense.getIncidentalsValue());
                newExpense.setDocumentNumber(documentNumber);
                newExpense.setVersionNumber(new Long(1));
                newExpense.setObjectId(null);
                newExpense.setId(null);
                newPerDiemExpenses.add(newExpense);
            }
        }
        return newPerDiemExpenses;
    }    

    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyTravelAdvances(java.util.List, java.lang.String)
     */
    public List<TravelAdvance> copyTravelAdvances(List<TravelAdvance> travelAdvances, String documentNumber) {
        List<TravelAdvance> newTravelAdvances = new ArrayList<TravelAdvance>();
        if (travelAdvances != null) {
            for (TravelAdvance travelAdvance : travelAdvances){
                TravelAdvance newTravelAdvance = (TravelAdvance) ObjectUtils.deepCopy(travelAdvance);
                newTravelAdvance.setDocumentNumber(documentNumber);
                newTravelAdvance.setVersionNumber(new Long(1));
                newTravelAdvance.setObjectId(null);
                newTravelAdvance.setId(null);
                newTravelAdvances.add(newTravelAdvance);
            }
        }
        return newTravelAdvances;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyTravelerDetailEmergencyContact(java.util.List, java.lang.String)
     */
    public List<TravelerDetailEmergencyContact> copyTravelerDetailEmergencyContact(List<TravelerDetailEmergencyContact> emergencyContacts, String documentNumber) {
        List<TravelerDetailEmergencyContact> newEmergencyContacts = new ArrayList<TravelerDetailEmergencyContact>();
        if (emergencyContacts != null) {
            for (TravelerDetailEmergencyContact emergencyContact : emergencyContacts){
                TravelerDetailEmergencyContact newEmergencyContact = new TravelerDetailEmergencyContact();
                try {
                    BeanUtils.copyProperties(newEmergencyContact, emergencyContact);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                newEmergencyContact.setDocumentNumber(documentNumber);
                newEmergencyContact.setVersionNumber(new Long(1));
                newEmergencyContact.setObjectId(null);
                newEmergencyContact.setId(null);
                newEmergencyContacts.add(newEmergencyContact);
            }
        }
        return newEmergencyContacts;
    }     
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copySpecialCircumstances(java.util.List, java.lang.String)
     */
    public List<SpecialCircumstances> copySpecialCircumstances(List<SpecialCircumstances> specialCircumstancesList, String documentNumber) {
        List<SpecialCircumstances> newSpecialCircumstancesList = new ArrayList<SpecialCircumstances>();
        if (specialCircumstancesList != null) {
            for (SpecialCircumstances specialCircumstances : specialCircumstancesList){
                SpecialCircumstances newSpecialCircumstances = new SpecialCircumstances();
                try {
                    BeanUtils.copyProperties(newSpecialCircumstances, specialCircumstances);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                newSpecialCircumstances.setDocumentNumber(documentNumber);
                newSpecialCircumstances.setVersionNumber(new Long(1));
                newSpecialCircumstances.setObjectId(null);
                newSpecialCircumstances.setId(null);
                newSpecialCircumstancesList.add(newSpecialCircumstances);
            }
        }
        return newSpecialCircumstancesList;
    }    
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyTransportationModeDetails(java.util.List, java.lang.String)
     */
    public List<TransportationModeDetail> copyTransportationModeDetails(List<TransportationModeDetail> transportationModeDetails, String documentNumber) {
        List<TransportationModeDetail> newTransportationModeDetails = new ArrayList<TransportationModeDetail>();
        if (transportationModeDetails != null) {
            for (TransportationModeDetail detail : transportationModeDetails){
                TransportationModeDetail newDetail = new TransportationModeDetail();
                try {
                    BeanUtils.copyProperties(newDetail, detail);
                }
                catch (IllegalAccessException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                newDetail.setDocumentNumber(documentNumber);
                newDetail.setVersionNumber(new Long(1));
                newDetail.setObjectId(null);
                newTransportationModeDetails.add(newDetail);
            }
        }
        return newTransportationModeDetails;
    }  
    
    public void showNoTravelAuthorizationError(TravelReimbursementDocument document){
        if (document.getTripType() != null && document.getTripType().getTravelAuthorizationRequired()){
            try {
                TravelAuthorizationDocument authorization = (TravelAuthorizationDocument) findCurrentTravelAuthorization(document);
                if (authorization == null){
                    GlobalVariables.getMessageMap().putError(KNSPropertyConstants.DOCUMENT + "." + TemPropertyConstants.TRIP_TYPE_CODE, TemKeyConstants.ERROR_TRIP_TYPE_TA_REQUIRED, document.getTripType().getName());
                }
            }
            catch (WorkflowException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#getAdvancesTotalFor(TravelDocument)
     */
    public KualiDecimal getAdvancesTotalFor(TravelDocument travelDocument) {
        debug("Looking for travel advances for travel: ", travelDocument.getDocumentNumber());
        KualiDecimal retval = KualiDecimal.ZERO;
        TravelAuthorizationDocument authorization = null;
        try {
            authorization = (TravelAuthorizationDocument) findCurrentTravelAuthorization(travelDocument);
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        if (authorization == null) {
            return retval;
        }
        authorization.refreshReferenceObject(TemPropertyConstants.TRVL_ADV);

        for (final TravelAdvance advance : authorization.getTravelAdvances()) {
            retval = retval.add(advance.getTravelAdvanceRequested());
        }
        return retval;
    }    
    
    /*public Map<String, Object> calculateTotalsFor(final TravelDocument travelDocument) {
        final Map<String, Object> retval = new HashMap<String, Object>();
        KualiDecimal totalExpenses = KualiDecimal.ZERO;
        KualiDecimal nonReimbursable = KualiDecimal.ZERO;
        KualiDecimal eligibleForReimbursement = KualiDecimal.ZERO;
        KualiDecimal totalReimbursable = KualiDecimal.ZERO;
        KualiDecimal lessAdvances = KualiDecimal.ZERO;
        KualiDecimal reimbursementTotal = KualiDecimal.ZERO;
        KualiDecimal encumbranceAmount = KualiDecimal.ZERO;

        // Get expenseAmount for expensetype=mileage
        calculateExpenseAmountTotalForMileage(travelDocument.getActualExpenses());
        
        // Get totals on perdiem and other expense. Then, add them
        debug("Got other expenses total ", travelDocument.getActualExpensesTotal());
        debug("Got Perdiem Mileage total ", travelDocument.getDailyTotalGrandTotal());        
        debug("per diem size is ", travelDocument.getPerDiemExpenses().size());

        totalExpenses = travelDocument.getDailyTotalGrandTotal().add(travelDocument.getActualExpensesTotal());

        nonReimbursable = travelDocument.getNonReimbursableTotal();
        eligibleForReimbursement = travelDocument.getApprovedAmount();
        totalReimbursable = travelDocument.getReimbursableTotal();
        lessAdvances = getAdvancesTotalFor(travelDocument);
        reimbursementTotal = totalReimbursable.subtract(lessAdvances);

        if (KualiDecimal.ZERO.isGreaterThan(reimbursementTotal)) {
            reimbursementTotal = KualiDecimal.ZERO;
        }

        retval.put(TOTAL_EXPENSES_ATTRIBUTE, totalExpenses);
        retval.put(NON_REIMBURSABLE_ATTRIBUTE, nonReimbursable);
        retval.put(ELIGIBLE_FOR_REIMB_ATTRIBUTE, eligibleForReimbursement);
        retval.put(TOTAL_REIMBURSABLE_ATTRIBUTE, totalReimbursable);
        retval.put(LESS_ADVANCES_ATTRIBUTE, lessAdvances);
        retval.put(REIMBURSEMENT_ATTRIBUTE, reimbursementTotal);
        retval.put(ENCUMBRANCE_AMOUNT_ATTRIBUTE, encumbranceAmount);

        return retval;
    }*/
    
    @Override
    public String retrieveAddressFromLocationCode(String locationCode) {
        String address = "";
        try {
            address = ((DisbursementVoucherDocumentationLocation) retrieveObjectByKey(DisbursementVoucherDocumentationLocation.class, locationCode)).getDisbursementVoucherDocumentationLocationAddress();
        }
        catch (NullPointerException e) {
            // ignored
        }

        return address;
    }
    
    @Override
    public boolean validateSourceAccountingLines(TravelDocument travelDocument, boolean addToErrorPath) {
        boolean success = true;
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        fieldValues.put(KNSPropertyConstants.DOCUMENT_NUMBER, travelDocument.getDocumentNumber());
        
        List<SourceAccountingLine> currentLines = (List<SourceAccountingLine>) getBusinessObjectService().findMatching(SourceAccountingLine.class, fieldValues);
        
        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(travelDocument);
        boolean canUpdate = documentPresentationController.enableForTravelManager(travelDocument.getDocumentHeader().getWorkflowDocument());
        
        for (int i=0;i<travelDocument.getSourceAccountingLines().size();i++){
            AccountingLine line = (AccountingLine) travelDocument.getSourceAccountingLines().get(i);
            if (addToErrorPath){
                GlobalVariables.getMessageMap().getErrorPath().add("document." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + i + "]");
            }
            if(StringUtils.isBlank(line.getAccountNumber())){
                success = false;
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Account Number");
            }
            else{
                if ((!travelDocument.getAppDocStatus().equalsIgnoreCase("Initiated")) 
                        && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.IN_PROCESS))
                        && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.CHANGE_IN_PROCESS))){
                    if ((i < currentLines.size()) && (!(currentLines.get(i)).getAccountNumber().equals(line.getAccountNumber()))
                            || (i >= currentLines.size())){
                        try{
                            if (!line.getAccount().getAccountFiscalOfficerUser().getPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())
                                    && !canUpdate){
                                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TA_FISCAL_OFFICER_ACCOUNT, line.getAccountNumber());
                                success = false;
                            }
                        }
                        catch(Exception e){
                            //do nothing, other validation will figure out this account doesn't exist 
                        }
                    }
                }
            }
            if(StringUtils.isBlank(line.getChartOfAccountsCode())){
                success = false;
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_REQUIRED, "Chart");
            }
            if (addToErrorPath){
                GlobalVariables.getMessageMap().getErrorPath().remove(GlobalVariables.getMessageMap().getErrorPath().size()-1);
            }
        }
        
        return success;
    }    
    
    protected PersistableBusinessObject retrieveObjectByKey(Class clazz, String keyValue) {
        List primaryKeyFields = persistenceStructureService.listPrimaryKeyFieldNames(clazz);
        if (primaryKeyFields.size() != 1) {
            throw new IllegalArgumentException("multi-part key found. expecting single key field for " + clazz.getName());
        }
        Map primaryKeys = new HashMap();
        primaryKeys.put(primaryKeyFields.get(0), keyValue);
        PersistableBusinessObject b = businessObjectService.findByPrimaryKey(clazz, primaryKeys);

        return b;
    }
    
    /**
     * This method parses out the options from the parameters table and sets boolean values for each one LODGING MILEAGE PER_DIEM
     * 
     * @param perDiemCats
     */
    private boolean showPerDiem(List<String> perDiemCats, String perDiemType) {
        for (String category : perDiemCats) {
            String[] pair = category.split("=");
            if (pair[0].equalsIgnoreCase(perDiemType)) {
                return pair[1].equalsIgnoreCase(TemConstants.YES);
            }
            if (pair[0].equalsIgnoreCase(perDiemType)) {
                return pair[1].equalsIgnoreCase(TemConstants.YES);
            }
            if (pair[0].equalsIgnoreCase(perDiemType)) {
                return pair[1].equalsIgnoreCase(TemConstants.YES);
            }
        }
        
        return false;
    }    
    
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
            
    public TravelerService getTravelerService() {
        return travelerService;
    }

    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    @Override
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
        //implement the common functionality       
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public KualiRuleService getKualiRuleService() {
        return kualiRuleService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public StateService getStateService() {
        return stateService;
    }

    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }
    
    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }    
    
	/**
	 * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getOutstandingTravelAdvanceByInvoice(java.util.Set)
	 */
    @Override
    public List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumbers) {
        return travelDocumentDao.getOutstandingTravelAdvanceByInvoice(arInvoiceDocNumbers);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findLatestTaxableRamificationNotificationDate()
     */
    @Override
    @Transactional
    public Date findLatestTaxableRamificationNotificationDate() {
        return travelDocumentDao.findLatestTaxableRamificationNotificationDate();
    }

    @Override
    public void detachImportedExpenses(TravelDocument document) {
        for (ImportedExpense importedExpense : document.getImportedExpenses()){
            ExpenseUtils.assignExpense(importedExpense.getHistoricalTravelExpenseId(), null, null, null, false);
        }
        document.setImportedExpenses(new ArrayList<ImportedExpense>());
        document.setHistoricalTravelExpenses(new ArrayList<HistoricalTravelExpense>());
    } 
    
    @Override
    public void attachImportedExpenses(TravelDocument document) {
        for (ImportedExpense importedExpense : document.getImportedExpenses()){
            ExpenseUtils.assignExpense(importedExpense.getHistoricalTravelExpenseId(), document.getTravelDocumentIdentifier(),document.getDocumentNumber(), document.getFinancialDocumentTypeCode(), true);
        }
    } 

    /**
     * Check to see if the hold new fiscal year encumbrance indicator is true 
     * and the trip end date is after the current fiscal year end date to determine
     * whether or not to mark all the GLPEs as 'H' (Hold)    
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#holdGLPEs(org.kuali.kfs.module.tem.document.TravelDocument)
     */
	@Override
	public boolean checkHoldGLPEs(TravelDocument document) {
		if(getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.HOLD_NEW_FY_ENCUMBRANCES_IND)) {
            
            java.util.Date endDate = getUniversityDateService().getLastDateOfFiscalYear(getUniversityDateService().getCurrentFiscalYear());
            if (ObjectUtils.isNotNull(document.getTripBegin()) && document.getTripBegin().after(endDate)) {
            	return true;
            }
        
        }
		
		return false;
	}

	/**
	 * Gets the universityDateService attribute. 
	 * @return Returns the universityDateService.
	 */
	public UniversityDateService getUniversityDateService() {
		return universityDateService;
	}

	/**
	 * Sets the universityDateService attribute value.
	 * @param universityDateService The universityDateService to set.
	 */
	public void setUniversityDateService(UniversityDateService universityDateService) {
		this.universityDateService = universityDateService;
	}

    /**
     * Called when an amendment is canceled.  
     */
    public void revertOriginalDocument(TravelDocument travelDocument, String status) {
        Map<String, List<Document>> relatedDocs = new HashMap<String, List<Document>>();
        try {
            relatedDocs = getDocumentsRelatedTo(travelDocument);
        }
        catch (WorkflowException ex1) {
            // TODO Auto-generated catch block
            ex1.printStackTrace();
        }
        List<Document> taDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        List<Document> taaDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        if (taDocs == null) {
            taDocs = new ArrayList<Document>();
        }

        if (taaDocs != null) {
            taDocs.addAll(taaDocs);
        }

        for (Document taDocument : taDocs) {
            if (taDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)) {
                TravelAuthorizationDocument taDoc = (TravelAuthorizationDocument) taDocument;
                taDoc.updateAppDocStatus(status);

                try {
                    Note cancelNote = getDocumentService().createNoteFromDocument(taDoc, ((KualiConfigurationService) SpringContext.getService("kualiConfigurationService")).getPropertyString(TemKeyConstants.TA_MESSAGE_AMEND_DOCUMENT_CANCELLED_TEXT));
                    getDocumentService().addNoteToDocument(taDoc, cancelNote);
                    getDocumentService().saveDocument(taDoc);
                }
                catch (Exception ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public String getDocumentType(TravelDocument document) {
        String documentType = null;
        
        if (document != null) {
            if (document instanceof TravelAuthorizationDocument) {
                documentType = TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT;
            }
            else if (document instanceof TravelReimbursementDocument) {
                documentType = TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT;
            }
            else if (document instanceof TravelEntertainmentDocument) {
                documentType = TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT;
            }
            else if (document instanceof TravelRelocationDocument) {
                documentType = TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT;
            }
        }
        
        return documentType;
    }

}
