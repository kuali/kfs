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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_IMPORTED_EXPENSE_LINE;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.beanutils.BeanUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;


public class AddImportedExpenseEvent implements Observer {

    @SuppressWarnings("null")
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) arg1;

        final TravelDocument document = wrapper.getTravelDocument();
        final ImportedExpense newImportedExpenseLine = wrapper.getNewImportedExpenseLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddImportedExpenseLineEvent<ImportedExpense>(NEW_IMPORTED_EXPENSE_LINE, document, newImportedExpenseLine));

        if (rulePassed){
            if(newImportedExpenseLine != null){
                document.addExpense(newImportedExpenseLine);
            }

            ImportedExpense newExpense = new ImportedExpense();
            try {
                BeanUtils.copyProperties(newExpense, newImportedExpenseLine);
                newExpense.setConvertedAmount(null);
                newExpense.setExpenseParentId(newExpense.getId());
                newExpense.setId(null);
                newExpense.setNotes(null);
                newExpense.setExpenseLineTypeCode(null);
            }
            catch (IllegalAccessException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }

            //ExpenseUtils.disableImportNonReimbursable(wrapper, newImportedExpenseLine, false);
            wrapper.setNewImportedExpenseLine(new ImportedExpense());
            wrapper.getNewImportedExpenseLines().add(newExpense);
            wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));

            //Add the appropriate source accounting line
            if (newImportedExpenseLine.getCardType() != null && newImportedExpenseLine.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)){
                HistoricalTravelExpense historicalTravelExpense = getBusinessObjectService().findBySinglePrimaryKey(HistoricalTravelExpense.class, newImportedExpenseLine.getHistoricalTravelExpenseId());
                historicalTravelExpense.refreshReferenceObject("agencyStagingData");
                List<TripAccountingInformation> tripAccountinfoList = historicalTravelExpense.getAgencyStagingData().getTripAccountingInformation();

                for (TripAccountingInformation tripAccountingInformation : tripAccountinfoList){
                    TemSourceAccountingLine importedLine = new TemSourceAccountingLine();
                    importedLine.setAmount(ObjectUtils.isNotNull(tripAccountingInformation.getAmount()) ? tripAccountingInformation.getAmount() : KualiDecimal.ZERO);
                    importedLine.setChartOfAccountsCode(tripAccountingInformation.getTripChartCode());

                    importedLine.setAccountNumber(tripAccountingInformation.getTripAccountNumber());
                    importedLine.setSubAccountNumber(tripAccountingInformation.getTripSubAccountNumber());
                    importedLine.setFinancialObjectCode(tripAccountingInformation.getObjectCode());
                    importedLine.setFinancialSubObjectCode(tripAccountingInformation.getSubObjectCode());
                    importedLine.setProjectCode(tripAccountingInformation.getProjectCode());
                    importedLine.setOrganizationReferenceId(tripAccountingInformation.getOrganizationReference());
                    importedLine.setCardType(TemConstants.TRAVEL_TYPE_CTS);
                    importedLine.getPostingYear();
                    importedLine.refresh();
                    document.addSourceAccountingLine(importedLine);
                }
            }
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

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
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
