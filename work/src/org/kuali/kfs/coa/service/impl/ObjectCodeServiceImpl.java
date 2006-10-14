/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/impl/ObjectCodeServiceImpl.java,v $
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

import java.util.List;

import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.dao.ObjectCodeDao;
import org.kuali.module.chart.service.ObjectCodeService;

/**
 * This class is the service implementation for the ObjectCode structure. This is the default implementation, that is delivered with
 * Kuali.
 * 
 * 
 */
public class ObjectCodeServiceImpl implements ObjectCodeService {
    private ObjectCodeDao objectCodeDao;

    /**
     * Retrieves an Account object based on primary key.
     * 
     * @param universityFiscalYear - University Fiscal Year
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     */
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        return objectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @return ObjectCodeDao
     */
    public ObjectCodeDao getObjectCodeDao() {
        return objectCodeDao;
    }

    /**
     * @param objectCodeDao
     */
    public void setObjectCodeDao(ObjectCodeDao objectCodeDao) {
        this.objectCodeDao = objectCodeDao;
    }

    public List getYearList(String chartOfAccountsCode, String financialObjectCode) {
        return objectCodeDao.getYearList(chartOfAccountsCode, financialObjectCode);
    }

}
