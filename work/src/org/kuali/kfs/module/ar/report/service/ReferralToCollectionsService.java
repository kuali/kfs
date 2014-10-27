/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Methods to help Referral to Collections reports and document
 */
public interface ReferralToCollectionsService {
    /**
     * This helper method returns a list of award lookup results based on the referral to collections lookup
     *
     * @param invoices The list of invoices.
     * @return Returns the list of ROC lookup result object.
     */
    public Collection<ReferralToCollectionsLookupResult> getPopulatedReferralToCollectionsLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices);
}