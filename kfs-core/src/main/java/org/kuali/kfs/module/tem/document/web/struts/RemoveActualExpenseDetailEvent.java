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
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.KualiRuleService;

public class RemoveActualExpenseDetailEvent implements Observer {

    public static Logger LOG = Logger.getLogger(RemoveActualExpenseDetailEvent.class);

    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;
    private static final int SELECTED_DETAIL_LINE_ARG_IDX = 2;

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
        final Integer deleteDetailIndex = (Integer) args[SELECTED_DETAIL_LINE_ARG_IDX];

        ActualExpense line = document.getActualExpenses().get(deleteIndex.intValue());
        document.removeExpenseDetail(line, deleteDetailIndex);

        List<ActualExpense> actualExpenses = wrapper.getNewActualExpenseLines();

        KualiDecimal detailTotal = line.getTotalDetailExpenseAmount();

        if (detailTotal.isLessThan(line.getExpenseAmount())){
            KualiDecimal remainderExpense = line.getExpenseAmount().subtract(detailTotal);
            KualiDecimal remainderConverted = line.getConvertedAmount().subtract(new KualiDecimal(detailTotal.bigDecimalValue().multiply(line.getCurrencyRate())));

            wrapper.getNewActualExpenseLines().get(deleteIndex).setExpenseAmount(remainderExpense);
            wrapper.getNewActualExpenseLines().get(deleteIndex).setConvertedAmount(remainderConverted);
        }

        ExpenseUtils.calculateMileage(document, document.getActualExpenses());
        for (String disabledProperty : document.getDisabledProperties().keySet()) {
            getTravelDocumentService().restorePerDiemProperty(document, disabledProperty);
        }
        wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));
        document.getDisabledProperties().clear();

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
