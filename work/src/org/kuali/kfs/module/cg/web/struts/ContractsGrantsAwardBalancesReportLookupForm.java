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
package org.kuali.kfs.module.cg.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Form Class for Contracts Grants Award Balances Report Lookup.
 */
public class ContractsGrantsAwardBalancesReportLookupForm extends LookupForm {

    protected String htmlFormAction;

    public ContractsGrantsAwardBalancesReportLookupForm() {
        setHtmlFormAction("contractsGrantsAwardBalancesReport");
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.print");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_genprintfile.gif");
        printButton.setExtraButtonAltText("Print");
        buttons.add(printButton);
        return buttons;
    }

    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

}
