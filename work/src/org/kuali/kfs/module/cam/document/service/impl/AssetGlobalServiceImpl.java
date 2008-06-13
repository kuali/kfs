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
                //postable.setObjectCode(assetPaymentDetail.getObjectCode());
            };

        },
        PAYMENT_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPaymentOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT_OFFSET);
                postable.setFinancialObjectCode(acquisitionType.getIncomeAssetObjectCode());
                //postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

        },
        CAPITALIZATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setCapitalization(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_CAPITALIZATION);
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                //postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

        },
        CAPITALIZATION_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setCapitalizationOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_CAPITALIZATION_OFFSET);
                postable.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                //postable.setObjectCode(offsetDefinition.getFinancialObject());
            };

        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType);

        // abstract boolean isObjectCodeExists(AssetObjectCode assetObjectCode);
    }

    // TODO: replaced by system parameters
    public static final String MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "CM;CF;C1;C2;UC;UF;BR;BY";
    public static final String NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES = "AM;BD;BF;BI;CP;ES;IF;LA;LE;LI;LF;LR";
    public static final String DEFAULT_GAIN_LOSS_DISPOSITION_FINANCIAL_OBJECT_CODE = "4998";

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
        String organizationOwnerChartOfAccountsCode = null;
        postable.setChartOfAccountsCode(organizationOwnerChartOfAccountsCode);
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPaymentDetail.getProjectCode());
        postable.setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());
        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(organizationOwnerChartOfAccountsCode, assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), organizationOwnerChartOfAccountsCode, CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
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
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
            AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetPaymentDetails.get(0).getObjectCode().getFinancialObjectSubTypeCode());
            AssetPaymentDetail firstAssetPaymentDetail = assetPaymentDetails.get(0);
            firstAssetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
            String finObjectSubTypeCode = firstAssetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode();
            boolean movableAsset = getAssetService().isMovableFinancialObjectSubtypeCode(finObjectSubTypeCode);

            if (isGLPostable(assetGlobal, movableAsset)) {
                Account srcPlantAcct = null;
                // TODO: needed ??
                OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetGlobal.getOrganizationOwnerChartOfAccountsCode(), CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);

                if (movableAsset) {
                    srcPlantAcct = assetGlobal.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
                }
                else {
                    srcPlantAcct = assetGlobal.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                    if (isPaymentEligibleForGLPosting(assetPaymentDetail)) {
                        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
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
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @return
     */
    private Account getPlantFundAccount(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        Account plantFundAccount = null;

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);

        if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && ObjectUtils.isNotNull(assetGlobal.getOrganizationOwnerAccount())) {
            String financialObjectSubTypeCode = assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode();

            if (StringUtils.isNotBlank(financialObjectSubTypeCode)) {
                if (Arrays.asList(MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = assetGlobal.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                else if (Arrays.asList(NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES.split(";")).contains(financialObjectSubTypeCode)) {
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
    public boolean isPaymentEligibleForGLPosting(AssetPaymentDetail assetPaymentDetail) {
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
    public boolean isPaymentFinancialObjectActive(AssetPaymentDetail assetPayment) {
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
    public boolean isPaymentFederalContribution(AssetPaymentDetail assetPaymentDetail) {
        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
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
