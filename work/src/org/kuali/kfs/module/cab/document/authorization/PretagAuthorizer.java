/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * AssetAuthorizer for Asset edit.
 */
public class PretagAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        
        Pretag pretag = null;
        if (businessObject instanceof MaintenanceDocument) {
            pretag = (Pretag) ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        }
        else {
            pretag = (Pretag) businessObject;
        }
        
        String chart = pretag.getChartOfAccountsCode();
        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);
        
        String org = pretag.getOrganizationCode();
        attributes.put(KfsKimAttributes.ORGANIZATION_CODE, org);
    }
}
