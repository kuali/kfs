/*
 * Copyright 2005-2007 The Kuali Foundation
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
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.dao.LookupDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.OjbCollectionAware;

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
     * Try to find {@link TravelerDetail} instances of employees. Employees have a <code>travelerTypeCode</code>
     * for employees. This means they also have valid employment information.
     *
     * @return {@link Collection} of {@linK TravelerDetail} instances
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

        return (Collection<AccountsReceivableCustomer>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    @Override
    public void setLookupDao(final LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    protected LookupDao getLookupDao() {
        return lookupDao;
    }
    
    /**
     * Gets the accountsReceivableModuleService attribute.
     * 
     * @return Returns the accountsReceivableModuleService.
     */
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        
        return accountsReceivableModuleService;
    }

    /**
     * Sets the accountsReceivableModuleService attribute value.
     * 
     * @param accountsReceivableModuleService The accountsReceivableModuleService to set.
     */
    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }    
}
