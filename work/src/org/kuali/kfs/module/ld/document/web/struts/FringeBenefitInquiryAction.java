/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ld.businessobject.BenefitInquiry;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class FringeBenefitInquiryAction extends KualiAction {

    public ActionForward calculateFringeBenefit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FringeBenefitInquiryForm accountingLineForm = (FringeBenefitInquiryForm) form;

        Integer payrollFiscalyear = new Integer(accountingLineForm.getPayrollEndDateFiscalYear());
        String chartOfAccountsCode = accountingLineForm.getChartOfAccountsCode();
        String objectCode = accountingLineForm.getFinancialObjectCode();
        KualiDecimal amount = new KualiDecimal(accountingLineForm.getAmount());
        Collection<PositionObjectBenefit> positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getActivePositionObjectBenefits(payrollFiscalyear, chartOfAccountsCode, objectCode);

        List<BenefitInquiry> fringebenefitEntries = new ArrayList<BenefitInquiry>();
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            if (positionObjectBenefit.getBenefitsCalculation().isActive()) {
                BenefitInquiry benefitInquiry = new BenefitInquiry();
                String fringeBenefitObjectCode = positionObjectBenefit.getBenefitsCalculation().getPositionFringeBenefitObjectCode();
                benefitInquiry.setFringeBenefitObjectCode(fringeBenefitObjectCode);
                KualiDecimal benefitAmount = SpringContext.getBean(LaborBenefitsCalculationService.class).calculateFringeBenefit(positionObjectBenefit, amount, accountingLineForm.getAccountNumber(), accountingLineForm.getSubAccountNumber());
                benefitInquiry.setBenefitAmount(benefitAmount);
                fringebenefitEntries.add(benefitInquiry);
            }
        }

        Collections.sort(fringebenefitEntries,Collections.reverseOrder());

        accountingLineForm.setBenefitInquiry(fringebenefitEntries);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


}
