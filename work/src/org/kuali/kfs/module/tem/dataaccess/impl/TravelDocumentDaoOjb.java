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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;
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

        final ReportQueryByCriteria query = new ReportQueryByCriteria(travelDocumentClass, c);
        query.setAttributes(new String[] { "documentNumber" });

        final List<String> retval = new ArrayList<String>();

        for (final Iterator it = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
             it.hasNext();) {
            final Object[] obj = (Object[]) it.next();
            LOG.debug("Got Id "+ obj[0]);
            retval.add("" + obj[0]);
        }


        return retval;
    }

	/**
	 * @see org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao#findPerDiem(java.util.Map)
	 */
	@Override
    public PerDiem findPerDiem(Map<String, Object> fieldValues){
	    Criteria criteria = new Criteria();

	    //Add all field values but the date
	    for (String key : fieldValues.keySet()){
	        if (!key.equals(TemPropertyConstants.PER_DIEM_LOOKUP_DATE)){
	            if (StringUtils.isNotBlank((String)fieldValues.get(key))){
	                criteria.addEqualTo(""+key+"", ""+fieldValues.get(key)+"");
	            }
	        }
	    }

	    //Add date criteria so the date falls in a specific range
	    //or their is no "To" date.  (Open-ended)
        Criteria dateBetweenCriteria = new Criteria();
        Criteria dateNullCriteria = new Criteria();

        Timestamp dayStart = (Timestamp) fieldValues.get(TemPropertyConstants.PER_DIEM_LOOKUP_DATE);
        Date date = KfsDateUtils.clearTimeFields(new Date(dayStart.getTime()));
        dayStart = new Timestamp(date.getTime());

        Calendar cal = new GregorianCalendar();
        cal.setTime(dayStart);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.MILLISECOND, -1);

        Timestamp dayEnd = new Timestamp(cal.getTimeInMillis());

        dateBetweenCriteria.addGreaterOrEqualThan(TemPropertyConstants.PER_DIEM_EFFECTIVE_TO_DATE, dayStart);
        dateBetweenCriteria.addLessOrEqualThan(TemPropertyConstants.PER_DIEM_EFFECTIVE_FROM_DATE, dayEnd);

        dateNullCriteria.addIsNull(TemPropertyConstants.PER_DIEM_EFFECTIVE_TO_DATE);
        dateNullCriteria.addLessOrEqualThan(TemPropertyConstants.PER_DIEM_EFFECTIVE_FROM_DATE, dayEnd);

        dateBetweenCriteria.addOrCriteria(dateNullCriteria);
        criteria.addAndCriteria(dateBetweenCriteria);
	    QueryByCriteria query = QueryFactory.newQuery(PerDiem.class, criteria);

	    PerDiem perDiem =  (PerDiem) getPersistenceBrokerTemplate().getObjectByQuery(query);

	    return perDiem;
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

    @Override
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType) {
        Criteria criteria = new Criteria();
        if (tripType.equals(TemConstants.TEMTripTypes.INTERNATIONAL)){
            criteria.addEqualTo("tripType", tripType);
        }
        else if (tripType.equals(TemConstants.TEMTripTypes.DOMESTIC)){
            criteria.addNotEqualTo("tripType", TemConstants.TEMTripTypes.INTERNATIONAL);
        }
        Query query = QueryFactory.newQuery(PrimaryDestination.class, criteria,true);
        List<PrimaryDestination> results = (List<PrimaryDestination>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return results;
    }

    @Override
    public List findDefaultPrimaryDestinations(Class clazz, String countryCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.PER_DIEM_COUNTRY_STATE, countryCode);
        Criteria likeCriteria = new Criteria();
        likeCriteria.addLike(TemPropertyConstants.PER_DIEM_NAME + (clazz.getSimpleName().equals(TemConstants.PRIMARY_DESTINATION_CLASS_NAME)?"Name":""), "%" + TemConstants.OTHER_PRIMARY_DESTINATION+ "%");
        criteria.addAndCriteria(likeCriteria);

        Query query = QueryFactory.newQuery(clazz, criteria,true);
        List results = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        if (results.size() > 0){
            for (Object object : results){
                if (object instanceof PrimaryDestination){
                    PrimaryDestination temp = (PrimaryDestination) object;
                    temp.setPrimaryDestinationName(temp.getPrimaryDestinationName().replace("[", "").replace("]", ""));
                }
                else if (object instanceof PerDiem){
                    PerDiem temp = (PerDiem) object;
                    temp.setPrimaryDestination(temp.getPrimaryDestination().replace("[", "").replace("]", ""));
                }
            }
        }
        else{
            criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.PER_DIEM_COUNTRY_STATE, TemConstants.ALL_STATES);
            query = QueryFactory.newQuery(clazz, criteria,true);
            results = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        }

        return results;
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
        documents.addAll((Collection<TravelAuthorizationDocument>)getTravelDocumentsByHeaderStatus(TravelAuthorizationDocument.class, statusCode, immediatesOnly, TemPropertyConstants.ADVANCE_TRAVEL_PAYMENT));
        documents.addAll((Collection<TravelAuthorizationAmendmentDocument>)getTravelDocumentsByHeaderStatus(TravelAuthorizationAmendmentDocument.class, statusCode, immediatesOnly, TemPropertyConstants.ADVANCE_TRAVEL_PAYMENT));
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

}
