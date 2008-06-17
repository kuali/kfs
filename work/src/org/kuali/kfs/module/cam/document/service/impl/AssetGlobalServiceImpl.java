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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetAcquisitionType;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
//import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.cams.service.AssetGlobalService;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.UniversityDateService;

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
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

        },
        CAPITALIZATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setCapitalization(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_CAPITALIZATION);
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

        },
        CAPITALIZATION_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setCapitalizationOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_CAPITALIZATION_OFFSET);
                postable.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                postable.setObjectCode(offsetDefinition.getFinancialObject());
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
     * @param plantAccount Plant account for the organization
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    protected AssetGlpeSourceDetail createAssetGlpePostable(AssetGlobal document, Account plantAccount, AssetPaymentDetail assetPaymentDetail, AmountCategory amountCategory) {
        LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + ")");
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
        postable.setAccount(plantAccount);
        postable.setAmount(assetPaymentDetail.getAmount());
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        postable.setChartOfAccountsCode(assetPaymentDetail.getChartOfAccountsCode());
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPaymentDetail.getProjectCode());
        postable.setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());
        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetPaymentDetail.getChartOfAccountsCode(), CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        document.refreshReferenceObject("acquisitionType");
        amountCategory.setParams(postable, assetPaymentDetail, assetObjectCode, offsetDefinition, document.getAcquisitionType());
        LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + "-" + ")");
        return postable;
    }

    /**
     * @see org.kuali.module.cams.service.AssetGlobalService#createGLPostables(org.kuali.module.cams.document.AssetGlobal)
     */
    public boolean createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster) {

        String acquisitionTypeCode = assetGlobal.getAcquisitionTypeCode();
        // Create GL entries only for capital assets
        if (!CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE.equals(acquisitionTypeCode) && !assetGlobal.getAssetPaymentDetails().isEmpty()) {
            List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
            AssetPaymentDetail firstAssetPaymentDetail = assetPaymentDetails.get(0);
            firstAssetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
            String finObjectSubTypeCode = firstAssetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode();
            boolean movableAsset = getAssetService().isMovableFinancialObjectSubtypeCode(finObjectSubTypeCode);

            if (isGLPostable(assetGlobal, movableAsset)) {
                Account srcPlantAcct = null;
                OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetGlobal.getOrganizationOwnerChartOfAccountsCode(), CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
                firstAssetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
                if (ObjectUtils.isNull(firstAssetPaymentDetail.getAccount())){
                    return false;
                }
                if (movableAsset) {
                    srcPlantAcct = firstAssetPaymentDetail.getAccount().getOrganization().getOrganizationPlantAccount();
                }
                else {
                    srcPlantAcct = firstAssetPaymentDetail.getAccount().getOrganization().getCampusPlantAccount();
                }
                for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                    if (isPaymentEligibleForGLPosting(assetPaymentDetail)) {
                        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
                        if (ObjectUtils.isNotNull(assetPaymentDetail.getFinancialObjectCode())) {
                            KualiDecimal accountChargeAmount = assetPaymentDetail.getAmount();
                            if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                                assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, srcPlantAcct, assetPaymentDetail, AmountCategory.CAPITALIZATION));
                                assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, srcPlantAcct, assetPaymentDetail, AmountCategory.CAPITALIZATION_OFFSET));
                                assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, srcPlantAcct, assetPaymentDetail, AmountCategory.PAYMENT));
                                assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, srcPlantAcct, assetPaymentDetail, AmountCategory.PAYMENT_OFFSET));
                            }

                        }
                    }
                }
            }
        }
        return true;
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


    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        int numberOfTotalAsset = 0;

        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            numberOfTotalAsset += assetSharedDetail.getAssetGlobalUniqueDetails().size();
        }

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            totalAmount = totalAmount.add(assetPaymentDetail.getAmount());
        }

        if (numberOfTotalAsset != 0) {
            return totalAmount.divide(new KualiDecimal(numberOfTotalAsset));
        }
        return totalAmount;
    }


    public boolean existsInGroup(String groupName, String memberName) {
        if (StringUtils.isBlank(groupName) || StringUtils.isBlank(memberName)) {
            return false;
        }
        return Arrays.asList(groupName.split(";")).contains(memberName);
    }

    public KualiDecimal totalNonFederalPaymentByAsset(AssetGlobal assetGlobal) {
        KualiDecimal totalNonFederal = KualiDecimal.ZERO;
        int numberOfAssets = assetGlobal.getAssetGlobalDetails().size();

        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && !Arrays.asList(parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES).split(";")).contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode())) {
                totalNonFederal = totalNonFederal.add(assetPaymentDetail.getAmount());
            }
        }

        if (numberOfAssets != 0) {
            return totalNonFederal.divide(new KualiDecimal(numberOfAssets));
        }
        return totalNonFederal;
    }

   
    /**
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @return
     */
    private Account getPlantFundAccount(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        Account plantFundAccount = null;

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);

        if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && ObjectUtils.isNotNull(assetGlobal.getOrganizationOwnerAccount())) {
            String financialObjectSubTypeCode = assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode();

            if (StringUtils.isNotBlank(financialObjectSubTypeCode)) {
                if (Arrays.asList(parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).split(";")).contains(financialObjectSubTypeCode)) {    
                    plantFundAccount = assetGlobal.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                else if (Arrays.asList(parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = assetGlobal.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
                }
            }
        }

        return plantFundAccount;
    }

    public boolean isCapitablObjectCode(ObjectCode objectCode) {
        return ObjectUtils.isNotNull(objectCode) && StringUtils.isNotBlank(objectCode.getFinancialObjectSubTypeCode()) && Arrays.asList(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITAL_OBJECT_SUB_TYPES).split(";")).contains(objectCode.getFinancialObjectSubTypeCode());
    }

    /**
     * @see org.kuali.module.cams.service.AssetGlobalService#isPaymentEligibleForGLPosting(org.kuali.module.cams.bo.AssetPaymentDetail)
     */
    private boolean isPaymentEligibleForGLPosting(AssetPaymentDetail assetPaymentDetail) {
        boolean isEligible = true;
        // Financial object code is currently active
        isEligible &= isPaymentFinancialObjectActive(assetPaymentDetail);
        // Payment is not federally funded
        isEligible &= !isPaymentFederalContribution(assetPaymentDetail);
        return isEligible;
    }

    /**
     * @see org.kuali.module.cams.service.AssetGlobalService#isPaymentFinancialObjectActive(org.kuali.module.cams.bo.AssetPaymentDetail)
     */
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
     * @see org.kuali.module.cams.service.AssetGlobalService#isPaymentFederalContribution(org.kuali.module.cams.bo.AssetPaymentDetail)
     */
    private boolean isPaymentFederalContribution(AssetPaymentDetail assetPaymentDetail) {
        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode())) {
            return this.getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES).contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
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
     * Checks if it is ready for GL Posting by validating the accounts and plant account numbers
     * 
     * @return true if all accounts are valid
     */
    protected boolean isGLPostable(AssetGlobal assetGlobal, boolean movableAsset) {
        boolean isGLPostable = true;

        Account plantAcct = null;

        if (ObjectUtils.isNotNull(assetGlobal.getOrganizationOwnerAccount())) {
            if (movableAsset) {
                plantAcct = assetGlobal.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
            }
            else {
                plantAcct = assetGlobal.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
            }
        }

        if (ObjectUtils.isNull(plantAcct)) {
            isGLPostable &= false;
        }

        return isGLPostable;
    }

}
