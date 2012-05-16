/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.impl.GenericOrganizationReversionCategory;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 *
 * This service implementation is the default implementation of the OrganizationReversion service that is delivered with Kuali.
 */

@NonTransactional
public class OrganizationReversionServiceImpl implements OrganizationReversionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String orgCode) {
        Map<String, Object> keys = new HashMap<String, Object>(3);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        keys.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationReversion.class, keys);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getCategories()
     */
    @Override
    public Map<String, OrganizationReversionCategoryLogic> getCategories() {
        List<OrganizationReversionCategory> cats = getCategoryList();
        Map<String, OrganizationReversionCategoryLogic> orgReversionCategoryLogicMap = SpringContext.getBeansOfType(OrganizationReversionCategoryLogic.class);
        Map<String, OrganizationReversionCategoryLogic> categories = new HashMap<String, OrganizationReversionCategoryLogic>(cats.size());

        for ( OrganizationReversionCategory orc : cats ) {
            String categoryCode = orc.getOrganizationReversionCategoryCode();
            OrganizationReversionCategoryLogic cat = null;
            String key = "gl" + categoryCode + "OrganizationReversionCategory";
            if (orgReversionCategoryLogicMap.containsKey(key)) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Found Organization Reversion Category Logic for " + key);
                }
                cat = orgReversionCategoryLogicMap.get(key);
            } else {
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("No Organization Reversion Category Logic for " + key + "; using generic");
                }
                // This is a prototype bean - a new instance is pulled every time this is called
                cat = SpringContext.getBean(GenericOrganizationReversionCategory.class);
                ((GenericOrganizationReversionCategory) cat).setCategoryCode(categoryCode);
                ((GenericOrganizationReversionCategory) cat).setCategoryName(orc.getOrganizationReversionCategoryName());
            }
            categories.put(categoryCode, cat);
        }
        return categories;
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getCategoryList()
     */
    @Override
    public List<OrganizationReversionCategory> getCategoryList() {
        return new ArrayList<OrganizationReversionCategory>(
                businessObjectService.findMatchingOrderBy(OrganizationReversionCategory.class, Collections.singletonMap(KFSPropertyConstants.ACTIVE, true), "organizationReversionSortCode", true)
                );
    }


    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#getOrganizationReversionDetaiFromSystemParameters()
     */
    @Override
    public String getOrganizationReversionDetaiFromSystemParameters() {
        return parameterService.getParameterValueAsString(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.UNALLOC_OBJECT_CODE_PARM);
    }


    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#isCategoryActive(java.lang.String)
     */
    @Override
    public boolean isCategoryActive(String categoryCode) {
        OrganizationReversionCategory category = businessObjectService.findBySinglePrimaryKey(OrganizationReversionCategory.class, categoryCode);
        if (category == null) {
            return false;
        }
        return category.isActive();
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionService#isCategoryActiveByName(java.lang.String)
     */
    @Override
    public boolean isCategoryActiveByName(String categoryName) {
        Collection<OrganizationReversionCategory> categories = businessObjectService.findMatching(OrganizationReversionCategory.class, Collections.singletonMap("organizationReversionCategoryName", categoryName));
        for ( OrganizationReversionCategory category : categories ) {
            if ( category.isActive() ) {
                return true;
            }
        }
        return false;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }
}
