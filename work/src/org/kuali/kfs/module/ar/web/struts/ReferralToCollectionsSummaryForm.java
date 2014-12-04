/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Referral To Collections Summary.
 */
public class ReferralToCollectionsSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<ReferralToCollectionsLookupResult> referralToCollectionsLookupResults;

    /**
     * Default constructor. Initializes referralToCollectionsLookupResults.
     */
    public ReferralToCollectionsSummaryForm() {
        referralToCollectionsLookupResults = new ArrayList<ReferralToCollectionsLookupResult>();
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

}
