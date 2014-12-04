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
package org.kuali.kfs.module.ar.report.service;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;

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

    /**
     * Populates fields and detail lines on the Referral to Collections document with the given invoices
     * @param rcDoc the Referral to Collections document to populate
     * @param invoices the invoices to populate the Referral to Collections document with
     */
    public void populateReferralToCollectionsDocumentWithInvoices(ReferralToCollectionsDocument rcDoc, Collection<ContractsGrantsInvoiceDocument> invoices);
}
