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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.dataaccess.AssetPaymentDao;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
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
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#getMaxSequenceNumber(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public Integer getMaxSequenceNumber(Long capitalAssetNumber) {
        return this.getAssetPaymentDao().getMaxSquenceNumber(capitalAssetNumber);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentFederalOwned(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentFederalOwned(AssetPayment assetPayment) {
        assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
            return this.getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.FEDERAL_OWNED_OBJECT_SUB_TYPES).contains(assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentFinancialObjectActive(org.kuali.kfs.module.cam.businessobject.AssetPayment)
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
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#processApprovedAssetPayment(org.kuali.kfs.module.cam.document.AssetPaymentDocument)
     */
    public void processApprovedAssetPayment(AssetPaymentDocument document, KualiDecimal totalHistoricalAmount) {        
        // Creating new asset payment records
        processPayments(document);        
    }

     
    /**
     * Creates a new asset payment record for each new asset payment detail record and then save them
     * 
     * @param document
     */
    private void processPayments(AssetPaymentDocument document) {
        List<AssetPaymentDetail> assetPaymentDetailLines = document.getSourceAccountingLines();
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        List<PersistableBusinessObject> assetPayments = new ArrayList<PersistableBusinessObject>();
        Integer maxSequenceNo=new Integer(0);        

        try {
            Double totalHistoricalCost = new Double(document.getAssetsTotalHistoricalCost().toString());
            // Creating a new payment record for each asset that has payments.
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {
                maxSequenceNo = getMaxSequenceNumber(assetPaymentAssetDetail.getCapitalAssetNumber());
                
                Double previousTotalCostAmount = new Double(assetPaymentAssetDetail.getPreviousTotalCostAmount().toString());                
                Double percentage = (previousTotalCostAmount/totalHistoricalCost);
                KualiDecimal totalAmount = new KualiDecimal(0);
                
                for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetailLines) {
                    Double paymentAmount = new Double(assetPaymentDetail.getAmount().toString());
                    KualiDecimal amount = new KualiDecimal(paymentAmount.doubleValue() * percentage.doubleValue());
                    totalAmount = totalAmount.add(amount);
                    //LOG.info("Asset:"+assetPaymentAssetDetail.getCapitalAssetNumber() + " - Previous Cost:"+previousTotalCostAmount+" - Allocated Amount:"+amount.toString()+" - % :"+percentage.doubleValue());
                    
                    AssetPayment assetPayment = new AssetPayment(assetPaymentDetail);
                    assetPayment.setCapitalAssetNumber(assetPaymentAssetDetail.getCapitalAssetNumber());
                    assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_N);
                    assetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    assetPayment.setDocumentNumber(document.getDocumentNumber());
                    assetPayment.setAccountChargeAmount(amount);

                    KualiDecimal baseAmount = new KualiDecimal(0);
    
                    // If the object sub type is not in the list of federally owned object sub types, then...
                    ObjectCode objectCode = this.getObjectCodeService().getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getFinancialObjectCode());
    
                    // Depreciation Base Amount will be assigned to each payment only when the object code's sub type code is not a
                    // federally owned one
                    if (!this.isNonDepreciableFederallyOwnedObjSubType(objectCode.getFinancialObjectSubTypeCode())) {
                        baseAmount = baseAmount.add(amount);
                    }
                    assetPayment.setPrimaryDepreciationBaseAmount(baseAmount);
    
                    // Resetting each period field its value with nulls
                    this.adjustPaymentAmounts(assetPayment, false, true);
    
                    // add new payment
                    assetPayments.add(assetPayment);

                }
                //*********************BEGIN - Updating Asset cost ***********************************************************
                //Retrieving the asset that will have its cost updated
                HashMap<String,Long> keys = new HashMap<String,Long>();
                keys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER,assetPaymentAssetDetail.getCapitalAssetNumber());
                
                Asset asset = (Asset) getBusinessObjectService().findByPrimaryKey(Asset.class, keys);        
                asset.setTotalCostAmount(assetPaymentAssetDetail.getPreviousTotalCostAmount().add(totalAmount));
                
                //Saving changes
                getBusinessObjectService().save(asset);                    
                //*********************END - Updating Asset cost ***********************************************************                                    
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Finally, saving all the asset payment records.
        this.getBusinessObjectService().save(assetPayments);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#adjustPaymentAmounts(org.kuali.kfs.module.cam.businessobject.AssetPayment, boolean,
     *      boolean)
     */
    public void adjustPaymentAmounts(AssetPayment assetPayment, boolean reverseAmount, boolean nullPeriodDepreciation) throws IllegalAccessException, InvocationTargetException {
        LOG.debug("Starting - adjustAmounts() ");
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetPayment.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
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
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentEligibleForGLPosting(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment) {
        // Transfer payment code flag is not Y
        boolean isEligible = !CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode());
        // Financial object code is currently active
        isEligible &= isPaymentFinancialObjectActive(assetPayment);
        // Payment is not federally funded
        isEligible &= !isPaymentFederalOwned(assetPayment);
        return isEligible;
    }

    /**
     * Checks if object sub type is a non-depreciable federally owned object sub type
     * 
     * @param string objectSubType2
     * @return true if is NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     */
    public boolean isNonDepreciableFederallyOwnedObjSubType(String objectSubType) {
        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (this.getParameterService().parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = this.getParameterService().getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES);
        }
        return federallyOwnedObjectSubTypes.contains(objectSubType);
    }

}
