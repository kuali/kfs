/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.BCConstants.OrgSelControlOption;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the BudgetOrganizationTreeService interface
 */
@Transactional
public class BudgetOrganizationTreeServiceImpl implements BudgetOrganizationTreeService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetOrganizationTreeServiceImpl.class);

    protected BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    protected BusinessObjectService businessObjectService;
    protected BudgetConstructionDao budgetConstructionDao;
    protected BudgetPullupDao budgetPullupDao;
    protected PersistenceService persistenceServiceOjb;

    // controls used to trap any runaways due to cycles in the reporting tree
    protected static final int MAXLEVEL = 50;
    private int curLevel;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#buildPullup(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void buildPullup(String principalName, String chartOfAccountsCode, String organizationCode) {
        cleanPullup(principalName);
        BudgetConstructionOrganizationReports bcOrgRpts = budgetConstructionOrganizationReportsService.getByPrimaryId(chartOfAccountsCode, organizationCode);
        if (bcOrgRpts != null) {
            if (bcOrgRpts.getOrganization().isActive()) {
                curLevel = 0;
                buildSubTree(principalName, bcOrgRpts, curLevel);
            }
        }
    }

    protected void buildSubTree(String principalName, BudgetConstructionOrganizationReports bcOrgRpts, int curLevel) {

        curLevel++;
        BudgetConstructionPullup bcPullup = new BudgetConstructionPullup();
        bcPullup.setPrincipalId(principalName);
        bcPullup.setChartOfAccountsCode(bcOrgRpts.getChartOfAccountsCode());
        bcPullup.setOrganizationCode(bcOrgRpts.getOrganizationCode());
        bcPullup.setReportsToChartOfAccountsCode(bcOrgRpts.getReportsToChartOfAccountsCode());
        bcPullup.setReportsToOrganizationCode(bcOrgRpts.getReportsToOrganizationCode());
        bcPullup.setPullFlag(new Integer(0));
        businessObjectService.save(bcPullup);

        if (curLevel <= MAXLEVEL) {
            // getActiveChildOrgs does not return orgs that report to themselves
            List childOrgs = budgetConstructionOrganizationReportsService.getActiveChildOrgs(bcOrgRpts.getChartOfAccountsCode(), bcOrgRpts.getOrganizationCode());
            if (childOrgs.size() > 0) {
                for (Iterator iter = childOrgs.iterator(); iter.hasNext();) {
                    BudgetConstructionOrganizationReports bcOrg = (BudgetConstructionOrganizationReports) iter.next();
                    buildSubTree(principalName, bcOrg, curLevel);
                }
            }
        }
        else {
            LOG.warn(String.format("\n%s/%s reports to organization more than maxlevel of %d", bcOrgRpts.getChartOfAccountsCode(), bcOrgRpts.getOrganizationCode(), MAXLEVEL));
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#buildPullupSql(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void buildPullupSql(String principalName, String chartOfAccountsCode, String organizationCode) {
        cleanPullup(principalName);
        BudgetConstructionOrganizationReports bcOrgRpts = budgetConstructionOrganizationReportsService.getByPrimaryId(chartOfAccountsCode, organizationCode);
        if (bcOrgRpts != null) {
            if (bcOrgRpts.getOrganization().isActive()) {
                curLevel = 0;
                buildSubTreeSql(principalName, bcOrgRpts, curLevel);
            }
        }
    }

    protected void buildSubTreeSql(String principalName, BudgetConstructionOrganizationReports bcOrgRpts, int curLevel) {

        curLevel++;
        budgetPullupDao.buildSubTree(principalName, bcOrgRpts.getChartOfAccountsCode(), bcOrgRpts.getOrganizationCode(), curLevel);
//      budgetPullupDao.initPointOfView(principalName, bcOrgRpts.getChartOfAccountsCode(), bcOrgRpts.getOrganizationCode(), curLevel);
//      budgetPullupDao.insertChildOrgs(principalName, curLevel);
      
        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#cleanPullup(java.lang.String)
     */
    public void cleanPullup(String principalName) {

//        budgetConstructionDao.deleteBudgetConstructionPullupByUserId(principalName);
        budgetPullupDao.cleanGeneralLedgerObjectSummaryTable(principalName);

        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#getPullupChildOrgs(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List<BudgetConstructionPullup> getPullupChildOrgs(String principalId, String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("String parameter principalId was null or blank.");
        }
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        return (List<BudgetConstructionPullup>) budgetConstructionDao.getBudgetConstructionPullupChildOrgs(principalId, chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#resetPullFlag(java.lang.String)
     */
    public void resetPullFlag(String principalId) {

        if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("String parameter principalId was null or blank.");
        }
        List<BudgetConstructionPullup> results = budgetConstructionDao.getBudgetConstructionPullupFlagSetByUserId(principalId);
        if (!results.isEmpty()) {
            for (BudgetConstructionPullup selOrg : results) {
                selOrg.setPullFlag(OrgSelControlOption.NO.getKey());
            }
            businessObjectService.save(results);
        }

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService#getSelectedOrgs(java.lang.String)
     */
    public List<BudgetConstructionPullup> getSelectedOrgs(String principalId) {

        if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("String parameter principalId was null or blank.");
        }
        return (List<BudgetConstructionPullup>) budgetConstructionDao.getBudgetConstructionPullupFlagSetByUserId(principalId);
    }

    /**
     * Gets the budgetConstructionOrganizationReportsService attribute.
     * 
     * @return Returns the budgetConstructionOrganizationReportsService.
     */
    public BudgetConstructionOrganizationReportsService getBudgetConstructionOrganizationReportsService() {
        return budgetConstructionOrganizationReportsService;
    }

    /**
     * Sets the budgetConstructionOrganizationReportsService attribute value.
     * 
     * @param budgetConstructionOrganizationReportsService The budgetConstructionOrganizationReportsService to set.
     */
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
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
     * Gets the budgetConstructionDao attribute.
     * 
     * @return Returns the budgetConstructionDao.
     */
    public BudgetConstructionDao getBudgetConstructionDao() {
        return budgetConstructionDao;
    }

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

    /**
     * Gets the budgetPullupDao attribute.
     * 
     * @return Returns the budgetPullupDao.
     */
    public BudgetPullupDao getBudgetPullupDao() {
        return budgetPullupDao;
    }

    /**
     * Sets the budgetPullupDao attribute value.
     * 
     * @param budgetPullupDao The budgetPullupDao to set.
     */
    public void setBudgetPullupDao(BudgetPullupDao budgetPullupDao) {
        this.budgetPullupDao = budgetPullupDao;
    }

    /**
     * Gets the persistenceServiceOjb attribute.
     * 
     * @return Returns the persistenceServiceOjb
     */
    
    public PersistenceService getPersistenceServiceOjb() {
        return persistenceServiceOjb;
    }

    /**	
     * Sets the persistenceServiceOjb attribute.
     * 
     * @param persistenceServiceOjb The persistenceServiceOjb to set.
     */
    public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
        this.persistenceServiceOjb = persistenceServiceOjb;
    }

}
