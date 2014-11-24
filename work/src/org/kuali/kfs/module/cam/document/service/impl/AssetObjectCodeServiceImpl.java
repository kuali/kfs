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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class AssetObjectCodeServiceImpl implements AssetObjectCodeService {
    UniversityDateService universityDateService;
    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetObjectCodeService#findAssetObjectCode(java.lang.String,
     *      org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public AssetObjectCode findAssetObjectCode(String chartOfAccountsCode, String financialObjectSubTypeCode) {
        Map<String, Object> pkKeys = new HashMap<String, Object>();
        pkKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityDateService().getCurrentFiscalYear());
        pkKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        pkKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE, financialObjectSubTypeCode);
        return (AssetObjectCode) getBusinessObjectService().findByPrimaryKey(AssetObjectCode.class, pkKeys);
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
