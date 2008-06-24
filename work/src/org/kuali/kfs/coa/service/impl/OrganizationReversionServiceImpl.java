/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.dataaccess.OrganizationReversionDao;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.impl.GenericOrganizationReversionCategory;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * 
 * This service implementation is the default implementation of the OrganizationReversion service that is delivered with Kuali.
 */

@NonTransactional
public class OrganizationReversionServiceImpl implements OrganizationReversionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionServiceImpl.class);

    private OrganizationReversionDao organizationReversionDao;

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String orgCode) {
        LOG.debug("getByPrimaryId() started");
        return organizationReversionDao.getByPrimaryId(fiscalYear, chartCode, orgCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getCategories()
     */
    public Map<String, OrganizationReversionCategoryLogic> getCategories() {
        LOG.debug("getCategories() started");

        Map<String, OrganizationReversionCategoryLogic> categories = new HashMap<String, OrganizationReversionCategoryLogic>();

        Collection cats = organizationReversionDao.getCategories();

        for (Iterator iter = cats.iterator(); iter.hasNext();) {
            OrganizationReversionCategory orc = (OrganizationReversionCategory) iter.next();

            String categoryCode = orc.getOrganizationReversionCategoryCode();

            Map<String, OrganizationReversionCategoryLogic> beanMap = SpringContext.getBeansOfType(OrganizationReversionCategoryLogic.class);
            if (beanMap.containsKey("gl" + categoryCode + "OrganizationReversionCategory")) {
                LOG.info("Found Organization Reversion Category Logic for gl" + categoryCode + "OrganizationReversionCategory");
                categories.put(categoryCode, beanMap.get("gl" + categoryCode + "OrganizationReversionCategory"));
            }
            else {
                LOG.info("No Organization Reversion Category Logic for gl" + categoryCode + "OrganizationReversionCategory; using generic");
                GenericOrganizationReversionCategory cat = SpringContext.getBean(GenericOrganizationReversionCategory.class);
                cat.setCategoryCode(categoryCode);
                cat.setCategoryName(orc.getOrganizationReversionCategoryName());
                categories.put(categoryCode, (OrganizationReversionCategoryLogic) cat);
            }
        }
        return categories;
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getCategoryList()
     */
    public List<OrganizationReversionCategory> getCategoryList() {
        LOG.debug("getCategoryList() started");

        return organizationReversionDao.getCategories();
    }

    /**
     * 
     * This method injects the OrganizationReversionDao
     * @param orgDao
     */
    public void setOrganizationReversionDao(OrganizationReversionDao orgDao) {
        organizationReversionDao = orgDao;
    }
}
