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
package org.kuali.kfs.sys.service.impl;

import java.util.Map;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;

/**
 * An Electronic Funds Transfer action which simply returns to the portal.
 */
public class ElectronicFundTransferCancelActionHelper implements ElectronicFundTransferActionHelper {
    private static final String PORTAL_FORWARD = "portal";

    /**
     * Just return to the portal.
     * @see org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper#performAction(org.kuali.rice.kns.web.struts.form.KualiForm, org.apache.struts.action.ActionMapping)
     */
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map paramsMap, String basePath) {
        return mapping.findForward(ElectronicFundTransferCancelActionHelper.PORTAL_FORWARD);
    }

}
