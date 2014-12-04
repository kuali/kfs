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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class BalanceTypeRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processRouteDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        boolean result = super.processRouteDocument(document);
        result &= checkForBlockingOffsetDefinitions((BalanceType)(((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject()));
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processSaveDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        boolean result = super.processSaveDocument(document);
        result &= checkForBlockingOffsetDefinitions((BalanceType)(((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject()));
        return result;
    }
    
    /**
     * Determines if the given document is inactivating the balance type being maintained
     * @param document the maintenance document maintaining a balance type
     * @return true if the document is inactivating that balance type, false otherwise
     */
    protected boolean isInactivating(MaintenanceDocument document) {
        if (!document.isEdit() || document.getOldMaintainableObject() == null) return false;
        return ((Inactivatable)document.getOldMaintainableObject().getBusinessObject()).isActive() && !((Inactivatable)document.getNewMaintainableObject().getBusinessObject()).isActive();
    }

    /**
     * Determines if the inactivating balance type should be blocked by offset definitions
     * @param balanceType the balance type to check
     * @return true if the balance type shouldn't be blocked by any existing offset definitions, false otherwise
     */
    protected boolean checkForBlockingOffsetDefinitions(BalanceType balanceType) {
        final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        boolean result = true;
        
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("financialBalanceTypeCode", balanceType.getFinancialBalanceTypeCode());
        keys.put("universityFiscalYear", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        
        final int matchingCount = businessObjectService.countMatching(OffsetDefinition.class, keys);
        if (matchingCount > 0) {
            GlobalVariables.getMessageMap().putErrorForSectionId("Edit Balance Type",KFSKeyConstants.ERROR_DOCUMENT_BALANCETYPMAINT_INACTIVATION_BLOCKING,new String[] {balanceType.getFinancialBalanceTypeCode(), Integer.toString(matchingCount), OffsetDefinition.class.getName()});
            result = false;
        }
        return result;
    }
}
