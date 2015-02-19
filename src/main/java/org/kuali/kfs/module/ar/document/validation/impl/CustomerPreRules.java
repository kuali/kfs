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
