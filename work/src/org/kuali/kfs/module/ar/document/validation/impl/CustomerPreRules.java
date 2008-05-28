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
package org.kuali.module.ar.rules;

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.service.CustomerService;

public class CustomerPreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
        preRulesOK &= conditionallyAskQuestion(document);
        return preRulesOK;
    }

    /**
     * This method checks if there is another customer with the same name and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean conditionallyAskQuestion(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        Customer newCostomer = (Customer) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        boolean shouldAskQuestion = maintenanceDocument.isNew() && checkIfOtherCustomerSameName(newCostomer);

        if (shouldAskQuestion) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(ArConstants.CustomerConstants.MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(ArConstants.CustomerConstants.GENERATE_CUSTOMER_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return true;
    }

    /**
     * This method checks if a customer with the same name already exists
     * 
     * @param newCustomer
     * @return true if exists, false otherwise
     */
    public boolean checkIfOtherCustomerSameName(Customer newCustomer) {
        boolean exists = false;
        Customer customer = SpringContext.getBean(CustomerService.class).getCustomerByName(newCustomer.getCustomerName());
        if (ObjectUtils.isNotNull(customer)) {
            exists = true;
            GlobalVariables.getMessageList().add(ArConstants.CustomerConstants.MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS);
        }
        return exists;
    }

}
