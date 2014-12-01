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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.dataaccess.BalanceTypeDao;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali. It uses the
 * balance types that are defined in the Kuali database.
 */

@NonTransactional
public class BalanceTypeServiceImpl implements BalanceTypeService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypeServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected BalanceTypeDao balanceTypeDao;
    protected UniversityDateService universityDateService;

    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance type's code.
     *
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    @Override
    @Cacheable(value=BalanceType.CACHE_NAME, key="'code='+#p0")
    public BalanceType getBalanceTypeByCode(String code) {
       return businessObjectService.findBySinglePrimaryKey(BalanceType.class, code);
    }

    /**
     * @see org.kuali.kfs.coa.service.BalanceTypService#getAllBalanceTyps()
     */
    @Override
    @Cacheable(value=BalanceType.CACHE_NAME, key="'{getAllBalanceTypes}'")
    public Collection<BalanceType> getAllBalanceTypes() {
        return businessObjectService.findAll(BalanceType.class);
    }

    @Cacheable(value=BalanceType.CACHE_NAME, key="'{getAllEncumbranceBalanceTypes}'")
    @Override
    public Collection<BalanceType> getAllEncumbranceBalanceTypes() {
        return balanceTypeDao.getEncumbranceBalanceTypes();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    public void setBalanceTypeDao(BalanceTypeDao balanceTypeDao) {
        this.balanceTypeDao = balanceTypeDao;
    }
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * @see org.kuali.kfs.coa.service.BalanceTypeService#getCostShareEncumbranceBalanceType(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{getCostShareEncumbranceBalanceType}'+#p0")
    public String getCostShareEncumbranceBalanceType(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        return option.getCostShareEncumbranceBalanceTypeCd();
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.BalanceTypeService#getEncumbranceBalanceTypes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{getEncumbranceBalanceTypes}'+#p0")
    public List<String> getEncumbranceBalanceTypes(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
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
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{getCostShareEncumbranceBalanceType}CurrentFY'")
    public String getCurrentYearCostShareEncumbranceBalanceType() {
        return getCostShareEncumbranceBalanceType(universityDateService.getCurrentFiscalYear());
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.BalanceTypService#getCurrentYearEncumbranceBalanceTypes()
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{getEncumbranceBalanceTypes}CurrentFY'")
    public List<String> getCurrentYearEncumbranceBalanceTypes() {
        return getEncumbranceBalanceTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.BalanceTypService#getContinuationAccountBypassBalanceTypeCodes(java.lang.Integer)
     */
    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'{getContinuationAccountBypassBalanceTypeCodes}'+#p0")
    public List<String> getContinuationAccountBypassBalanceTypeCodes(Integer universityFiscalYear) {
        SystemOptions option = businessObjectService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
        List<String> continuationAccountBypassBalanceTypes = new ArrayList<String>(3);
        continuationAccountBypassBalanceTypes.add(option.getExtrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getIntrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getPreencumbranceFinBalTypeCd());
        return continuationAccountBypassBalanceTypes;
    }
}
