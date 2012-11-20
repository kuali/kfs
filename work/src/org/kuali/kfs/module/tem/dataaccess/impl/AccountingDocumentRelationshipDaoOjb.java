/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.OjbCollectionAware;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountingDocumentRelationshipDaoOjb extends PlatformAwareDaoBaseOjb implements AccountingDocumentRelationshipDao, OjbCollectionAware {

    private static final Logger LOG = Logger.getLogger(AccountingDocumentRelationshipDaoOjb.class);

    @Override
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationshipByDocumentNumber(String value) {
        return findAccountingDocumentRelationshipByDocumentNumber(null, value);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao#findAccountingDocumentRelationshipByDocumentNumber(java.lang.String,
     *      java.lang.String) This method finds all the AccountingDocumentRelationship using the attribute parameter. If the
     *      attribute is set to null, the lookup will be done on both the @link {@link AccountingDocumentRelationship#DOC_NBR} and
     *      {@link AccountingDocumentRelationship#NEXT_DOC_NBR} using an OR criteria combination.
     */
    @Override
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationshipByDocumentNumber(String attribute, String value) {
        if (value != null) {
            Criteria c = new Criteria();
            if (attribute != null) {
                c.addEqualTo(attribute, value);
            }
            else {
                c.addEqualTo(AccountingDocumentRelationship.DOC_NBR, value);

                Criteria c2 = new Criteria();
                c2.addEqualTo(AccountingDocumentRelationship.REL_DOC_NBR, value);

                c.addOrCriteria(c2);
            }

            return find(c);
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao#findAccountingDocumentRelationship(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     *      This method finds all the AccountingDocumentRelationship using the AccountingDocumentRelationship example.
     */
    @Override
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationship(AccountingDocumentRelationship adr) {
        if (adr != null) {
            Criteria c = new Criteria();
            if (adr.getId() != null) {
                c.addEqualTo(AccountingDocumentRelationship.ID, adr.getId());
            }
            if (adr.getDocumentNumber() != null) {
                c.addEqualTo(AccountingDocumentRelationship.DOC_NBR, adr.getDocumentNumber());
            }
            if (adr.getRelDocumentNumber() != null) {
                c.addEqualTo(AccountingDocumentRelationship.REL_DOC_NBR, adr.getRelDocumentNumber());
            }

            return find(c);
        }

        return null;
    }

    private List<AccountingDocumentRelationship> find(Criteria c) {
        LOG.debug("Creating query for type AccountingDocumentRelationship.class using criteria " + c);
        final Query query = QueryFactory.newQuery(AccountingDocumentRelationship.class, c);
        List<AccountingDocumentRelationship> results = (List<AccountingDocumentRelationship>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return results;
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao#save(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     *      This method saves an accountingDocumentRelationship
     */
    @Override
    public void save(AccountingDocumentRelationship accountingDocumentRelationship) {
        if (accountingDocumentRelationship.getDocumentNumber() != null &&
                accountingDocumentRelationship.getRelDocumentNumber() != null &&
                !accountingDocumentRelationship.getDocumentNumber().equals(accountingDocumentRelationship.getRelDocumentNumber())) {
            accountingDocumentRelationship.refresh();

            // Check if accountingDocumentRelationship already exists. Skip save and set accountingDocumentRelationship if it does exist.
            List<AccountingDocumentRelationship> adrList = findAccountingDocumentRelationship(accountingDocumentRelationship);
            if (!adrList.isEmpty()) {
                if (adrList.size() > 1) {
                    LOG.error("Found multiple AccountingDocumentRelationships with the same data. This should never happen.");
                }

                return;
            }

            // Check if accountingDocumentRelationship has been defined somewhere else.
            adrList = findAccountingDocumentRelationship(new AccountingDocumentRelationship(accountingDocumentRelationship.getRelDocumentNumber(), accountingDocumentRelationship.getDocumentNumber()));
            if (!adrList.isEmpty()) {
                if (adrList.size() > 1) {
                    LOG.error("Found multiple AccountingDocumentRelationships with the same data. This should never happen.");
                }

                return;
            }

            getPersistenceBrokerTemplate().store(accountingDocumentRelationship);
        }
        else {
            LOG.warn("Bad accountingDocumentRelationship. " + accountingDocumentRelationship.toStringMapper());
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao#delete(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     *      This method deletes an accountingDocumentRelationship
     */
    @Override
    public void delete(AccountingDocumentRelationship accountingDocumentRelationship) {
        accountingDocumentRelationship.refresh();
        getPersistenceBrokerTemplate().delete(accountingDocumentRelationship);
    }

}
