/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeePaymentType;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.impl.FrequencyCodeServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;

public class FeeMethodDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        FeeMethod feeMethod = (FeeMethod) document.getNewMaintainableObject().getBusinessObject();

        String frequencyCode = feeMethod.getFeeFrequencyCode();
        if (StringUtils.isNotEmpty(frequencyCode)) {
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = SpringContext.getBean(FrequencyCodeServiceImpl.class);
            feeMethod.setFeeNextProcessDate(frequencyCodeServiceImpl.calculateProcessDate(frequencyCode));
        }

        String feeTypeCode = feeMethod.getFeeTypeCode();

        if (EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_PAYMENTS.equalsIgnoreCase(feeTypeCode)) {
            // rule# 8: If fee type code is "P" then fee base code should be "I"
            feeMethod.setFeeBaseCode(EndowConstants.FeeMethod.FEE_BASE_CD_VALUE);
            readOnlyPropertyNames.add(EndowPropertyConstants.FEE_BASE_CD);
        }

        String feeMethodCode = feeMethod.getCode();
        // the frequency code on a Fee Method cannot be changed if that Fee Method is used on at least one KEMID
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) && StringUtils.isNotEmpty(feeMethodCode)) {
            FeeMethodService feeMethodService = SpringContext.getBean(FeeMethodService.class);
            if (feeMethodService.isFeeMethodUsedOnAnyKemid(feeMethodCode)) {
                readOnlyPropertyNames.add(EndowPropertyConstants.FEE_METHOD_FREQUENCY_CODE);
            }
        }

        return readOnlyPropertyNames;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> readOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);

        // make all the tabs read only to begin with
        readOnlySectionIds.add(EndowConstants.FeeMethod.CLASS_CODES_TAB_ID);
        readOnlySectionIds.add(EndowConstants.FeeMethod.SECURITY_TAB_ID);
        readOnlySectionIds.add(EndowConstants.FeeMethod.PAYMENT_TYPES_TAB_ID);
        readOnlySectionIds.add(EndowConstants.FeeMethod.TRANSACTION_TYPES_TAB_ID);
        readOnlySectionIds.add(EndowConstants.FeeMethod.ENDOWMENT_TRANSACTION_CODES_TAB_ID);

        FeeMethod feeMethod = (FeeMethod)document.getNewMaintainableObject().getDataObject();
        FeeMethod oldFeeMethod = (FeeMethod)document.getOldMaintainableObject().getDataObject();

        String feeTypeCode = feeMethod.getFeeTypeCode();

        if (StringUtils.isEmpty(feeTypeCode)) {
            return readOnlySectionIds;
        }

        // if Fee Type Code is T then valid tabs are Transaction Type and Endowment Transaction Type
        // read only the rest of the tabs...
        // also clear the old collection records
        // also clear the read only tables collection records as they are no longer valid...
        if (EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS.equalsIgnoreCase(feeTypeCode)) {
            readOnlySectionIds.remove(EndowConstants.FeeMethod.TRANSACTION_TYPES_TAB_ID);
            readOnlySectionIds.remove(EndowConstants.FeeMethod.ENDOWMENT_TRANSACTION_CODES_TAB_ID);
            readOnlySectionIds.remove(EndowConstants.FeeMethod.PAYMENT_TYPES_TAB_ID);

            List<FeeClassCode> feeClassCodes = feeMethod.getFeeClassCodes();
            feeClassCodes.clear();

            List<FeeClassCode> oldFeeClassCodes = oldFeeMethod.getFeeClassCodes();
            oldFeeClassCodes.clear();

            List<FeeSecurity> feeSecurity = feeMethod.getFeeSecurity();
            feeSecurity.clear();

            List<FeeSecurity> olFeeSecurity = oldFeeMethod.getFeeSecurity();
            olFeeSecurity.clear();

       //     List<FeePaymentType> feePaymentTypes = (List<FeePaymentType>) feeMethod.getFeePaymentTypes();
       //     feePaymentTypes.clear();

       //     List<FeePaymentType> oldFeePaymentTypes = (List<FeePaymentType>) oldFeeMethod.getFeePaymentTypes();
       //     oldFeePaymentTypes.clear();

            return readOnlySectionIds;
        }

        // if Fee Type Code is B then tabs Class Codes and Security are valid
        // read only the rest of the tabs...
        // also clear the old collection records
        // also clear the read only tables collection records as they are not longer valid...
        if (EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES.equalsIgnoreCase(feeTypeCode)) {
            readOnlySectionIds.remove(EndowConstants.FeeMethod.CLASS_CODES_TAB_ID);
            readOnlySectionIds.remove(EndowConstants.FeeMethod.SECURITY_TAB_ID);

            List<FeePaymentType> feePaymentTypes = feeMethod.getFeePaymentTypes();
            feePaymentTypes.clear();
            List<FeePaymentType> oldFeePaymentTypes = oldFeeMethod.getFeePaymentTypes();
            oldFeePaymentTypes.clear();

            List<FeeTransaction> feeTransactions = feeMethod.getFeeTransactions();
            feeTransactions.clear();
            List<FeeTransaction> oldFeeTransactions = oldFeeMethod.getFeeTransactions();
            oldFeeTransactions.clear();

            List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes = feeMethod.getFeeEndowmentTransactionCodes();
            feeEndowmentTransactionCodes.clear();
            List<FeeEndowmentTransactionCode> oldFeeEndowmentTransactionCodes = oldFeeMethod.getFeeEndowmentTransactionCodes();
            oldFeeEndowmentTransactionCodes.clear();

            return readOnlySectionIds;
        }

        // if Fee Type Code is P then only Payment Type tab is valid
        // also clear the read only tables collection records as they are not longer valid...
        // also clear the old collection records
        // read only the rest of the tabs...
        if (EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_PAYMENTS.equalsIgnoreCase(feeTypeCode)) {
            readOnlySectionIds.remove(EndowConstants.FeeMethod.PAYMENT_TYPES_TAB_ID);

            List<FeeClassCode> feeClassCodes = feeMethod.getFeeClassCodes();
            feeClassCodes.clear();
            List<FeeClassCode> oldFeeClassCodes = oldFeeMethod.getFeeClassCodes();
            oldFeeClassCodes.clear();

            List<FeeSecurity> feeSecurity = feeMethod.getFeeSecurity();
            feeSecurity.clear();
            List<FeeSecurity> olFeeSecurity = oldFeeMethod.getFeeSecurity();
            olFeeSecurity.clear();

            List<FeeTransaction> feeTransactions = feeMethod.getFeeTransactions();
            feeTransactions.clear();
            List<FeeTransaction> oldFeeTransactions = oldFeeMethod.getFeeTransactions();
            oldFeeTransactions.clear();

            List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes = feeMethod.getFeeEndowmentTransactionCodes();
            feeEndowmentTransactionCodes.clear();
            List<FeeEndowmentTransactionCode> oldFeeEndowmentTransactionCodes = oldFeeMethod.getFeeEndowmentTransactionCodes();
            oldFeeEndowmentTransactionCodes.clear();

            return readOnlySectionIds;
        }

        return readOnlySectionIds;
    }
}
