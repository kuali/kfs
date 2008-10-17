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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants.CAPITAL_ASSETS_BATCH;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetGlobalServiceImpl implements AssetGlobalService {

    private enum AmountCategory {

        PAYMENT {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPayment(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT);
                postable.setFinancialObjectCode(assetPaymentDetail.getFinancialObjectCode());
                postable.setObjectCode(assetPaymentDetail.getObjectCode());
            };

        },
        PAYMENT_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPaymentOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT_OFFSET);
                postable.setFinancialObjectCode(acquisitionType.getIncomeAssetObjectCode());
                postable.setObjectCode(SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), acquisitionType.getIncomeAssetObjectCode()));
            };

        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType);
    }

    private ParameterService parameterService;
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private AssetObjectCodeService assetObjectCodeService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;

    private static final Logger LOG = Logger.getLogger(AssetGlobalServiceImpl.class);

    // ***********
    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     * 
     * @param universityDateService University Date Service
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    protected AssetGlpeSourceDetail createAssetGlpePostable(AssetGlobal document, AssetPaymentDetail assetPaymentDetail, AmountCategory amountCategory) {
        LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + ")");
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
        postable.setAccount(assetPaymentDetail.getAccount());

        postable.setAmount(assetPaymentDetail.getAmount());
        postable.setAccountNumber(assetPaymentDetail.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        postable.setChartOfAccountsCode(assetPaymentDetail.getChartOfAccountsCode());
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPaymentDetail.getProjectCode());
        postable.setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());

        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetPaymentDetail.getChartOfAccountsCode(), CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        document.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE);
        amountCategory.setParams(postable, assetPaymentDetail, assetObjectCode, offsetDefinition, document.getAcquisitionType());

        LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + "-" + ")");
        return postable;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#createGLPostables(org.kuali.module.cams.document.AssetGlobal)
     */
    public void createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster) {
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            if (isPaymentFinancialObjectActive(assetPaymentDetail)) {
                KualiDecimal accountChargeAmount = assetPaymentDetail.getAmount();
                if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT));
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT_OFFSET));
                }
            }
        }
    }


    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * Gets the assetObjectCodeService attribute.
     * 
     * @return Returns the assetObjectCodeService.
     */
    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }


    /**
     * Sets the assetObjectCodeService attribute value.
     * 
     * @param assetObjectCodeService The assetObjectCodeService to set.
     */
    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    /**
     * Gets the assetPaymentService attribute.
     * 
     * @return Returns the assetPaymentService.
     */
    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }


    /**
     * Sets the assetPaymentService attribute value.
     * 
     * @param assetPaymentService The assetPaymentService to set.
     */
    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }


    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }


    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * We need to calculate asset total amount from the sum of its payments. Otherwise, the asset total amount could mismatch with the sum of payments.
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#totalPaymentByAsset(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal, boolean lastEntry) {
        KualiDecimal assetTotalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        int numberOfTotalAsset = assetGlobal.getAssetGlobalDetails().size();
        if (numberOfTotalAsset > 0) {
            for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                KualiDecimal assetPaymentUnitCost = assetPaymentDetail.getAmount().divide(new KualiDecimal(numberOfTotalAsset));
                if (lastEntry) {
                    assetPaymentUnitCost = assetPaymentDetail.getAmount().subtract(assetPaymentUnitCost.multiply(new KualiDecimal(numberOfTotalAsset - 1)));
                }
                assetTotalAmount = assetTotalAmount.add(assetPaymentUnitCost);
            }
        }

        return assetTotalAmount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#existsInGroup(java.lang.String, java.lang.String)
     */
    public boolean existsInGroup(String groupName, String memberName) {
        if (StringUtils.isBlank(groupName) || StringUtils.isBlank(memberName)) {
            return false;
        }
        return Arrays.asList(groupName.split(";")).contains(memberName);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#totalNonFederalPaymentByAsset(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public KualiDecimal totalNonFederalPaymentByAsset(AssetGlobal assetGlobal) {
        KualiDecimal totalNonFederal = KualiDecimal.ZERO;

        // each paymentDetails contains the totalNonFederal amount for all the new created asset(s).
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            // It should be in the parameter list of CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
            // instead of CamsConstants.Parameters.FEDERAL_OWNED_OBJECT_SUB_TYPES
            if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && !Arrays.asList(parameterService.getParameterValue(CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES).split(";")).contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode())) {
                totalNonFederal = totalNonFederal.add(assetPaymentDetail.getAmount());
            }
        }
        return totalNonFederal;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#isCapitablObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    public boolean isCapitablObjectCode(ObjectCode objectCode) {
        return ObjectUtils.isNotNull(objectCode) && StringUtils.isNotBlank(objectCode.getFinancialObjectSubTypeCode()) && Arrays.asList(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITAL_OBJECT_SUB_TYPES).split(";")).contains(objectCode.getFinancialObjectSubTypeCode());
    }

    private boolean isPaymentFinancialObjectActive(AssetPaymentDetail assetPayment) {
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

    /**
     * Gets the assetService attribute.
     * 
     * @return Returns the assetService.
     */
    public AssetService getAssetService() {
        return assetService;
    }

    /**
     * Sets the assetService attribute value.
     * 
     * @param assetService The assetService to set.
     */
    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Validates if the document type is that of Asset Separate.
     * 
     * @param assetGlobal
     * @return boolean
     */
    public boolean isAssetSeparateDocument(AssetGlobal assetGlobal) {
        if (ObjectUtils.isNotNull(assetGlobal.getFinancialDocumentTypeCode()) && assetGlobal.getFinancialDocumentTypeCode().equals(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE)) {
            return true;
        }
        return false;
    }

    /**
     * Add and return the total amount for all unique assets created.
     * 
     * @param assetGlobal
     * @return Returns the total separate source amount
     */
    public KualiDecimal getUniqueAssetsTotalAmount(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        // add new asset location
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            // existing
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                totalAmount = totalAmount.add(assetGlobalUniqueDetail.getSeparateSourceAmount());
            }
        }
        return totalAmount;
    }
}
