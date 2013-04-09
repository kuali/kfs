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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;
import org.kuali.kfs.module.purap.util.ThresholdField;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class ThresholdRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ThresholdRule.class);
    protected ChartService chartService;
    protected AccountService accountService;
    protected ReceivingThreshold newThreshold;
    protected ReceivingThreshold oldThreshold;

    public ThresholdRule(){
        chartService = SpringContext.getBean(ChartService.class);
        accountService = SpringContext.getBean(AccountService.class);
    }

    protected boolean isValidDocument(ReceivingThreshold newThreshold, boolean checkDuplicate){

        boolean valid = isValidThresholdCriteria(newThreshold);
        if (valid) {
            valid = isValidVendorNumber(newThreshold);

            // check duplication if needed
            if (valid && checkDuplicate){
                valid = !isDuplicateEntry(newThreshold);
            }
        } else {
            constructFieldError(newThreshold);
        }

        return valid;
    }

    protected void constructFieldError(ReceivingThreshold threshold){

        if (StringUtils.isNotBlank(threshold.getAccountTypeCode())){
            putFieldError(ThresholdField.ACCOUNT_TYPE_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getSubFundGroupCode())){
            putFieldError(ThresholdField.SUBFUND_GROUP_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getPurchasingCommodityCode())){
            putFieldError(ThresholdField.COMMODITY_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getFinancialObjectCode())){
            putFieldError(ThresholdField.FINANCIAL_OBJECT_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getOrganizationCode())){
            putFieldError(ThresholdField.ORGANIZATION_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getVendorNumber())){
            putFieldError(ThresholdField.VENDOR_NUMBER.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }

    }

    protected boolean isValidVendorNumber(ReceivingThreshold threshold){

        if (StringUtils.isNotBlank(threshold.getVendorNumber())){
            String vendorNumber = threshold.getVendorNumber();

            if (StringUtils.isNotBlank(vendorNumber)){
                Map<String, Integer> keys = new HashMap<String, Integer>();

                Integer headerId = VendorUtils.getVendorHeaderId(vendorNumber);
                Integer detailId = VendorUtils.getVendorDetailId(vendorNumber);

                keys.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, headerId);
                keys.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, detailId);

                VendorDetail vendorDetail = getBoService().findByPrimaryKey(VendorDetail.class, keys);

                if (ObjectUtils.isNull(vendorDetail)) {
                    putFieldError(KFSPropertyConstants.VENDOR_NUMBER, PurapKeyConstants.THRESHOLD_FIELD_INVALID, "Vendor Number " + vendorNumber);
                    return false;
                }
                else{
                    VendorDetail vendor = threshold.getVendorDetail();
                    vendor.setVendorHeaderGeneratedIdentifier(headerId);
                    vendor.setVendorDetailAssignedIdentifier(detailId);
                }
            }
            return true;
        }
        return true;
    }

    protected boolean isValidThresholdCriteria(ReceivingThreshold threshold){

        if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
            StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
            StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
            StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
            StringUtils.isBlank(threshold.getOrganizationCode()) &&
            StringUtils.isBlank(threshold.getVendorNumber())){
            return true;
        }else if (StringUtils.isNotBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isBlank(threshold.getVendorNumber())){
                  return true;
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isNotBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isBlank(threshold.getVendorNumber())){
                  return true;
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isNotBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isBlank(threshold.getVendorNumber())){
                  return true;
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isNotBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isBlank(threshold.getVendorNumber())){
                  return true;
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isNotBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isBlank(threshold.getVendorNumber())){
                  return true;
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) &&
                  StringUtils.isNotBlank(threshold.getVendorNumber())){
                  return true;
        }
        return false;
    }

    protected boolean isDuplicateEntry(ReceivingThreshold newThreshold){

        Map fieldValues = new HashMap();
        fieldValues.put(ThresholdField.CHART_OF_ACCOUNTS_CODE.getName(), newThreshold.getChartOfAccountsCode());

        if (StringUtils.isNotBlank(newThreshold.getAccountTypeCode())){
            fieldValues.put(ThresholdField.ACCOUNT_TYPE_CODE.getName(), newThreshold.getAccountTypeCode());
        }else if (StringUtils.isNotBlank(newThreshold.getSubFundGroupCode())){
            fieldValues.put(ThresholdField.SUBFUND_GROUP_CODE.getName(), newThreshold.getSubFundGroupCode());
        }else if (StringUtils.isNotBlank(newThreshold.getPurchasingCommodityCode())){
            fieldValues.put(ThresholdField.COMMODITY_CODE.getName(), newThreshold.getPurchasingCommodityCode());
        }else if (StringUtils.isNotBlank(newThreshold.getFinancialObjectCode())){
            fieldValues.put(ThresholdField.FINANCIAL_OBJECT_CODE.getName(), newThreshold.getFinancialObjectCode());
        }else if (StringUtils.isNotBlank(newThreshold.getOrganizationCode())){
            fieldValues.put(ThresholdField.ORGANIZATION_CODE.getName(), newThreshold.getOrganizationCode());
        }else if (StringUtils.isNotBlank(newThreshold.getVendorNumber())){
            fieldValues.put(ThresholdField.VENDOR_HEADER_GENERATED_ID.getName(), newThreshold.getVendorHeaderGeneratedIdentifier());
            fieldValues.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID.getName(), newThreshold.getVendorDetailAssignedIdentifier());
        }

        Collection<ReceivingThreshold> result = getBoService().findMatching(ReceivingThreshold.class, fieldValues);
        if (result != null && result.size() > 0) {
            putGlobalError(PurapKeyConstants.PURAP_GENERAL_POTENTIAL_DUPLICATE);
            return true;
        }
        return false;
    }


    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;

        if (document.isNew() || document.isEdit() || document.isNewWithExisting()) {
            newThreshold = (ReceivingThreshold) document.getNewMaintainableObject().getBusinessObject();
            oldThreshold = document.getOldMaintainableObject() != null ? (ReceivingThreshold)document.getOldMaintainableObject().getBusinessObject() : null;

            // compare oldThreshold and newThreshold, check if there's any update on the various code fields
            // if yes, then we need to check duplicate of the new threshold among other thresholds; otherwise no need to check
            boolean checkDuplicate = oldThreshold == null;
            checkDuplicate |= !StringUtils.equals(newThreshold.getChartOfAccountsCode(), oldThreshold.getChartOfAccountsCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getAccountTypeCode(), oldThreshold.getAccountTypeCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getSubFundGroupCode(), oldThreshold.getSubFundGroupCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getPurchasingCommodityCode(), oldThreshold.getPurchasingCommodityCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getFinancialObjectCode(), oldThreshold.getFinancialObjectCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getOrganizationCode(), oldThreshold.getOrganizationCode());
            checkDuplicate |= !StringUtils.equals(newThreshold.getVendorNumber(), oldThreshold.getVendorNumber());
            valid &= isValidDocument(newThreshold, checkDuplicate);
        }

        return valid && super.processCustomRouteDocumentBusinessRules(document);
    }
}
