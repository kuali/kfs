/*
 * Copyright 2008 The Kuali Foundation
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
