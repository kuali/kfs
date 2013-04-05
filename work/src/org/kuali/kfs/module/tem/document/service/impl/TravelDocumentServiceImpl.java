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
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_EXCEEDED_MAX_LENGTH;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_INVALID_VALUE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherDocumentationLocation;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
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
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.GroupTravelerCsvRecord;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.MileageRateObjCode;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstancesQuestion;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.exception.UploadParserException;
import org.kuali.kfs.module.tem.service.CsvRecordFactory;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
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
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Travel Service Implementation
 */
public class TravelDocumentServiceImpl implements TravelDocumentService {

    protected static Logger LOG = Logger.getLogger(TravelDocumentServiceImpl.class);

    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private TravelDocumentDao travelDocumentDao;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private TravelerService travelerService;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private TEMRoleService temRoleService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiRuleService kualiRuleService;
    private StateService stateService;
    private PersistenceStructureService persistenceStructureService;
    private UniversityDateService universityDateService;
    private List<String> defaultAcceptableFileExtensions;
    private CsvRecordFactory<GroupTravelerCsvRecord> csvRecordFactory;

    /**
     * Creates and populates an individual per diem item.
     *
     * @param perDiemId is the id for the referenced {@link PerDiem} object that gets attached
     * @return date of the item
     */
    protected PerDiemExpense createPerDiemItem(final TravelDocument document, final PerDiem newPerDiem, final Timestamp ts, final boolean prorated) {
        final PerDiemExpense expense = newPerDiemExpense();
        expense.setPerDiem(newPerDiem);
        expense.setPerDiemId(newPerDiem.getId());
        expense.refreshPerDiem();
        expense.setProrated(prorated);
        expense.setMileageDate(ts);

        PerDiem perDiem = expense.getPerDiem();
        expense.setPrimaryDestination(perDiem.getPrimaryDestination());
        expense.setCountryState(perDiem.getCountryState());
        expense.setCounty(perDiem.getCounty());

        //default first to per diem's values
        expense.setBreakfastValue(new KualiDecimal(perDiem.getBreakfast()));
        expense.setLunchValue(new KualiDecimal(perDiem.getLunch()));
        expense.setDinnerValue(new KualiDecimal(perDiem.getDinner()));
        expense.setIncidentalsValue(perDiem.getIncidentals());
        // if prorated, recalculate the values
        if(expense.isProrated()){
            Integer perDiemPercent = calculateProratePercentage(expense, document.getTripType().getPerDiemCalcMethod(), document.getTripEnd());
            expense.setDinnerValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getDinnerValue(), perDiemPercent));
            expense.setLunchValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getLunchValue(), perDiemPercent));
            expense.setBreakfastValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getBreakfastValue(), perDiemPercent));
            expense.setIncidentalsValue(PerDiemExpense.calculateMealsAndIncidentalsProrated(expense.getIncidentalsValue(), perDiemPercent));
        }
        expense.setLodging(perDiem.getLodging());
        return expense;
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
        fieldValues.put(TemPropertyConstants.TRIP_TYPE, document.getTripTypeCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        // find a valid per diem for each date.  If per diem is null, make it a custom per diem.
        for (final Timestamp eachDate : dateRange(start, end)) {
            fieldValues.put(TemPropertyConstants.PER_DIEM_LOOKUP_DATE, eachDate);
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

            LOG.debug("Iterating over date range from "+ start+ " to "+ end);
            int counter = 0;
            for (final Timestamp someDate : dateRange(start, end)) {
                // Check if a per diem entry exists for this date
                if (!perDiemMapped.containsKey(someDate)) {
                    boolean prorated = !KfsDateUtils.isSameDay(start, end) && (KfsDateUtils.isSameDay(someDate, start) || KfsDateUtils.isSameDay(someDate, end));
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
            LOG.debug("Adding "+ perDiemMapped.get(someDate)+ " to perdiem list");
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
            List<KeyValue> mileageRates = getMileageRateKeyValues(searchDate);
            if (ObjectUtils.isNotNull(mileageRates)) {
                for (KeyValue pair : mileageRates) {
                    if (ObjectUtils.isNotNull(oldExpense) && oldExpense.getMileageRateId().intValue() == Integer.valueOf(pair.getKey()).intValue()) {
                        // use the same mileage rate as before
                        newExpense.setMileageRateId(oldExpense.getMileageRateId());
                        newExpense.setMiles(oldExpense.getMiles());
                        break;
                    }
                }
                if (ObjectUtils.isNull(newExpense.getMileageRateId())) {
                    // mileage rate is different than it was before the change, use the first element in the list
                    newExpense.setMileageRateId(Integer.valueOf(mileageRates.get(0).getKey()));
                }
            }
        }
        catch (java.text.ParseException ex) {
            LOG.error("Unable to convert timestamp to sql date. Unable to populate PerDiemExpense with MileageRate.");
            ex.printStackTrace();
        }
        return newExpense;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#getMileageRateKeyValues(java.sql.Date)
     */
    @Override
    public List<KeyValue> getMileageRateKeyValues(Date searchDate) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        TravelDocument document = (TravelDocument) ((TravelFormBase)KNSGlobalVariables.getKualiForm()).getDocument();
        String documentType = SpringContext.getBean(TravelDocumentService.class).getDocumentType(document);
        Map<String,Object> fieldValues = new HashMap<String,Object>();

        if(document.getTripTypeCode() != null){
            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, document.getTripTypeCode());
        }
        fieldValues.put(KFSPropertyConstants.DOCUMENT_TYPE, documentType);
        fieldValues.put(TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD, document.getTraveler().getTravelerTypeCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        final Collection<MileageRateObjCode> mileageRateObjectCodes = SpringContext.getBean(BusinessObjectService.class).findMatching(MileageRateObjCode.class,fieldValues);

        for (final MileageRateObjCode mileageRateObject : mileageRateObjectCodes) {
            mileageRateObject.refreshNonUpdateableReferences();
            MileageRate mileageRate = mileageRateObject.getMileageRate();

            final Date fromDate = mileageRate.getActiveFromDate();
            final Date toDate = mileageRate.getActiveToDate();
            if(ObjectUtils.isNull(searchDate)) {
                //just add them all
                keyValues.add(new ConcreteKeyValue(mileageRate.getId().toString(), mileageRate.getName()));
            } else if((fromDate.equals(searchDate) || fromDate.before(searchDate)) && (toDate.equals(searchDate) || toDate.after(searchDate))) {

                if (mileageRate != null && mileageRate.isActive()) {
                    keyValues.add(new ConcreteKeyValue(mileageRate.getId().toString(), mileageRate.getCodeAndRate(mileageRateObject)));
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
        }
        return relatedDocumentList;
    }

    /**
     * looks like its not used, deprecating it
     *
     * @param documentClass
     * @return
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
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
        LOG.debug("Found "+ results.size()+ " documents with ids in "+ headerIds);
        docMap.put(docTypeName, results);
    }

    /**
     * Make sure that the elements returned are of the right class.
     *
     * @param clazz
     * @param results
     */
    @SuppressWarnings("rawtypes")
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
        return getBusinessObjectService().findMatching(DocumentHeader.class, criteria);
    }

    @Override
    public List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType) {
        List<SpecialCircumstances> retval = new ArrayList<SpecialCircumstances>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);

        // add specialCircumstances with specific documentType SpecialCircumstancesQuestion
        criteria.put(KFSPropertyConstants.DOCUMENT_TYPE, documentType);
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

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findAuthorizationDocuments(java.lang.String)
     */
    @Override
    public List<TravelAuthorizationDocument> findAuthorizationDocuments(final String travelDocumentNumber){
        final List<String> ids = getTravelDocumentDao().findDocumentNumbers(TravelAuthorizationDocument.class, travelDocumentNumber);

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
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#findReimbursementDocuments(java.lang.String)
     */
    @Override
    public List<TravelReimbursementDocument> findReimbursementDocuments(final String travelDocumentNumber) {
        final List<String> ids = getTravelDocumentDao().findDocumentNumbers(TravelReimbursementDocument.class, travelDocumentNumber);

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
            LOG.error(wfe.getMessage(), wfe);
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

        document.getDocumentHeader().getWorkflowDocument().returnToPreviousNode(noteText, TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED);

        addAdHocFYIRecipient(document, document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());

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
        String perDiemPercentage = null;

        if (perDiemExpense.isProrated()) {
            if (perDiemCalcMethod != null && perDiemCalcMethod.equals(TemConstants.PERCENTAGE)) {
                try {
                    perDiemPercentage = parameterService.getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE);
                    perDiemPercent = Integer.parseInt(perDiemPercentage);
                }
                catch (Exception e1) {
                    LOG.error("Failed to process prorate percentage for FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE parameter.", e1);
                }
            }
            else {
                perDiemPercent = calculatePerDiemPercentageFromTimestamp(perDiemExpense, tripEnd);
            }
        }

        return perDiemPercent;
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
                if (!KfsDateUtils.isSameDay(prorateDate.getTime(), tripEnd)) {
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
        retval.setAccommodationTypeCode(perDiemExpense.getAccommodationTypeCode());
        retval.setAccommodationName(perDiemExpense.getAccommodationName());
        retval.setAccommodationPhoneNum(perDiemExpense.getAccommodationPhoneNum());
        retval.setAccommodationAddress(perDiemExpense.getAccommodationAddress());

        LOG.debug("Setting mileage rate on new Per Diem Object to "+ perDiemExpense.getMileageRateId());
        retval.setMileageRateId(perDiemExpense.getMileageRateId());
        retval.refreshReferenceObject("mileageRate");

        LOG.debug("Got mileage "+ retval.getMileageRate());
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
    @Override
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
                    parentActualExpense.setExpenseAmount(actualExpense.getTotalDetailExpenseAmount());
                    // total = total.add(parentActualExpense.getExpenseAmount());
                    parentActualExpense.setConvertedAmount(parentActualExpense.getExpenseAmount().multiply(parentActualExpense.getCurrencyRate()));
                }

            }
        }
        return total;
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
            final KualiDecimal rate = newActualExpenseLine.getCurrencyRate();
            final KualiDecimal amount = newActualExpenseLine.getExpenseAmount();

            newActualExpenseLine.setConvertedAmount(amount.multiply(rate));
            LOG.debug("Set converted amount for "+ newActualExpenseLine+ " to "+ newActualExpenseLine.getConvertedAmount());

            if (isHostedMeal(newActualExpenseLine)) {
                KNSGlobalVariables.getMessageList().add(TemKeyConstants.MESSAGE_HOSTED_MEAL_ADDED,
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
    @Override
    public boolean isHostedMeal(final ExpenseTypeAware havingExpenseType) {
        if (ObjectUtils.isNull(havingExpenseType) || ObjectUtils.isNull(havingExpenseType.getTravelExpenseTypeCode())) {
            return false;
        }

        final String code = havingExpenseType.getTravelExpenseTypeCode().getCode();
        final String hostedCodes = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.HOSTED_MEAL_EXPENSE_TYPES);

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

    @Override
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    @Override
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

    @Override
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

    public TEMRoleService getTemRoleService() {
        return temRoleService;
    }

    public void setTemRoleService(TEMRoleService temRoleService) {
        this.temRoleService = temRoleService;
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
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
     * is this document in a final workflow state
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
            return doc.getOpenAmount();
        }
        catch (Exception ex) {
            //  Auto-generated catch block
            //.printStackTrace();
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
    public TravelDocument findCurrentTravelAuthorization(TravelDocument document) throws WorkflowException{

        TravelDocument travelDocument = null;

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
        else {
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
        return travelDocument;
    }

    @Override
    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document) {
        KualiDecimal trTotal = KualiDecimal.ZERO;

        List<Document> relatedTravelReimbursementDocuments = getDocumentsRelatedTo(document, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        for(Document trDoc: relatedTravelReimbursementDocuments) {
            List<AccountingLine> lines = ((TravelReimbursementDocument)trDoc).getSourceAccountingLines();
            for(AccountingLine line: lines) {
                trTotal = trTotal.add(line.getAmount());
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
        try {
            taDoc = (TravelAuthorizationDocument) findCurrentTravelAuthorization(document);
        }
        catch (WorkflowException ex) {
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
            try {
                if (line != null && line.getAccount() != null) {
                    Person accountFiscalOfficerUser = line.getAccount().getAccountFiscalOfficerUser();
                    if (accountFiscalOfficerUser != null && accountFiscalOfficerUser.getPrincipalId().equals(principalId)) {
                        accountList.add(line.getAccountNumber());
                    }
                }
            } catch (PersistenceBrokerException ex){
                //COA Account getAccountFiscalOfficerUser() is generating PersistenceBrokerException when we lookup an invalid account number.
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
        if (getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.NON_EMPLOYEE_TRAVELER_TYPES).contains(travelerTypeCode)) {
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
            catch (Exception e) {
                throw new InfrastructureException("unable to complete line population.", e);
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

        final Map<String,List<Integer>> header = parseHeader(rows.remove(0));

        for (final String[] row : rows) {
            final GroupTravelerCsvRecord record = createGroupTravelerCsvRecord(header, row);
            final GroupTraveler traveler = new GroupTraveler();
            traveler.setGroupTravelerEmpId(record.getGroupTravelerEmpId());
            traveler.setName(record.getName());
            traveler.setTravelerTypeCode(record.getTravelerTypeCode());
            retval.add(traveler);
        }

        return retval;
    }

    protected GroupTravelerCsvRecord createGroupTravelerCsvRecord(final Map<String, List<Integer>> header, final String[] record) throws Exception {
        return getCsvRecordFactory().newInstance(header, record);
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
                BeanUtils.copyProperties(actualExpense, newActualExpense);
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
                newTravelAdvance.setId(null);
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
     *
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
            try {
                TravelAuthorizationDocument authorization = (TravelAuthorizationDocument) findCurrentTravelAuthorization(document);
                if (authorization == null){
                    GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.TRIP_TYPE_CODE, TemKeyConstants.ERROR_TRIP_TYPE_TA_REQUIRED, document.getTripType().getName());
                }
            }
            catch (WorkflowException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#getAdvancesTotalFor(TravelDocument)
     */
    @Override
    public KualiDecimal getAdvancesTotalFor(TravelDocument travelDocument) {
        LOG.debug("Looking for travel advances for travel: "+ travelDocument.getDocumentNumber());
        KualiDecimal retval = KualiDecimal.ZERO;
        TravelAuthorizationDocument authorization = null;
        try {
            authorization = (TravelAuthorizationDocument) findCurrentTravelAuthorization(travelDocument);
        }
        catch (WorkflowException ex) {
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
        LOG.debug("Got other expenses total ", travelDocument.getActualExpensesTotal());
        LOG.debug("Got Perdiem Mileage total ", travelDocument.getDailyTotalGrandTotal());
        LOG.debug("per diem size is ", travelDocument.getPerDiemExpenses().size());

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

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#retrieveAddressFromLocationCode(java.lang.String)
     */
    @Override
    public String retrieveAddressFromLocationCode(String locationCode) {
        DisbursementVoucherDocumentationLocation dvDocumentLocation = businessObjectService.findBySinglePrimaryKey(DisbursementVoucherDocumentationLocation.class, locationCode);
        String address = ObjectUtils.isNotNull(dvDocumentLocation)? dvDocumentLocation.getDisbursementVoucherDocumentationLocationAddress() : "";
        return address;
    }

    @Override
    public boolean validateSourceAccountingLines(TravelDocument travelDocument, boolean addToErrorPath) {
        boolean success = true;
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        fieldValues.put(KRADPropertyConstants.DOCUMENT_NUMBER, travelDocument.getDocumentNumber());

        List<SourceAccountingLine> currentLines = (List<SourceAccountingLine>) getBusinessObjectService().findMatching(SourceAccountingLine.class, fieldValues);

        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(travelDocument);
        boolean canUpdate = documentPresentationController.enableForDocumentManager(GlobalVariables.getUserSession().getPerson());

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

        List<Document> relatedDocumentList = getDocumentsRelatedTo(travelDocument, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT,
                TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        for (Document taDocument : relatedDocumentList) {
            if (taDocument.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)) {
                TravelAuthorizationDocument taDoc = (TravelAuthorizationDocument) taDocument;
                taDoc.updateAppDocStatus(status);

                try {
                    Note cancelNote = getDocumentService().createNoteFromDocument(taDoc, ((ConfigurationService) SpringContext.getService("ConfigurationService")).getPropertyValueAsString(TemKeyConstants.TA_MESSAGE_AMEND_DOCUMENT_CANCELLED_TEXT));
                    taDoc.addNote(cancelNote);
                    getDocumentService().saveDocument(taDoc);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
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
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#trimFinancialSystemDocumentHeader(org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader)
     */
    @Override
    public void trimFinancialSystemDocumentHeader(FinancialSystemDocumentHeader header){
        final int DOC_DESC_MAX_LEN = 40;
        if (header.getDocumentDescription().length() >= DOC_DESC_MAX_LEN) {
            String truncatedDocumentDescription = header.getDocumentDescription().substring(0, DOC_DESC_MAX_LEN - 1);
            header.setDocumentDescription(truncatedDocumentDescription);
        }
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

    public void adjustEncumbranceForAmendment(final TravelDocument taDocument) {}

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
