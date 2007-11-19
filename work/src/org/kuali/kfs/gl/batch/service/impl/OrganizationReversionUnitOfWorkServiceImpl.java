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
package org.kuali.module.gl.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.gl.bo.OrgReversionUnitOfWork;
import org.kuali.module.gl.bo.OrgReversionUnitOfWorkCategoryAmount;
import org.kuali.module.gl.dao.OrgReversionUnitOfWorkDao;
import org.kuali.module.gl.service.OrgReversionUnitOfWorkService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of OrgReversionUnitOfWorkService
 */
@Transactional
public class OrgReversionUnitOfWorkServiceImpl implements OrgReversionUnitOfWorkService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrgReversionUnitOfWorkServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private OrgReversionUnitOfWorkDao orgReversionUnitOfWorkDao;

    /**
     * This method takes a unit of work retrieved from the persistence store and loads its categories
     * 
     * @param orgRevUnitOfWork org reversion unit of work to load categories for
     * @return the org reversion unit of work with loaded categories
     * @see org.kuali.module.gl.service.OrgReversionUnitOfWorkService#loadCategories(org.kuali.module.gl.bo.OrgReversionUnitOfWork)
     */
    public OrgReversionUnitOfWork loadCategories(OrgReversionUnitOfWork orgRevUnitOfWork) {
        Collection categoryAmounts = businessObjectService.findMatching(OrgReversionUnitOfWorkCategoryAmount.class, orgRevUnitOfWork.toStringMapper());
        Map<String, OrgReversionUnitOfWorkCategoryAmount> categories = orgRevUnitOfWork.getCategoryAmounts();
        Iterator iter = categoryAmounts.iterator();
        while (iter.hasNext()) {
            OrgReversionUnitOfWorkCategoryAmount catAmount = (OrgReversionUnitOfWorkCategoryAmount) iter.next();
            categories.put(catAmount.getCategoryCode(), catAmount);
        }
        return orgRevUnitOfWork;
    }

    /**
     * Immediate deletion awaits all entries of the unit of work summary tables in the persistence store once
     * you call this method, for this method is both powerful and deadly and also gets called to clear out
     * those tables before every single org reversion run.
     * @see org.kuali.module.gl.service.OrgReversionUnitOfWorkService#removeAll()
     */
    public void destroyAllUnitOfWorkSummaries() {
        orgReversionUnitOfWorkDao.destroyAllUnitOfWorkSummaries();
    }

    /**
     * This save method is guaranteed to save the category data as well.
     * 
     * @param orgRevUnitOfWork organizationReversionUnitOfWork to save
     * @see org.kuali.module.gl.service.OrgReversionUnitOfWorkService#save(org.kuali.module.gl.bo.OrgReversionUnitOfWork)
     */
    public void save(OrgReversionUnitOfWork orgRevUnitOfWork) {
        LOG.debug("Saving org reversion summary for " + orgRevUnitOfWork.toString() + "; its category keys are: " + orgRevUnitOfWork.getCategoryAmounts().keySet());
        businessObjectService.save(orgRevUnitOfWork);
        CollectionUtils.forAllDo(orgRevUnitOfWork.getCategoryAmounts().entrySet(), new Closure() {
            public void execute(Object categoryEntryAsObject) {
                OrgReversionUnitOfWorkCategoryAmount categoryAmount = (OrgReversionUnitOfWorkCategoryAmount) ((Map.Entry) categoryEntryAsObject).getValue();
                LOG.debug("Saving category amount for " + categoryAmount.toString());
                businessObjectService.save(categoryAmount);
            }
        });
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the orgReversionUnitOfWorkDao attribute.
     * 
     * @return Returns the orgReversionUnitOfWorkDao.
     */
    public OrgReversionUnitOfWorkDao getOrgReversionUnitOfWorkDao() {
        return orgReversionUnitOfWorkDao;
    }

    /**
     * Sets the orgReversionUnitOfWorkDao attribute value.
     * 
     * @param orgReversionUnitOfWorkDao The orgReversionUnitOfWorkDao to set.
     */
    public void setOrgReversionUnitOfWorkDao(OrgReversionUnitOfWorkDao orgReversionUnitOfWorkDao) {
        this.orgReversionUnitOfWorkDao = orgReversionUnitOfWorkDao;
    }

}
