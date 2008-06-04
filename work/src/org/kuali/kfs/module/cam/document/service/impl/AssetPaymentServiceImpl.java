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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.dao.AssetPaymentDao;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetPaymentServiceImpl implements AssetPaymentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private AssetPaymentDao assetPaymentDao;
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private ObjectCodeService objectCodeService;
    private AssetRetirementService assetRetirementService;
    private AssetService assetService;

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#getMaxSequenceNumber(org.kuali.module.cams.bo.AssetPayment)
     */
    public Integer getMaxSequenceNumber(Long capitalAssetNumber) {
        return this.getAssetPaymentDao().getMaxSquenceNumber(capitalAssetNumber);
    }

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#isPaymentFederalContribution(org.kuali.module.cams.bo.AssetPayment)
     */
    public boolean isPaymentFederalContribution(AssetPayment assetPayment) {
        assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
            return this.getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES).contains(assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
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


    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#processApprovedAssetPayment(org.kuali.module.cams.document.AssetPaymentDocument)
     */
    public void processApprovedAssetPayment(AssetPaymentDocument document) {
        // Creating new asset payment records
        createNewPayments(document);

        // Updating the total cost of the asset
        updateAssetTotalCost(document.getAsset(), document.getSourceTotal());
    }


    /**
     * This method updates the total cost amount of the asset by adding the total cost of the new asset payments
     * 
     * @param asset bo where the update will occur
     * @param subTotal amount of the new asset payment detail records
     */
    private void updateAssetTotalCost(Asset asset, KualiDecimal subTotal) {
        KualiDecimal totalCost = subTotal.add(asset.getTotalCostAmount());
        asset = (Asset) getBusinessObjectService().retrieve(asset);
        asset.setTotalCostAmount(totalCost);
        getBusinessObjectService().save(asset);
    }

    /**
     * Creates a new asset payment record for each new asset payment detail record and then save them
     * 
     * @param document
     */
    private void createNewPayments(AssetPaymentDocument document) {
        List<AssetPaymentDetail> assetPaymentDetailLines = document.getAssetPaymentDetail();
        List<PersistableBusinessObject> assetPayments = new ArrayList<PersistableBusinessObject>();
        Integer maxSequenceNo = this.getMaxSequenceNumber(document.getAsset().getCapitalAssetNumber());

        try {
            // Creating a new payment record for each asset payment detail.
            for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetailLines) {
                AssetPayment assetPayment = new AssetPayment(assetPaymentDetail);
                assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_N);
                assetPayment.setCapitalAssetNumber(document.getAsset().getCapitalAssetNumber());
                assetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                assetPayment.setDocumentNumber(document.getDocumentNumber());

                KualiDecimal baseAmount = new KualiDecimal(0);

                // If the object sub type is not in the list of federally owned object sub types, then...
                ObjectCode objectCode = this.getObjectCodeService().getByPrimaryId(assetPaymentDetail.getFinancialDocumentPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getFinancialObjectCode());

                // Depreciation Base Amount will be assigned to each payment only when the object code's sub type code is not a
                // federally owned one
                if (!this.isFederallyOwnedObjectSubType(objectCode.getFinancialObjectSubTypeCode())) {
                    baseAmount = assetPaymentDetail.getAmount();
                }
                assetPayment.setPrimaryDepreciationBaseAmount(baseAmount);

                // Resetting each period field its value with nulls
                this.adjustPaymentAmounts(assetPayment, false, true);

                // add new payment
                assetPayments.add(assetPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Finally, saving all the asset payment records.
        this.getBusinessObjectService().save(assetPayments);
    }

    /**
     * This method checks that a given object sub type exists in a list of federally owned object sub types
     * 
     * @param objectSubType
     * @return boolean
     */
    private boolean isFederallyOwnedObjectSubType(String objectSubType) {
        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (this.getParameterService().parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = this.getParameterService().getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES);
        }
        return federallyOwnedObjectSubTypes.contains(objectSubType);
    }

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#adjustPaymentAmounts(org.kuali.module.cams.bo.AssetPayment, boolean,
     *      boolean)
     */
    public void adjustPaymentAmounts(AssetPayment assetPayment, boolean reverseAmount, boolean nullPeriodDepreciation) throws IllegalAccessException, InvocationTargetException {
        LOG.debug("Starting - adjustAmounts() ");
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetPayment.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && propertyDescriptor.getPropertyType() != null && propertyDescriptor.getPropertyType().isAssignableFrom(KualiDecimal.class)) {
                KualiDecimal amount = (KualiDecimal) readMethod.invoke(assetPayment);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null && amount != null) {
                    // Reset periodic depreciation expenses
                    if (nullPeriodDepreciation && Pattern.matches(CamsConstants.SET_PERIOD_DEPRECIATION_AMOUNT_REGEX, writeMethod.getName().toLowerCase())) {
                        Object[] nullVal = new Object[] { null };
                        writeMethod.invoke(assetPayment, nullVal);
                    }
                    else if (reverseAmount) {
                        // reverse the amounts
                        writeMethod.invoke(assetPayment, (amount).multiply(new KualiDecimal(-1)));
                    }
                }

            }
        }
        LOG.debug("Finished - adjustAmounts()");
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
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

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetRetirementService getAssetRetirementService() {
        return assetRetirementService;
    }

    public void setAssetRetirementService(AssetRetirementService assetRetirementService) {
        this.assetRetirementService = assetRetirementService;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * @see org.kuali.module.cams.service.AssetPaymentService#isPaymentEligibleForGLPosting(org.kuali.module.cams.bo.AssetPayment)
     */
    public boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment) {
        // Payment transfer code is not "Y", Financial Object Code is active for the Payment and is not a Federal Contribution
        return !CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode()) && isPaymentFinancialObjectActive(assetPayment) && !isPaymentFederalContribution(assetPayment);
    }
}