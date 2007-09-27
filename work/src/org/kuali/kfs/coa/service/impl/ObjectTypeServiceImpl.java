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

import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.dao.OptionsDao;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.dao.ObjectTypeDao;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ObjectTypeServiceImpl implements ObjectTypeService {

    private UniversityDateService universityDateService;
    private ObjectTypeDao objectTypeDao;
    private OptionsDao optionsDao;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getByPrimaryKey(java.lang.String)
     */
    public ObjectType getByPrimaryKey(String objectTypeCode) {
        return objectTypeDao.getByPrimaryKey(objectTypeCode);
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

    public String getAssetObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeAssetsCd();
    }
    
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }    

    public List<String> getBasicExpenseObjectTypes(Integer universityFiscalYear) {
        
        List<String> basicExpenseObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        basicExpenseObjectTypes.add(option.getFinObjTypeExpenditureexpCd());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpendNotExpCode());
        basicExpenseObjectTypes.add(option.getFinObjTypeExpNotExpendCode());
        
        return basicExpenseObjectTypes;
    }

    public List<String> getBasicIncomeObjectTypes(Integer universityFiscalYear) {
        
        List<String> basicIncomeObjectTypes = new ArrayList<String>();
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        basicIncomeObjectTypes.add(option.getFinObjectTypeIncomecashCode());
        basicIncomeObjectTypes.add(option.getFinObjTypeIncomeNotCashCd());
        basicIncomeObjectTypes.add(option.getFinObjTypeCshNotIncomeCd());
        
        return basicIncomeObjectTypes;
    }

    public String getExpenseTransferObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeTransferExpenseCd();
    }

    public String getIncomeTransferObjectType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getFinancialObjectTypeTransferIncomeCd();
    }

    public String getCurrentYearAssetObjectType() {
        return getAssetObjectType( universityDateService.getCurrentFiscalYear() );
    }

    public List<String> getCurrentYearBasicExpenseObjectTypes() {
        return getBasicExpenseObjectTypes(universityDateService.getCurrentFiscalYear() );
    }

    public List<String> getCurrentYearBasicIncomeObjectTypes() {
        return getBasicIncomeObjectTypes(universityDateService.getCurrentFiscalYear());
    }

    public String getCurrentYearExpenseTransferObjectType() {
        return getExpenseTransferObjectType(universityDateService.getCurrentFiscalYear());
    }

    public String getCurrentYearIncomeTransferObjectType() {
        return getIncomeTransferObjectType(universityDateService.getCurrentFiscalYear());
    }
}
