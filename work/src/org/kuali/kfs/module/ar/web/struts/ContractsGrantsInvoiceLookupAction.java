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
package org.kuali.kfs.module.ar.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsInvoiceResultRow;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Action class for Contracts Grants Invoice Lookup.
 */
public class ContractsGrantsInvoiceLookupAction extends ContractsGrantsMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceLookupAction.class);

    /**
     * Uses the initiate permission for the CINV doc to check if authorized
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);

        if (!SpringContext.getBean(IdentityManagementService.class).isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, null)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        }
    }

    /**
     * Collects from the given resultTable - collecting ids from children rows for ContractsGrantsInvoiceResultRows
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#collectSelectedObjectIds(java.util.List)
     */
    @Override
    protected Map<String, String> collectSelectedObjectIds(List<ResultRow> resultTable) {
        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {
            // actual object ids are on sub result rows, not on parent rows
            if (row instanceof ContractsGrantsInvoiceResultRow) {
                for (ResultRow subResultRow : ((ContractsGrantsInvoiceResultRow) row).getSubResultRows()) {
                    String objId = subResultRow.getObjectId();
                    selectedObjectIds.put(objId, objId);
                }
            }
            else {
                String objId = row.getObjectId();
                selectedObjectIds.put(objId, objId);
            }
        }

        return selectedObjectIds;
    }

    /**
     * Returns "arContractsGrantsInvoiceSummary.do"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#getActionUrl()
     */
    @Override
    protected String getActionUrl() {
        return "arContractsGrantsInvoiceSummary.do";
    }
}
