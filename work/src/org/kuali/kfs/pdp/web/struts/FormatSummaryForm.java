/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Jan 14, 2005
 *
 */
package org.kuali.module.pdp.form.format;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 */
public class FormatSummaryForm extends ActionForm {

    private String processId;

    public FormatSummaryForm() {
        super();
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String btnPressed = GeneralUtilities.whichButtonWasPressed(request);
        ActionErrors actionErrors = new ActionErrors();

        if ("btnSearch".equals(btnPressed)) {
            if (GeneralUtilities.isStringEmpty(this.processId)) {
                actionErrors.add("errors", new ActionMessage("FormatSummaryForm.criteria.noneEntered"));
            }
            else if (!(GeneralUtilities.isStringAllNumbers(this.processId))) {
                actionErrors.add("errors", new ActionMessage("FormatSummaryForm.processId.nonNumeric"));
            }
        }
        return actionErrors;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
