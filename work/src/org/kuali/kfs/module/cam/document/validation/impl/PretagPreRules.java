/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.rules;


import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.cams.bo.PretagDetail;
import org.kuali.module.chart.bo.OrganizationReversionGlobal;


/**
 * PreRules checks for the {@link Pretag} that needs to occur while still in the Struts processing. This includes defaults
 */
public class PretagPreRules extends PreRulesContinuationBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PretagRule.class);

    /**
     * This is called from the rules service to execute our rules A hook is provided here for sub-classes to override the
     * {@link PreRulesContinuationBase#doRules(org.kuali.core.rules)}
     * {@link OrganizationReversionGlobalPreRules#copyKeyAttributesToCollections(OrganizationReversionGlobal)}</li>
     * 
     */
    @Override
    public boolean doRules(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        Pretag pretag = (Pretag) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        copyKeyAttributesToCollections(pretag);
        return true;
    }
    
    /**
     * This method copy purchaseOrderNumer and LineItemNumber from Pretag into PretagDetail.
     * 
     */
    public void copyKeyAttributesToCollections(Pretag pretag) {
        for (PretagDetail pretagDetail : pretag.getPretagDetails()) {
            pretagDetail.setPurchaseOrderNumber(pretag.getPurchaseOrderNumber());
        }
        for (PretagDetail pretagDetail : pretag.getPretagDetails()) {
            pretagDetail.setLineItemNumber(pretag.getLineItemNumber());
        }
    }

}