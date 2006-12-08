/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/impl/PriorYearAccountServiceImpl.java,v $
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


import org.kuali.module.chart.bo.PriorYearAccount;
import org.kuali.module.chart.dao.PriorYearAccountDao;
import org.kuali.module.chart.service.PriorYearAccountService;

/**
 */
public class PriorYearAccountServiceImpl implements PriorYearAccountService {

    private PriorYearAccountDao priorYearAccountDao;
    
    public PriorYearAccountServiceImpl() {
        super();
    }

    /**
     * @param priorYearAccountDao The priorYearAccountDao to set.
     */
    public void setPriorYearAccountDao(PriorYearAccountDao priorYearAccountDao) {
        this.priorYearAccountDao = priorYearAccountDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.PriorYearAccountService#getByPrimaryKey(java.lang.String, java.lang.String)
     */
    public PriorYearAccount getByPrimaryKey(String chartCode, String accountNumber) {
        return priorYearAccountDao.getByPrimaryId(chartCode, accountNumber);
    }

}
