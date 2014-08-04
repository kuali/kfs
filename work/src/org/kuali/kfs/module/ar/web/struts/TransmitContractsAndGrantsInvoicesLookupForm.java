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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.ui.ExtraButton;


/**
 * Form class for the Transmit Contracts & Grants Invoices Lookup.
 */
public class TransmitContractsAndGrantsInvoicesLookupForm extends ContractsGrantsReportLookupForm {

    /**
     * Default constructor.
     */
    public TransmitContractsAndGrantsInvoicesLookupForm() {
        setHtmlFormAction(ArConstants.Actions.TRANSMIT_CONTRACTS_AND_GRANTS_INVOICES);
    }

    /**
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + ArConstants.PRINT_METHOD);
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}" + ArConstants.TRANSMIT_GENERATE_BUTTON_FILE_NAME);
        printButton.setExtraButtonAltText(ArConstants.TRANSMIT_GENERATE_BUTTON_ALT_TEXT);
        buttons.add(printButton);
        return buttons;
    }

}
