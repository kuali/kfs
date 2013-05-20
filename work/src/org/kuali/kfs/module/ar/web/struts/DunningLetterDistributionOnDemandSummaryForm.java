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

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import java.util.ArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Contracts Grants Invoice On Demand Summary.
 */
public class DunningLetterDistributionOnDemandSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<DunningLetterDistributionOnDemandLookupResult> dunningLetterDistributionOnDemandLookupResults;
    private boolean dunningLetterNotSent;

    /**
     * Initialize contractsGrantsInvoiceOnDemandLookupResults and awardInvoiced.
     */
    public DunningLetterDistributionOnDemandSummaryForm() {
        dunningLetterDistributionOnDemandLookupResults = new ArrayList<DunningLetterDistributionOnDemandLookupResult>();
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
     * Gets the collection dunningLetterDistributionOnDemandLookupResults.
     * 
     * @return Returns the collection dunningLetterDistributionOnDemandLookupResults.
     */
    public Collection<DunningLetterDistributionOnDemandLookupResult> getDunningLetterDistributionOnDemandLookupResults() {
        return dunningLetterDistributionOnDemandLookupResults;
    }

    /**
     * Sets the dunningLetterDistributionOnDemandLookupResults attribute.
     * 
     * @param dunningLetterDistributionOnDemandLookupResults The dunningLetterDistributionOnDemandLookupResults collection to set.
     */
    public void setDunningLetterDistributionOnDemandLookupResults(Collection<DunningLetterDistributionOnDemandLookupResult> dunningLetterDistributionOnDemandLookupResults) {
        this.dunningLetterDistributionOnDemandLookupResults = dunningLetterDistributionOnDemandLookupResults;
    }

    /**
     * Gets the dunningLetterDistributionOnDemandLookupResult from the specific index.
     * 
     * @param index
     * @return Returns the dunningLetterDistributionOnDemandLookupResult at given index from the list.
     */
    public DunningLetterDistributionOnDemandLookupResult getDunningLetterDistributionOnDemandLookupResult(int index) {
        DunningLetterDistributionOnDemandLookupResult dunningLetterDistributionOnDemandLookupResult = ((List<DunningLetterDistributionOnDemandLookupResult>) getDunningLetterDistributionOnDemandLookupResults()).get(index);
        return dunningLetterDistributionOnDemandLookupResult;
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
