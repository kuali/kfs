/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.fp.document.web.struts.DisbursementVoucherForm;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class returns list of payment reason value pairs.
 */
public class PaymentReasonValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        // Retrieve all the payment reason codes
        List<PaymentReasonCode> boList = (List<PaymentReasonCode>) SpringContext.getBean(KeyValuesService.class).findAllOrderBy(PaymentReasonCode.class, KFSPropertyConstants.NAME, true);
        
        KualiForm kauliForm = GlobalVariables.getKualiForm();
        if(kauliForm instanceof DisbursementVoucherForm) {
            DisbursementVoucherForm dvForm = (DisbursementVoucherForm) GlobalVariables.getKualiForm();
            return this.populatePaymentReasonsByPayee(dvForm, boList);
        }

        return this.populatePaymentReasons(boList);

    }

    // the default popluation logic for payment reason code key value list  
    private List populatePaymentReasons(List<PaymentReasonCode> boList) {
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));

        for (PaymentReasonCode payReason : boList) {
            if(payReason.isActive()) {
                keyValues.add(new KeyLabelPair(payReason.getCode(), payReason.getCodeAndDescription()));
            }
        }
        
        return keyValues;
    }

    // populate the payment reason code key value list based on the selected payee type
    private List<KeyLabelPair> populatePaymentReasonsByPayee(DisbursementVoucherForm dvForm, List<PaymentReasonCode> boList) {
        List<String> payeeSpecificCodes = new ArrayList<String>();
        DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) dvForm.getDocument();

        // If the payee type is an employee, remove all the vendor specific payment reasons from the collection
        if(StringUtils.equals(dvDoc.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
            payeeSpecificCodes = SpringContext.getBean(ParameterService.class).getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
        } else { // Remove all the employee specific payment reasons
            payeeSpecificCodes = SpringContext.getBean(ParameterService.class).getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
        }

        return parsePaymentReasonsByPayee(payeeSpecificCodes, boList, dvDoc.getCampusCode());
    }

    /**
     * 
     * This method generates the list of payment reason codes to be displayed on the disbursement voucher.  The payment reason
     * code list is specific to the disbursement voucher, in that only valid values are displayed.  These values are determined 
     * based on payee type and associated campus code of the disbursement voucher.
     * 
     * @param payeeSpecificCodes Allowable payment reason codes for the given payee type.
     * @param boList List of all possible payment reason codes.
     * @param campusCode Campus code of the associated disbursement voucher, to be used to check accessibility of some 
     * payment reason codes, which have campus code-related restrictions.
     * @return A collection of payment reasons allowed for display on a corresponding disbursement voucher.
     */
    private List<KeyLabelPair> parsePaymentReasonsByPayee(List<String> payeeSpecificCodes, List<PaymentReasonCode> boList, String campusCode) {
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));

        for (PaymentReasonCode payReason : boList) {
            if(payeeSpecificCodes.contains(payReason.getCode()) && payReason.isActive()) {
                keyValues.add(new KeyLabelPair(payReason.getCode(), payReason.getCodeAndDescription()));
            }
        }
        
        return keyValues;
    }
    
}
