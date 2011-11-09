/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
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
    protected boolean conditionallyAskQuestion(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        Customer newCostomer = (Customer) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        boolean shouldAskQuestion = maintenanceDocument.isNew() && checkIfOtherCustomerSameName(newCostomer);

        if (shouldAskQuestion) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.CustomerConstants.MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(ArKeyConstants.CustomerConstants.GENERATE_CUSTOMER_QUESTION_ID, questionText);
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
            KNSGlobalVariables.getMessageList().add(ArKeyConstants.CustomerConstants.MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS);
        }
        return exists;
    }

}
