/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.util.Collection;

import org.kuali.core.service.KualiCodeService;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.dao.BalanceTypeDao;
import org.kuali.module.chart.service.BalanceTypService;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali. It uses the
 * balance typs that are defined in the Kuali database.
 * 
 * 
 */
public class BalanceTypServiceImpl implements BalanceTypService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypServiceImpl.class);

    // balance type constants
    private static final String ACTUAL_BALANCE_TYPE = "AC";

    private KualiCodeService kualiCodeService;
    private BalanceTypeDao balanceTypeDao;

    /**
     * @see org.kuali.module.chart.service.BalanceTypService#getActualBalanceTyp()
     */
    public BalanceTyp getActualBalanceTyp() {
        return getBalanceTypByCode(ACTUAL_BALANCE_TYPE);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getEncumbranceBalanceTypes()
     */
    public Collection getEncumbranceBalanceTypes() {
        LOG.debug("getEncumbranceBalanceTypes() started");

        return balanceTypeDao.getEncumbranceBalanceTypes();
    }

    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance typ's code.
     * 
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceTyp getBalanceTypByCode(String code) {
        return (BalanceTyp) kualiCodeService.getByCode(BalanceTyp.class, code);
    }

    /**
     * @see org.kuali.module.chart.service.BalanceTypService#getAllBalanceTyps()
     */
    public Collection getAllBalanceTyps() {
        return kualiCodeService.getAll(BalanceTyp.class);
    }

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    public void setBalanceTypeDao(BalanceTypeDao balanceTypeDao) {
        this.balanceTypeDao = balanceTypeDao;
    }
}