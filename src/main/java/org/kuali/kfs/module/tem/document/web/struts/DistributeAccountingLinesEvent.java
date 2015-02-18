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
package org.kuali.kfs.module.tem.document.web.struts;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AssignDistributionAccountingLinesEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiRuleService;

public class DistributeAccountingLinesEvent implements Observer {

    @Override
    public void update(Observable arg0, Object arg1) {

        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) arg1;
        boolean rulePassed = true;
        wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(wrapper.getTravelDocument()));
        // check any business rules
        rulePassed &= getRuleService().applyRules(new AssignDistributionAccountingLinesEvent(TemPropertyConstants.NEW_IMPORTED_EXPENSE_LINE, wrapper.getTravelDocument(), wrapper));

        if (rulePassed){
            List<TemSourceAccountingLine> newLines = getAccountingDistributionService().distributionToSouceAccountingLines(wrapper.getAccountDistributionsourceAccountingLines(),
                    wrapper.getDistribution(), wrapper.getTravelDocument().getSourceTotal(), wrapper.getTravelDocument().getExpenseLimit());

            for (TemSourceAccountingLine newLine : newLines) {
                wrapper.getTravelDocument().addSourceAccountingLine(newLine);
            }
            wrapper.setAccountDistributionsourceAccountingLines(new ArrayList<TemDistributionAccountingLine>());
            wrapper.setAccountDistributionnextSourceLineNumber(new Integer(1));
        }

    }

    /**
     * Gets the travelReimbursementService attribute.
     *
     * @return Returns the travelReimbursementService.
     */
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }


    /**
     * Gets the kualiRulesService attribute.
     *
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return SpringContext.getBean(KualiRuleService.class);
    }

    protected AccountingDistributionService getAccountingDistributionService() {
        return SpringContext.getBean(AccountingDistributionService.class);
    }
}
