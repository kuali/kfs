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
package org.kuali.kfs.module.ar.document.web.struts;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

/**
 * Form class for Collection Activity Document.
 */
public class ContractsGrantsCollectionActivityDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    // Indicates which result set we are using when refreshing/returning from a multi-value lookup.
    protected String lookupResultsSequenceNumber;
    // a selected proposal number, if we are supposed to pre-fill the document
    protected String selectedProposalNumber;

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getSelectedProposalNumber() {
        return selectedProposalNumber;
    }

    public void setSelectedProposalNumber(String selectedProposalNumber) {
        this.selectedProposalNumber = selectedProposalNumber;
    }

    /**
     * This method gets the collection activity document
     *
     * @return the collection activity document
     */
    public ContractsGrantsCollectionActivityDocument getCollectionActivityDocument() {
        return (ContractsGrantsCollectionActivityDocument) getDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_COLLECTION_ACTIVTY;
    }
}
