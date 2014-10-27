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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for ContractsGrantsInvoiceDocumentDao DAO.
 */
public class ContractsGrantsInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements ContractsGrantsInvoiceDocumentDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria, true));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(java.util.Map,
     *      String) Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        Criteria referralNull = new Criteria();
        Criteria referralOutside = new Criteria();
        Criteria subCriteria = new Criteria();


        referralNull.addIsNull(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.REFERRAL_TYPE);
        referralOutside.addNotEqualTo(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.REFERRAL_TYPE, outsideColAgencyCodeToExclude);

        subCriteria.addOrCriteria(referralNull);
        subCriteria.addOrCriteria(referralOutside);

        criteria.addAndCriteria(subCriteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     *      Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByProposalNumber(Long proposalNumber) {
        if (proposalNumber == null) {
            throw new IllegalArgumentException("Cannot find contracts and grants invoices for blank proposal number");
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumber);

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);
        criteria.addIsNull(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);

        Criteria subCri = new Criteria();
        subCri.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        subCri.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);
        subCri.addNotNull(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);
        ReportQueryByCriteria errorCorrectedDocumentsQuery = new ReportQueryByCriteria(ContractsGrantsInvoiceDocument.class, new String[] { KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER }, subCri);
        criteria.addNotIn(KFSPropertyConstants.DOCUMENT_NUMBER, errorCorrectedDocumentsQuery);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getOpenAndFinalInvoicesByCustomerNumber(java.lang.String)
     *      Retrieve CG Invoices that are open and final, with param customer number.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCustomerNumber(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, Boolean.TRUE);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

}
