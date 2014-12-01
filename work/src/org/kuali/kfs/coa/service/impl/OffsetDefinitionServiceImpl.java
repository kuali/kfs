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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the OffsetDefinition structure. This is the default implementation, that is
 * delivered with Kuali.
 */

@NonTransactional
public class OffsetDefinitionServiceImpl implements OffsetDefinitionService {

    /**
     * @see org.kuali.kfs.coa.service.OffsetDefinitionService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public OffsetDefinition getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, financialDocumentTypeCode);
        keys.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, financialBalanceTypeCode);
        return (OffsetDefinition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, keys);
    }

}
