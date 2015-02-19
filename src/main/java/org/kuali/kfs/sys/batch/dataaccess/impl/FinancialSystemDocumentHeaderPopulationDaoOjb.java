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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.dataaccess.FinancialSystemDocumentHeaderPopulationDao;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeaderMissingFromWorkflow;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Base implementation of the FinancialSystemDocumentHeaderPopulationDao DAO
 */
public class FinancialSystemDocumentHeaderPopulationDaoOjb extends PlatformAwareDaoBaseOjb implements FinancialSystemDocumentHeaderPopulationDao {

    /**
     *
     * @see org.kuali.kfs.sys.batch.dataaccess.FinancialSystemDocumentHeaderPopulationDao#countTotalFinancialSystemDocumentHeadersToProcess()
     */
    @Override
    public int countTotalFinancialSystemDocumentHeadersToProcess() {
        Criteria c = buildFinancialSystemDocumentHeaderCriteria();
        QueryByCriteria query = QueryFactory.newQuery(FinancialSystemDocumentHeader.class, c);
        final int numOfFSDocHeaders = getPersistenceBrokerTemplate().getCount(query);
        return numOfFSDocHeaders;
    }

    /**
     * Reads in a set of FinancialSystemDocumentHeader records, limited by the indices, ordered by document number
     * @see org.kuali.kfs.sys.batch.dataaccess.FinancialSystemDocumentHeaderPopulationDao#getFinancialSystemDocumentHeadersForBatch(int, int)
     */
    @Override
    public Collection<FinancialSystemDocumentHeader> getFinancialSystemDocumentHeadersForBatch(int batchStartIndex, int batchEndIndex) {
        Criteria c = buildFinancialSystemDocumentHeaderCriteria();
        QueryByCriteria query = QueryFactory.newQuery(FinancialSystemDocumentHeader.class, c);
        query.setStartAtIndex(batchStartIndex);
        query.setEndAtIndex(batchEndIndex);
        query.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER); // roughly try to process newer documents first

        Collection<FinancialSystemDocumentHeader> documentHeaders = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return documentHeaders;
    }

    /**
     * @return the criteria which will be used to variously find all remaining-to-be-processed FinancialSystemDocumentHeader records
     */
    protected Criteria buildFinancialSystemDocumentHeaderCriteria() {
        Criteria c = new Criteria();
        c.addIsNull(KFSPropertyConstants.INITIATOR_PRINCIPAL_ID);
        c.addNotIn(KFSPropertyConstants.DOCUMENT_NUMBER, buildFinancialSystemDocumentHeadersWithNoWorkflowHeadersSubQuery());
        return c;
    }

    /**
     * @return a query which returns all data objects of class FinancialSystemDocumentHeaderMissingFromWorkflow
     */
    protected Query buildFinancialSystemDocumentHeadersWithNoWorkflowHeadersSubQuery() {
        ReportQueryByCriteria query = QueryFactory.newReportQuery(FinancialSystemDocumentHeaderMissingFromWorkflow.class, new Criteria());
        query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        return query;
    }
}
