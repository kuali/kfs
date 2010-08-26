/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

public class KemidDerivedRolesValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        RoleManagementService roleManagementService = SpringContext.getBean(RoleManagementService.class);
        KimTypeInfoService kimTypeInfoService = SpringContext.getBean(KimTypeInfoService.class);

        // KimTypeInfo kimTypeInfo = kimTypeInfoService.getKimTypeByName("KFS-ENDOW", "Derived Role: KEMID");
        KimTypeInfo kimTypeInfo = kimTypeInfoService.getKimTypeByName("KFS-COA", "Derived Role: Account");

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeInfo.getKimTypeId());
        List roles = roleManagementService.getRolesSearchResults(fieldValues);

        List labels = new ArrayList();

        labels.add(new KeyLabelPair("", ""));
        for (Iterator iter = roles.iterator(); iter.hasNext();) {
            Role role = (Role) iter.next();
            labels.add(new KeyLabelPair(role.getRoleId(), role.getRoleName()));
        }
        return labels;
    }

}
