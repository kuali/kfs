/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.dao.SubFundGroupDao;
import org.kuali.module.chart.service.SubFundGroupService;

/**
 *  
 */
public class SubFundGroupServiceImpl implements SubFundGroupService {

    private SubFundGroupDao subFundGroupDao;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.SubFundGroupService#getByPrimaryId(java.lang.String)
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode) {
        return subFundGroupDao.getByPrimaryId(subFundGroupCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.SubFundGroupService#getByChartAndAccount(java.lang.String, java.lang.String)
     */
    public SubFundGroup getByChartAndAccount(String chartCode, String accountNumber) {
        return subFundGroupDao.getByChartAndAccount(chartCode, accountNumber);
    }

    /**
     * 
     * @param subFundGroupDao
     */
    public void setSubFundGroupDao(SubFundGroupDao subFundGroupDao) {
        this.subFundGroupDao = subFundGroupDao;
    }
}
