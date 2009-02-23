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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants.NRATaxParameters;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurchasingAccountsPayableProcessAccountSummaryValidation extends GenericValidation {

    private ParameterService parameterService;    
    private PurapAccountingService purapAccountingService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument)event.getDocument();
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccounts(document);
        for (SummaryAccount summaryAccount : summaryAccounts) {
            //TODO: ctk - do we need all these null checks
            if (ObjectUtils.isNotNull(summaryAccount) && ObjectUtils.isNotNull(summaryAccount.getAccount()) && ObjectUtils.isNotNull(summaryAccount.getAccount().getAmount())) {
                boolean isTaxAccount = false;

                // check if the summary account is for tax withholding
                if (document instanceof PaymentRequestDocument) {
                    String incomeClassCode = ((PaymentRequestDocument)document).getTaxClassificationCode();
                    if (StringUtils.isNotEmpty(incomeClassCode)) {
                        
                        String federalChartCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                        String federalAccountNumber = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                        String federalObjectCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                        String stateChartCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                        String stateAccountNumber = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                        String stateObjectCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                        String summaryChartCode = summaryAccount.getAccount().getChartOfAccountsCode();
                        String summaryAccountNumber = summaryAccount.getAccount().getAccountNumber();
                        String summaryObjectCode = summaryAccount.getAccount().getFinancialObjectCode();

                        boolean isFederalAccount = StringUtils.equals(federalChartCode, summaryChartCode);
                        isFederalAccount = isFederalAccount && StringUtils.equals(federalAccountNumber, summaryAccountNumber);
                        isFederalAccount = isFederalAccount && StringUtils.equals(federalObjectCode, summaryObjectCode);
                        
                        boolean isStateAccount = StringUtils.equals(stateChartCode, summaryChartCode);
                        isStateAccount = isStateAccount && StringUtils.equals(stateAccountNumber, summaryAccountNumber);
                        isStateAccount = isStateAccount && StringUtils.equals(stateObjectCode, summaryObjectCode);
                        
                        isTaxAccount = isFederalAccount || isStateAccount;
                    }
                }

                // exclude tax withholding accounts from non-negative requirement
                if (!isTaxAccount && summaryAccount.getAccount().getAmount().isNegative()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_ACCOUNT_NEGATIVE,summaryAccount.getAccount().getAccountNumber());
                }
            }
        }
        return valid;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public PurapAccountingService getPurapAccountingService() {
        return purapAccountingService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

}
