/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
