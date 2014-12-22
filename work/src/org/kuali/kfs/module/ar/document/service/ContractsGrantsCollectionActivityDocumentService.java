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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Service class for Collection Activity Document.
 */
public interface ContractsGrantsCollectionActivityDocumentService {

    /**
     * Creates and saves new collection events for the Collection Activity Document.
     *
     * @param colActDoc The Collection Activity Document object.
     */
    public void createAndSaveCollectionEvents(ContractsGrantsCollectionActivityDocument colActDoc);

    /**
     * Retrieves the award by given proposal number.
     *
     * @param proposalNumber The proposal number of award.
     * @return Returns the award object.
     */
    public ContractsAndGrantsBillingAward retrieveAwardByProposalNumber(Long proposalNumber);

    /**
     * To retrieve the payment amount by given document number.
     *
     * @param documentNumber The invoice number of the document.
     * @return Returns the total payment amount.
     */
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber);
}
