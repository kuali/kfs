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
package org.kuali.kfs.module.external.kc.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

public class AccountAutoCreateDefaultsMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument,
     *      org.kuali.rice.kns.maintenance.Maintainable)
     * 
     * KRAD Conversion: Performs customization of the adding fields to the section.
     * 
     * No use of data dictionary for field definitions.
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        boolean isNew = document.isNew();

        for (Section section : sections) {
            for (Row row : section.getRows()) {
                List<Field> updatedFields = new ArrayList<Field>();
                for (Field field : row.getFields()) {
                    if (isReadOnly(field, isNew)) 
                        field.setReadOnly(true);
                    if (shouldIncludeField(field)) {
                        updatedFields.add(field);
                    }
                    
                }

                row.setFields(updatedFields);
            }
        }
        return sections;
    }

    private boolean isReadOnly(Field field, boolean isNew) {
        if (KcConstants.AccountCreationDefaults.KcUnit.equals(field.getPropertyName())) {
            if (!isNew)
                return true;
        }
        return false;
    }

    private boolean shouldIncludeField(Field field) {
        return true;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        // clear the pre-tag details when coyping pre-tag information
        AccountAutoCreateDefaults newAcctAuto = (AccountAutoCreateDefaults) document.getNewMaintainableObject().getBusinessObject();
        newAcctAuto.setKcUnit("");
        newAcctAuto.setKcUnitName("");
    }

}
