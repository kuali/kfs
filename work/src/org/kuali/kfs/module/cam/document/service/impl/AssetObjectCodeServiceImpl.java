/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetObjectCodeServiceImpl implements AssetObjectCodeService {
    UniversityDateService universityDateService;
    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.cams.service.AssetObjectCodeService#findAssetObjectCode(java.lang.String,
     *      org.kuali.module.cams.bo.AssetPayment)
     */
    public AssetObjectCode findAssetObjectCode(String chartOfAccountsCode, AssetPayment assetPayment) {
        Map<String, Object> pkKeys = new HashMap<String, Object>();
        pkKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityDateService().getCurrentFiscalYear());
        pkKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        pkKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE, assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
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
