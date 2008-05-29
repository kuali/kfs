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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.dao.OptionsDao;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.dao.ObjectTypeDao;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service implementation is the default implementation of the ObjectType service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectTypeServiceImpl implements ObjectTypeService {

    private UniversityDateService universityDateService;
    private ObjectTypeDao objectTypeDao;
    private OptionsDao optionsDao;

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getByPrimaryKey(java.lang.String)
     */
    public ObjectType getByPrimaryKey(String objectTypeCode) {
        return objectTypeDao.getByPrimaryKey(objectTypeCode);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getAssetObjectType(java.lang.Integer)
     */
    public String getAssetObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeAssetsCd();
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getBasicExpenseObjectTypes(java.lang.Integer)
     */
    public List<String> getBasicExpenseObjectTypes(Integer universityFiscalYear) {

        List<String> basicExpenseObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        basicExpenseObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpNotExpendCode());

        return basicExpenseObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getExpenseObjectTypes(java.lang.Integer)
     */
    public List<String> getExpenseObjectTypes(Integer universityFiscalYear) {
        List<String> expenseObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        expenseObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        expenseObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        expenseObjectTypes.add(option.getFinObjTypeExpNotExpendCode());
        expenseObjectTypes.add(option.getFinancialObjectTypeTransferExpenseCd());

        return expenseObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getBasicIncomeObjectTypes(java.lang.Integer)
     */
    public List<String> getBasicIncomeObjectTypes(Integer universityFiscalYear) {

        List<String> basicIncomeObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        basicIncomeObjectTypes.add(option.getFinObjectTypeIncomecashCode());
        basicIncomeObjectTypes.add(option.getFinObjTypeIncomeNotCashCd());
        basicIncomeObjectTypes.add(option.getFinObjTypeCshNotIncomeCd());

        return basicIncomeObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getExpenseTransferObjectType(java.lang.Integer)
     */
    public String getExpenseTransferObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeTransferExpenseCd();
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getIncomeTransferObjectType(java.lang.Integer)
     */
    public String getIncomeTransferObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeTransferIncomeCd();
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearAssetObjectType()
     */
    public String getCurrentYearAssetObjectType() {
        return getAssetObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearBasicExpenseObjectTypes()
     */
    public List<String> getCurrentYearBasicExpenseObjectTypes() {
        return getBasicExpenseObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearExpenseObjectTypes()
     */
    public List<String> getCurrentYearExpenseObjectTypes() {
        return getExpenseObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearBasicIncomeObjectTypes()
     */
    public List<String> getCurrentYearBasicIncomeObjectTypes() {
        return getBasicIncomeObjectTypes(universityDateService.getCurrentFiscalYear());
    }
    
    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearExpenseTransferObjectType()
     */
    public String getCurrentYearExpenseTransferObjectType() {
        return getExpenseTransferObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getCurrentYearIncomeTransferObjectType()
     */
    public String getCurrentYearIncomeTransferObjectType() {
        return getIncomeTransferObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getNominalActivityClosingAllowedObjectTypes(java.lang.Integer)
     */
    public List<String> getNominalActivityClosingAllowedObjectTypes(Integer fiscalYear) {
        List<String> nominalClosingObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(fiscalYear);
        nominalClosingObjectTypes.add(option.getFinObjTypeExpNotExpendCode());
        nominalClosingObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        nominalClosingObjectTypes.add(option.getFinancialObjectTypeTransferExpenseCd());
        nominalClosingObjectTypes.add(option.getFinancialObjectTypeTransferIncomeCd());
        nominalClosingObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        nominalClosingObjectTypes.add(option.getFinObjTypeCshNotIncomeCd());
        nominalClosingObjectTypes.add(option.getFinObjTypeIncomeNotCashCd());
        nominalClosingObjectTypes.add(option.getFinObjectTypeIncomecashCode());
        return nominalClosingObjectTypes;
    }

    /**
     * @see org.kuali.module.chart.service.ObjectTypeService#getGeneralForwardBalanceObjectTypes(java.lang.Integer)
     */
    public List<String> getGeneralForwardBalanceObjectTypes(Integer fiscalYear) {
        Options option = optionsDao.getByPrimaryId(fiscalYear);
        List<String> generalBalanceForwardObjectTypes = new ArrayList<String>();
        generalBalanceForwardObjectTypes.add(option.getFinancialObjectTypeAssetsCd());
        generalBalanceForwardObjectTypes.add(option.getFinObjectTypeLiabilitiesCode());
        generalBalanceForwardObjectTypes.add(option.getFinObjectTypeFundBalanceCd());
        return generalBalanceForwardObjectTypes;
    }

    /**
     * @see org.kuali.module.chart.service.ObjectTypeService#getCumulativeForwardBalanceObjectTypes(java.lang.Integer)
     */
    public List<String> getCumulativeForwardBalanceObjectTypes(Integer fiscalYear) {
        Options option = optionsDao.getByPrimaryId(fiscalYear);
        List<String> cumulativeBalanceForwardsObjectTypes = new ArrayList<String>();
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjTypeExpNotExpendCode());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjTypeIncomeNotCashCd());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinancialObjectTypeTransferExpenseCd());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinancialObjectTypeTransferIncomeCd());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjectTypeIncomecashCode());
        cumulativeBalanceForwardsObjectTypes.add(option.getFinObjTypeCshNotIncomeCd());
        return cumulativeBalanceForwardsObjectTypes;
    }
    
    /**
     * @param objectTypeDao The objectTypeDao to set.
     */
    public void setObjectTypeDao(ObjectTypeDao objectTypeDao) {
        this.objectTypeDao = objectTypeDao;
    }

    public OptionsDao getOptionsDao() {
        return optionsDao;
    }

    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
