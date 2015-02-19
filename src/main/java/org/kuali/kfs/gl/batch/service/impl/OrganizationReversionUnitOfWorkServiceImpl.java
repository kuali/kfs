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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.batch.dataaccess.OrganizationReversionUnitOfWorkDao;
import org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService;
import org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWork;
import org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWorkCategoryAmount;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of OrganizationReversionUnitOfWorkService
 */
@Transactional
public class OrganizationReversionUnitOfWorkServiceImpl implements OrganizationReversionUnitOfWorkService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionUnitOfWorkServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected OrganizationReversionUnitOfWorkDao orgReversionUnitOfWorkDao;

    /**
     * This method takes a unit of work retrieved from the persistence store and loads its categories
     * 
     * @param orgRevUnitOfWork org reversion unit of work to load categories for
     * @return the org reversion unit of work with loaded categories
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService#loadCategories(org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWork)
     */
    public OrgReversionUnitOfWork loadCategories(OrgReversionUnitOfWork orgRevUnitOfWork) {
        Map<String,Object> criteria = new HashMap<String, Object>();
        criteria.put("chartOfAccountsCode", orgRevUnitOfWork.chartOfAccountsCode);
        criteria.put("accountNbr", orgRevUnitOfWork.accountNumber);
        criteria.put("subAccountNbr", orgRevUnitOfWork.subAccountNumber);

        Collection<OrgReversionUnitOfWorkCategoryAmount> categoryAmounts = businessObjectService.findMatching(OrgReversionUnitOfWorkCategoryAmount.class, criteria);
        Map<String, OrgReversionUnitOfWorkCategoryAmount> categories = orgRevUnitOfWork.getCategoryAmounts();
        for ( OrgReversionUnitOfWorkCategoryAmount catAmount : categoryAmounts ) {
            categories.put(catAmount.getCategoryCode(), catAmount);
        }
        return orgRevUnitOfWork;
    }

    /**
     * Immediate deletion awaits all entries of the unit of work summary tables in the persistence store once
     * you call this method, for this method is both powerful and deadly and also gets called to clear out
     * those tables before every single org reversion run.
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService#removeAll()
     */
    public void destroyAllUnitOfWorkSummaries() {
        orgReversionUnitOfWorkDao.destroyAllUnitOfWorkSummaries();
    }

    /**
     * This save method is guaranteed to save the category data as well.
     * 
     * @param orgRevUnitOfWork organizationReversionUnitOfWork to save
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService#save(org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWork)
     */
    public void save(OrgReversionUnitOfWork orgRevUnitOfWork) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving org reversion summary for " + orgRevUnitOfWork.toString() + "; its category keys are: " + orgRevUnitOfWork.getCategoryAmounts().keySet());
        }
        businessObjectService.save(orgRevUnitOfWork);
        for (String category: orgRevUnitOfWork.getCategoryAmounts().keySet()) {
            final OrgReversionUnitOfWorkCategoryAmount categoryAmount = orgRevUnitOfWork.getCategoryAmounts().get(category);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Saving category amount for " + categoryAmount.toString());
            }
            businessObjectService.save(categoryAmount);
        }
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
     * Sets the orgReversionUnitOfWorkDao attribute value.
     * 
     * @param orgReversionUnitOfWorkDao The orgReversionUnitOfWorkDao to set.
     */
    public void setOrgReversionUnitOfWorkDao(OrganizationReversionUnitOfWorkDao orgReversionUnitOfWorkDao) {
        this.orgReversionUnitOfWorkDao = orgReversionUnitOfWorkDao;
    }

}
