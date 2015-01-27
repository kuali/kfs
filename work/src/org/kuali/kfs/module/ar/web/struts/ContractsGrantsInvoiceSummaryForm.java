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

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult;

/**
 * Form class for Contracts & Grants Invoice Summary.
 */
public class ContractsGrantsInvoiceSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<ContractsGrantsInvoiceLookupResult> contractsGrantsInvoiceLookupResults;
    private boolean awardInvoiced;

    /**
     * Initialize contractsGrantsInvoiceLookupResults and awardInvoiced.
     */
    public ContractsGrantsInvoiceSummaryForm() {
        contractsGrantsInvoiceLookupResults = new ArrayList<ContractsGrantsInvoiceLookupResult>();
        awardInvoiced = false;
    }

    /**
     * @return lookupResultsSequenceNumber
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * Gets the contractsGrantsInvoiceLookupResults attribute.
     *
     * @return Returns the contractsGrantsInvoiceLookupResults.
     */
    public Collection<ContractsGrantsInvoiceLookupResult> getContractsGrantsInvoiceLookupResults() {
        return contractsGrantsInvoiceLookupResults;
    }

    /**
     * Sets the contractsGrantsInvoiceLookupResults attribute value.
     *
     * @param contractsGrantsInvoiceLookupResults The contractsGrantsInvoiceLookupResults to set.
     */
    public void setContractsGrantsInvoiceLookupResults(Collection<ContractsGrantsInvoiceLookupResult> contractsGrantsInvoiceLookupResults) {
        this.contractsGrantsInvoiceLookupResults = contractsGrantsInvoiceLookupResults;
    }

    /**
     * @param lookupResultsSequenceNumber
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * @param index
     * @return
     */
    public ContractsGrantsInvoiceLookupResult getContractsGrantsInvoiceLookupResult(int index) {
        ContractsGrantsInvoiceLookupResult contractsGrantsInvoiceLookupResult = ((List<ContractsGrantsInvoiceLookupResult>) getContractsGrantsInvoiceLookupResults()).get(index);
        return contractsGrantsInvoiceLookupResult;
    }

    /**
     * Gets the awardInvoiced attribute.
     *
     * @return Returns the awardInvoiced.
     */
    public boolean isAwardInvoiced() {
        return awardInvoiced;
    }

    /**
     * Sets the awardInvoiced attribute value.
     *
     * @param awardInvoiced The awardInvoiced to set.
     */
    public void setAwardInvoiced(boolean awardInvoiced) {
        this.awardInvoiced = awardInvoiced;
    }

}
