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

import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.service.ObjectConsService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectConsServiceImpl implements ObjectConsService {

    /**
     * @see org.kuali.kfs.coa.service.ObjectConsService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjectConsolidation getByPrimaryId(String chartOfAccountsCode, String objectConsCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, objectConsCode);
        return (ObjectConsolidation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectConsolidation.class, keys);
    }


}
