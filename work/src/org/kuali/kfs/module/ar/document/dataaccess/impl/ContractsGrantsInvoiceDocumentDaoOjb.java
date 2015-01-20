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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;

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
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     *      Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getCollectionEligibleContractsGrantsInvoicesByProposalNumber(Long proposalNumber) {
        if (proposalNumber == null) {
            throw new IllegalArgumentException("Cannot find contracts and grants invoices for blank proposal number");
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumber);

        Set<String> successfulDocumentStatuses = new HashSet<>();
        for (DocumentStatus docStatus : DocumentStatus.getStatusesForCategory(DocumentStatusCategory.SUCCESSFUL)) {
            successfulDocumentStatuses.add(docStatus.getCode());
        }

        Set<String> unsuccessfulDocumentStatuses = new HashSet<>();
        for (DocumentStatus docStatus : DocumentStatus.getStatusesForCategory(DocumentStatusCategory.UNSUCCESSFUL)) {
            unsuccessfulDocumentStatuses.add(docStatus.getCode());
        }

        criteria.addIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, successfulDocumentStatuses);
        criteria.addIsNull(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);

        Criteria subCri = new Criteria();
        subCri.addNotIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, unsuccessfulDocumentStatuses);
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
