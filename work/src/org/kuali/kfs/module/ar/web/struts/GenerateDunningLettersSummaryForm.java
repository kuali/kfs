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

import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Generate Dunning Letters Summary.
 */
public class GenerateDunningLettersSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<GenerateDunningLettersLookupResult> generateDunningLettersLookupResults;
    private boolean dunningLettersGenerated;

    /**
     * Initialize contractsGrantsInvoiceLookupResults and dunningLetterNotSent.
     */
    public GenerateDunningLettersSummaryForm() {
        generateDunningLettersLookupResults = new ArrayList<GenerateDunningLettersLookupResult>();
        dunningLettersGenerated = false;
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
     * Gets the collection generateDunningLettersLookupResults.
     *
     * @return Returns the collection generateDunningLettersLookupResults.
     */
    public Collection<GenerateDunningLettersLookupResult> getGenerateDunningLettersLookupResults() {
        return generateDunningLettersLookupResults;
    }

    /**
     * Sets the generateDunningLettersLookupResults attribute.
     *
     * @param generateDunningLettersLookupResults The generateDunningLettersLookupResults collection to set.
     */
    public void setGenerateDunningLettersLookupResults(Collection<GenerateDunningLettersLookupResult> generateDunningLettersLookupResults) {
        this.generateDunningLettersLookupResults = generateDunningLettersLookupResults;
    }

    /**
     * Gets the dunningLetterDistributionLookupResult from the specific index.
     *
     * @param index
     * @return Returns the dunningLetterDistributionLookupResult at given index from the list.
     */
    public GenerateDunningLettersLookupResult getGenerateDunningLettersLookupResults(int index) {
        GenerateDunningLettersLookupResult generateDunningLettersLookupResult = ((List<GenerateDunningLettersLookupResult>) getGenerateDunningLettersLookupResults()).get(index);
        return generateDunningLettersLookupResult;
    }

    /**
     * Gets the dunningLettersGenerated attribute.
     *
     * @return Returns the dunningLettersGenerated value.
     */
    public boolean isDunningLettersGenerated() {
        return dunningLettersGenerated;
    }

    /**
     * Sets the dunningLettersGenerated attribute.
     *
     * @param dunningLettersGenerated The dunningLettersGenerated value to set.
     */
    public void setDunningLettersGenerated(boolean dunningLettersGenerated) {
        this.dunningLettersGenerated = dunningLettersGenerated;
    }

}
