/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgList2PLGReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionTwoPlugListMove;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionList2PLGReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionList2PLGReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionList2PLGReportServiceImpl implements BudgetConstructionList2PLGReportService {

    protected BudgetConstructionList2PLGReportDao budgetConstructionList2PLGReportDao;
    protected BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected PersistenceService persistenceServiceOjb; 

    @Override
    public void updateList2PLGReport(String principalName, Integer universityFiscalYear) {
        budgetConstructionList2PLGReportDao.updateList2PLGReportsTable(principalName);

    }

    @Override
    public Collection<BudgetConstructionOrgList2PLGReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgList2PLGReport> reportSet = new ArrayList();
        BudgetConstructionOrgList2PLGReport orgList2PLGReportEntry;

        // force OJB to go to DB since it is populated using JDBC
        // normally done in BudgetConstructionReportsServiceHelperImpl.getDataForBuildingReports
        persistenceServiceOjb.clearCache();

        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalName);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionTwoPlugListMove> twoPlugList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionTwoPlugListMove.class, searchCriteria, orderList);
        for (BudgetConstructionTwoPlugListMove orgList2PLGEntry : twoPlugList) {
            orgList2PLGReportEntry = new BudgetConstructionOrgList2PLGReport();
            buildReportsHeader(universityFiscalYear, orgList2PLGReportEntry, orgList2PLGEntry);
            buildReportsBody(orgList2PLGReportEntry, orgList2PLGEntry);
            reportSet.add(orgList2PLGReportEntry);
        }
        return reportSet;
    }

    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgList2PLGReport orgList2PLGReportEntry, BudgetConstructionTwoPlugListMove twoPlugListMoveEntry) {
        String orgChartDesc = twoPlugListMoveEntry.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = twoPlugListMoveEntry.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = twoPlugListMoveEntry.getOrganization().getOrganizationName();
        String finChartDesc = twoPlugListMoveEntry.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgList2PLGReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgList2PLGReportEntry.setOrgChartOfAccountsCode(twoPlugListMoveEntry.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgList2PLGReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgList2PLGReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgList2PLGReportEntry.setOrganizationCode(twoPlugListMoveEntry.getOrganizationCode());
        if (orgName == null) {
            orgList2PLGReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgList2PLGReportEntry.setOrganizationName(orgName);
        }

        orgList2PLGReportEntry.setChartOfAccountsCode(twoPlugListMoveEntry.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgList2PLGReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgList2PLGReportEntry.setChartOfAccountDescription(chartDesc);
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgList2PLGReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
    }

    public void buildReportsBody(BudgetConstructionOrgList2PLGReport orgList2PLGReportEntry, BudgetConstructionTwoPlugListMove twoPlugListMoveEntry) {
        orgList2PLGReportEntry.setAccountNumber(twoPlugListMoveEntry.getAccountNumber());
        orgList2PLGReportEntry.setSubAccountNumber(twoPlugListMoveEntry.getSubAccountNumber());
//        orgList2PLGReportEntry.setAccountSubAccountName(twoPlugListMoveEntry.getAccount().getAccountName());

        if (twoPlugListMoveEntry.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
            if (twoPlugListMoveEntry.getAccount().getAccountName() == null) {
                orgList2PLGReportEntry.setAccountSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
            }
            else {
                orgList2PLGReportEntry.setAccountSubAccountName(twoPlugListMoveEntry.getAccount().getAccountName());
            }
        }
        else {
            try {
                if (twoPlugListMoveEntry.getSubAccount().getSubAccountName() == null) {
                    orgList2PLGReportEntry.setAccountSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
                }
                else {
                    orgList2PLGReportEntry.setAccountSubAccountName(twoPlugListMoveEntry.getSubAccount().getSubAccountName());
                }
            }
            catch (PersistenceBrokerException e) {
                orgList2PLGReportEntry.setAccountSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
            }
        }

        orgList2PLGReportEntry.setReqAmount(new Integer(twoPlugListMoveEntry.getAccountLineAnnualBalanceAmount().intValue()));
    }

    /**
     * builds orderByList for sort order.
     *
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        return returnList;
    }

    public void setBudgetConstructionList2PLGReportDao(BudgetConstructionList2PLGReportDao budgetConstructionList2PLGReportDao) {
        this.budgetConstructionList2PLGReportDao = budgetConstructionList2PLGReportDao;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
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

