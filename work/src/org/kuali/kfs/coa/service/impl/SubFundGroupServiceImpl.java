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

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.FundGroup;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.dao.SubFundGroupDao;
import org.kuali.module.chart.service.SubFundGroupService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service implementation is the default implementation of the SubFundGroup service that is delivered with Kuali.
 */
@Transactional
public class SubFundGroupServiceImpl implements SubFundGroupService {
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private SubFundGroupDao subFundGroupDao;

    /**
     * @see org.kuali.module.chart.service.SubFundGroupService#isForContractsAndGrants(org.kuali.module.chart.bo.SubFundGroup)
     */
    public boolean isForContractsAndGrants(SubFundGroup subFundGroup) {
        if (ObjectUtils.isNull(subFundGroup)) {
            return false;
        }
        else if (fundGroupDenotesContractsAndGrants()) {
            return getContractsAndGrantsDenotingValue().equals(subFundGroup.getFundGroupCode());
        }
        else {
            return getContractsAndGrantsDenotingValue().equals(subFundGroup.getSubFundGroupCode());
        }
    }

    /**
     * @see org.kuali.module.chart.service.SubFundGroupService#getContractsAndGrantsDenotingAttributeLabel()
     */
    public String getContractsAndGrantsDenotingAttributeLabel() {
        if (fundGroupDenotesContractsAndGrants()) {
            return dataDictionaryService.getAttributeLabel(FundGroup.class, KFSConstants.FUND_GROUP_CODE_PROPERTY_NAME);
        }
        else {
            return dataDictionaryService.getAttributeLabel(SubFundGroup.class, KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME);
        }
    }

    /**
     * 
     * @see org.kuali.module.chart.service.SubFundGroupService#getContractsAndGrantsDenotingValue(org.kuali.module.chart.bo.SubFundGroup)
     */
    public String getContractsAndGrantsDenotingValue(SubFundGroup subFundGroup) {
        if (fundGroupDenotesContractsAndGrants()) {
            return subFundGroup.getFundGroupCode();
        }
        else {
            return subFundGroup.getSubFundGroupCode();
        }
    }


    /**
     * @see org.kuali.module.chart.service.SubFundGroupService#getContractsAndGrantsDenotingValue()
     */
    public String getContractsAndGrantsDenotingValue() {
        return parameterService.getParameterValue(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE);
    }

    /**
     * 
     * This checks to see if there is a value for checking if a Fund Group denotes Contracts and Grants
     * @return false if there is no value
     */
    private boolean fundGroupDenotesContractsAndGrants() {
        return parameterService.getIndicatorParameter(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG);
    }

    /**
     * @see org.kuali.module.chart.service.SubFundGroupService#getByPrimaryId(java.lang.String)
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode) {
        return subFundGroupDao.getByPrimaryId(subFundGroupCode);
    }

    /**
     * @see org.kuali.module.chart.service.SubFundGroupService#getByChartAndAccount(java.lang.String, java.lang.String)
     */
    public SubFundGroup getByChartAndAccount(String chartCode, String accountNumber) {
        return subFundGroupDao.getByChartAndAccount(chartCode, accountNumber);
    }

    /**
     * 
     * This method injects the ParameterService
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the subFundGroupDao attribute values.
     * 
     * @param subFundGroupDao The subFundGroupDao to set.
     */
    public void setSubFundGroupDao(SubFundGroupDao subFundGroupDao) {
        this.subFundGroupDao = subFundGroupDao;
    }

    /**
     * Sets the dataDictionarySerivce
     * 
     * @param dataDictionaryService The dataDictionaryService implementation to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
