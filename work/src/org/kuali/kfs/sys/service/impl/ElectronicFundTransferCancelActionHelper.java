/*
 * Copyright 2008 The Kuali Foundation
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
