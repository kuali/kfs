/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * @see AwardDao
 */
public class ContractsGrantsInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements ContractsGrantsInvoiceDocumentDao {

    public Collection<ContractsGrantsInvoiceDocument> getAllOpen() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("openInvoiceIndicator", true);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria);

        Collection<ContractsGrantsInvoiceDocument> customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return customerinvoicedocuments;
    }

    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria);

        Collection<ContractsGrantsInvoiceDocument> customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return customerinvoicedocuments;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getInvoicesByCriteria(org.apache.ojb.broker.query.Criteria)
     *      Retrieve CG Invoices that are open and final, with some addtional criteria passed.
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByCriteria(Criteria criteria) {
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return (Collection<ContractsGrantsInvoiceDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getOpenAndFinalInvoicesByCriteria(org.apache.ojb.broker.query.Criteria)
     *      Retrieve CG Invoices that are open and final, with some addtional criteria passed.
     */
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCriteria(Criteria criteria) {
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return (Collection<ContractsGrantsInvoiceDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getOpenAndFinalInvoicesByCustomerNumber(java.lang.String)
     *      Retrieve CG Invoices that are open and final, with param customer number.
     */
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCustomerNumber(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return (Collection<ContractsGrantsInvoiceDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

}
