/*
 * Copyright 2009 The Kuali Foundation.
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
