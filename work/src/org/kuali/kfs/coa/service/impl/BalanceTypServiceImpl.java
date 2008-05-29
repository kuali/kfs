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
package org.kuali.module.chart.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.dao.OptionsDao;
import org.kuali.kfs.service.KualiCodeService;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.dao.BalanceTypeDao;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali. It uses the
 * balance types that are defined in the Kuali database.
 */

@NonTransactional
public class BalanceTypServiceImpl implements BalanceTypService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypServiceImpl.class);

    // balance type constants

    private KualiCodeService kualiCodeService;
    private BalanceTypeDao balanceTypeDao;

    private UniversityDateService universityDateService;
    private OptionsDao optionsDao;

    private HashMap<String,BalanceTyp> balanceTypes = new HashMap<String,BalanceTyp>(); 
    
    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance typ's code.
     * 
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceTyp getBalanceTypByCode(String code) {
        loadBalanceTypes();
        return balanceTypes.get(code);
    }

    /**
     * @see org.kuali.module.chart.service.BalanceTypService#getAllBalanceTyps()
     */
    public Collection getAllBalanceTyps() {
        loadBalanceTypes();
        return balanceTypes.values();
    }
    
    private void loadBalanceTypes() {
        if ( balanceTypes.isEmpty() ) {
            Collection<BalanceTyp> balTypes = kualiCodeService.getAll(BalanceTyp.class);
            for ( BalanceTyp bt : balTypes ) {
                balanceTypes.put(bt.getCode(), bt);
            }
        }
    }

    /**
     * 
     * This method injects the KualiCodeService
     * @param kualiCodeService
     */
    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
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
     * @see org.kuali.module.chart.service.BalanceTypService#getCostShareEncumbranceBalanceType(java.lang.Integer)
     */
    @CacheNoCopy
    public String getCostShareEncumbranceBalanceType(Integer universityFiscalYear) {
        return optionsDao.getByPrimaryId(universityFiscalYear).getCostShareEncumbranceBalanceTypeCd();
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getEncumbranceBalanceTypes(java.lang.Integer)
     */
    @CacheNoCopy
    public List<String> getEncumbranceBalanceTypes(Integer universityFiscalYear) {
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        List<String> encumberanceBalanceTypes = new ArrayList<String>();
        encumberanceBalanceTypes.add(option.getExtrnlEncumFinBalanceTypCd());
        encumberanceBalanceTypes.add(option.getIntrnlEncumFinBalanceTypCd());
        encumberanceBalanceTypes.add(option.getPreencumbranceFinBalTypeCd());
        encumberanceBalanceTypes.add(option.getCostShareEncumbranceBalanceTypeCd());
        return encumberanceBalanceTypes;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getCurrentYearCostShareEncumbranceBalanceType()
     */
    @CacheNoCopy
    public String getCurrentYearCostShareEncumbranceBalanceType() {
        return getCostShareEncumbranceBalanceType(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getCurrentYearEncumbranceBalanceTypes()
     */
    @CacheNoCopy
    public List<String> getCurrentYearEncumbranceBalanceTypes() {
        return getEncumbranceBalanceTypes(universityDateService.getCurrentFiscalYear());
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getContinuationAccountBypassBalanceTypeCodes(java.lang.Integer)
     */
    @CacheNoCopy
    public List<String> getContinuationAccountBypassBalanceTypeCodes(Integer universityFiscalYear) {
        Options option = optionsDao.getByPrimaryId(universityFiscalYear);
        List<String> continuationAccountBypassBalanceTypes = new ArrayList<String>();
        continuationAccountBypassBalanceTypes.add(option.getExtrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getIntrnlEncumFinBalanceTypCd());
        continuationAccountBypassBalanceTypes.add(option.getPreencumbranceFinBalTypeCd());
        return continuationAccountBypassBalanceTypes;
    }
}