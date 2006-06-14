/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
