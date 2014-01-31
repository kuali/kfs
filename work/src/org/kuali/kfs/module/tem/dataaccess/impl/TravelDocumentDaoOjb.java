/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.OjbCollectionAware;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class TravelDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements TravelDocumentDao, OjbCollectionAware {

    public static Logger LOG = Logger.getLogger(TravelDocumentDaoOjb.class);


	@Override
    public List<String> findDocumentNumbers(final Class<?> travelDocumentClass, final String travelDocumentNumber) {
        final Criteria c = new Criteria();
        c.addEqualTo(TRAVEL_DOCUMENT_IDENTIFIER, travelDocumentNumber);

        LOG.debug("Creating query for type "+ travelDocumentClass+ " using criteria "+ c);

        final List<String> retval = new ArrayList<String>();

        Collection<? extends TravelDocument> documents = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(travelDocumentClass, c));

        for (Iterator it = documents.iterator(); it.hasNext();) {
            TravelDocument document = (TravelDocument) it.next();
            retval.add(document.getDocumentNumber());
        }

        return retval;
    }

	/**
	 * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#findPerDiem(int, java.sql.Date, java.sql.Date)
	 */
	@Override
    public PerDiem findPerDiem(int primaryDestinationId, java.sql.Timestamp perDiemDate, java.sql.Date effectiveDate){
	    Criteria criteria = new Criteria();
	    criteria.addEqualTo(TemPropertyConstants.PRIMARY_DESTINATION_ID, new Integer(primaryDestinationId));
	    criteria.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

	    //Add date criteria so the date falls in a specific range
	    //or their is no "To" date.  (Open-ended)
        Criteria dateBetweenCriteria = new Criteria();
        Criteria dateNullCriteria = new Criteria();

        Date date = KfsDateUtils.clearTimeFields(new Date(perDiemDate.getTime()));

        dateBetweenCriteria.addGreaterOrEqualThan(TemPropertyConstants.PER_DIEM_EFFECTIVE_TO_DATE, effectiveDate);
        dateBetweenCriteria.addLessOrEqualThan(TemPropertyConstants.PER_DIEM_EFFECTIVE_FROM_DATE, effectiveDate);

        dateNullCriteria.addIsNull(TemPropertyConstants.PER_DIEM_EFFECTIVE_TO_DATE);

        dateBetweenCriteria.addOrCriteria(dateNullCriteria);
        criteria.addAndCriteria(dateBetweenCriteria);
	    QueryByCriteria query = QueryFactory.newQuery(PerDiem.class, criteria);

	    List<PerDiem> possiblePerDiems = new ArrayList<PerDiem>();
	    possiblePerDiems.addAll(getPersistenceBrokerTemplate().getCollectionByQuery(query));
	    if (possiblePerDiems.isEmpty()) {
	        return null;
	    }
	    if (possiblePerDiems.size() == 1) {
	        return possiblePerDiems.get(0);
	    }

	    Collections.sort(possiblePerDiems, new PerDiemComparator());
	    PerDiem foundPerDiem = null;
	    for (PerDiem perDiem : possiblePerDiems) {
	        if (isOnOrAfterSeasonBegin(perDiem.getSeasonBeginMonthAndDay(), perDiemDate)) {
	            foundPerDiem = perDiem;
	        }
	    }

	    return foundPerDiem;
	}

	/**
	 * Comparator to help us sort per diem records by season begin month/day
	 */
	protected class PerDiemComparator implements Comparator<PerDiem> {
	    /**
	     * next compare method I write will use patty and selma, I promise
	     * Sorts the season begin month/days such that earlier dates are chosen before later dates
	     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	     */
        @Override
        public int compare(PerDiem viola, PerDiem sebastian) {
            if (StringUtils.isBlank(viola.getSeasonBeginMonthAndDay())) {
                if (StringUtils.isBlank(sebastian.getSeasonBeginMonthAndDay())) {
                    return 0;
                }
                return 1; // sebastian has a value - choose sebastian
            }
            if (StringUtils.isBlank(sebastian.getSeasonBeginMonthAndDay())) {
                return -1; // viola has a value but not sebastian - choose viola
            }

            final String[] violaSeasonBegin = viola.getSeasonBeginMonthAndDay().split("/");
            final String[] sebastianSeasonBegin = sebastian.getSeasonBeginMonthAndDay().split("/");

            final int violaBeginMonth = Integer.parseInt(violaSeasonBegin[0]);
            final int sebastianBeginMonth = Integer.parseInt(sebastianSeasonBegin[0]);
            if (violaBeginMonth != sebastianBeginMonth) {
                return sebastianBeginMonth - violaBeginMonth;
            }

            final int violaBeginDay = Integer.parseInt(violaSeasonBegin[1]);
            final int sebastianBeginDay = Integer.parseInt(sebastianSeasonBegin[1]);
            if (violaBeginDay != sebastianBeginDay) {
                return sebastianBeginDay - violaBeginDay;
            }
            return 0;
        }
	}

	/**
	 * Determines if the given date happens on or after the given season begin month/day for the year of the given date
	 * @param seasonBegin the season begin month/day to check
	 * @param d the date to check if on or after season begin
	 * @return true if the given date is on or after the season begin date, false otherwise
	 */
	protected boolean isOnOrAfterSeasonBegin(String seasonBegin, java.sql.Timestamp d) {
	    if (StringUtils.isBlank(seasonBegin)) {
	        return true; // no season begin/end?  Well...then we're after that, I should think
	    }

	    Calendar dCal = Calendar.getInstance();
	    dCal.setTime(d);
	    final int year = dCal.get(Calendar.YEAR);

	    Calendar seasonBeginCal = getSeasonBeginMonthDayCalendar(seasonBegin, year);

	    if (KfsDateUtils.isSameDay(dCal, seasonBeginCal)) { // let's see if they're on the same day, regardless of time
	        return true;
	    }
	    if (dCal.after(seasonBeginCal)) { // now that we know they're not on the same day, time isn't such a big deal
	        return true;
	    }
	    return false;
	}

	/**
	 * Given a season begin month/day and a year, returns a Calendar representing the date
	 * @param seasonBegin the season begin month/day
	 * @param year the year to set for the calendar
	 * @return the Calendar from the given date information
	 */
	protected Calendar getSeasonBeginMonthDayCalendar(String seasonBegin, int year) {
	    final String[] seasonBeginMonthDay = seasonBegin.split("/");
        Calendar seasonBeginCal = Calendar.getInstance();
        seasonBeginCal.set(Calendar.MONTH, Integer.parseInt(seasonBeginMonthDay[0]));
        seasonBeginCal.set(Calendar.DATE, Integer.parseInt(seasonBeginMonthDay[1]));
        seasonBeginCal.set(Calendar.YEAR, year);
        return seasonBeginCal;
	}

	/**
	 * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getOutstandingTravelAdvanceByInvoice(java.util.Set)
	 */
    @Override
    public List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumbers) {
        if (ObjectUtils.isNull(arInvoiceDocNumbers) || arInvoiceDocNumbers.isEmpty()) {
            return new ArrayList<TravelAdvance>();
        }

        Criteria criteria = new Criteria();
        criteria.addIn(TemPropertyConstants.AR_INVOICE_DOC_NUMBER, arInvoiceDocNumbers);
        criteria.addIsNull(TemPropertyConstants.TAXABLE_RAMIFICATION_NOTIFICATION_DATE);

        Query query = QueryFactory.newQuery(TravelAdvance.class, criteria);

        return (List<TravelAdvance>)getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#findLatestTaxableRamificationNotificationDate()
     */
    @Override
    public Object[] findLatestTaxableRamificationNotificationDate() {
        getPersistenceBrokerTemplate().clearCache();

        Criteria criteria = new Criteria();
        criteria.addNotNull(TemPropertyConstants.TAXABLE_RAMIFICATION_NOTIFICATION_DATE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(TravelAdvance.class, criteria);

        String selectionField = "max(" + TemPropertyConstants.TAXABLE_RAMIFICATION_NOTIFICATION_DATE + ")";
        query.setAttributes(new String[] { selectionField });
        query.setDistinct(true);

        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        return TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);


    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getReimbursementDocumentsByHeaderStatus(java.lang.String, boolean)
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getReimbursementDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly) {
        return (Collection<? extends TEMReimbursementDocument>)getTravelDocumentsByHeaderStatus(TravelReimbursementDocument.class, statusCode, immediatesOnly, TemPropertyConstants.TRAVEL_PAYMENT);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getRelocationDocumentsByHeaderStatus(java.lang.String, boolean)
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getRelocationDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly) {
        return (Collection<? extends TEMReimbursementDocument>)getTravelDocumentsByHeaderStatus(TravelRelocationDocument.class, statusCode, immediatesOnly, TemPropertyConstants.TRAVEL_PAYMENT);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getEntertainmentDocumentsByHeaderStatus(java.lang.String, boolean)
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getEntertainmentDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly) {
        return (Collection<? extends TEMReimbursementDocument>)getTravelDocumentsByHeaderStatus(TravelEntertainmentDocument.class, statusCode, immediatesOnly, TemPropertyConstants.TRAVEL_PAYMENT);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getAuthorizationDocumentsByHeaderStatus(java.lang.String, boolean)
     */
    @Override
    public Collection<? extends TravelAuthorizationDocument> getAuthorizationsAndAmendmentsByHeaderStatus(String statusCode, boolean immediatesOnly) {
        List<TravelAuthorizationDocument> documents = new ArrayList<TravelAuthorizationDocument>();
        documents.addAll((Collection<TravelAuthorizationDocument>)getTravelDocumentsByHeaderStatus(TravelAuthorizationDocument.class, statusCode, immediatesOnly, TemPropertyConstants.ADVANCE_TRAVEL_PAYMENT)); // extents will get travel auth amendments
        return documents;
    }

    /**
     * Do a lookup of reimbursable documents of the given class with the given financial system document header status code and a payment method of "check" (ie, PDP will pay out)
     * @param documentClazz the class of the document to look up
     * @param statusCode the status code of the documents to look up
     * @param immediatesOnly true if only those reimbursable documents
     * @return a Collection of qualifying documents
     */
    protected Collection<? extends TravelDocument> getTravelDocumentsByHeaderStatus(Class<? extends TravelDocument> documentClazz, String statusCode, boolean immediatesOnly, String travelPaymentProperty) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getReimbursableDocumentsByHeaderStatus() started");
        }

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, statusCode);
        criteria.addEqualTo(travelPaymentProperty+".paymentMethodCode", KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK);
        if (immediatesOnly) {
            criteria.addEqualTo(travelPaymentProperty+".immediatePaymentIndicator", Boolean.TRUE);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(documentClazz, criteria));
    }

    /**
     * Retrieves all travel reimbursement documents which have corporate card expenses to extract but which have not yet been extracted
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getReimbursementDocumentsNeedingCorporateCardExtraction()
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getReimbursementDocumentsNeedingCorporateCardExtraction() {
        return (Collection<? extends TEMReimbursementDocument>)getUnExtractedCorporateCardTravelDocuments(TravelReimbursementDocument.class);

    }

    /**
     * Retrieves all entertainment reimbursement documents which have corporate card expenses to extract but which have not yet been extracted
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getReimbursementDocumentsNeedingCorporateCardExtraction()
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getEntertainmentDocumentsNeedingCorporateCardExtraction() {
        return (Collection<? extends TEMReimbursementDocument>)getUnExtractedCorporateCardTravelDocuments(TravelEntertainmentDocument.class);

    }

    /**
     * Retrieves all moving and relocation reimbursement documents which have corporate card expenses to extract but which have not yet been extracted
     * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#getReimbursementDocumentsNeedingCorporateCardExtraction()
     */
    @Override
    public Collection<? extends TEMReimbursementDocument> getRelocationDocumentsNeedingCorporateCardExtraction() {
        return (Collection<? extends TEMReimbursementDocument>)getUnExtractedCorporateCardTravelDocuments(TravelRelocationDocument.class);

    }

    /**
     * Retrieves all documents of the given TravelDocument class that a) are extracted or approved by document header, but b) have a null corporate card extraction date and c) actually have corporate card expenses
     * @param documentClazz the class of the travel documents to retrieve
     * @return a Collection of matching documents
     */
    protected Collection<? extends TravelDocument> getUnExtractedCorporateCardTravelDocuments(Class<? extends TravelDocument> documentClazz) {
        Criteria criteria = new Criteria();

        List<String> statusCodes = new ArrayList<String>();
        statusCodes.add(KFSConstants.DocumentStatusCodes.Payments.EXTRACTED);
        statusCodes.add(KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addIn(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, statusCodes);
        criteria.addIsNull(TemPropertyConstants.CORPORATE_CARD_PAYMENT_EXTRACT_DATE);

        Criteria expenseCriteria = new Criteria();
        expenseCriteria.addEqualTo(TemPropertyConstants.CARD_TYPE, TemConstants.TRAVEL_TYPE_CORP);
        expenseCriteria.addEqualTo(TemPropertyConstants.EXPENSE_LINE_TYPE_CODE, TemConstants.EXPENSE_IMPORTED);
        ReportQueryByCriteria expensesSubQuery = QueryFactory.newReportQuery(ImportedExpense.class, expenseCriteria);
        expensesSubQuery.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});
        criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, expensesSubQuery);

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(documentClazz, criteria));
    }


    @Override
    public Collection<? extends TEMReimbursementDocument> findMatchingTrips (Integer temProfileId ,Timestamp tripBegin, Timestamp tripEnd)  {


        final Criteria criteria = new Criteria();
        final Criteria orEndDateCriteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TEM_PROFILE_ID, temProfileId);
        criteria.addEqualTo(TemPropertyConstants.TRIP_BEGIN_DT, tripBegin);
        orEndDateCriteria.addEqualTo(TemPropertyConstants.TRIP_END_DT, tripEnd);
        criteria.addOrCriteria(orEndDateCriteria);
        criteria.addNotIn("documentHeader.financialDocumentStatusCode", Arrays.asList(DocumentStatusCodes.INITIATED));

        return  getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(TravelReimbursementDocument.class, criteria));


    }
}
