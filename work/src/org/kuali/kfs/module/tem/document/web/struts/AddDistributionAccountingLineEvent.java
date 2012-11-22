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


import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddDistributionAccountingLineValidationEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiRuleService;

public class AddDistributionAccountingLineEvent implements Observer {
    private static final int WRAPPER_ARG_IDX       = 0;
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof TravelMvcWrapperBean)) {
            return;
        }
        TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) arg1;
        

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddDistributionAccountingLineValidationEvent(TemPropertyConstants.ACCOUNT_DISTRIBUTION_NEW_SRC_LINE, wrapper.getTravelDocument(), wrapper));
    
        if (rulePassed){
            wrapper.addAccountDistributionsourceAccountingLine(wrapper.getAccountDistributionnewSourceLine());
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
