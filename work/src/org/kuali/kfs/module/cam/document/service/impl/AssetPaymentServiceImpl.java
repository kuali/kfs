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

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.dao.AssetPaymentDao;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetPaymentServiceImpl implements AssetPaymentService {

    private AssetPaymentDao assetPaymentDao;
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public AssetPaymentDao getAssetPaymentDao() {
        return assetPaymentDao;
    }


    public void setAssetPaymentDao(AssetPaymentDao assetPaymentDao) {
        this.assetPaymentDao = assetPaymentDao;
    }


    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }


    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#getMaxSequenceNumber(org.kuali.module.cams.bo.AssetPayment)
     */
    public Integer getMaxSequenceNumber(AssetPayment assetPayment) {
        return this.assetPaymentDao.getMaxSquenceNumber(assetPayment);
    }

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#isPaymentFederalContribution(org.kuali.module.cams.bo.AssetPayment)
     */
    public boolean isPaymentFederalContribution(AssetPayment assetPayment) {
        assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
            return parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES).contains(assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
        }
        return false;
    }

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#isPaymentFinancialObjectActive(org.kuali.module.cams.bo.AssetPayment)
     */
    public boolean isPaymentFinancialObjectActive(AssetPayment assetPayment) {
        ObjectCode financialObjectCode = new ObjectCode();
        financialObjectCode.setUniversityFiscalYear(getUniversityDateService().getCurrentFiscalYear());
        financialObjectCode.setChartOfAccountsCode(assetPayment.getChartOfAccountsCode());
        financialObjectCode.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        financialObjectCode = (ObjectCode) getBusinessObjectService().retrieve(financialObjectCode);
        if (ObjectUtils.isNotNull(financialObjectCode)) {
            return financialObjectCode.isActive();
        }
        return false;
    }

}
