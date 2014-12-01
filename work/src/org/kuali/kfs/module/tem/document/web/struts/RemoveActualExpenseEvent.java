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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.rice.krad.service.KualiRuleService;

public class RemoveActualExpenseEvent implements Observer {

    public static Logger LOG = Logger.getLogger(RemoveActualExpenseEvent.class);

    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;

    private TravelDocumentService travelDocumentService;
    private AccountingDistributionService accountingDistributionService;
    private KualiRuleService ruleService;

    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof Object[])) {
            return;
        }
        final Object[] args = (Object[]) arg1;
        LOG.debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) args[WRAPPER_ARG_IDX];

        final TravelDocument document = wrapper.getTravelDocument();
        final Integer deleteIndex = (Integer) args[SELECTED_LINE_ARG_IDX];

        ActualExpense line = document.getActualExpenses().get(deleteIndex.intValue());
        document.removeExpense(line, deleteIndex);
        wrapper.getNewActualExpenseLines().remove(deleteIndex.intValue());

        List<ActualExpense> actualExpenses = wrapper.getNewActualExpenseLines();

        wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));
        for (String disabledProperty : document.getDisabledProperties().keySet()) {
            getTravelDocumentService().restorePerDiemProperty(document, disabledProperty);
        }
        document.getDisabledProperties().clear(); // and let the execute regenerate them
    }

    /**
     * Gets the travelReimbursementService attribute.
     *
     * @return Returns the travelReimbursementService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * Gets the kualiRulesService attribute.
     *
     * @return Returns the kualiRuleseService.
     */
    public KualiRuleService getRuleService() {
        return ruleService;
    }

    public void setRuleService(final KualiRuleService ruleService) {
        this.ruleService = ruleService;
    }

    public AccountingDistributionService getAccountingDistributionService() {
        if (accountingDistributionService == null) {
            accountingDistributionService = org.kuali.kfs.sys.context.SpringContext.getBean(AccountingDistributionService.class);
        }
        return accountingDistributionService;
    }

    public void setAccountingDistributionService(final AccountingDistributionService accountingDistributionService) {
        this.accountingDistributionService = accountingDistributionService;
    }
}
