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

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceOnDemandLookupResult;
import java.util.ArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Contracts Grants Invoice On Demand Summary.
 */
public class ContractsGrantsInvoiceOnDemandSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<ContractsGrantsInvoiceOnDemandLookupResult> contractsGrantsInvoiceOnDemandLookupResults;
    private boolean awardInvoicedInd;

    /**
     * Initialize contractsGrantsInvoiceOnDemandLookupResults and awardInvoicedInd.
     */
    public ContractsGrantsInvoiceOnDemandSummaryForm() {
        contractsGrantsInvoiceOnDemandLookupResults = new ArrayList<ContractsGrantsInvoiceOnDemandLookupResult>();
        awardInvoicedInd = false;
    }

    /**
     * @return lookupResultsSequenceNumber
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * Gets the contractsGrantsInvoiceOnDemandLookupResults attribute.
     *
     * @return Returns the contractsGrantsInvoiceOnDemandLookupResults.
     */
    public Collection<ContractsGrantsInvoiceOnDemandLookupResult> getContractsGrantsInvoiceOnDemandLookupResults() {
        return contractsGrantsInvoiceOnDemandLookupResults;
    }

    /**
     * Sets the contractsGrantsInvoiceOnDemandLookupResults attribute value.
     *
     * @param contractsGrantsInvoiceOnDemandLookupResults The contractsGrantsInvoiceOnDemandLookupResults to set.
     */
    public void setContractsGrantsInvoiceOnDemandLookupResults(Collection<ContractsGrantsInvoiceOnDemandLookupResult> contractsGrantsInvoiceOnDemandLookupResults) {
        this.contractsGrantsInvoiceOnDemandLookupResults = contractsGrantsInvoiceOnDemandLookupResults;
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
    public ContractsGrantsInvoiceOnDemandLookupResult getContractsGrantsInvoiceOnDemandLookupResult(int index) {
        ContractsGrantsInvoiceOnDemandLookupResult contractsGrantsInvoiceOnDemandLookupResult = ((List<ContractsGrantsInvoiceOnDemandLookupResult>) getContractsGrantsInvoiceOnDemandLookupResults()).get(index);
        return contractsGrantsInvoiceOnDemandLookupResult;
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
     * Sets the awardInvoicedInd attribute value.
     *
     * @param awardInvoicedInd The awardInvoicedInd to set.
     */
    public void setAwardInvoicedInd(boolean awardInvoicedInd) {
        this.awardInvoicedInd = awardInvoicedInd;
    }

}
