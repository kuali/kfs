/*
 * Copyright 2005 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.dataaccess.OffsetDefinitionDao;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * This class is the service implementation for the OffsetDefinition structure. This is the default implementation, that is
 * delivered with Kuali.
 */

@NonTransactional
public class OffsetDefinitionServiceImpl implements OffsetDefinitionService {
    private OffsetDefinitionDao offsetDefinitionDao;

    /**
     * @see org.kuali.kfs.coa.service.OffsetDefinitionService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public OffsetDefinition getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode) {
        return offsetDefinitionDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialDocumentTypeCode, financialBalanceTypeCode);
    }

    /**
     * @param offsetDefinitionDao The offsetDefinitionDao to set.
     */
    public void setOffsetDefinitionDao(OffsetDefinitionDao offsetDefinitionDao) {
        this.offsetDefinitionDao = offsetDefinitionDao;
    }
}
