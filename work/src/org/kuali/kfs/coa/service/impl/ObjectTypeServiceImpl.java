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
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This service implementation is the default implementation of the ObjectType service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectTypeServiceImpl implements ObjectTypeService {

    protected UniversityDateService universityDateService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getByPrimaryKey(java.lang.String)
     */
    @Override
    @Cacheable(value=ObjectType.CACHE_NAME,key="#p0")
    public ObjectType getByPrimaryKey(String objectTypeCode) {
        return businessObjectService.findBySinglePrimaryKey(ObjectType.class, objectTypeCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getAssetObjectType(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{AssetObjectType}'+#p0")
    public String getAssetObjectType(Integer universityFiscalYear) {
        SystemOptions options = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        return options.getFinancialObjectTypeAssetsCd();
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getBasicExpenseObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{BasicExpenseObjectTypes}'+#p0")
    public List<String> getBasicExpenseObjectTypes(Integer universityFiscalYear) {

        List<String> basicExpenseObjectTypes = new ArrayList<String>();
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        basicExpenseObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpNotExpendCode());

        return basicExpenseObjectTypes;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getExpenseObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{ExpenseObjectTypes}'+#p0")
    public List<String> getExpenseObjectTypes(Integer universityFiscalYear) {
        List<String> expenseObjectTypes = new ArrayList<String>();
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        expenseObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        expenseObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        expenseObjectTypes.add(option.getFinObjTypeExpNotExpendCode());
        expenseObjectTypes.add(option.getFinancialObjectTypeTransferExpenseCd());

        return expenseObjectTypes;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getBasicIncomeObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{BasicIncomeObjectTypes}'+#p0")
    public List<String> getBasicIncomeObjectTypes(Integer universityFiscalYear) {

        List<String> basicIncomeObjectTypes = new ArrayList<String>();
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        basicIncomeObjectTypes.add(option.getFinObjectTypeIncomecashCode());
        basicIncomeObjectTypes.add(option.getFinObjTypeIncomeNotCashCd());
        basicIncomeObjectTypes.add(option.getFinObjTypeCshNotIncomeCd());

        return basicIncomeObjectTypes;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getExpenseTransferObjectType(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{ExpenseTransferObjectType}'+#p0")
    public String getExpenseTransferObjectType(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        return option.getFinancialObjectTypeTransferExpenseCd();
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getIncomeTransferObjectType(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{IncomeTransferObjectType}'+#p0")
    public String getIncomeTransferObjectType(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        return option.getFinancialObjectTypeTransferIncomeCd();
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearAssetObjectType()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{AssetObjectType}CurrentFY'")
    public String getCurrentYearAssetObjectType() {
        return getAssetObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearBasicExpenseObjectTypes()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{BasicExpenseObjectTypes}CurrentFY'")
    public List<String> getCurrentYearBasicExpenseObjectTypes() {
        return getBasicExpenseObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearExpenseObjectTypes()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{ExpenseObjectTypes}CurrentFY'")
    public List<String> getCurrentYearExpenseObjectTypes() {
        return getExpenseObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearBasicIncomeObjectTypes()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{BasicIncomeObjectTypes}CurrentFY'")
    public List<String> getCurrentYearBasicIncomeObjectTypes() {
        return getBasicIncomeObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearExpenseTransferObjectType()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{ExpenseTransferObjectType}CurrentFY'")
    public String getCurrentYearExpenseTransferObjectType() {
        return getExpenseTransferObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCurrentYearIncomeTransferObjectType()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{IncomeTransferObjectType}CurrentFY'")
    public String getCurrentYearIncomeTransferObjectType() {
        return getIncomeTransferObjectType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getNominalActivityClosingAllowedObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{NominalActivityClosingAllowedObjectTypes}'+#p0")
    public List<String> getNominalActivityClosingAllowedObjectTypes(Integer universityFiscalYear) {
        List<String> nominalClosingObjectTypes = new ArrayList<String>();
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
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
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getGeneralForwardBalanceObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{GeneralForwardBalanceObjectTypes}'+#p0")
    public List<String> getGeneralForwardBalanceObjectTypes(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        List<String> generalBalanceForwardObjectTypes = new ArrayList<String>();
        generalBalanceForwardObjectTypes.add(option.getFinancialObjectTypeAssetsCd());
        generalBalanceForwardObjectTypes.add(option.getFinObjectTypeLiabilitiesCode());
        generalBalanceForwardObjectTypes.add(option.getFinObjectTypeFundBalanceCd());
        return generalBalanceForwardObjectTypes;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectTypeService#getCumulativeForwardBalanceObjectTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME,key="'{CumulativeForwardBalanceObjectTypes}'+#p0")
    public List<String> getCumulativeForwardBalanceObjectTypes(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
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

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
