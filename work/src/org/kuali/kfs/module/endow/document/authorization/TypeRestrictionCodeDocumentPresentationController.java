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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class TypeRestrictionCodeDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);
        TypeRestrictionCode typeRestrictionCode = (TypeRestrictionCode) document.getNewMaintainableObject().getBusinessObject();
        
        String code = typeRestrictionCode.getCode();
        
        if (EndowConstants.TypeRestrictionPresetValueCodes.PERMANENT_TYPE_RESTRICTION_CODE.equalsIgnoreCase(code)) {
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_PERM);
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_ACTIVE_INDICATOR);
            return fields;            
        }
        
        if (EndowConstants.TypeRestrictionPresetValueCodes.UNRESTRICTED_TYPE_RESTRICTION_CODE.equalsIgnoreCase(code)) {
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_PERM);
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_ACTIVE_INDICATOR);            
            return fields;
        }    
        
        if (EndowConstants.TypeRestrictionPresetValueCodes.NOT_APPLICABLE_TYPE_RESTRICTION_CODE.equalsIgnoreCase(code)) {
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_PERM);
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_ACTIVE_INDICATOR);            
            return fields;
        }
        
        if (EndowConstants.TypeRestrictionPresetValueCodes.TEMPORARY_RESTRICTED_TYPE_RESTRICTION_CODE.equalsIgnoreCase(code)) {
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_PERM);
            fields.add(EndowConstants.TypeRestrictionPresetValueCodes.TYPE_RESTRICTION_ACTIVE_INDICATOR);            
            return fields;
        }
        
        return fields;            
    }
}
