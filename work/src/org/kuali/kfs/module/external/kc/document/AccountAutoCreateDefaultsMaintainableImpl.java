/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.KcAccountService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

public class AccountAutoCreateDefaultsMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     * 
     * KRAD Conversion: Performs customization of the adding fields to the section.
     * 
     * No use of data dictionary for field definitions.
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        
           for (Section section : sections) {
            for (Row row : section.getRows()) {
                List<Field> updatedFields = new ArrayList<Field>();
                for (Field field : row.getFields()) {
                    if (shouldIncludeField(field)) {
                        updatedFields.add(field);
                    }
                }
                row.setFields(updatedFields);
            }
        }
        return sections;
    }

    private boolean shouldIncludeField(Field field) {
        if (KcConstants.AccountCreationDefaults.CHART_OF_ACCOUNT_CODE.equals(field.getPropertyName())) {
             KcAccountService kcAccountService = SpringContext.getBean(KcAccountService.class);
            if (kcAccountService.accountsCanCrossCharts()) return false;
         }
        return true;
    }
}
