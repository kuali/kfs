/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityType;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Document class for Referral To Collections TD.
 */
public class ReferralToCollectionsDocument extends FinancialSystemTransactionalDocumentBase {

    private String agencyNumber;
    private String customerNumber;
    private String agencyFullName;
    private String customerName;
    private String customerTypeCode;
    private String customerTypeDescription;
    private String collectionStatusCode;
    private String referralTypeCode;

    private List<ReferralToCollectionsDetail> referralToCollectionsDetails;

    /**
     * Default constructor.
     */
    public ReferralToCollectionsDocument() {
        super();
        referralToCollectionsDetails = new ArrayList<ReferralToCollectionsDetail>();
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agency number.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber The ageny number to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the agencyFullName attribute.
     *
     * @return Returns the agency full name.
     */
    public String getAgencyFullName() {
        return agencyFullName;
    }

    /**
     * Sets the agencyFullName attribute.
     *
     * @param agencyFullName The agency full name to set.
     */
    public void setAgencyFullName(String agencyFullName) {
        this.agencyFullName = agencyFullName;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerName attribute.
     *
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute.
     *
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerTypeCode attribute.
     *
     * @return Returns the customerTypeCode.
     */
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    /**
     * Sets the customerTypeCode attribute.
     *
     * @param customerTypeCode The customerTypeCode to set.
     */
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    /**
     * Gets the collectionStatusCode attribute.
     *
     * @return Returns the collectionStatusCode.
     */
    public String getCollectionStatusCode() {
        return collectionStatusCode;
    }

    /**
     * Sets the collectionStatusCode attribute.
     *
     * @param collectionStatusCode The collectionStatusCode to set.
     */
    public void setCollectionStatusCode(String collectionStatusCode) {
        this.collectionStatusCode = collectionStatusCode;
    }

    /**
     * Gets the referralTypeCode attribute.
     *
     * @return Returns the referralTypeCode attribute.
     */
    public String getReferralTypeCode() {
        return referralTypeCode;
    }

    /**
     * Sets the referralTypeCode attribute.
     *
     * @param referralTypeCode The referral type to set.
     */
    public void setReferralTypeCode(String referralTypeCode) {
        this.referralTypeCode = referralTypeCode;
    }

    /**
     * Gets the list of ReferralToCollectionsDetail object.
     *
     * @return Returns the referralToCollectionsDetails list.
     */
    public List<ReferralToCollectionsDetail> getReferralToCollectionsDetails() {
        return referralToCollectionsDetails;
    }

    /**
     * Sets the referralToCollectionsDetails list.
     *
     * @param referralToCollectionsDetails The referralToCollectionsDetails list to set.
     */
    public void setReferralToCollectionsDetails(List<ReferralToCollectionsDetail> referralToCollectionsDetails) {
        this.referralToCollectionsDetails = referralToCollectionsDetails;
    }

    /**
     * Gets the object of ReferralToCollectionsDetail from list.
     *
     * @param index The index of object.
     * @return Returns the ReferralToCollectionsDetail object.
     */
    public ReferralToCollectionsDetail getReferralToCollectionsDetail(int index) {
        if (index >= referralToCollectionsDetails.size()) {
            for (int i = referralToCollectionsDetails.size(); i <= index; i++) {
                referralToCollectionsDetails.add(new ReferralToCollectionsDetail());
            }
        }
        return referralToCollectionsDetails.get(index);
    }

    /**
     * Deletes the ReferralToCollectionsDetail from list.
     *
     * @param index The index of object to delete.
     */
    public void deleteReferralToCollectionsDetail(int index) {
        ReferralToCollectionsDetail rcDetail = referralToCollectionsDetails.remove(index);
    }

    /**
     * @return Returns true if Document is final.
     */
    public boolean isFinal() {
        return isApproved();
    }

    /**
     * @return Returns true if Document is approved.
     */
    public boolean isApproved() {
        return getDocumentHeader().getWorkflowDocument().isApproved();
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        Person authorUniversal1 = GlobalVariables.getUserSession().getPerson();

        // If the document is final, do the required changes.
        if (getDocumentHeader().getWorkflowDocument().isFinal()) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            List<ReferralToCollectionsDetail> rocDetails = this.getReferralToCollectionsDetails();

            if (ObjectUtils.isNotNull(rocDetails) && !rocDetails.isEmpty()) {
                for (ReferralToCollectionsDetail rcDetail : rocDetails) {
                    ContractsGrantsInvoiceDocument invoice = rcDetail.getInvoiceDocument();

                    // Set the referral type of invoices
                    if (ObjectUtils.isNotNull(invoice)) {
                        invoice.setReferralTypeCode(this.getReferralTypeCode());
                        invoice.setFinalDispositionCode(rcDetail.getFinalDispositionCode());
                        boService.save(invoice);

                        Map<String, String> fieldValues = new HashMap<String, String>();
                        fieldValues.put(ArPropertyConstants.CollectionActivityTypeFields.REFERRAL_INDICATOR, "true");
                        fieldValues.put(ArPropertyConstants.CollectionActivityTypeFields.ACTIVE, "true");
                        List<CollectionActivityType> activityTypes = (List<CollectionActivityType>) boService.findMatching(CollectionActivityType.class, fieldValues);
                        String activityCode = CollectionUtils.isNotEmpty(activityTypes) ? activityTypes.get(0).getActivityCode() : null;

                        if (ObjectUtils.isNotNull(activityCode)) {
                            // create the event
                            Event event = new Event();
                            int lastEventCode = this.getFinalEventsCount(invoice.getEvents()) + 1;
                            event.setInvoiceNumber(invoice.getDocumentNumber());
                            String eventCode = event.getInvoiceNumber() + "-" + String.format("%03d", lastEventCode);
                            event.setEventCode(eventCode);
                            event.setActivityCode(activityCode);
                            event.setActivityDate(new java.sql.Date(new Date().getTime()));
                            ReferralType refType = boService.findBySinglePrimaryKey(ReferralType.class, invoice.getReferralTypeCode());
                            event.setActivityText(ObjectUtils.isNotNull(refType) ? refType.getDescription() : invoice.getReferralTypeCode());

                            // Set author and posted date
                            final Date now = SpringContext.getBean(DateTimeService.class).getCurrentDate();
                            event.setPostedDate(now);
                            if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
                                Person authorUniversal = GlobalVariables.getUserSession().getPerson();
                                event.setUserPrincipalId(authorUniversal.getPrincipalId());
                                event.setUser(authorUniversal);
                            }
                            boService.save(event);
                        }
                    }

                }
            }
        }
    }

    /**
     * Gets the number of final events in list.
     *
     * @param events The list of events.
     * @return Returns the number of final events.
     */
    private int getFinalEventsCount(List<Event> events) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(events)) {
            for (Event event : events) {
                if (ObjectUtils.isNull(event.getEventRouteStatus()) || event.getEventRouteStatus().equals(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }
}
