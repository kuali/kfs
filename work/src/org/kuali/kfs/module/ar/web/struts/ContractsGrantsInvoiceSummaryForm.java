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

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Form class for Contracts Grants Invoice Summary.
 */
public class ContractsGrantsInvoiceSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<ContractsGrantsInvoiceLookupResult> contractsGrantsInvoiceLookupResults;
    private boolean awardInvoicedInd;

    /**
     * Initialize contractsGrantsInvoiceLookupResults and awardInvoicedInd.
     */
    public ContractsGrantsInvoiceSummaryForm() {
        contractsGrantsInvoiceLookupResults = new ArrayList<ContractsGrantsInvoiceLookupResult>();
        awardInvoicedInd = false;
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
