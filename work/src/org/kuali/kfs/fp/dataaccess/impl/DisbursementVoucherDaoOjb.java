/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.dao.DisbursementVoucherDao;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;

public class DisbursementVoucherDaoOjb extends PlatformAwareDaoBaseOjb implements DisbursementVoucherDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDaoOjb.class);

    public void save(DisbursementVoucherDocument document) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(document);
    }

    /**
     * @see org.kuali.module.financial.dao.DisbursementVoucherDao#getDocument(java.lang.String)
     */
    public DisbursementVoucherDocument getDocument(String fdocNbr) {
        LOG.debug("getDocument() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", fdocNbr);

        return (DisbursementVoucherDocument)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DisbursementVoucherDocument.class,criteria));
    }

    /**
     * @see org.kuali.module.financial.dao.DisbursementVoucherDao#getDocumentsByHeaderStatus(java.lang.String)
     */
    public Collection getDocumentsByHeaderStatus(String statusCode) {
        LOG.debug("getDocumentsByHeaderStatus() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", statusCode);
        criteria.addEqualTo("disbVchrPaymentMethodCode", DisbursementVoucherRuleConstants.PAYMENT_METHOD_CHECK);

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DisbursementVoucherDocument.class,criteria));
    }

    /**
     * @see org.kuali.module.financial.dao.DisbursementVoucherDao#getPayee(java.lang.String)
     */
    public Payee getPayee(String payeeId) {
        LOG.debug("getPayee() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("payeeIdNumber", payeeId);

        return (Payee)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Payee.class,criteria));
    }
}
