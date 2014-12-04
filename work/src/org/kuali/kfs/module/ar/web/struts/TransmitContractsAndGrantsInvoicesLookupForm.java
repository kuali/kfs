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
