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
package org.kuali.module.chart.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.dao.OrganizationReversionDao;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.module.gl.service.impl.orgreversion.GenericOrganizationReversionCategory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganizationReversionServiceImpl implements OrganizationReversionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionServiceImpl.class);

    private OrganizationReversionDao organizationReversionDao;
    private KualiConfigurationService kualiConfigurationService;
    private static final String ORDERING_NUMBER_RULE_SEPERATOR = "_";

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

            Map<String, OrganizationReversionCategoryLogic> beanMap = SpringContext.getBeansOfType(OrganizationReversionCategoryLogic.class);
            if (beanMap.containsKey("gl" + categoryCode + "OrganizationReversionCategory")) {
                LOG.info("Found Organization Reversion Category Logic for gl"+categoryCode+"OrganizationReversionCategory");
                categories.put(categoryCode, beanMap.get("gl" + categoryCode + "OrganizationReversionCategory"));
            } else {
                LOG.info("No Organization Reversion Category Logic for gl" + categoryCode + "OrganizationReversionCategory; using generic");
                GenericOrganizationReversionCategory cat = SpringContext.getBean(GenericOrganizationReversionCategory.class);
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

    /**
     * @see org.kuali.module.chart.service.OrganizationReversionService#getBeginningOfYearSelectionRules()
     */
    public Map<Integer, Parameter> getBeginningOfYearSelectionRules() {
        Map<Integer, Parameter> rules = this.getEndOfYearSelectionRules();
        for (Entry<Integer, Parameter> entry: rules.entrySet()) {
            Parameter param = entry.getValue();
            param.setParameterValue(param.getParameterValue().replaceAll("account\\.", "priorYearAccount."));
        }
        return rules;
    }

    /**
     * @see org.kuali.module.chart.service.OrganizationReversionService#getEndOfYearSelectionRules()
     */
    public Map<Integer, Parameter> getEndOfYearSelectionRules() {
        Map<String, Parameter> rules = kualiConfigurationService.getParametersByDetailTypeAsMap(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ORGANIZATION_REVERSION );
                
        Map<Integer, Parameter> parsedRules = new TreeMap<Integer, Parameter>();
        // get the selection rule parameters
        for(Entry<String, Parameter> entry : rules.entrySet()) {
            if ( entry.getKey().startsWith("SELECTION") ) {
                parsedRules.put(
                        new Integer( StringUtils.substringAfter(entry.getKey(), ORDERING_NUMBER_RULE_SEPERATOR) ),
                        entry.getValue());
            }
        }
        return parsedRules;
    }

    public void setOrganizationReversionDao(OrganizationReversionDao orDao) {
        organizationReversionDao = orDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }
}
