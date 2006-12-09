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

import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.SubObjectCodeDao;
import org.kuali.module.chart.service.SubObjectCodeService;

/**
 * This class is the service implementation for the SubObjectCode structure. This is the default implementation that gets delivered
 * with Kuali.
 * 
 * 
 */
public class SubObjectCodeServiceImpl implements SubObjectCodeService {
    private SubObjectCodeDao subObjectCodeDao;

    /**
     * @see org.kuali.module.chart.service.SubObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return subObjectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * @return Returns the SubObjectCodeDao
     */
    public SubObjectCodeDao getSubObjectCodeDao() {
        return subObjectCodeDao;
    }

    /**
     * @param subObjectCodeDao The SubObjectCode object to set
     */
    public void setSubObjectCodeDao(SubObjectCodeDao subObjectCodeDao) {
        this.subObjectCodeDao = subObjectCodeDao;
    }
}