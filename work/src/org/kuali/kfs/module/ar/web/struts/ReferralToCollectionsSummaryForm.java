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
package org.kuali.kfs.module.ar.web.struts;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import java.util.ArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Referral To Collections Summary.
 */
public class ReferralToCollectionsSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<ReferralToCollectionsLookupResult> referralToCollectionsLookupResults;
    private boolean awardInvoicedInd;

    /**
     * Default constructor. Initializes referralToCollectionsLookupResults and awardInvoicedInd.
     */
    public ReferralToCollectionsSummaryForm() {
        referralToCollectionsLookupResults = new ArrayList<ReferralToCollectionsLookupResult>();
        awardInvoicedInd = false;
    }

    /**
     * Gets the lookupResultsSequenceNumber attribute.
     *
     * @return Returns the lookupResultsSequenceNumber attribute.
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * Sets the lookupResultsSequenceNumber attribute.
     *
     * @param lookupResultsSequenceNumber The lookup results sequence number to set.
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * Gets the referralToCollectionsLookupResults attribute.
     *
     * @return Returns the referralToCollectionsLookupResults collection.
     */
    public Collection<ReferralToCollectionsLookupResult> getReferralToCollectionsLookupResults() {
        return referralToCollectionsLookupResults;
    }

    /**
     * Sets the referralToCollectionsLookupResults attribute.
     *
     * @param referralToCollectionsLookupResults The referralToCollectionsLookupResults list to set.
     */
    public void setReferralToCollectionsLookupResults(Collection<ReferralToCollectionsLookupResult> referralToCollectionsLookupResults) {
        this.referralToCollectionsLookupResults = referralToCollectionsLookupResults;
    }

    /**
     * Gets the referralToCollectionsLookupResult object by index.
     *
     * @param index The index of the object to fetch.
     * @return Return the object from list.
     */
    public ReferralToCollectionsLookupResult getReferralToCollectionsLookupResult(int index) {
        ReferralToCollectionsLookupResult referralToCollectionsLookupResult = ((List<ReferralToCollectionsLookupResult>) this.getReferralToCollectionsLookupResults()).get(index);
        return referralToCollectionsLookupResult;
    }

    /**
     * Gets the awardInvoicedInd attribute.
     *
     * @return Returns the awardInvoicedInd.
     */
    public boolean isAwardInvoicedInd() {
        return awardInvoicedInd;
    }

    /**
     * Sets the awardInvoicedInd attribute.
     *
     * @param awardInvoicedInd The awardInvoicedInd to set.
     */
    public void setAwardInvoicedInd(boolean awardInvoicedInd) {
        this.awardInvoicedInd = awardInvoicedInd;
    }
}
