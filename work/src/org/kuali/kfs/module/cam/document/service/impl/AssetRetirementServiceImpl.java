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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetRetirementServiceImpl implements AssetRetirementService {

    protected enum AmountCategory {

        CAPITALIZATION {
	    @Override
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalization(true);
                ParameterService parameterService = SpringContext.getBean(ParameterService.class);
                String lineDescription = parameterService.getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.AssetRetirementGlobal.CAPITALIZATION_LINE_DESCRIPTION);
                postable.setFinancialDocumentLineDescription(lineDescription);
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };
        },
        ACCUMMULATE_DEPRECIATION {
		@Override
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setAccumulatedDepreciation(true);
                ParameterService parameterService = SpringContext.getBean(ParameterService.class);
                String lineDescription = parameterService.getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.AssetRetirementGlobal.ACCUMULATED_DEPRECIATION_LINE_DESCRIPTION);
                postable.setFinancialDocumentLineDescription(lineDescription);
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
            };
        },
        OFFSET_AMOUNT {
		@Override
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalizationOffset(true);
                ParameterService parameterService = SpringContext.getBean(ParameterService.class);
                String lineDescription = parameterService.getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.AssetRetirementGlobal.OFFSET_AMOUNT_LINE_DESCRIPTION);
                postable.setFinancialDocumentLineDescription(lineDescription);

                KualiDecimal accumlatedDepreciationAmount = (assetPayment.getAccumulatedPrimaryDepreciationAmount() == null ? new KualiDecimal(0) : assetPayment.getAccumulatedPrimaryDepreciationAmount());

                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(accumlatedDepreciationAmount));
                postable.setFinancialObjectCode(SpringContext.getBean(ParameterService.class).getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE).trim());

                Map pkMap = new HashMap();
                UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
                String gainDispositionObjectCode = parameterService.getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE);

                pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, assetPayment.getAsset().getOrganizationOwnerChartOfAccountsCode());
                pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, gainDispositionObjectCode);

                ObjectCode offsetFinancialObject = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pkMap);

                postable.setObjectCode(offsetFinancialObject);
            };
        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode);
    }

    protected UniversityDateService universityDateService;
    protected AssetObjectCodeService assetObjectCodeService;
    protected BusinessObjectService businessObjectService;
    protected AssetPaymentService assetPaymentService;
    protected ParameterService parameterService;
    protected AssetService assetService;
    protected OrganizationService organizationService;
    protected ObjectCodeService objectCodeService;

    public ObjectCodeService getObjectCodeService(){
	    return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService serv){
	    this.objectCodeService = serv;
    }

    public OrganizationService getOrganizationService(){
	    return this.organizationService;
    }

    public void setOrganizationService(OrganizationService serv){
	    this.organizationService = serv;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }

    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredBySoldOrAuction(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
    public boolean isAssetRetiredByAuction(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.AUCTION.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredBySold(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
    public boolean isAssetRetiredBySold(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByExternalTransferOrGift(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
    public boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByMerged(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
    public boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByTheft(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
	public boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#getAssetRetirementReasonName(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    @Override
	public String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal) {
        return assetRetirementGlobal.getRetirementReason() == null ? new String() : assetRetirementGlobal.getRetirementReason().getRetirementReasonName();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#generateOffsetPaymentsForEachSource(org.kuali.kfs.module.cam.businessobject.Asset,
     *      java.util.List, java.lang.String)
     */
    @Override
	public void generateOffsetPaymentsForEachSource(Asset sourceAsset, List<PersistableBusinessObject> persistables, String currentDocumentNumber) {
        List<AssetPayment> offsetPayments = new ArrayList<AssetPayment>();
        Integer maxSequenceNo = assetPaymentService.getMaxSequenceNumber(sourceAsset.getCapitalAssetNumber());

        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment offsetPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, offsetPayment);
                offsetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_RETIREMENT_MERGE);
                offsetPayment.setDocumentNumber(currentDocumentNumber);
                offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                assetPaymentService.adjustPaymentAmounts(offsetPayment, true, false);
                offsetPayments.add(offsetPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating offset payment in retirement", e);
        }
        persistables.addAll(offsetPayments);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#generateNewPaymentForTarget(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset, java.util.List, java.lang.Integer, java.lang.String)
     */
    @Override
	public Integer generateNewPaymentForTarget(Asset targetAsset, Asset sourceAsset, List<PersistableBusinessObject> persistables, Integer maxSequenceNo, String currentDocumentNumber) {
        List<AssetPayment> newPayments = new ArrayList<AssetPayment>();
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment newPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, newPayment);
                newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                newPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_RETIREMENT_MERGE);
                newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                newPayment.setDocumentNumber(currentDocumentNumber);
                assetPaymentService.adjustPaymentAmounts(newPayment, false, false);
                newPayments.add(newPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating new payment in retirement", e);
        }
        persistables.addAll(newPayments);
        return maxSequenceNo;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isRetirementReasonCodeInGroup(java.lang.String,
     *      java.lang.String)
     */
    @Override
	public boolean isRetirementReasonCodeInGroup(String reasonCodeGroup, String reasonCode) {
        if (StringUtils.isBlank(reasonCodeGroup) || StringUtils.isBlank(reasonCode)) {
            return false;
        }
        return Arrays.asList(reasonCodeGroup.split(";")).contains(reasonCode);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAllowedRetireMultipleAssets(java.lang.String)
     */
    @Override
	public boolean isAllowedRetireMultipleAssets(MaintenanceDocument maintenanceDocument) {
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(maintenanceDocument);
        boolean isAuthorized = documentAuthorizer.isAuthorized(maintenanceDocument, CamsConstants.CAM_MODULE_CODE,
                CamsConstants.PermissionNames.RETIRE_MULTIPLE, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        return isAuthorized;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#createGLPostables(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal,
     *      org.kuali.module.cams.gl.CamsGlPosterBase)
     */
    @Override
	public void createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster) {

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            Asset asset = assetRetirementGlobalDetail.getAsset();

            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment) && !("Y".equals(assetPayment.getTransferPaymentCode()))) {
                    List<GeneralLedgerPendingEntrySourceDetail> postables = generateGlPostablesForOnePayment(assetRetirementGlobal.getDocumentNumber(), assetRetirementGlPoster, asset, assetPayment);
                    assetRetirementGlPoster.getPostables().addAll(postables);
                }
            }
        }
    }

    /**
     * Generate a collection of Postables for each payment.
     *
     * @param documentNumber
     * @param assetRetirementGlPoster
     * @param asset
     * @param assetPayment
     * @return
     */
    protected List<GeneralLedgerPendingEntrySourceDetail> generateGlPostablesForOnePayment(String documentNumber, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster, Asset asset, AssetPayment assetPayment) {
        List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        Account plantAccount = getPlantFundAccount(assetPayment);

        if (ObjectUtils.isNotNull(plantAccount)) {
            if (assetPaymentService.isPaymentEligibleForCapitalizationGLPosting(assetPayment)) {
                createNewPostable(AmountCategory.CAPITALIZATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (assetPaymentService.isPaymentEligibleForAccumDeprGLPosting(assetPayment)) {
                createNewPostable(AmountCategory.ACCUMMULATE_DEPRECIATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (assetPaymentService.isPaymentEligibleForOffsetGLPosting(assetPayment)) {
                createNewPostable(AmountCategory.OFFSET_AMOUNT, asset, assetPayment, documentNumber, plantAccount, postables);
            }
        }
        return postables;
    }

    /**
     * This method creates one postable and sets the values.
     *
     * @param category
     * @param asset
     * @param assetPayment
     * @param documentNumber
     * @param plantAccount
     * @return
     */
    protected void createNewPostable(AmountCategory category, Asset asset, AssetPayment assetPayment, String documentNumber, Account plantAccount, List<GeneralLedgerPendingEntrySourceDetail> postables) {
        boolean success = true;
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        AssetObjectCode assetObjectCode = getAssetObjectCode(asset, assetPayment);
        category.setParams(postable, assetPayment, assetObjectCode);

        // Set Postable attributes which are common among Capitalized, Accumulated Depreciation and gain/loss disposition .
        postable.setDocumentNumber(documentNumber);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        postable.setChartOfAccountsCode(plantAccount.getChartOfAccountsCode());

        postable.setPostingYear(universityDateService.getCurrentFiscalYear());
        // Fields copied from payment
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
        postables.add(postable);
    }

    protected AssetObjectCode getAssetObjectCode(Asset asset, AssetPayment assetPayment) {
        ObjectCodeService objectCodeService = getObjectCodeService();
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());

        AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());
        return assetObjectCode;
    }


    /**
     * Get the offset Object Code.
     *
     * @param asset
     * @return
     */
    @Override
	public ObjectCode getOffsetFinancialObject(String chartOfAccountsCode) {
        Map pkMap = new HashMap();
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String gainDispositionObjectCode = parameterService.getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE);

        pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
        pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, gainDispositionObjectCode);

        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pkMap);
    }


    /**
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     *
     * @param asset
     * @param payment
     * @return
     */
    protected Account getPlantFundAccount(AssetPayment payment) {
	Account plantFundAccount = null;

	/*Don't check for null. If this fails, we are without hope.*/
	payment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
	String finObjectCode = payment.getFinancialObjectCode();
	if (StringUtils.isNotEmpty(finObjectCode)){
		ObjectCodeService obj = getObjectCodeService();
		ObjectCode objectCode = obj.getByPrimaryIdForCurrentYear(payment.getChartOfAccountsCode(), finObjectCode);

		String financialObjectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();

		/*KFSCNTRB-1629 the plant fund account should be that from the payment's object code,
		* not the asset's object code.*/
		String coaCode = payment.getChartOfAccountsCode();
		Account tempAcct = payment.getAccount();
		String orgCode = tempAcct.getOrganizationCode();
		Organization org = getOrganizationService().getByPrimaryId(coaCode, orgCode);

		if (assetService.isAssetMovableCheckByPayment(financialObjectSubTypeCode)) {
			plantFundAccount = org.getOrganizationPlantAccount();
		} else {
			plantFundAccount = org.getCampusPlantAccount();
		}
	}

        return plantFundAccount;
    }

    /**
     * This method generates the calculatedTotal amount based on salePrice + handlingFeeAmount + preventiveMaintenanceAmount.
     *
     * @param salePrice
     * @param handlingFeeAmount
     * @param preventiveMaintenanceAmount
     * @return
     */
    @Override
	public String generateCalculatedTotal(String salePrice, String handlingFeeAmount, String preventiveMaintenanceAmount) {
        KualiDecimal calculatedTotal = KualiDecimal.ZERO;

        if (!salePrice.isEmpty()) {
            KualiDecimal testAmount = toKualiDecimal(salePrice);
            if(testAmount.isZero()){
              return "Please enter Sale Price in 1,234,567.00 Format";
            }
            calculatedTotal = calculatedTotal.add(testAmount);
        }
        if (!handlingFeeAmount.isEmpty()) {
            KualiDecimal testAmount = toKualiDecimal(handlingFeeAmount);
            if(testAmount.isZero()){
               return "Please enter Handling Fee Amount in 1,234,567.00 Format";
            }
            calculatedTotal = calculatedTotal.add(testAmount);
        }
        if (!preventiveMaintenanceAmount.isEmpty()) {
            KualiDecimal testAmount = toKualiDecimal(preventiveMaintenanceAmount);
            if(testAmount.isZero()){
              return "Please enter Preventive Maintenance Amount in 1,234,567.00 Format";
            }
            calculatedTotal = calculatedTotal.add(testAmount);
        }

        return calculatedTotal.toString();
    }

    /**
     * This method converts a String to a KualiDecimal via Double. Or else returns a Zero to invoke proper error message and to avoid breaking DWR call
     *
     * @param amount
     * @return
     */
    protected KualiDecimal toKualiDecimal(String amount) {
        KualiDecimal newAmount;
        try{
            newAmount = new KualiDecimal(java.lang.Double.parseDouble(amount));
        }catch(NumberFormatException e){
            return KualiDecimal.ZERO;
        }
        return newAmount;
    }

}

