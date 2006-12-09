/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Delegate;

/**
 * This class...
 * 
 * 
 */
public class DelegatePreRules implements PreRulesCheck {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegatePreRules.class);

    /**
     * 
     * Default no-args constructor.
     * 
     */
    public DelegatePreRules() {
    }

    /**
     * @see org.kuali.core.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, org.kuali.core.rule.event.PreRulesCheckEvent)
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {
        LOG.info("Entering processPreRuleChecks");

        // create some references to the relevant objects being looked at
        MaintenanceDocument document = (MaintenanceDocument) event.getDocument();
        Delegate delegate = (Delegate) document.getNewMaintainableObject().getBusinessObject();

        // set the defaults on the document
        setUnconditionalDefaults(delegate);

        return true;
    }

    private void setUnconditionalDefaults(Delegate delegate) {

        // FROM amount defaults to zero
        if (ObjectUtils.isNull(delegate.getFinDocApprovalFromThisAmt())) {
            delegate.setFinDocApprovalFromThisAmt(new KualiDecimal(0));
        }

        // TO amount defaults to zero
        if (ObjectUtils.isNull(delegate.getFinDocApprovalToThisAmount())) {
            delegate.setFinDocApprovalToThisAmount(new KualiDecimal(0));
        }
    }

}
