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
