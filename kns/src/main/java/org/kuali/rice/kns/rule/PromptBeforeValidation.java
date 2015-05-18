/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.rule;

import org.apache.struts.action.ActionForm;
import org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import javax.servlet.http.HttpServletRequest;

/**
 * An interface for a class that provides the ability to prompt the user with a question prior to running a document action.
 * An implementation class of this interface may be specified in the document data dictionary file.
 * 
 * By default, unless KualiDocumentActionBase is overridden, the sole method will be invoked upon using the "approve", "blanketApprove",
 * "performRouteReport", and "route" methodToCalls.
 */
public interface PromptBeforeValidation extends BusinessRule {

    /**
     * Callback method from Maintenance action that allows checks to be done and response redirected via the PreRulesCheckEvent
     * 
     * @param form
     * @param request
     * @param event stores various information necessary to render the question prompt
     * @return boolean indicating whether the validation (and if validation successful, the action) should continue.  If false, the
     * values within the event parameter will determine how the struts action handler should proceed
     */
    public boolean processPrompts(ActionForm form, HttpServletRequest request, PromptBeforeValidationEvent event);
}
