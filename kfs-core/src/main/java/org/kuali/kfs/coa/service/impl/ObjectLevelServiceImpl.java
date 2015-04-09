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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This service implementation is the default implementation of the ObjLevel service that is delivered with Kuali.
 */
public class ObjectLevelServiceImpl implements ObjectLevelService {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.coa.service.ObjectLevelService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectLevel getByPrimaryId(String chartOfAccountsCode, String objectLevelCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, objectLevelCode);
        return getBusinessObjectService().findByPrimaryKey(ObjectLevel.class, keys);
    }

    @Override
    public List<ObjectLevel> getObjectLevelsByConsolidationsIds(List<String> consolidationIds) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, consolidationIds);

        List<ObjectLevel> results = new ArrayList<ObjectLevel>();
        results.addAll(getBusinessObjectService().findMatching(ObjectLevel.class, fieldValues));
        return results;
    }

    @Override
    public List<ObjectLevel> getObjectLevelsByLevelIds(List<String> levelCodes) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCodes);

        List<ObjectLevel> results = new ArrayList<ObjectLevel>();
        results.addAll(getBusinessObjectService().findMatching(ObjectLevel.class, fieldValues));
        return results;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
