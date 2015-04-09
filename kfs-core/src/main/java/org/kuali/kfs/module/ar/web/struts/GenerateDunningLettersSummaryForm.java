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
