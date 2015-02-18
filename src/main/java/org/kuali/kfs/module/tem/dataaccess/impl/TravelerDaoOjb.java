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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.dataaccess.TravelerDao;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.util.OjbCollectionAware;

/**
 * This is the data access interface for Travelers.
 *
 */
public class TravelerDaoOjb extends PlatformAwareDaoBaseOjb implements TravelerDao, OjbCollectionAware{

    public static Logger LOG = Logger.getLogger(TravelerDaoOjb.class);

    private static final String CUSTOMER_ADDRESSES_ATTR_PREFIX = "customerAddresses.";

    private LookupDao lookupDao;
    private AccountsReceivableModuleService accountsReceivableModuleService;

    /**
     * Try to find TravelerDetail instances of employees. Employees have a travelerTypeCode
     * for employees. This means they also have valid employment information.
     *
     * @see org.kuali.kfs.module.tem.dataaccess.TravelerDao#findCustomersBy(Map)
     */
    @Override
    public Collection<AccountsReceivableCustomer> findCustomersBy(final Map<String, String> criteria) {
        final Criteria customerCrit = new Criteria();
        final Criteria addressCrit  = new Criteria();

        for (final String key : criteria.keySet()) {
            final String value  = criteria.get(key);
            String newKey       = key;
            BusinessObject obj  = getAccountsReceivableModuleService().createCustomer();
            Criteria crit       = customerCrit;

            if (key.contains(CUSTOMER_ADDRESSES_ATTR_PREFIX)) {
                newKey = key.substring(CUSTOMER_ADDRESSES_ATTR_PREFIX.length());
                crit = addressCrit;
                obj = getAccountsReceivableModuleService().createCustomerAddress();
            }

            LOG.debug("Adding "+ newKey+ "="+ value+ " to criteria");

            LOG.debug("Criteria added successfully "+ getLookupDao().createCriteria(obj, value, newKey, crit));

            LOG.debug("New criteria is "+ crit);
        }

        if (!addressCrit.isEmpty()) {
            LOG.debug("Adding Query with criteria "+ addressCrit);
            customerCrit.addIn("customerNumber", QueryFactory.newReportQuery(getAccountsReceivableModuleService().createCustomerAddress().getClass(), new String[] { "customerNumber" }, addressCrit, false));
        }

        LOG.debug("Creating query with criteria "+ customerCrit);

        final Query query = QueryFactory.newQuery(getAccountsReceivableModuleService().createCustomer().getClass(), customerCrit);

        LOG.debug("Searching for Customers with query "+ query);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    public void setLookupDao(final LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    protected LookupDao getLookupDao() {
        return lookupDao;
    }

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }

        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }
}
