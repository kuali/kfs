/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Contracts Grants Invoice Summary.
 */
public class DunningLetterDistributionSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<DunningLetterDistributionLookupResult> dunningLetterDistributionLookupResults;
    private boolean dunningLetterNotSent;

    /**
     * Initialize contractsGrantsInvoiceLookupResults and awardInvoiced.
     */
    public DunningLetterDistributionSummaryForm() {
        dunningLetterDistributionLookupResults = new ArrayList<DunningLetterDistributionLookupResult>();
        dunningLetterNotSent = false;
    }

    /**
     * Gets the collection lookupResultsSequenceNumber.
     *
     * @return Returns the lookupResultsSequenceNumber.
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * Sets the lookupResultsSequenceNumber attribute.
     *
     * @param lookupResultsSequenceNumber The lookupResultsSequenceNumber to set.
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * Gets the collection dunningLetterDistributionLookupResults.
     *
     * @return Returns the collection dunningLetterDistributionLookupResults.
     */
    public Collection<DunningLetterDistributionLookupResult> getDunningLetterDistributionLookupResults() {
        return dunningLetterDistributionLookupResults;
    }

    /**
     * Sets the dunningLetterDistributionLookupResults attribute.
     *
     * @param dunningLetterDistributionLookupResults The dunningLetterDistributionLookupResults collection to set.
     */
    public void setDunningLetterDistributionLookupResults(Collection<DunningLetterDistributionLookupResult> dunningLetterDistributionLookupResults) {
        this.dunningLetterDistributionLookupResults = dunningLetterDistributionLookupResults;
    }

    /**
     * Gets the dunningLetterDistributionLookupResult from the specific index.
     *
     * @param index
     * @return Returns the dunningLetterDistributionLookupResult at given index from the list.
     */
    public DunningLetterDistributionLookupResult getDunningLetterDistributionLookupResult(int index) {
        DunningLetterDistributionLookupResult dunningLetterDistributionLookupResult = ((List<DunningLetterDistributionLookupResult>) getDunningLetterDistributionLookupResults()).get(index);
        return dunningLetterDistributionLookupResult;
    }

    /**
     * Gets the collection dunningLetterNotSent.
     *
     * @return Returns the dunningLetterNotSent.
     */
    public boolean isDunningLetterNotSent() {
        return dunningLetterNotSent;
    }

    /**
     * Sets the dunningLetterNotSent attribute.
     *
     * @param dunningLetterNotSent The dunningLetterNotSent set.
     */
    public void setDunningLetterNotSent(boolean dunningLetterNotSent) {
        this.dunningLetterNotSent = dunningLetterNotSent;
    }


}
