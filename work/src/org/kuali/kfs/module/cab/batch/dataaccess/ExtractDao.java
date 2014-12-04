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
package org.kuali.kfs.module.cab.batch.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;

public interface ExtractDao {
    /**
     * This method implementation should find all GL Entries matching the parameters listed in the batch parameters
     *
     * @param batchParameters Batch Parameters
     * @return GL Entries
     */
    Collection<Entry> findMatchingGLEntries(BatchParameters batchParameters);


    /**
     * This method implementation should find valid Credit Memo account line changes as per the batch parameters
     *
     * @param batchParameters Batch Parameters
     * @return List of Credit memo account history records
     */
    Collection<CreditMemoAccountRevision> findCreditMemoAccountRevisions(BatchParameters batchParameters);

    /**
     * This method implementation should find valid Payment Request account line changes as per the batch parameters
     *
     * @param batchParameters Batch Parameters
     * @return List of Payment Request account history records
     */
    Collection<PaymentRequestAccountRevision> findPaymentRequestAccountRevisions(BatchParameters batchParameters);

    /**
     * @deprecated This method should not be used as it is not working properly after moving getDocumentsNumbersAwaitingPurchaseOrderOpenStatus
     *             method to the service layer.
     */
    @Deprecated
    Collection<PurchaseOrderAccount> findPreTaggablePOAccounts(BatchParameters batchParameters);

    /**
     * This method implementation should retrieve all eligible pretaggable PO account lines from Purchasing module
     *
     * @param batchParameters Batch Parameters
     * @param docNumbersAwaitingPurchaseOrderStatus Document numbers awaiting PO Open status
     * @return List of pre-taggable purchase order account lines
     */
    Collection<PurchaseOrderAccount> findPreTaggablePOAccounts(BatchParameters batchParameters, List<String> docNumbersAwaitingPurchaseOrderStatus);

}
