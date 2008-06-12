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
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.module.cams.service.AssetGlobalService;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetGlobalServiceImpl implements AssetGlobalService {
    private enum AmountCategory {
        CAPITALIZATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalization(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_PLANT_FUND_FOR_FMS);
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.CAPITALIZATION_FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(assetObjectCode.getCapitalizationFinancialObject())) {
                    return false;
                }
                return true;
            };
        },
        ACCUMMULATE_DEPRECIATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setAccumulatedDepreciation(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_ACCUMULATED_DEPRECIATION);
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.ACCUMULATED_DEPRECIATION_FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(assetObjectCode.getAccumulatedDepreciationFinancialObject())) {
                    return false;
                }
                return true;
            };
        },
        OFFSET_AMOUNT {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_GAIN_LOSS_DISPOSITION);
                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
                postable.setFinancialObjectCode(DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
                postable.setObjectCode(getOffsetFinancialObject(assetPayment.getAsset()));
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                return true;
            };
        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode);

        abstract boolean isObjectCodeExists(AssetObjectCode assetObjectCode);
    }

    // TODO: replaced by system parameters
    public static final String MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "CM;CF;C1;C2;UC;UF;BR;BY";
    public static final String NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "AM;BD;BF;BI;CP;ES;IF;LA;LE;LI;LF;LR";
    public static final String DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE = "4998";

    ParameterService parameterService;
    UniversityDateService universityDateService;
    AssetObjectCodeService assetObjectCodeService;
    BusinessObjectService businessObjectService;
    AssetPaymentService assetPaymentService;

    private static final Logger LOG = Logger.getLogger(AssetTransferServiceImpl.class);

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
            if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES, assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode()) != null) {
                totalNonFederal = totalNonFederal.add(assetPaymentDetail.getAmount());
            }
        }

        if (numberOfAssets != 0) {
            return totalNonFederal.divide(new KualiDecimal(numberOfAssets));
        }
        return totalNonFederal;
    }

    /**
     * 
     * @see org.kuali.module.cams.service.AssetRetirementService#createGLPostables(org.kuali.module.cams.bo.AssetRetirementGlobal,
     *      org.kuali.module.cams.gl.CamsGlPosterBase)
     */
    public boolean createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster) {
        boolean success = true;

        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();

        for (AssetGlobalDetail assetGlobalDetail : assetGlobalDetails) {
            Asset asset = assetGlobalDetail.getAsset();

            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
                if (success = generateGlPostablesForOnePayment(assetGlobal.getDocumentNumber(), assetRetirementGlPoster, asset, assetPayment, postables)) {
                    assetRetirementGlPoster.getPostables().addAll(postables);
                }
            }

        }
        return success;
    }

    /**
     * 
     * Generate a collection of Postables for each payment.
     * 
     * @param documentNumber
     * @param assetGlobalGlPoster
     * @param asset
     * @param assetPayment
     * @return
     */
    private boolean generateGlPostablesForOnePayment(String documentNumber, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster, Asset asset, AssetPayment assetPayment, List<GeneralLedgerPendingEntrySourceDetail> postables) {
        boolean success = true;
        Account plantAccount = getPlantFundAccount(asset, assetPayment);

        if (ObjectUtils.isNotNull(plantAccount)) {
            KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
            KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();

            if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                success = createNewPostable(AmountCategory.CAPITALIZATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (accumlatedDepreciationAmount != null && !accumlatedDepreciationAmount.isZero()) {
                success = createNewPostable(AmountCategory.ACCUMMULATE_DEPRECIATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (accountChargeAmount != null && accumlatedDepreciationAmount != null && !accountChargeAmount.subtract(accumlatedDepreciationAmount).isZero()) {
                success = createNewPostable(AmountCategory.OFFSET_AMOUNT, asset, assetPayment, documentNumber, plantAccount, postables);
            }
        }
        return success;
    }

    /**
     * 
     * This method creates one postable and sets the values.
     * 
     * @param category
     * @param asset
     * @param assetPayment
     * @param documentNumber
     * @param plantAccount
     * @return
     */
    private boolean createNewPostable(AmountCategory category, Asset asset, AssetPayment assetPayment, String documentNumber, Account plantAccount, List<GeneralLedgerPendingEntrySourceDetail> postables) {
        boolean success = true;
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment);
        if (category.isObjectCodeExists(assetObjectCode)) {
            category.setParams(postable, assetPayment, assetObjectCode);

            // Set Postable attributes which are common among Capitalized, Accumulated Depreciation and gain/loss disposition .
            postable.setDocumentNumber(documentNumber);
            postable.setAccount(plantAccount);
            postable.setAccountNumber(plantAccount.getAccountNumber());
            postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
            postable.setChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());

            postable.setPostingYear(universityDateService.getCurrentFiscalYear());
            // Fields copied from payment
            postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
            postable.setProjectCode(assetPayment.getProjectCode());
            postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
            postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
            postables.add(postable);
        }
        else {
            success = false;
        }
        return success;
    }


    /**
     * Get the offset Object Code.
     * 
     * @param asset
     * @return
     */
    static private ObjectCode getOffsetFinancialObject(Asset asset) {
        Map pkMap = new HashMap();
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        pkMap.put("universityFiscalYear", universityDateService.getCurrentFiscalYear());
        pkMap.put("chartOfAccountsCode", asset.getOrganizationOwnerChartOfAccountsCode());
        pkMap.put("financialObjectCode", DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        ObjectCode offsetFinancialObject = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pkMap);

        if (ObjectUtils.isNull(offsetFinancialObject)) {
            throw new ReferentialIntegrityException("Object code is not defined for this universityFiscalYear=" + universityDateService.getCurrentFiscalYear() + ", chartOfAccountsCode=" + asset.getOrganizationOwnerChartOfAccountsCode() + ", financialObjectCode=" + DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE);
        }

        return offsetFinancialObject;
    }


    /**
     * 
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     * 
     * @param asset
     * @param payment
     * @return
     */
    private Account getPlantFundAccount(Asset asset, AssetPayment payment) {
        Account plantFundAccount = null;

        payment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);

        if (ObjectUtils.isNotNull(payment.getFinancialObject()) && ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
            String financialObjectSubTypeCode = payment.getFinancialObject().getFinancialObjectSubTypeCode();

            if (StringUtils.isNotBlank(financialObjectSubTypeCode)) {
                if (Arrays.asList(MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                else if (Arrays.asList(NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
                }
            }
        }

        return plantFundAccount;
    }

    public boolean isCapitablObjectCode(ObjectCode objectCode) {
        return ObjectUtils.isNotNull(objectCode) && StringUtils.isNotBlank(objectCode.getFinancialObjectSubTypeCode()) && Arrays.asList(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITAL_OBJECT_SUB_TYPES).split(";")).contains(objectCode.getFinancialObjectSubTypeCode());
    }
}
