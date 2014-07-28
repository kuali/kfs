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

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_INVALID_NUMERIC_VALUE;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_LINE;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_PROPERTY;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_LODGING_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_MEAL_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_EXCEEDED_MAX_LENGTH;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_INVALID_VALUE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
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
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.GroupTravelerCsvRecord;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstancesQuestion;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TemRegion;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.MileageRateService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.exception.UploadParserException;
import org.kuali.kfs.module.tem.service.CsvRecordFactory;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Travel Service Implementation
 */
@Transactional
public class TravelDocumentServiceImpl implements TravelDocumentService {

    protected static Logger LOG = Logger.getLogger(TravelDocumentServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected DocumentService documentService;
    protected BusinessObjectService businessObjectService;
    protected TravelDocumentDao travelDocumentDao;
    protected TravelAuthorizationService travelAuthorizationService;
    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;
    protected AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    protected TemRoleService temRoleService;
    protected StateService stateService;
    protected ConfigurationService configurationService;
    protected UniversityDateService universityDateService;
    protected List<String> defaultAcceptableFileExtensions;
    protected CsvRecordFactory<GroupTravelerCsvRecord> csvRecordFactory;
    protected List<String> groupTravelerColumns;
    protected volatile AccountsReceivableModuleService accountsReceivableModuleService;
    protected PerDiemService perDiemService;
    protected TravelExpenseService travelExpenseService;
    protected NoteService noteService;
    protected TravelService travelService;
    protected MileageRateService mileageRateService;


    /**
     * Creates and populates an individual per diem item.
     *
     * @param perDiemId is the id for the referenced {@link PerDiem} object that gets attached
     * @return date of the item
     */
    protected PerDiemExpense createPerDiemItem(final TravelDocument document, final PerDiem newPerDiem, final Timestamp ts, final boolean prorated, String mileageRateExpenseTypeCode) {
        final PerDiemExpense expense = newPerDiemExpense();
        expense.setPrimaryDestinationId(newPerDiem.getPrimaryDestinationId());
        expense.setProrated(prorated);
        expense.setMileageDate(ts);

        expense.setPrimaryDestination(newPerDiem.getPrimaryDestination().getPrimaryDestinationName());
        expense.setCountryState(newPerDiem.getPrimaryDestination().getRegion().getRegionName());
        expense.setCounty(newPerDiem.getPrimaryDestination().getCounty());

        setPerDiemMealsAndIncidentals(expense, newPerDiem, document.getTripType(), document.getTripEnd(), expense.isProrated());
        final KualiDecimal lodgingAmount = getPerDiemService().isPerDiemHandlingLodging() && !KfsDateUtils.isSameDay(document.getTripEnd(), ts) ? newPerDiem.getLodging() : KualiDecimal.ZERO;
        expense.setLodging(lodgingAmount);
        expense.setMileageRateExpenseTypeCode(mileageRateExpenseTypeCode);
        return expense;
    }

    /**
     * returns a new instance of a PerDiemExpense turned into a service call so that we can provide our own instance during testing
     */
    protected PerDiemExpense newPerDiemExpense() {
        return new PerDiemExpense();
    }

    /**
     * Sets the meal and incidental amounts on the given per diem expense
     * @param expense the expense to set amounts on
     * @param perDiem the per diem record amounts are based off of
     * @param tripType the trip type being taken
     * @param tripEnd the end time of the trip
     * @param shouldProrate whether this expense should be prorated
     */
    @Override
    public void setPerDiemMealsAndIncidentals(PerDiemExpense expense, PerDiem perDiem, TripType tripType, Timestamp tripEnd, boolean shouldProrate) {
        String perDiemCalcMethod = null;
        if (!ObjectUtils.isNull(tripType)) {
            perDiemCalcMethod = tripType.getPerDiemCalcMethod();
        }
        //default first to per diem's values
        expense.setBreakfastValue(perDiem.getBreakfast());
        expense.setLunchValue(perDiem.getLunch());
        expense.setDinnerValue(perDiem.getDinner());
        expense.setIncidentalsValue(perDiem.getIncidentals());
        // if prorated, recalculate the values
        if(shouldProrate){
            Integer perDiemPercent = calculateProratePercentage(expense, perDiemCalcMethod, tripEnd);
            expense.setDinnerValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getDinnerValue(), perDiemPercent));
            expense.setLunchValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getLunchValue(), perDiemPercent));
            expense.setBreakfastValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getBreakfastValue(), perDiemPercent));
            expense.setIncidentalsValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getIncidentalsValue(), perDiemPercent));

            correctProratedPerDiemExpense(expense, perDiemPercent, perDiem);
        }
    }

    /**
     * Makes sure that any rounding in determining prorated meals or incidentals amounts will not be more than the meals and incidentals totals allowed by the per diem.
     * Extra change will be taken from breakfast.
     * @param expense the expense to correct
     * @param perDiemPercent the percentage of the proration for this per diem
     * @param perDiem the per diem record to work against
     */
    protected void correctProratedPerDiemExpense(PerDiemExpense expense, Integer perDiemPercent, PerDiem perDiem) {
        final KualiDecimal mealAndIncidentalLimit = PerDiemExpense.calculateMealsAndIncidentalsProrated(perDiem.getMealsAndIncidentals(), perDiemPercent);
        if (expense.getMealsAndIncidentals().isGreaterThan(mealAndIncidentalLimit)) {
            // take the difference from breakfast
            final KualiDecimal delta = expense.getMealsAndIncidentals().subtract(mealAndIncidentalLimit);
            expense.setBreakfastValue(expense.getBreakfastValue().subtract(delta));
        }
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

            for (; !cal.getTime().after(end) || KfsDateUtils.isSameDay(cal.getTime(), end); cal.add(Calendar.DATE, 1)) {
                if (KfsDateUtils.isSameDay(cal.getTime(), end)) {
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
    @SuppressWarnings("null")
    @Override
    public void updatePerDiemItemsFor(final TravelDocument document, final List<PerDiemExpense> perDiemExpenseList, final Integer perDiemId, final Timestamp start, final Timestamp end) {
        final String mileageRateExpenseTypeCode = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE, KFSConstants.EMPTY_STRING);

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

        // find a valid per diem for each date.  If per diem is null, make it a custom per diem.
        for (final Timestamp eachTimestamp : dateRange(start, end)) {
            PerDiem perDiem = getPerDiemService().getPerDiem(document.getPrimaryDestinationId(), eachTimestamp, document.getEffectiveDateForPerDiem(eachTimestamp));
            if (perDiem == null || perDiem.getPrimaryDestinationId() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
                perDiem = new PerDiem();
                perDiem.setPrimaryDestination(new PrimaryDestination());
                perDiem.getPrimaryDestination().setRegion(new TemRegion());
                perDiem.getPrimaryDestination().getRegion().setTripType(new TripType());
                perDiem.setPrimaryDestinationId(TemConstants.CUSTOM_PRIMARY_DESTINATION_ID);
                perDiem.getPrimaryDestination().getRegion().setRegionName(document.getPrimaryDestinationCountryState());
                perDiem.getPrimaryDestination().setCounty(document.getPrimaryDestinationCounty());
                perDiem.getPrimaryDestination().getRegion().setTripType(document.getTripType());
                perDiem.getPrimaryDestination().getRegion().setTripTypeCode(document.getTripTypeCode());
                perDiem.getPrimaryDestination().setPrimaryDestinationName(document.getPrimaryDestinationName());
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

            LOG.debug("Iterating over date range from "+ start+ " to "+ end);
            int counter = 0;
            for (final Timestamp someDate : dateRange(start, end)) {
                // Check if a per diem entry exists for this date
                if (!perDiemMapped.containsKey(someDate)) {
                    final boolean prorated = shouldProrate(someDate, start, end);
                    PerDiemExpense perDiemExpense = createPerDiemItem(document,perDiemList.get(counter), someDate, prorated, mileageRateExpenseTypeCode);
                    perDiemExpense.setDocumentNumber(document.getDocumentNumber());
                    perDiemMapped.put(someDate, perDiemExpense);
                }
                counter++;
            }
        }

        // Sort the dates and recreate the collection
        perDiemExpenseList.clear();
        for (final Timestamp someDate : new TreeSet<Timestamp>(perDiemMapped.keySet())) {
            LOG.debug("Adding "+ perDiemMapped.get(someDate)+ " to perdiem list");
            perDiemExpenseList.add(perDiemMapped.get(someDate));
        }
    }

    /**
     * Determines if per diem expenses on the given date should be prorated
     * @param perDiemDate the timestamp of the per diem
     * @param tripBegin the begin timestamp of the trip
     * @param tripEnd the end timestamp of the trip
     * @return true if the per diem expense should be prorated, false otherwise
     */
    protected boolean shouldProrate(Timestamp perDiemDate, Timestamp tripBegin, Timestamp tripEnd) {
        final boolean prorated = !KfsDateUtils.isSameDay(tripBegin, tripEnd) && (KfsDateUtils.isSameDay(perDiemDate, tripBegin) || KfsDateUtils.isSameDay(perDiemDate, tripEnd));
        return prorated;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getMileageRateKeyValues(java.sql.Date)
     */
    @Override
    public List<KeyValue> getMileageRateKeyValues(Date searchDate) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        TravelDocument document = (TravelDocument) ((TravelFormBase)KNSGlobalVariables.getKualiForm()).getDocument();
        String documentType = getDocumentType(document);
        final String travelerType = ObjectUtils.isNull(document.getTraveler()) ? null : document.getTraveler().getTravelerTypeCode();

        final Collection<ExpenseType> expenseTypes = getTravelExpenseService().getExpenseTypesForDocument(documentType, document.getTripTypeCode(), travelerType, false);

        for (final ExpenseType expenseType : expenseTypes) {
            if (TemConstants.ExpenseTypeMetaCategory.MILEAGE.getCode().equals(expenseType.getExpenseTypeMetaCategoryCode())) {
                final MileageRate mileageRate = getMileageRateService().findMileageRateByExpenseTypeCodeAndDate(expenseType.getCode(), searchDate);
                if (mileageRate != null) {
                    keyValues.add(new ConcreteKeyValue(expenseType.getCode(), expenseType.getCode()+" - "+mileageRate.getRate().toString()));
                }
            }
        }

        //sort by label
        Comparator<KeyValue> labelComparator = new Comparator<KeyValue>() {
            @Override
            public int compare(KeyValue o1, KeyValue o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        };

        Collections.sort(keyValues, labelComparator);

        return keyValues;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyDownPerDiemExpense(int, java.util.List)
     */
    @Override
    public void copyDownPerDiemExpense(TravelDocument travelDocument, int copyIndex, List<PerDiemExpense> perDiemExpenses) {

        PerDiemExpense lineToCopy = perDiemExpenses.get(copyIndex);
        PerDiemExpense restoredLine = getRestoredPerDiemForCopying(travelDocument, lineToCopy);

        List<PerDiemExpense> tempPerDiemExpenses = new ArrayList<PerDiemExpense>();

        if (copyIndex < perDiemExpenses.size()) {
            for (int i = 0; i < perDiemExpenses.size(); i++) {
                PerDiemExpense perDiemExpense = new PerDiemExpense();
                if (perDiemExpenses != null && i < copyIndex) {
                    // copy over from the old list
                    perDiemExpense = perDiemExpenses.get(i);
                }
                else if (i > copyIndex) {
                    perDiemExpense = copyPerDiemExpense(restoredLine);
                    perDiemExpense.setMileageDate(perDiemExpenses.get(i).getMileageDate());
                    if (shouldProrate(perDiemExpense.getMileageDate(), travelDocument.getTripBegin(), travelDocument.getTripEnd())) {
                        // prorate
                        perDiemExpense.setProrated(true);
                        if (perDiemExpense.getPrimaryDestinationId() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID) {
                            // prorate the restored line to create new per diem
                            final PerDiem perDiem = copyIntoPerDiem(restoredLine);
                            final Integer perDiemPercent = lookupProratePercentage(perDiemExpense, travelDocument.getTripType().getPerDiemCalcMethod(), travelDocument.getTripEnd());
                            perDiemExpense.setDinnerValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(perDiemExpense.getDinnerValue(), perDiemPercent));
                            perDiemExpense.setLunchValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(perDiemExpense.getLunchValue(), perDiemPercent));
                            perDiemExpense.setBreakfastValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(perDiemExpense.getBreakfastValue(), perDiemPercent));
                            perDiemExpense.setIncidentalsValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(perDiemExpense.getIncidentalsValue(), perDiemPercent));
                        } else {
                            final PerDiem perDiem = getPerDiemService().getPerDiem(restoredLine.getPrimaryDestinationId(), perDiemExpense.getMileageDate(), travelDocument.getEffectiveDateForPerDiem(perDiemExpense));
                            setPerDiemMealsAndIncidentals(perDiemExpense, perDiem, travelDocument.getTripType(), travelDocument.getTripEnd(), true);
                        }
                    }
                    if (travelDocument.getTripEnd() != null && KfsDateUtils.isSameDay(travelDocument.getTripEnd(), perDiemExpense.getMileageDate())) {
                        // set lodging to 0
                        perDiemExpense.setLodging(KualiDecimal.ZERO);
                    }
                }
                else {
                    // are we copying a prorated line to a non-prorated spot?

                    // then let's restore all values before copying
                    perDiemExpense = lineToCopy;
                }

                tempPerDiemExpenses.add(perDiemExpense);

            }
        }

        perDiemExpenses.clear();
        perDiemExpenses.addAll(tempPerDiemExpenses);
    }

    /**
     * If the given perDiemExpense was prorated, restores the original values
     * @param travelDocument the travel document the expense is on
     * @param perDiemExpense the per diem expense to restore
     * @return a PerDiemExpense with all values restored
     */
    protected PerDiemExpense getRestoredPerDiemForCopying(TravelDocument travelDocument, PerDiemExpense perDiemExpense) {
        PerDiemExpense restoredExpense = copyPerDiemExpense(perDiemExpense);
        if (travelDocument.getPrimaryDestinationId() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID && shouldProrate(perDiemExpense.getMileageDate(), travelDocument.getTripBegin(), travelDocument.getTripEnd())) {
            final Integer perDiemPercentage = lookupProratePercentage(perDiemExpense, travelDocument.getTripType().getPerDiemCalcMethod(), travelDocument.getTripEnd());
            if (perDiemPercentage != null) {
                final KualiDecimal perDiemPercentageDecimal = new KualiDecimal((double)perDiemPercentage*0.01);
                restoredExpense.setBreakfastValue(perDiemExpense.getBreakfastValue().divide(perDiemPercentageDecimal));
                restoredExpense.setLunchValue(perDiemExpense.getLunchValue().divide(perDiemPercentageDecimal));
                restoredExpense.setDinnerValue(perDiemExpense.getDinnerValue().divide(perDiemPercentageDecimal));
                restoredExpense.setIncidentalsValue(perDiemExpense.getIncidentalsValue().divide(perDiemPercentageDecimal));
            }
            perDiemExpense.setProrated(false);
        } else {
            // look up per diem
            final PerDiem perDiem = getPerDiemService().getPerDiem(perDiemExpense.getPrimaryDestinationId(), perDiemExpense.getMileageDate(), travelDocument.getEffectiveDateForPerDiem(perDiemExpense));
            setPerDiemMealsAndIncidentals(restoredExpense, perDiem, travelDocument.getTripType(), travelDocument.getTripEnd(), false);
        }
        return restoredExpense;
    }

    /**
     * Takes the values from the given per diem expense and copies them into a per diem
     * @param perDiemExpense the per diem expense to copy values from
     * @return a fake PerDiem record copied from those values
     */
    protected PerDiem copyIntoPerDiem(PerDiemExpense perDiemExpense) {
        PerDiem perDiem = new PerDiem();
        perDiem.setPrimaryDestinationId(perDiemExpense.getPrimaryDestinationId());
        perDiem.setBreakfast(perDiemExpense.getBreakfastValue());
        perDiem.setLunch(perDiemExpense.getLunchValue());
        perDiem.setDinner(perDiemExpense.getDinnerValue());
        perDiem.setIncidentals(perDiemExpense.getIncidentalsValue());
        return perDiem;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getDocumentsRelatedTo(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public Map<String, List<Document>> getDocumentsRelatedTo(final TravelDocument document) throws WorkflowException {
        return getDocumentsRelatedTo(document.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getDocumentsRelatedTo(java.lang.String)
     */
    @Override
    public Map<String, List<Document>> getDocumentsRelatedTo(final String documentNumber) throws WorkflowException {
        final Map<String, List<Document>> retval = new HashMap<String, List<Document>>();

        Set<String> documentNumbers = accountingDocumentRelationshipService.getAllRelatedDocumentNumbers(documentNumber);
        if (!documentNumbers.isEmpty()) {
            for (String documentHeaderId : documentNumbers) {
                Document doc = documentService.getByDocumentHeaderIdSessionless(documentHeaderId);
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
        }

        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getDocumentsRelatedTo(org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String[])
     */
    @Override
    public List<Document> getDocumentsRelatedTo(final TravelDocument document, String... documentTypeList){
        List<Document> relatedDocumentList = new ArrayList<Document>();
        Map<String, List<Document>> relatedDocumentMap;
        try {
            relatedDocumentMap = getDocumentsRelatedTo(document);
            for (String documentType : documentTypeList){
                if (relatedDocumentMap.containsKey(documentType)){
                    relatedDocumentList.addAll(relatedDocumentMap.get(documentType));
                }
            }
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return relatedDocumentList;
    }

    @Override
    public List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType) {
        List<SpecialCircumstances> retval = new ArrayList<SpecialCircumstances>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);

        // add specialCircumstances with specific documentType SpecialCircumstancesQuestion
        Set<String> documentTypesToCheck = new HashSet<String>();
        documentTypesToCheck.add(documentType);
        final Set<String> parentDocTypes = getTravelService().getParentDocumentTypeNames(documentType);
        documentTypesToCheck.addAll(parentDocTypes);
        criteria.put(KFSPropertyConstants.DOCUMENT_TYPE, documentTypesToCheck);
        retval.addAll(buildSpecialCircumstances(documentNumber, criteria));

        return retval;
    }


    protected List<SpecialCircumstances> buildSpecialCircumstances(String documentNumber, Map<String, Object> criteria) {
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

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findAuthorizationDocuments(java.lang.String)
     */
    @Override
    public List<TravelAuthorizationDocument> findAuthorizationDocuments(final String travelDocumentIdentifier){
        final List<String> ids = findAuthorizationDocumentNumbers(travelDocumentIdentifier);

        List<TravelAuthorizationDocument> resultDocumentLists = new ArrayList<TravelAuthorizationDocument>();
        //retrieve the actual documents
        try {
            if (!ids.isEmpty()) {
                for (Document document : getDocumentService().getDocumentsByListOfDocumentHeaderIds(TravelAuthorizationDocument.class, ids)){
                    resultDocumentLists.add((TravelAuthorizationDocument)document);
                }
            }
        }catch (WorkflowException wfe){
            LOG.error(wfe.getMessage(), wfe);
        }
        return resultDocumentLists;
    }

    /**
     * Gets the document numbers from the TravelDocumentDao for the given trip id
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findAuthorizationDocumentNumbers(java.lang.String)
     */
    @Override
    public List<String> findAuthorizationDocumentNumbers(final String travelDocumentIdentifier) {
        final List<String> ids = getTravelDocumentDao().findDocumentNumbers(TravelAuthorizationDocument.class, travelDocumentIdentifier);
        return ids;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findReimbursementDocuments(java.lang.String)
     */
    @Override
    public List<TravelReimbursementDocument> findReimbursementDocuments(final String travelDocumentIdentifier) {
        final List<String> ids = getTravelDocumentDao().findDocumentNumbers(TravelReimbursementDocument.class, travelDocumentIdentifier);

        List<TravelReimbursementDocument> resultDocumentLists = new ArrayList<TravelReimbursementDocument>();
        // retrieve the actual documents
        try {
            if (!ids.isEmpty()) {
                for (Document document : getDocumentService().getDocumentsByListOfDocumentHeaderIds(TravelReimbursementDocument.class, ids)) {
                    resultDocumentLists.add((TravelReimbursementDocument) document);
                }
            }
        }
        catch (WorkflowException wfe) {
            throw new RuntimeException(wfe);
        }
        return resultDocumentLists;
    }


    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocFYIRecipient(org.kuali.rice.kns.document.Document)
     */
    @Override
    public void addAdHocFYIRecipient(final Document document) {
        addAdHocFYIRecipient(document, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocFYIRecipient(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
    public void addAdHocFYIRecipient(final Document document, String initiatorUserId) {
        addAdHocRecipient(document, initiatorUserId, KewApiConstants.ACTION_REQUEST_FYI_REQ);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#addAdHocRecipient(Document, String, String)
     */
    @Override
    public void addAdHocRecipient(Document document, String initiatorUserId, String actionRequested) {
        List<AdHocRoutePerson> adHocRoutePersons = document.getAdHocRoutePersons();
        List<String> adHocRoutePersonIds = new ArrayList<String>();
        if(!adHocRoutePersons.isEmpty()){
            for(AdHocRoutePerson ahrp : adHocRoutePersons){
                adHocRoutePersonIds.add(ahrp.getId());
            }
        }

        // Add adhoc for initiator
        if (!adHocRoutePersonIds.contains(initiatorUserId)) {
            if (initiatorUserId != null) {
                final Person finSysUser = SpringContext.getBean(PersonService.class).getPerson(initiatorUserId);
                if (finSysUser != null) {
                    final AdHocRoutePerson recipient = buildAdHocRecipient(finSysUser.getPrincipalName(), actionRequested);
                    final DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(document);
                    if (documentAuthorizer.canReceiveAdHoc(document, finSysUser, actionRequested)) {
                        adHocRoutePersons.add(recipient);
                    }
                }
                else {
                    LOG.warn("finSysUser is null.");
                }
            }
            else {
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

        dailyTotals.put(TemConstants.MILEAGE_TOTAL_ATTRIBUTE, perDiemExpense.getMileageTotal());
        dailyTotals.put(TemConstants.LODGING_TOTAL_ATTRIBUTE, perDiemExpense.getLodgingTotal());
        dailyTotals.put(TemConstants.MEALS_AND_INC_TOTAL_ATTRIBUTE, perDiemExpense.getMealsAndIncidentals());
        dailyTotals.put(TemConstants.DAILY_TOTAL, perDiemExpense.getDailyTotal());

        return dailyTotals;
    }

    @Override
    public void routeToFiscalOfficer(final TravelDocument document, final String noteText) throws WorkflowException, Exception {
        // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'

        final Note newNote = getDocumentService().createNoteFromDocument(document, noteText);
        document.addNote(newNote);

        document.getDocumentHeader().getWorkflowDocument().returnToPreviousNode(noteText, KFSConstants.RouteLevelNames.ACCOUNT);

        addAdHocFYIRecipient(document, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());

        document.getFinancialSystemDocumentHeader().setApplicationDocumentStatus(TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL);

        getDocumentService().saveDocument(document);
    }

    /**
     *
     * This method calculates the prorate percentage value based on perDiemCalcMethod (P/Q)
     * @param expense
     * @param perDiemCalcMethod
     * @return
     */
    @Override
    public Integer calculateProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd) {
        Integer perDiemPercent = 100;

        if (perDiemExpense.isProrated()) {
            perDiemPercent = lookupProratePercentage(perDiemExpense, perDiemCalcMethod, tripEnd);
        }
        return perDiemPercent;
    }

    /**
     * Looks up the prorate percentage, even if the per diem doesn't think it's prorated
     * @param perDiemExpense the per diem expense to find a percentage for (if the quarterly method is used)
     * @param perDiemCalcMethod the per diem calculation method
     * @param tripEnd the last day of the trip (used for the quarterly method)
     * @return a prorate percentage, or 100 if nothing could be found
     */
    protected Integer lookupProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd) {
        if (perDiemCalcMethod != null && perDiemCalcMethod.equals(TemConstants.PERCENTAGE)) {
            try {
                final String perDiemPercentage = parameterService.getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE, "100");
                final Integer perDiemPercent = Integer.parseInt(perDiemPercentage);
                return perDiemPercent;
            }
            catch (Exception e1) {
                LOG.error("Failed to process prorate percentage for FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE parameter.", e1);
            }
        }
        else {
            return calculatePerDiemPercentageFromTimestamp(perDiemExpense, tripEnd);
        }
        return 100;
    }

    @Override
    public Integer calculatePerDiemPercentageFromTimestamp(PerDiemExpense perDiemExpense, Timestamp tripEnd) {
        if (perDiemExpense.getMileageDate() != null) {
            try {
                Collection<String> quarterTimes = parameterService.getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.QUARTER_DAY_TIME_TABLE);

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
                if (tripEnd != null && !KfsDateUtils.isSameDay(prorateDate.getTime(), tripEnd)) {
                    return 100 - ((quadrantIndex - 1) * TemConstants.QUADRANT_PERCENT_VALUE);
                }
                else { // Prorate on trip end. (12:01 AM departure = 25%, 11:59 PM arrival = 100%).
                    return quadrantIndex * TemConstants.QUADRANT_PERCENT_VALUE;
                }
            }
            catch (IllegalArgumentException e2) {
                LOG.error("IllegalArgumentException.", e2);
            }
        }

        return 100;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#transferPerDiemMileage(org.kuali.kfs.module.tem.businessobject.PerDiemMileage)
     */
    @Override
    public PerDiemExpense copyPerDiemExpense(PerDiemExpense perDiemExpense) {
        final PerDiemExpense retval = new PerDiemExpense();
        retval.setDocumentNumber(perDiemExpense.getDocumentNumber());

        retval.setCountryState(perDiemExpense.getCountryState());
        retval.setCounty(perDiemExpense.getCounty());
        retval.setPrimaryDestination(perDiemExpense.getPrimaryDestination());
        retval.setMileageDate(perDiemExpense.getMileageDate());
        retval.setMiles(perDiemExpense.getMiles());
        retval.setMileageRateExpenseTypeCode(perDiemExpense.getMileageRateExpenseTypeCode());
        retval.setAccommodationTypeCode(perDiemExpense.getAccommodationTypeCode());
        retval.setAccommodationName(perDiemExpense.getAccommodationName());
        retval.setAccommodationPhoneNum(perDiemExpense.getAccommodationPhoneNum());
        retval.setAccommodationAddress(perDiemExpense.getAccommodationAddress());
        retval.setPrimaryDestinationId(perDiemExpense.getPrimaryDestinationId());

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

        LOG.debug("estimated meals and incidentals "+ retval.getMealsAndIncidentals());

        return retval;
    }

    @Override
    /**
     * Calculates Mileage and returns total mileage amount
     * @param ActualExpense actualExpense
     */
    public KualiDecimal calculateMileage(ActualExpense actualExpense) {
        KualiDecimal mileageTotal = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(actualExpense.getExpenseTypeCode()) && actualExpense.isMileage()) {
            mileageTotal = actualExpense.getMileageTotal();
        }
        return mileageTotal;
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
    @Override
    public void handleNewActualExpense(final ActualExpense newActualExpenseLine) {
        if (newActualExpenseLine.getExpenseAmount() != null) {
            final BigDecimal rate = newActualExpenseLine.getCurrencyRate();
            final KualiDecimal amount = newActualExpenseLine.getExpenseAmount();

            newActualExpenseLine.setConvertedAmount(new KualiDecimal(amount.bigDecimalValue().multiply(rate)));
            LOG.debug("Set converted amount for "+ newActualExpenseLine+ " to "+ newActualExpenseLine.getConvertedAmount());

            if (isHostedMeal(newActualExpenseLine)) {
                KNSGlobalVariables.getMessageList().add(TemKeyConstants.MESSAGE_HOSTED_MEAL_ADDED,
                        new SimpleDateFormat("MM/dd/yyyy").format(newActualExpenseLine.getExpenseDate()),
                        newActualExpenseLine.getExpenseTypeObjectCode().getExpenseType().getName());
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
    @Override
    public boolean isHostedMeal(final ExpenseTypeAware havingExpenseType) {
        if (ObjectUtils.isNull(havingExpenseType) || StringUtils.isBlank(havingExpenseType.getExpenseTypeCode())) {
            return false;
        }

        if (havingExpenseType instanceof PersistableBusinessObject) {
            ((PersistableBusinessObject)havingExpenseType).refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
        }
        if (ObjectUtils.isNull(havingExpenseType.getExpenseType())) {
            return false;
        }
        return havingExpenseType.getExpenseType().isHosted();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#isTravelManager(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isTravelManager(final Person user) {
        return getTemRoleService().isTravelManager(user);
    }

    /**
     * Digs up a message from the {@link ConfigurationService} by key
     */
    @Override
    public String getMessageFrom(final String messageType, String... args) {
        String strTemp = getConfigurationService().getPropertyValueAsString(messageType);
        for(int i=0;i<args.length;i++){
            strTemp = strTemp.replaceAll("\\{"+i+"\\}", args[i]);
        }
        return strTemp;
    }

    /**
     * is this document in an open for reimbursement workflow state?
     *
     * @param reqForm
     * @return
     */
    @Override
    public boolean isOpen(TravelDocument document) {
        return document.getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
    }

    /**
     * is this document in a final workflow state.
     *
     * @param reqForm
     * @return
     */
    @Override
    public boolean isFinal(TravelDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().isFinal();
    }

    /**
     *
     * @param document
     * @return
     */
    @Override
    public boolean isTravelAuthorizationProcessed(TravelAuthorizationDocument document){
        return isFinal(document) || isProcessed(document);
    }

    /**
     *
     * @param document
     * @return
     */
    @Override
    public boolean isTravelAuthorizationOpened(TravelAuthorizationDocument document){
        return isTravelAuthorizationProcessed(document) && isOpen(document);
    }

    /**
     * is this document in a processed workflow state?
     *
     * @param reqForm
     * @return
     */
    @Override
    public boolean isProcessed(TravelDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().isProcessed();
    }

    @Override
    public KualiDecimal getAmountDueFromInvoice(String documentNumber, KualiDecimal requestedAmount) {
        try {
            AccountsReceivableCustomerInvoice doc = (AccountsReceivableCustomerInvoice) documentService.getByDocumentHeaderId(documentNumber);
            if (doc != null) {
                return doc.getOpenAmount();
            }
        }
        catch (WorkflowException we) {
            throw new RuntimeException(we);
        }

        return requestedAmount;
    }

    /**
     * Find the current travel authorization.  This includes any amendments.
     *
     * @param trDocument
     * @return
     * @throws WorkflowException
     */
    @Override
    public TravelAuthorizationDocument findCurrentTravelAuthorization(TravelDocument document) {

        TravelAuthorizationDocument travelDocument = null;

        try {
            final Map<String, List<Document>> relatedDocuments = getDocumentsRelatedTo(document);
            List<Document> taDocs  = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            List<Document> taaDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
            List<Document> tacDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);

            //If TAC exists, it will always be the most current travel auth doc
            if (tacDocs != null && !tacDocs.isEmpty()) {
                travelDocument =  (TravelAuthorizationDocument) tacDocs.get(0);
            }
            //find the TAA with the correct status
            else if (taaDocs != null && !taaDocs.isEmpty()){
                for (Document tempDocument : taaDocs){
                    //Find the doc that is the open to perform actions against.
                    if (isTravelAuthorizationOpened((TravelAuthorizationDocument)tempDocument)){
                        travelDocument = (TravelAuthorizationDocument) tempDocument;
                    }
                }
            }
            //return TA doc if no amendments exist
            if (travelDocument == null) {
                //if the taDocs is null, initialize an empty list
                taDocs = taDocs == null? new ArrayList<Document>() : taDocs;

                if (taDocs.isEmpty()) {
                    //this should find the TA document for sure
                    final List<TravelAuthorizationDocument> tempTaDocs = findAuthorizationDocuments(document.getTravelDocumentIdentifier());
                    if (!tempTaDocs.isEmpty()){
                        travelDocument = tempTaDocs.get(0);
                    }
                }else{
                    travelDocument = (TravelAuthorizationDocument) taDocs.get(0);
                }
            }
        }
        catch (WorkflowException we) {
            final String docNum = (document != null && !StringUtils.isBlank(document.getDocumentNumber())) ? document.getDocumentNumber() : "???";
            throw new RuntimeException("Could not find documents related to document #"+docNum);
        }
        return travelDocument;
    }

    /**
     * Find the root document for creating a travel reimbursement from a previous document.
     *
     * @param trDocument
     * @return
     * @throws WorkflowException
     */
    @Override
    public TravelDocument findRootForTravelReimbursement(String travelDocumentIdentifier) {

        TravelDocument rootTravelDocument = null;

        try {
            //look for a current authorization first

            //use the travelDocumentIdentifier to find any saved authorization
            final Collection<TravelAuthorizationDocument> tempTaDocs = getTravelAuthorizationService().find(travelDocumentIdentifier);

            if (!tempTaDocs.isEmpty()) {
                TravelAuthorizationDocument taDoc = null;
                for(TravelAuthorizationDocument tempTaDoc : tempTaDocs) {
                    taDoc = tempTaDoc;
                    break;
                }

                //find the current travel authorization
                rootTravelDocument = findCurrentTravelAuthorization(taDoc);
            }

            //no authorizations exist so the root should be a reimbursement
            else {
                final List<TravelReimbursementDocument> tempTrDocs = findReimbursementDocuments(travelDocumentIdentifier);
                //did not find any reimbursements either
                if (tempTrDocs.isEmpty()) {
                    LOG.debug("Did not find any authorizations or reimbursements for travelDocumentIndentifier: "+ travelDocumentIdentifier);
                    return null;
                }

                //if there is only one document then that is the root
                if (tempTrDocs.size() == 1) {
                    rootTravelDocument = tempTrDocs.get(0);
                }
                else {
                    //the root document can be found using any document in the list; just use the first one
                    String rootDocumentNumber = getAccountingDocumentRelationshipService().getRootDocumentNumber(tempTrDocs.get(0).getDocumentNumber());
                    TravelDocument tempDoc = (TravelDocument)documentService.getByDocumentHeaderIdSessionless(rootDocumentNumber);

                    rootTravelDocument = tempDoc;
                }
            }
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not find authorization or reimbursement documents related to trip id #"+travelDocumentIdentifier);
        }

        return rootTravelDocument;
    }

    @Override
    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document) {
        KualiDecimal trTotal = KualiDecimal.ZERO;

        List<Document> relatedTravelReimbursementDocuments = getDocumentsRelatedTo(document, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        for(Document trDoc: relatedTravelReimbursementDocuments) {
            final TravelReimbursementDocument tr = (TravelReimbursementDocument)trDoc;
            if (!KFSConstants.DocumentStatusCodes.CANCELLED.equals(tr.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode()) && !KFSConstants.DocumentStatusCodes.DISAPPROVED.equals(tr.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode())) {
                List<AccountingLine> lines = tr.getSourceAccountingLines();
                for(AccountingLine line: lines) {
                    trTotal = trTotal.add(line.getAmount());
                }
            }
        }

        if (document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)){
            List<AccountingLine> lines = document.getSourceAccountingLines();
            for(AccountingLine line: lines) {
                trTotal = trTotal.add(line.getAmount());
            }
        }

        return trTotal;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getTotalAuthorizedEncumbrance(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public KualiDecimal getTotalAuthorizedEncumbrance(TravelDocument document) {
        KualiDecimal taTotal = KualiDecimal.ZERO;
        TravelAuthorizationDocument taDoc = null;
        taDoc = findCurrentTravelAuthorization(document);
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
    @Override
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
        final Set<String> accountList = new HashSet<String>();
        for (AccountingLine line : lines) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            if (line != null && !ObjectUtils.isNull(line.getAccount())) {
                Person accountFiscalOfficerUser = line.getAccount().getAccountFiscalOfficerUser();
                if (accountFiscalOfficerUser != null && accountFiscalOfficerUser.getPrincipalId().equals(principalId)) {
                    accountList.add(line.getAccountNumber());
                }
            }
        }
        return new ArrayList<String>(accountList);
    }

    /**
     * This method checks to see if the type code is for a non-employee
     *
     * @param travelerTypeCode
     */
    @Override
    public boolean checkNonEmployeeTravelerTypeCode(String travelerTypeCode) {
        boolean foundCode = false;
        if (getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES).contains(travelerTypeCode)) {
            foundCode = true;
        }
        return foundCode;
    }


    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getAllStates(java.lang.String)
     */
    @Override
    public String getAllStates(final String countryCode) {

        final List<State> codes = getStateService().findAllStatesInCountry(countryCode);

        final StringBuffer sb = new StringBuffer();
        sb.append(";");
        for (final State state : codes) {
            if (state.isActive()) {
                sb.append(state.getCode()).append(";");
            }
        }

        return sb.toString();
    }

    /**
     *
     * This method imports the file and convert it to a list of objects (of the class specified in the parameter)
     *
     * TODO: re-evaluate KUALITEM-954 in regards to defaultValues and attributeMaxLength. Validation should not happen at parsing (these param are only used by importAttendees in TravelEntertainmentAction).
     *
     * @param formFile
     * @param c
     * @param attributeNames
     * @param tabErrorKey
     * @return
     */
    @Override
    public <T> List<T> importFile(final String fileContents, final Class<T> c, final String[] attributeNames, final Map<String,List<String>> defaultValues,
            final Integer[] attributeMaxLength, final String tabErrorKey) {
        if(attributeMaxLength != null && attributeNames.length != attributeMaxLength.length){
            throw new UploadParserException("Invalid parser configuration, the number of attribute names and attribute max length should be the same");
        }

        return importFile(fileContents, c, attributeNames, defaultValues, attributeMaxLength, tabErrorKey, getDefaultAcceptableFileExtensions());
    }

    /**
     *
     * This method imports the file and convert it to a list of objects (of the class specified in the parameter)
     * @param formFile
     * @param c
     * @param attributeNames
     * @param tabErrorKey
     * @param fileExtensions
     * @return
     */
    public <T> List<T> importFile(final String fileContents, Class<T> c, String[] attributeNames,Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, String tabErrorKey, List<String> fileExtensions) {
        final List<T> importedObjects = new ArrayList<T>();

        // parse file line by line
        Integer lineNo = 0;
        boolean failed = false;
        for (final String line : fileContents.split("\n")) {
            lineNo++;
            try {
                final T o = parseLine(line, c, attributeNames, defaultValues, attributeMaxLength, lineNo, tabErrorKey);
                importedObjects.add(o);
            }
            catch (UploadParserException e) {
                // continue to parse the rest of the lines after the current line fails
                // error messages are already dealt with inside parseFile, so no need to do anything here
                failed = true;
            }
        }

        if (failed) {
            throw new UploadParserException("errors in parsing lines in file ", ERROR_UPLOADPARSER_LINE);
        }

        return importedObjects;
    }

    /**
     *
     * This method parses a CSV line
     * @param line
     * @param c
     * @param attributeNames
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected <T> T parseLine(String line, Class<T> c, String[] attributeNames,Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, Integer lineNo, String tabErrorKey) {
        final Map<String, String> objectMap = retrieveObjectAttributes(line, attributeNames, defaultValues, attributeMaxLength, lineNo, tabErrorKey);
        final T obj = genObjectWithRetrievedAttributes(objectMap, c, lineNo, tabErrorKey);
        ((PersistableBusinessObject) obj).refresh();
        return obj;
    }

    /**
     *
     * This method generates an object instance and populates it with the specified attribute map.
     * @param objectMap
     * @param c
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected <T> T genObjectWithRetrievedAttributes(final Map<String, String> objectMap,
            final Class<T> c, final Integer lineNo, final String tabErrorKey) {
        T object;
        try {
            object = c.newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to complete line population.", e);
        }

        boolean failed = false;
        for (final Map.Entry<String, String> entry : objectMap.entrySet()) {
            try {
                try {
                    ObjectUtils.setObjectProperty(object, entry.getKey(), entry.getValue());
                }
                catch (FormatException e) {
                    String[] errorParams = { entry.getValue(), entry.getKey(), "" + lineNo };
                    throw new UploadParserException("invalid numeric property value: "
                            + entry.getKey() + " = " + entry.getValue() + " (line " + lineNo + ")", ERROR_UPLOADPARSER_INVALID_NUMERIC_VALUE, errorParams);
                }
            }
            catch (UploadParserException e) {
                // continue to parse the rest of the properties after the current property fails
                GlobalVariables.getMessageMap().putError(tabErrorKey, e.getErrorKey(), e.getErrorParameters());
                failed = true;
            }
            catch (NoSuchMethodException nsme) {
                throw new RuntimeException("Could not set property while parsing group travelers csv", nsme);
            }
            catch (InvocationTargetException ite) {
                throw new RuntimeException("Could not set property while parsing group travelers csv", ite);
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException("Could not set property while parsing group travelers csv", iae);
            }
        }

        if (failed) {
            throw new UploadParserException("empty or invalid properties in line " + lineNo + ")", ERROR_UPLOADPARSER_PROPERTY, ""+lineNo);
        }
        return object;
    }

    /**
     *
     * This method retrieves the attributes as key-value string pairs into a map.
     * @param line
     * @param attributeNames
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected Map<String, String> retrieveObjectAttributes(String line,
            String[] attributeNames,
            Map<String, List<String>> defaultValues,
            Integer[] attributeMaxLength,
            Integer lineNo, String tabErrorKey) {
        String[] attributeValues = StringUtils.splitPreserveAllTokens(line, ',');
        if (attributeNames.length != attributeValues.length) {
            String[] errorParams = { "" + attributeNames.length, "" + attributeValues.length, "" + lineNo };
            GlobalVariables.getMessageMap().putError(tabErrorKey, ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER, errorParams);
            throw new UploadParserException("wrong number of properties: " + attributeValues.length + " exist, " + attributeNames.length + " expected (line " + lineNo + ")", ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER, errorParams);
        }

        for (int i = 0; i < attributeNames.length; i++) {
            if (defaultValues != null && defaultValues.get(attributeNames[i]) != null) {
                List<String> defaultValue = defaultValues.get(attributeNames[i]);
                boolean found = false;
                for (String value : defaultValue) {
                    if (attributeValues[i].equalsIgnoreCase(value)) {
                        found = true;
                    }
                }
                if (!found) {
                    GlobalVariables.getMessageMap().putWarning(tabErrorKey, MESSAGE_UPLOADPARSER_INVALID_VALUE, attributeNames[i], attributeValues[i], (" " + lineNo));
                    throw new UploadParserException("Invalid value " + attributeValues[i] + " exist, " + "in line (" + lineNo + ")", ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER);
                }
            }

            if (attributeMaxLength != null) {
                if (attributeValues[i] != null && attributeValues[i].length() > attributeMaxLength[i]) {
                    attributeValues[i] = attributeValues[i].substring(0, attributeMaxLength[i]);
                    String[] errorParams = { "" + attributeNames[i], "" + attributeMaxLength[i], "" + lineNo };
                    GlobalVariables.getMessageMap().putWarning(tabErrorKey, MESSAGE_UPLOADPARSER_EXCEEDED_MAX_LENGTH, errorParams);
                }
            }
        }

        Map<String, String> objectMap = new HashMap<String, String>();
        for (int i = 0; i < attributeNames.length; i++) {
            objectMap.put(attributeNames[i], attributeValues[i]);
        }

        return objectMap;
    }

    /**
     * Parses a header into some usable form that can be used to parse records from the
     * CSV
     *
     * @param csvHeader is an array of columns for a csv record
     * @return a {@link Map} keyed by field names to their column numbers
     */
    protected Map<String, List<Integer>> parseHeader(final String[] csvHeader) {
        final Map<String, List<Integer>> retval = new HashMap<String, List<Integer>>();

        for (Integer i = 0; i < csvHeader.length; i++) {

            if (StringUtils.isBlank(csvHeader[i].trim())) {
                final String formattedName = nextHeader(csvHeader, i);
                final Integer start = i;
                final Integer end = csvHeader.length > i ? nextBlankHeader(csvHeader, i) : i;

                final List<Integer> indexes = new ArrayList<Integer>();

                for (Integer y = start; y < end; y++) {
                    indexes.add(y);
                }
                retval.put(formattedName, indexes);
            }
            else {
                final String formattedName = toCamelCase(csvHeader[i]);

                if (StringUtils.isNotBlank(formattedName)) {
                    retval.put(formattedName, Arrays.asList(new Integer[] { i }));
                }
            }
        }
        return retval;
    }

    protected String nextHeader(final String[] headers, final int start) {
        for (int i = start + 1; i < headers.length; i++) {
            if (StringUtils.isNotBlank(headers[i])) {
                return toCamelCase(headers[i]);
            }
        }
        return "";
    }


    protected Integer nextBlankHeader(final String[] headers, final int start) {
        for (int i = start + 1; i < headers.length; i++) {
            if (StringUtils.isBlank(headers[i])) {
                return i;
            }
        }
        return -1;
    }

    protected String toProperCase(final String s) {
        if (s == null || s.length() < 1) {
            return "";
        }

        final char[] arr = s.toLowerCase().toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);

        return new String(arr);
    }

    protected String toCamelCase(final String s) {
        final StringBuffer buffer = new StringBuffer();

        final List<String> words = new LinkedList<String>(Arrays.asList(s.toLowerCase().trim().replace('_', ' ').split(" ")));
        buffer.append(words.remove(0));

        for (final String word : words) {
            buffer.append(toProperCase(word));
        }
        return buffer.toString();
    }
    /**
     *
     */
    @Override
    public List<GroupTraveler> importGroupTravelers(final TravelDocument document, final String csvData) throws Exception {
        final List<GroupTraveler> retval = new LinkedList<GroupTraveler>();
        final BufferedReader bufferedFileReader = new BufferedReader(new StringReader(csvData));
        final CSVReader csvReader = new CSVReader(bufferedFileReader);

        final List<String[]> rows;
        try {
            rows = csvReader.readAll();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new ParseException("Could not  parse CSV file data", ex);
        }
        finally {
            try {
                csvReader.close();
            }
            catch (Exception e) {}
        }

        final Map<String,List<Integer>> header = getGroupTravelerHeaders();

        for (final String[] row : rows) {
            final GroupTravelerCsvRecord record = createGroupTravelerCsvRecord(header, row);
            final GroupTraveler traveler = new GroupTraveler();
            traveler.setGroupTravelerEmpId(record.getGroupTravelerEmpId());
            traveler.setName(record.getName());
            traveler.setGroupTravelerType(record.getGroupTravelerType());
            retval.add(traveler);
        }

        return retval;
    }

    protected GroupTravelerCsvRecord createGroupTravelerCsvRecord(final Map<String, List<Integer>> header, final String[] record) throws Exception {
        return getCsvRecordFactory().newInstance(header, record);
    }

    @Override
    public boolean isUnsuccessful(TravelDocument document) {
        String status = document.getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        List<String> unsuccessful = KewApiConstants.DOCUMENT_STATUS_PARENT_TYPES.get(KewApiConstants.DOCUMENT_STATUS_PARENT_TYPE_UNSUCCESSFUL);
        for (String tempStatus : unsuccessful){
            if (status.equals(tempStatus)){
                return true;
            }
        }

        return false;
    }

    /**
     * Turns the injected List of groupTravelerHeaders into a Map where the key is the name and the value is a single element array holding the position of the column (which is assumed to be in the order the columns were injected)
     * @return a Map of columns and positions
     */
    protected Map<String,List<Integer>> getGroupTravelerHeaders() {
        Map<String, List<Integer>> headers = new HashMap<String, List<Integer>>();
        if (getGroupTravelerColumns() != null && !getGroupTravelerColumns().isEmpty()) {
            int count = 0;
            while (count < getGroupTravelerColumns().size()) {
                List<Integer> countArray = new ArrayList<Integer>(2);
                countArray.add(new Integer(count));
                final String columnName = getGroupTravelerColumns().get(count);
                headers.put(columnName, countArray);
                count += 1;
            }
        }
        return headers;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyGroupTravelers(java.util.List, java.lang.String)
     */
    @Override
    public List<GroupTraveler> copyGroupTravelers(List<GroupTraveler> groupTravelers, String documentNumber) {
        List<GroupTraveler> newGroupTravelers = new ArrayList<GroupTraveler>();
        if (groupTravelers != null) {
            for (GroupTraveler groupTraveler : groupTravelers) {
                GroupTraveler newGroupTraveler = new GroupTraveler();
                BeanUtils.copyProperties(groupTraveler, newGroupTraveler);
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
    @Override
    public List<? extends TemExpense> copyActualExpenses(List<? extends TemExpense> actualExpenses, String documentNumber) {
        List<ActualExpense> newActualExpenses = new ArrayList<ActualExpense>();

        if (actualExpenses != null) {
            for (TemExpense expense : actualExpenses) {
                ActualExpense actualExpense = (ActualExpense)expense;
                ActualExpense newActualExpense = new ActualExpense();
                boolean nullCheck = false;
                if (actualExpense.getExpenseDate() == null) {
                    nullCheck = true;
                    actualExpense.setExpenseDate(new Date(0));
                }
                BeanUtils.copyProperties(actualExpense, newActualExpense);
                if (nullCheck) {
                    actualExpense.setExpenseDate(null);
                    newActualExpense.setExpenseDate(null);
                }

                List<TemExpense> newDetails = (List<TemExpense>) this.copyActualExpenses(actualExpense.getExpenseDetails(), documentNumber);
                newActualExpense.setExpenseDetails(newDetails);
                newActualExpense.setDocumentNumber(documentNumber);
                newActualExpense.setVersionNumber(new Long(1));
                newActualExpense.setId(null);
                newActualExpense.setObjectId(null);
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
    @Override
    public List<PerDiemExpense> copyPerDiemExpenses(List<PerDiemExpense> perDiemExpenses, String documentNumber) {
        List<PerDiemExpense> newPerDiemExpenses = new ArrayList<PerDiemExpense>();
        if (perDiemExpenses != null) {
            for (PerDiemExpense expense : perDiemExpenses){
                PerDiemExpense newExpense = new PerDiemExpense();
                BeanUtils.copyProperties(expense, newExpense);
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
    @Override
    public List<TravelAdvance> copyTravelAdvances(List<TravelAdvance> travelAdvances, String documentNumber) {
        List<TravelAdvance> newTravelAdvances = new ArrayList<TravelAdvance>();
        if (travelAdvances != null) {
            for (TravelAdvance travelAdvance : travelAdvances){
                TravelAdvance newTravelAdvance = (TravelAdvance) ObjectUtils.deepCopy(travelAdvance);
                newTravelAdvance.setDocumentNumber(documentNumber);
                newTravelAdvance.setVersionNumber(new Long(1));
                newTravelAdvance.setObjectId(null);
                newTravelAdvance.setTravelDocumentIdentifier(travelAdvance.getTravelDocumentIdentifier());
                newTravelAdvances.add(newTravelAdvance);
            }
        }
        return newTravelAdvances;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copySpecialCircumstances(java.util.List, java.lang.String)
     */
    @Override
    public List<SpecialCircumstances> copySpecialCircumstances(List<SpecialCircumstances> specialCircumstancesList, String documentNumber) {
        List<SpecialCircumstances> newSpecialCircumstancesList = new ArrayList<SpecialCircumstances>();
        if (specialCircumstancesList != null) {
            for (SpecialCircumstances specialCircumstances : specialCircumstancesList){
                SpecialCircumstances newSpecialCircumstances = new SpecialCircumstances();
                BeanUtils.copyProperties(specialCircumstances, newSpecialCircumstances);
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
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyTransportationModeDetails(java.util.List, java.lang.String)
     */
    @Override
    public List<TransportationModeDetail> copyTransportationModeDetails(List<TransportationModeDetail> transportationModeDetails, String documentNumber) {
        List<TransportationModeDetail> newTransportationModeDetails = new ArrayList<TransportationModeDetail>();
        if (transportationModeDetails != null) {
            for (TransportationModeDetail detail : transportationModeDetails){
                TransportationModeDetail newDetail = new TransportationModeDetail();
                BeanUtils.copyProperties(detail, newDetail);
                newDetail.setDocumentNumber(documentNumber);
                newDetail.setVersionNumber(new Long(1));
                newDetail.setObjectId(null);
                newTransportationModeDetails.add(newDetail);
            }
        }
        return newTransportationModeDetails;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#showNoTravelAuthorizationError(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
     */
    @Override
    public void showNoTravelAuthorizationError(TravelReimbursementDocument document){
        if (document.getTripType() != null && document.getTripType().getTravelAuthorizationRequired()){
            TravelAuthorizationDocument authorization = findCurrentTravelAuthorization(document);
            if (authorization == null){
                GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.TRIP_TYPE_CODE, TemKeyConstants.ERROR_TRIP_TYPE_TA_REQUIRED, document.getTripType().getName());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#getAdvancesTotalFor(TravelDocument)
     */
    @Override
    public KualiDecimal getAdvancesTotalFor(TravelDocument travelDocument) {
        KualiDecimal retval = KualiDecimal.ZERO;
        if (ObjectUtils.isNull(travelDocument)) {
            return retval;
        }

        LOG.debug("Looking for travel advances for travel: "+ travelDocument.getDocumentNumber());

        TravelAuthorizationDocument authorization = null;
        authorization = findCurrentTravelAuthorization(travelDocument);

        if (authorization == null) {
            return retval;
        }
        authorization.refreshReferenceObject(TemPropertyConstants.TRVL_ADV);

        if (authorization.shouldProcessAdvanceForDocument()) {
            retval = retval.add(authorization.getTravelAdvance().getTravelAdvanceRequested());
        }
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#retrieveAddressFromLocationCode(java.lang.String)
     */
    @Override
    public String retrieveAddressFromLocationCode(String locationCode) {
        PaymentDocumentationLocation dvDocumentLocation = businessObjectService.findBySinglePrimaryKey(PaymentDocumentationLocation.class, locationCode);
        String address = ObjectUtils.isNotNull(dvDocumentLocation)? dvDocumentLocation.getPaymentDocumentationLocationAddress() : "";
        return address;
    }

    @Override
    public boolean validateSourceAccountingLines(TravelDocument travelDocument, boolean addToErrorPath) {
        boolean success = true;
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        fieldValues.put(KRADPropertyConstants.DOCUMENT_NUMBER, travelDocument.getDocumentNumber());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE);

        List<TemSourceAccountingLine> currentLines = (List<TemSourceAccountingLine>) getBusinessObjectService().findMatchingOrderBy(TemSourceAccountingLine.class, fieldValues,KFSPropertyConstants.SEQUENCE_NUMBER, true);

        final boolean canUpdate = isAtTravelNode(travelDocument.getDocumentHeader().getWorkflowDocument());  // Are we at the travel node?  If so, there's a chance that accounting lines changed; if they did, that
                                        // was a permission granted to the travel manager so we should allow it

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
    public Date findLatestTaxableRamificationNotificationDate() {
        Object[] returnResult =  travelDocumentDao.findLatestTaxableRamificationNotificationDate();
        Date date = null;
        try {
          date =  ObjectUtils.isNotNull(returnResult[0])? dateTimeService.convertToSqlDate((Timestamp)returnResult[0]): null;
        }catch (java.text.ParseException ex) {
            LOG.error("Invalid latest taxable ramification notification date " + returnResult[0]);
        }

        return date;
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
        if(getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.HOLD_NEW_FISCAL_YEAR_ENCUMBRANCES_IND)) {

            java.util.Date endDate = getUniversityDateService().getLastDateOfFiscalYear(getUniversityDateService().getCurrentFiscalYear());
            if (ObjectUtils.isNotNull(document.getTripBegin()) && document.getTripBegin().after(endDate)) {
                return true;
            }

        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#revertOriginalDocument(org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    @Override
    public void revertOriginalDocument(TravelDocument travelDocument, String status) {
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(); // this service is not a good candidate for injection
        List<Document> relatedDocumentList = getDocumentsRelatedTo(travelDocument, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT,
                TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        for (Document taDocument : relatedDocumentList) {
            if (taDocument.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)) {
                TravelAuthorizationDocument taDoc = (TravelAuthorizationDocument) taDocument;
                try {
                    taDoc.updateAndSaveAppDocStatus(status);
                }
                catch (WorkflowException ex1) {
                    // TODO Auto-generated catch block
                    ex1.printStackTrace();
                }

                try {
                    Note cancelNote = getDocumentService().createNoteFromDocument(taDoc, "Amemdment Canceled");
                    Principal systemUser = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
                    cancelNote.setAuthorUniversalIdentifier(systemUser.getPrincipalId());
                    taDoc.addNote(cancelNote);
                    getNoteService().save(cancelNote);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                documentAttributeIndexingQueue.indexDocument(taDoc.getDocumentNumber());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getDocumentType(org.kuali.kfs.module.tem.document.TravelDocument)
     */
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

    /**
     * Check if workflow is at the specific node
     *
     * @param workflowDocument
     * @param nodeName
     * @return
     */
    protected boolean isAtTravelNode(WorkflowDocument workflowDocument) {
        Set<String> nodeNames = workflowDocument.getNodeNames();
        for (String nodeNamesNode : nodeNames) {
            if (TemWorkflowConstants.RouteNodeNames.AP_TRAVEL.equals(nodeNamesNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all travel advances associated with the given trip id
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#getTravelAdvancesForTrip(java.lang.String)
     */
    @Override
    public List<TravelAdvance> getTravelAdvancesForTrip(String travelDocumentIdentifier) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, travelDocumentIdentifier);
        List<TravelAdvance> advances = new ArrayList<TravelAdvance>();
        final Collection<TravelAdvance> foundAdvances = getBusinessObjectService().findMatchingOrderBy(TravelAdvance.class, criteria, KFSPropertyConstants.DOCUMENT_NUMBER, true);
        for (TravelAdvance foundAdvance: foundAdvances) {
            if (foundAdvance.isAtLeastPartiallyFilledIn() && isDocumentApprovedOrExtracted(foundAdvance.getDocumentNumber())) {
                advances.add(foundAdvance);
            }
        }
        return advances;
    }



    /**
     * Determines if the document with the given document number has been approved or not
     * @param documentNumber the document number of the document to check
     * @return true if the document has been approved, false otherwise
     */
    protected boolean isDocumentApprovedOrExtracted(String documentNumber) {
        final FinancialSystemDocumentHeader documentHeader = getBusinessObjectService().findBySinglePrimaryKey(FinancialSystemDocumentHeader.class, documentNumber);
        return KFSConstants.DocumentStatusCodes.APPROVED.equals(documentHeader.getFinancialDocumentStatusCode()) || KFSConstants.DocumentStatusCodes.Payments.EXTRACTED.equals(documentHeader.getFinancialDocumentStatusCode());
    }

    /**
     * Determines if the document with the given document number has been initiated or submitted for routing
     * @param documentNumber the document number of the document to check
     * @return true if the document has been approved, false otherwise
     */
    protected boolean isDocumentInitiatedOrEnroute(String documentNumber) {
        final FinancialSystemDocumentHeader documentHeader = getBusinessObjectService().findBySinglePrimaryKey(FinancialSystemDocumentHeader.class, documentNumber);
        return KFSConstants.DocumentStatusCodes.INITIATED.equals(documentHeader.getFinancialDocumentStatusCode()) || KFSConstants.DocumentStatusCodes.ENROUTE.equals(documentHeader.getFinancialDocumentStatusCode());
    }

    /**
     * Gets the {@link OrganizationOptions} to create a {@link AccountsReceivableDocumentHeader} for
     * {@link PaymentApplicationDocument}
     *
     * @return OrganizationOptions
     */
    @Override
    public AccountsReceivableOrganizationOptions getOrgOptions() {
        final String chartOfAccountsCode = parameterService.getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_CHART);
        final String organizationCode = parameterService.getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_ORGANIZATION);

        return getAccountsReceivableModuleService().getOrgOptionsIfExists(chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#disableDuplicateExpenses(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, org.kuali.kfs.module.tem.businessobject.ActualExpense)
     */
    @Override
    public void disableDuplicateExpenses(TravelDocument trDocument, ActualExpense actualExpense) {
        if (trDocument.getPerDiemExpenses() != null && !trDocument.getPerDiemExpenses().isEmpty()) {  // no per diems? then let's not bother
            if (actualExpense.getExpenseDetails() != null && !actualExpense.getExpenseDetails().isEmpty()) {
                for (TemExpense detail : actualExpense.getExpenseDetails()) {
                    checkActualExpenseAgainstPerDiems(trDocument, (ActualExpense)detail);
                }
            } else {
                checkActualExpenseAgainstPerDiems(trDocument, actualExpense);
            }
        }
    }

    /**
     * Checks the given actual expense (or detail) against each of the per diems on the TR document to disable
     * @param trDocument the travel reimbursement with per diems to check against
     * @param actualExpense
     */
    protected void checkActualExpenseAgainstPerDiems(TravelDocument trDocument, ActualExpense actualExpense) {
        int i = 0;
        for (final PerDiemExpense perDiemExpense : trDocument.getPerDiemExpenses()) {
            List<DisabledPropertyMessage> messages = disableDuplicateExpenseForPerDiem(actualExpense, perDiemExpense, i);
            if (messages != null && !messages.isEmpty()) {
                for (DisabledPropertyMessage message : messages) {
                    message.addToProperties(trDocument.getDisabledProperties());
                }
            }
            i+=1;
        }
    }

    /**
     * Given one actual expense and one per diem, determines if any of fields on the per diem should be disabled because the actual expense is already covering it
     * @param actualExpense the actual expense to check
     * @param perDiemExpense the per diem to check the actual expense against
     * @param otherExpenseLineCode the expense type code of the actual epxnese
     * @param perDiemCount the count of the per diems we have worked through
     * @return a List of any messages about disabled properties which occurred
     */
    protected List<DisabledPropertyMessage> disableDuplicateExpenseForPerDiem(ActualExpense actualExpense, PerDiemExpense perDiemExpense, int perDiemCount) {
        List<DisabledPropertyMessage> disabledPropertyMessages = new ArrayList<DisabledPropertyMessage>();

        if (actualExpense.getExpenseDate() == null){
            return disabledPropertyMessages;
        }
        final String expenseDate = getDateTimeService().toDateString(actualExpense.getExpenseDate());
        String meal = "";
        boolean valid = true;

        if (KfsDateUtils.isSameDay(perDiemExpense.getMileageDate(), actualExpense.getExpenseDate())) {
            if (perDiemExpense.getBreakfast() && actualExpense.isBreakfast() && (actualExpense.getExpenseType().isHosted() || actualExpense.getExpenseType().isGroupTravel())) {
                meal = TemConstants.HostedMeals.HOSTED_BREAKFAST;
                perDiemExpense.setBreakfast(false);
                perDiemExpense.setBreakfastValue(KualiDecimal.ZERO);
                valid = false;
            }
            else if (perDiemExpense.getLunch() && actualExpense.isLunch() && (actualExpense.getExpenseType().isHosted() || actualExpense.getExpenseType().isGroupTravel())) {
                meal = TemConstants.HostedMeals.HOSTED_LUNCH;
                perDiemExpense.setLunch(false);
                perDiemExpense.setLunchValue(KualiDecimal.ZERO);
                valid = false;
            }
            else if (perDiemExpense.getDinner() && actualExpense.isDinner() && (actualExpense.getExpenseType().isHosted() || actualExpense.getExpenseType().isGroupTravel())) {
                meal = TemConstants.HostedMeals.HOSTED_DINNER;
                perDiemExpense.setDinner(false);
                perDiemExpense.setDinnerValue(KualiDecimal.ZERO);
                valid = false;
            }

            if (!valid) {
                String temp = String.format(PER_DIEM_EXPENSE_DISABLED, perDiemCount, meal);
                String message = getMessageFrom(MESSAGE_TR_MEAL_ALREADY_CLAIMED, expenseDate, meal);
                disabledPropertyMessages.add(new DisabledPropertyMessage(temp, message));
            }

            // KUALITEM-483 add in check for lodging
            if (perDiemExpense.getLodging().isGreaterThan(KualiDecimal.ZERO) && !StringUtils.isBlank(actualExpense.getExpenseTypeCode()) && TemConstants.ExpenseTypes.LODGING.equals(actualExpense.getExpenseTypeCode())) {
                String temp = String.format(PER_DIEM_EXPENSE_DISABLED, perDiemCount, TemConstants.LODGING.toLowerCase());
                String message = getMessageFrom(MESSAGE_TR_LODGING_ALREADY_CLAIMED, expenseDate);
                perDiemExpense.setLodging(KualiDecimal.ZERO);
                disabledPropertyMessages.add(new DisabledPropertyMessage(temp, message));
            }
        }
        return disabledPropertyMessages;
    }


    @Override
    public List<String> findMatchingTrips(TravelDocument travelDocument) {

        String travelDocumentIdentifier = travelDocument.getTravelDocumentIdentifier();
        Integer temProfileId = travelDocument.getTemProfileId();
        Timestamp earliestTripBeginDate = null;
        Timestamp greatestTripEndDate = null;

        List<TravelReimbursementDocument> documents = findReimbursementDocuments(travelDocumentIdentifier);
       for (TravelReimbursementDocument document : documents) {
           Timestamp tripBegin = document.getTripBegin();
           Timestamp tripEnd = document.getTripEnd();
           if (ObjectUtils.isNull(earliestTripBeginDate) && ObjectUtils.isNull(greatestTripEndDate)) {
               earliestTripBeginDate = tripBegin;
               greatestTripEndDate = tripEnd;
           }
           else {
               earliestTripBeginDate = tripBegin.before(earliestTripBeginDate) ? tripBegin :earliestTripBeginDate;
               greatestTripEndDate = tripEnd.after(greatestTripEndDate)? tripEnd : greatestTripEndDate;

               }
        }

       // TR with no TAs created from mainmenu
       if(documents.isEmpty() && ObjectUtils.isNotNull(travelDocument.getTripBegin()) && ObjectUtils.isNotNull(travelDocument.getTripEnd())) {
           earliestTripBeginDate = getTripBeginDate(travelDocument.getTripBegin());
           greatestTripEndDate = getTripEndDate(travelDocument.getTripEnd());
       }

       List<TravelReimbursementDocument> matchDocs =  (List<TravelReimbursementDocument>) travelDocumentDao.findMatchingTrips(temProfileId ,earliestTripBeginDate, greatestTripEndDate);
        List<String> documentIds = new ArrayList<String>();
        for (TravelReimbursementDocument document : matchDocs) {
            if(!travelDocument.getDocumentNumber().equals(document.getDocumentNumber())) {
                documentIds.add(document.getDocumentNumber());
            }
        }
        return documentIds;
    }

    private Integer getDuplicateTripDateRangeDays() {
        String tripDateRangeDays = parameterService.getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelParameters.DUPLICATE_TRIP_DATE_RANGE_DAYS);
        Integer days = null;
        if (!StringUtils.isNumeric(tripDateRangeDays)) {
            days = TemConstants.DEFAULT_DUPLICATE_TRIP_DATE_RANGE_DAYS;
       }

       days = Integer.parseInt(tripDateRangeDays);
       return days;

    }

    private Timestamp getTripBeginDate(Timestamp tripBeginDate) {
        Timestamp tripBegin = null;
        Integer days = getDuplicateTripDateRangeDays();
         try {
             tripBegin = dateTimeService.convertToSqlTimestamp(dateTimeService.toDateString(DateUtils.addDays(tripBeginDate, (days * -1))));

         } catch (java.text.ParseException pe) {
             LOG.error("Exception while parsing trip begin date" + pe);
         }


         return tripBegin;

     }

     private Timestamp getTripEndDate(Timestamp tripEndDate) {
         Timestamp tripEnd = null;
         Integer days = getDuplicateTripDateRangeDays();
          try {
              tripEnd = dateTimeService.convertToSqlTimestamp(dateTimeService.toDateString((DateUtils.addDays(tripEndDate, days ))));

          } catch (java.text.ParseException pe) {
              LOG.error("Exception while parsing trip end date" + pe);
          }

          return tripEnd;

     }


    /**
     * Inner class to hold keys & messages for disabled properties
     */
    class DisabledPropertyMessage {
        private String key;
        private String message;

        DisabledPropertyMessage(String key, String message) {
            this.key = key;
            this.message = message;
        }

        void addToProperties(Map<String, String> messageMap) {
            messageMap.put(key, message);
        }
    }

    /**
     *
     * This method gets the current travel document by travel document identifier
     * @param travelDocumentIdentifier
     * @return
     */
    @Override
    public TravelDocument getParentTravelDocument(String travelDocumentIdentifier) {

       if (ObjectUtils.isNull(travelDocumentIdentifier) || StringUtils.equals(travelDocumentIdentifier,"")) {
           LOG.error("Received a null tripId/travelDocumentIdentifier; returning a null TravelDocument");
           return null;
       }

       try {
           TravelDocument travelDocument = findRootForTravelReimbursement(travelDocumentIdentifier);
           if (ObjectUtils.isNotNull(travelDocument)) {
               LOG.debug("Found "+ travelDocument.getDocumentNumber() +" ("+ travelDocument.getDocumentTypeName() +") for travelDocumentIdentifier: "+ travelDocumentIdentifier);
               return travelDocument;
           }

       } catch (Exception exception) {
           LOG.error("Exception occurred attempting to retrieve an authorization or remibursement travel document for travelDocumentIdentifier: "+ travelDocumentIdentifier, exception);
           return null;
       }

       Map<String, Object> fieldValues = new HashMap<String, Object>();
       fieldValues.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, travelDocumentIdentifier);
       fieldValues.put(TemPropertyConstants.TRIP_PROGENITOR, Boolean.TRUE);

       Collection<TravelEntertainmentDocument> entDocuments = getBusinessObjectService().findMatching(TravelEntertainmentDocument.class, fieldValues);
       if (entDocuments.iterator().hasNext()) {
           TravelDocument ent = entDocuments.iterator().next();
           LOG.debug("Found "+ ent.getDocumentNumber() +" ("+ ent.getDocumentTypeName() +") for travelDocumentIdentifier: "+ travelDocumentIdentifier);
           return ent;
       }

       Collection<TravelRelocationDocument> reloDocuments = getBusinessObjectService().findMatching(TravelRelocationDocument.class, fieldValues);
       if (reloDocuments.iterator().hasNext()) {
           TravelDocument relo = reloDocuments.iterator().next();
           LOG.info("Found "+ relo.getDocumentNumber() +" ("+ relo.getDocumentTypeName() +") for travelDocumentIdentifier: "+ travelDocumentIdentifier);
           return relo;
       }

       LOG.error("Unable to find any travel document for given Trip Id: "+ travelDocumentIdentifier);
       return null;
    }

    /**
     * Calculate the total of the source accounting lines on the document
     * @param travelDoc the travel document to calculate the source accounting line total for
     * @return the total of the source accounting lines
     */
    protected KualiDecimal getAccountingLineAmount(TravelDocument travelDoc) {
        KualiDecimal total = KualiDecimal.ZERO;
        if (travelDoc.getSourceAccountingLines() != null && !travelDoc.getSourceAccountingLines().isEmpty()) {
            for (TemSourceAccountingLine accountingLine : (List<TemSourceAccountingLine>)travelDoc.getSourceAccountingLines()) {
                total = total.add(accountingLine.getAmount());
            }
        }
        return total;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getTravelDocumentNumbersByTrip(java.lang.String)
     */
    @Override
    public Collection<String> getApprovedTravelDocumentNumbersByTrip(String travelDocumentIdentifier) {
        HashMap<String,String> documentNumbersToReturn = new HashMap<String,String>();

        List<TravelDocument> travelDocuments = new ArrayList<TravelDocument>();

        TravelDocument travelDocument = getParentTravelDocument(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(travelDocument)) {
            travelDocuments.add(travelDocument);
        }

        travelDocuments.addAll(getTravelDocumentDao().findDocuments(TravelReimbursementDocument.class, travelDocumentIdentifier));
        travelDocuments.addAll(getTravelDocumentDao().findDocuments(TravelEntertainmentDocument.class, travelDocumentIdentifier));
        travelDocuments.addAll(getTravelDocumentDao().findDocuments(TravelRelocationDocument.class, travelDocumentIdentifier));

        for(Iterator<TravelDocument> iter = travelDocuments.iterator(); iter.hasNext();) {
            TravelDocument document = iter.next();
            if (!documentNumbersToReturn.containsKey(document.getDocumentNumber()) && isDocumentStatusValidForReconcilingCharges(document)) {
                documentNumbersToReturn.put(document.getDocumentNumber(),document.getDocumentNumber());
            }
        }

        return documentNumbersToReturn.values();
    }

    @Override
    public boolean isDocumentStatusValidForReconcilingCharges(TravelDocument travelDocument) {

        String documentNumber = travelDocument.getDocumentNumber();

        if (isDocumentApprovedOrExtracted(documentNumber)) {
            return true;
        }

        if (travelDocument instanceof TravelAuthorizationDocument) {
            boolean vendorPaymentAllowedBeforeFinalAuthorization = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);

            if (vendorPaymentAllowedBeforeFinalAuthorization) {
                return isDocumentInitiatedOrEnroute(documentNumber);
            }
        }

        if (travelDocument instanceof TravelReimbursementDocument) {
            boolean vendorPaymentAllowedBeforeFinalReimbursement = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);

            if (vendorPaymentAllowedBeforeFinalReimbursement) {
                return isDocumentInitiatedOrEnroute(documentNumber);
            }
        }

        return false;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#restorePerDiemProperty(org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    @Override
    public void restorePerDiemProperty(TravelDocument document, String property) {
        try {
            final String[] perDiemPropertyParts = splitPerDiemProperty(property);
            PerDiemExpense perDiemExpense = (PerDiemExpense)ObjectUtils.getPropertyValue(document, perDiemPropertyParts[0]);
            final String mealName = perDiemPropertyParts[1];
            final boolean mealProperty = isMealProperty(mealName);
            final String mealSuffix = (mealProperty) ? "Value" : "";
            final String mealValueName = mealName+mealSuffix;

            KualiDecimal currentMealValue = (KualiDecimal)ObjectUtils.getPropertyValue(perDiemExpense, mealValueName);
            if (currentMealValue != null && currentMealValue.equals(KualiDecimal.ZERO)) {
                final PerDiem perDiem = getPerDiemService().getPerDiem(perDiemExpense.getPrimaryDestinationId(), perDiemExpense.getMileageDate(), document.getEffectiveDateForPerDiem(perDiemExpense));
                final KualiDecimal mealAmount = (KualiDecimal)ObjectUtils.getPropertyValue(perDiem, mealName);
                final boolean prorated = mealProperty && !KfsDateUtils.isSameDay(document.getTripBegin(), document.getTripEnd()) && (KfsDateUtils.isSameDay(perDiemExpense.getMileageDate(), document.getTripBegin()) || KfsDateUtils.isSameDay(perDiemExpense.getMileageDate(), document.getTripEnd()));
                if (prorated && !ObjectUtils.isNull(document.getTripType())) {
                    perDiemExpense.setProrated(true);
                    final String perDiemCalcMethod = document.getTripType().getPerDiemCalcMethod();
                    final Integer perDiemPercent = calculateProratePercentage(perDiemExpense, perDiemCalcMethod, document.getTripEnd());
                    final KualiDecimal proratedAmount = PerDiemExpense.calculateMealsAndIncidentalsProrated(mealAmount, perDiemPercent);
                    ObjectUtils.setObjectProperty(perDiemExpense, mealValueName, proratedAmount);
                } else {
                    ObjectUtils.setObjectProperty(perDiemExpense, mealValueName, mealAmount);
                }
                if (mealProperty) {
                    ObjectUtils.setObjectProperty(perDiemExpense, mealName, Boolean.TRUE);
                }
            }
        }
        catch (FormatException fe) {
            throw new RuntimeException("Could not set meal value on per diem expense", fe);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Could not set meal value on per diem expense", iae);
        }
        catch (InvocationTargetException ite) {
            throw new RuntimeException("Could not set meal value on per diem expense", ite);
        }
        catch (NoSuchMethodException nsme) {
            throw new RuntimeException("Could not set meal value on per diem expense", nsme);
        }
    }

    /**
     * Determines if the given property name represents a meal on a PerDiemExpense (ie, a property with a boolean property and a "Value" property)
     * @param property the property to check
     * @return true if the property represents a field with an extra "Value" field, false otherwise
     */
    protected boolean isMealProperty(String property) {
        return StringUtils.equals(property, TemPropertyConstants.BREAKFAST) || StringUtils.equals(property, TemPropertyConstants.LUNCH) || StringUtils.equals(property, TemPropertyConstants.DINNER) || StringUtils.equals(property, TemPropertyConstants.INCIDENTALS);
    }

    /**
     * Splits a property into the per diem part and the property of the per diem expense we should update
     * @param property the property to split
     * @return an Array where the first element is the property path to a per diem expense and the second is the property path to a meal value on that per diem
     */
    protected String[] splitPerDiemProperty(String property) {
        final String deDocumentedProperty = property.replace(KFSPropertyConstants.DOCUMENT+".", KFSConstants.EMPTY_STRING);
        final int lastDivider = deDocumentedProperty.lastIndexOf('.');
        final String perDiemPart = deDocumentedProperty.substring(0, lastDivider);
        final String mealPart = deDocumentedProperty.substring(lastDivider+1);
        return new String[] { perDiemPart, mealPart };
    }

    /**
     * Looks up the document with the progenitor document for the trip
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getRootTravelDocumentWithoutWorkflowDocument(java.lang.String)
     */
    @Override
    public TravelDocument getRootTravelDocumentWithoutWorkflowDocument(String travelDocumentIdentifier) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, travelDocumentIdentifier);
        fieldValues.put(TemPropertyConstants.TRIP_PROGENITOR, new Boolean(true));
        for (String documentType : getTravelDocumentTypesToCheck()) {
            final Class<? extends TravelDocument> docClazz = getTravelDocumentForType(documentType);
            Collection<TravelDocument> matchingDocs = (Collection<TravelDocument>)getBusinessObjectService().findMatching(docClazz, fieldValues);
            if (matchingDocs != null && !matchingDocs.isEmpty()) {
                List<TravelDocument> foundDocs = new ArrayList<TravelDocument>();
                foundDocs.addAll(matchingDocs);
                return foundDocs.get(0);
            }
        }
        return null;
    }

    /**
     * HEY EVERYONE! BIG CUSTOMIZATION OPPORTUNITY!
     * This method returns an ordered list of where to look for progenitor documents.  The order is based on my total guess of which
     * document type is most likely to be the progenitor, so it's TA, ENT, RELO, TR.  But, if you don't use TA's, then obviously TR's should
     * be first.  Anyhow, please feel free to rearrange this list as seems most helpful to you
     * @return a List of the document types to look for root documents in - in which order
     */
    protected List<String> getTravelDocumentTypesToCheck() {
        List<String> documentTypes = new ArrayList<String>();
        documentTypes.add(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        documentTypes.add(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
        documentTypes.add(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
        documentTypes.add(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        return documentTypes;
    }

    /**
     * Looks up the class associated with the given document type to check
     * @param documentType the document type name to find a class for
     * @return the class of that document type
     */
    protected Class<? extends TravelDocument> getTravelDocumentForType(String documentType) {
        return (Class<TravelDocument>)getDataDictionaryService().getDocumentClassByTypeName(documentType);
    }

    /**
     * This smooshes the accounting lines which will do advance clearing.  Here, since we're replacing the object code, we'll smooth together all accounting lines
     * which have the same chart - account - sub-acount.
     * @param originalAccountingLines the List of accounting lines to smoosh
     * @return the smooshed accounting lines
     */
    @Override
    public List<TemSourceAccountingLine> smooshAccountingLinesToSubAccount(List<TemSourceAccountingLine> originalAccountingLines) {
       final Map<SmooshLineKey, KualiDecimal> smooshLines =  smooshLinesToMap(originalAccountingLines);
       final List<TemSourceAccountingLine> unsmooshedLines = raiseMapToLines(smooshLines);
       return unsmooshedLines;
    }

    /**
     * Smooshes the lines into a Map
     * @param accountingLines the accounting lines to smoosh
     * @return the Map of smooshed lines
     */
    protected Map<SmooshLineKey, KualiDecimal> smooshLinesToMap(List<TemSourceAccountingLine> accountingLines) {
        Map<SmooshLineKey, KualiDecimal> smooshLines = new HashMap<SmooshLineKey, KualiDecimal>();
        for (TemSourceAccountingLine line : accountingLines) {
            final SmooshLineKey key = new SmooshLineKey(line);
            if (smooshLines.containsKey(key)) {
                KualiDecimal currAmount = smooshLines.get(key);
                KualiDecimal newAmount = currAmount.add(line.getAmount());
                smooshLines.put(key, newAmount);
            } else {
                smooshLines.put(key, line.getAmount());
            }
        }
        return smooshLines;
    }

    /**
     * According to thesaurus.com, "raise" is the antonym of "smoosh".  So this method takes our smooshed line information and turns them back into things which sort of resemble accounting lines
     * @param smooshLineMap the Map to turn back into accounting lines
     * @return the un-smooshed accounting lines.  Yeah, I like that verb better too
     */
    protected List<TemSourceAccountingLine> raiseMapToLines(Map<SmooshLineKey, KualiDecimal> smooshLineMap) {
        List<TemSourceAccountingLine> raisedLines = new ArrayList<TemSourceAccountingLine>();
        for (SmooshLineKey key : smooshLineMap.keySet()) {
            final TemSourceAccountingLine line = convertKeyAndAmountToLine(key, smooshLineMap.get(key));
            raisedLines.add(line);
        }
        return raisedLines;
    }

    /**
     * Converts a SmooshLineKey and an amount into a real - though somewhat less informative - accounting line
     * @param key the key
     * @param amount the amount
     * @return the reconstituted accounting line.  I like that verb too.
     */
    protected TemSourceAccountingLine convertKeyAndAmountToLine(SmooshLineKey key, KualiDecimal amount) {
        TemSourceAccountingLine line = new TemSourceAccountingLine();
        line.setChartOfAccountsCode(key.getChartOfAccountsCode());
        line.setAccountNumber(key.getAccountNumber());
        line.setSubAccountNumber(key.getSubAccountNumber());
        line.setAmount(amount);
        return line;
    }

    /**
     * Hash key of lines we want to smoosh
     */
    protected class SmooshLineKey {
        protected String chartOfAccountsCode;
        protected String accountNumber;
        protected String subAccountNumber;

        public SmooshLineKey(TemSourceAccountingLine accountingLine) {
            this.chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
            this.accountNumber = accountingLine.getAccountNumber();
            this.subAccountNumber = accountingLine.getSubAccountNumber();
        }

        public String getChartOfAccountsCode() {
            return chartOfAccountsCode;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getSubAccountNumber() {
            return subAccountNumber;
        }

        @Override
        public int hashCode() {
            HashCodeBuilder hcb = new HashCodeBuilder();
            hcb.append(getChartOfAccountsCode());
            hcb.append(getAccountNumber());
            hcb.append(getSubAccountNumber());
            return hcb.toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SmooshLineKey) || obj == null) {
                return false;
            }
            final SmooshLineKey golyadkin = (SmooshLineKey)obj;
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(getChartOfAccountsCode(), golyadkin.getChartOfAccountsCode());
            eb.append(getAccountNumber(), golyadkin.getAccountNumber());
            eb.append(getSubAccountNumber(), golyadkin.getSubAccountNumber());
            return eb.isEquals();
        }
    }

    /**
     * Parses the value of url.document.travelRelocation.agencySites and turns those into links
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getAgencyLinks(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<LinkField> getAgencyLinks(TravelDocument travelDocument) {
        List<LinkField> agencyLinks = new ArrayList<LinkField>();
        if (getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.ENABLE_AGENCY_SITES_URL)) {
            final String agencySitesURL = getConfigurationService().getPropertyValueAsString(TemKeyConstants.AGENCY_SITES_URL);
            final String target = "_blank";
            if(!StringUtils.isEmpty(agencySitesURL)){
                String[] sites = agencySitesURL.split(";");
                for (String site : sites){
                    String[] siteInfo = site.split("=");
                    String url = customizeAgencyLink(travelDocument, siteInfo[0], siteInfo[1]);
                    final String prefixedUrl = prefixUrl(url);
                    LinkField link = new LinkField();
                    link.setHrefText(prefixedUrl);
                    link.setTarget(target);
                    link.setLinkLabel(siteInfo[0]);
                    agencyLinks.add(link);
                }
            }
        }
        return agencyLinks;
    }

    /**
     * In the default version, checks if the "config.document.travelRelocation.agencySites.include.tripId" property is true and if it is, just dumbly
     * appends the tripId= doc's trip id to the link.  Really, out of the box, this isn't all that smart. Will mask the value if the parameter says to.
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#customizeAgencyLink(org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String, java.lang.String)
     */
    @Override
    public String customizeAgencyLink(TravelDocument travelDocument, String agencyName, String link) {
        final boolean passTrip = getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.PASS_TRIP_ID_TO_AGENCY_SITES);
        if (!passTrip ||  StringUtils.isBlank(travelDocument.getTravelDocumentIdentifier())) {
            return link; // nothing to add
        }

        if (travelDocument instanceof TravelAuthorizationDocument) {
            final boolean vendorPaymentAllowedBeforeFinal = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);
            if (!vendorPaymentAllowedBeforeFinal) {
                return link;
            }
        }
        final String linkWithTripId = link+"?tripId="+travelDocument.getTravelDocumentIdentifier();
        return linkWithTripId;
    }

    /**
     * Makes sure that url starts with https
     * @param url the url to prefix as needed
     * @return the url prefixed by protocol
     */
    protected String prefixUrl(String url) {
        String prefixedUrl = url;
        if (!prefixedUrl.startsWith("http")) {
            prefixedUrl = "https://"+prefixedUrl;
        }
        return prefixedUrl;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#isInitiatorTraveler(TravelDocument)
     */
    @Override
    public boolean isInitiatorTraveler(TravelDocument travelDoc) {
        String initiatorId = travelDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String travelerId = travelDoc.getTraveler().getPrincipalId();
        boolean is = travelerId != null && initiatorId.equals(travelerId);
        return is;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#requiresTravelerApproval(TravelAuthorizationDocument)
     */
    @Override
    public boolean requiresTravelerApproval(TravelAuthorizationDocument taDoc) {
        // If there's travel advances, route to traveler if necessary
        boolean require = taDoc.requiresTravelAdvanceReviewRouting();
        require &= !taDoc.getTravelAdvance().getTravelAdvancePolicy();

        // no need to route back to traveler if s/he is the initiator
        require &= !isInitiatorTraveler(taDoc);

        return require;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#requiresTravelerApproval(TEMReimbursementDocument)
     */
    @Override
    public boolean requiresTravelerApproval(TEMReimbursementDocument trDoc) {
        String travelerTypeCode = trDoc.getTraveler().getTravelerTypeCode();
        if (parameterService.getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES).contains(travelerTypeCode)) {
            return false;
        }

        // no need to route back to traveler if s/he is the initiator
        return !isInitiatorTraveler(trDoc);
    }

    /**
     * @return the system-ste implementation of the AccountsReceivableModuleService
     */
    public AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        return accountsReceivableModuleService;
    }

    public TravelAuthorizationService getTravelAuthorizationService() {
        return travelAuthorizationService;
    }

    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }

    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }

    public List<String> getGroupTravelerColumns() {
        return groupTravelerColumns;
    }

    public void setGroupTravelerColumns(List<String> groupTravelerColumns) {
        this.groupTravelerColumns = groupTravelerColumns;
    }

    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public TravelService getTravelService() {
        return travelService;
    }

    public void setTravelService(TravelService travelService) {
        this.travelService = travelService;
    }

    public MileageRateService getMileageRateService() {
        return mileageRateService;
    }

    public void setMileageRateService(MileageRateService mileageRateService) {
        this.mileageRateService = mileageRateService;
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

    public TemRoleService getTemRoleService() {
        return temRoleService;
    }

    public void setTemRoleService(TemRoleService temRoleService) {
        this.temRoleService = temRoleService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public StateService getStateService() {
        return stateService;
    }

    public void setStateService(StateService stateService) {
        this.stateService = stateService;
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

    public List<String> getDefaultAcceptableFileExtensions() {
        return defaultAcceptableFileExtensions;
    }

    public void setDefaultAcceptableFileExtensions(final List<String> defaultAcceptableFileExtensions) {
        this.defaultAcceptableFileExtensions = defaultAcceptableFileExtensions;
    }

    public void setCsvRecordFactory(final CsvRecordFactory<GroupTravelerCsvRecord> recordFactory) {
        this.csvRecordFactory = recordFactory;
    }

    public CsvRecordFactory<GroupTravelerCsvRecord> getCsvRecordFactory() {
        return this.csvRecordFactory;
    }
}
