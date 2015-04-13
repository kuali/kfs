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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.dataaccess.AccountsReceivableDocumentHeaderDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class AccountsReceivableDocumentHeaderDaoOjb extends PlatformAwareDaoBaseOjb implements AccountsReceivableDocumentHeaderDao {

    @Override
    public Collection getARDocumentHeadersByCustomerNumber(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER, customerNumber);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }

    @Override
    public Collection getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(String customerNumber, String processingChartOfAccountCode, String processingOrganizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER, customerNumber);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_COA_CODE, processingChartOfAccountCode);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_ORGANIZATION_CODE, processingOrganizationCode);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }

    @Override
    public Collection<String> getARDocumentNumbersIncludingHiddenApplicationByCustomerNumber(String customerNumber){
        // get the AR documents by the customer b=number
        List<String> documentNumbers = new ArrayList<String>();
        Criteria criteria1 = new Criteria();
        criteria1.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER,customerNumber);

        //Collection<AccountsReceivableDocumentHeader> arHeadersByCustomer = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria1));
        ReportQueryByCriteria query = new ReportQueryByCriteria(AccountsReceivableDocumentHeader.class, new String[]{KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER}, criteria1, true);
        Iterator results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while(results.hasNext()) {
            Object[] res = (Object[]) results.next();
            documentNumbers.add((String)res[0]);
        }

        // get the payment application documents, which belong to the customer but not in specified in AR_DOC_HDR_T
        if (!documentNumbers.isEmpty()) {
            Set<String> appDocumentNumbers = new HashSet<String>();
            Criteria criteria2 = new Criteria();
            criteria2.addIn("financialDocumentReferenceInvoiceNumber",documentNumbers);
            //Collection<InvoicePaidApplied> invoicePaidAppliedList = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(InvoicePaidApplied.class, criteria2));
            query = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[]{KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER}, criteria2, true);
            results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
            while(results.hasNext()) {
                Object[] res = (Object[])results.next();
                 String documentNumber = (String)res[0];
                if(!documentNumbers.contains(documentNumber)) {
                   documentNumbers.add(documentNumber);
                }
            }
        }

        return documentNumbers;
    }

    @Override
    public Collection<AccountsReceivableDocumentHeader> getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(String customerNumber) {
        Collection<String> documentNumbers = getARDocumentNumbersIncludingHiddenApplicationByCustomerNumber(customerNumber);

        // get the final AR documents
        if (!documentNumbers.isEmpty()) {
            Criteria criteria3 = new Criteria();
            criteria3.addIn(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER,documentNumbers);
            return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria3));
        }
        return new ArrayList<AccountsReceivableDocumentHeader>();
    }
}
