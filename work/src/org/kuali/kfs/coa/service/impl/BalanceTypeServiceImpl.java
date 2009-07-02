/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.dataaccess.BalanceTypeDao;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.dataaccess.OptionsDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.spring.CacheNoCopy;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali. It uses the
 * balance types that are defined in the Kuali database.
 */

@NonTransactional
public class BalanceTypeServiceImpl implements BalanceTypeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypeServiceImpl.class);

    // balance type constants

    private BusinessObjectService businessObjectService;
    private BalanceTypeDao balanceTypeDao;

    private UniversityDateService universityDateService;
    private OptionsDao optionsDao;

    
    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance type's code.
     * 
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceType getBalanceTypeByCode(String code) {
       return (BalanceType) businessObjectService.findBySinglePrimaryKey(BalanceType.class, code);
    }

    /**
     * @see org.kuali.kfs.coa.service.BalanceTypService#getAllBalanceTyps()
     */
    public Collection<BalanceType> getAllBalanceTypes() {
        return businessObjectService.findAll(BalanceType.class);
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * 
     * This method injects the BalanceTypeDao
     * @param balanceTypeDao
     */
    public void setBalanceTypeDao(BalanceTypeDao balanceTypeDao) {
        this.balanceTypeDao = balanceTypeDao;
    }

    /**
     * 
     * This method injects the UniversityDateService
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * 
     * This method injects the OptionsDao
     * @param optionsDao
     */
    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    /**
     * @see org.kuali.kfs.coa.service.BalanceTypeService#getCostShareEncumbranceBalanceType(java.lang.Integer)
     */
    @CacheNoCopy
    public String getCostShareEncumbranceBalanceType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getCostShareEncumbranceBalanceTypeCd();
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.BalanceTypeService#getEncumbranceBalanceTypes(java.lang.Integer)
     */
    @CacheNoCopy
    public List<String> getEncumbranceBalanceTypes(Integer universityFiscalYear) {
        SystemOptions option = optionsDao.getByPrimaryId(universityFiscalYear);
        List<String> encumberanceBalanceTypes = new ArrayList<String>();
        encumberanceBalanceTypes.add(option.getExtrnlEncumFinBalanceTypCd());
        encumberanceBalanceTypes.add(option.getIntrnlEncumFinBalanceTypCd());
        encumberanceBalanceTypes.add(option.getPreencumbranceFinBalTypeCd());
        encumberanceBalanceTypes.add(option.getCostShareEncumbranceBalanceTypeCd());
        return encumberanceBalanceTypes;
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.BalanceTypService#getCurrentYearCostShareEncumbranceBalanceType()
     */
    @CacheNoCopy
    public String getCurrentYearCostShareEncumbranceBalanceType() {
        return getCostShareEncumbranceBalanceType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.BalanceTypService#getCurrentYearEncumbranceBalanceTypes()
     */
    @CacheNoCopy
    public List<String> getCurrentYearEncumbranceBalanceTypes() {
        return getEncumbranceBalanceTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.BalanceTypService#getContinuationAccountBypassBalanceTypeCodes(java.lang.Integer)
     */
    @CacheNoCopy
    public List<String> getContinuationAccountBypassBalanceTypeCodes(Integer universityFiscalYear) {
        SystemOptions option = optionsDao.getByPrimaryId(universityFiscalYear);
        List<String> continuationAccountBypassBalanceTypes = new ArrayList<String>();
        continuationAccountBypassBalanceTypes.add(option.getExtrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getIntrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getPreencumbranceFinBalTypeCd());
        return continuationAccountBypassBalanceTypes;
    }
}
