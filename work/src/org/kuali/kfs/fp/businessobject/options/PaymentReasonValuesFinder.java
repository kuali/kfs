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
package org.kuali.module.financial.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.PaymentReasonCode;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.web.struts.form.DisbursementVoucherForm;

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
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));

        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) GlobalVariables.getKualiForm();
        DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) dvForm.getDocument();

        // If the payee type is an employee, remove all the vendor specific payment reasons from the collection
        if(StringUtils.equals(dvDoc.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
            List<String> employeeCodes = SpringContext.getBean(ParameterService.class).getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
            for (PaymentReasonCode element : boList) {
                if(employeeCodes.contains(element.getCode())) {
                    keyValues.add(new KeyLabelPair(element.getCode(), element.getCodeAndDescription()));
                }
            }
        } else { // Remove all the employee specific payment reasons
            List<String> vendorCodes = SpringContext.getBean(ParameterService.class).getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
            for (PaymentReasonCode element : boList) {
                if(vendorCodes.contains(element.getCode())) {
                    keyValues.add(new KeyLabelPair(element.getCode(), element.getCodeAndDescription()));
                }
            }
        }

        return keyValues;
    }

}
