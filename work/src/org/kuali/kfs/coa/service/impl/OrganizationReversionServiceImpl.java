/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.dao.OrganizationReversionDao;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.module.gl.service.impl.orgreversion.GenericOrganizationReversionCategory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 */
public class OrganizationReversionServiceImpl implements OrganizationReversionService, BeanFactoryAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionServiceImpl.class);

    private OrganizationReversionDao organizationReversionDao;
    private KualiConfigurationService kualiConfigurationService;
    private BeanFactory beanFactory;

    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationReversionService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String orgCode) {
        LOG.debug("getByPrimaryId() started");
        return organizationReversionDao.getByPrimaryId(fiscalYear, chartCode, orgCode);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationReversionService#getCategories()
     */
    public Map<String, OrganizationReversionCategoryLogic> getCategories() {
        LOG.debug("getCategories() started");

        Map<String, OrganizationReversionCategoryLogic> categories = new HashMap<String, OrganizationReversionCategoryLogic>();

        Collection cats = organizationReversionDao.getCategories();

        for (Iterator iter = cats.iterator(); iter.hasNext();) {
            OrganizationReversionCategory orc = (OrganizationReversionCategory) iter.next();

            String categoryCode = orc.getOrganizationReversionCategoryCode();

            if (beanFactory.containsBean("gl" + categoryCode + "OrganizationReversionCategory")) {
                // We have a custom implementation
                categories.put(categoryCode, (OrganizationReversionCategoryLogic) beanFactory.getBean("gl" + categoryCode + "OrganizationReversionCategory"));
            }
            else {
                // We'll get the generic implementation
                GenericOrganizationReversionCategory cat = (GenericOrganizationReversionCategory) beanFactory.getBean("glGenericOrganizationReversionCategory");
                cat.setCategoryCode(categoryCode);
                cat.setCategoryName(orc.getOrganizationReversionCategoryName());

                categories.put(categoryCode, (OrganizationReversionCategoryLogic) cat);
            }
        }
        return categories;
    }

    public List<OrganizationReversionCategory> getCategoryList() {
        LOG.debug("getCategoryList() started");

        return organizationReversionDao.getCategories();
    }

    public void setOrganizationReversionDao(OrganizationReversionDao orDao) {
        organizationReversionDao = orDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
