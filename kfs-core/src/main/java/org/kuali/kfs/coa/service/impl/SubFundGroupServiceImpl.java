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
package org.kuali.kfs.coa.service.impl;

import java.util.Collection;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.dataaccess.SubFundGroupDao;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This service implementation is the default implementation of the SubFundGroup service that is delivered with Kuali.
 */
public class SubFundGroupServiceImpl implements SubFundGroupService {
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private SubFundGroupDao subFundGroupDao;

    /**
     * @see org.kuali.kfs.coa.service.SubFundGroupService#isForContractsAndGrants(org.kuali.kfs.coa.businessobject.SubFundGroup)
     */
    public boolean isForContractsAndGrants(SubFundGroup subFundGroup) {
        if (ObjectUtils.isNull(subFundGroup)) {
            return false;
        }
        else if (fundGroupDenotesContractsAndGrants()) {
            return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE, subFundGroup.getFundGroupCode()).evaluationSucceeds();
    //      return getContractsAndGrantsDenotingValue(subFundGroup.getFundGroupCode());
        }
        else {
            return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE, subFundGroup.getSubFundGroupCode()).evaluationSucceeds();

           //return getContractsAndGrantsDenotingValue(subFundGroup.getSubFundGroupCode());
        }
    }

    /**
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getContractsAndGrantsDenotingAttributeLabel()
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
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getContractsAndGrantsDenotingValue(org.kuali.kfs.coa.businessobject.SubFundGroup)
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
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getContractsAndGrantsDenotingValues()
     */
    public Collection<String> getContractsAndGrantsDenotingValues() {
        return parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE);
    }
    
    
    /**
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getContractsAndGrantsDenotingValueForMessage()
     */
    public String getContractsAndGrantsDenotingValueForMessage() {
        return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE).getParameterValuesForMessage();
    }

    /**
     * 
     * This checks to see if there is a value for checking if a Fund Group denotes Contracts and Grants
     * @return false if there is no value
     */
    protected boolean fundGroupDenotesContractsAndGrants() {
        return parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG);
    }

    /**
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getByPrimaryId(java.lang.String)
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode) {
        return (SubFundGroup)SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SubFundGroup.class, subFundGroupCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.SubFundGroupService#getByChartAndAccount(java.lang.String, java.lang.String)
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
