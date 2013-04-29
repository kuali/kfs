/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.document;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is a special implementation of Maintainable specifically for Account Delegates. It was created to correctly update the
 * default Start Date on edits and copies, ala JIRA #KULRNE-62.
 */
public class CustomerProfileMaintenanceDocumentMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileMaintenanceDocumentMaintainableImpl.class);

    /**
     * This method will reset AccountDelegate's Start Date to the current timestamp on edits and copies
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {

        super.processAfterCopy( document, parameters );
        CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
        customerProfile.setChartCode(null);
        customerProfile.setUnitCode(null);
        customerProfile.setSubUnitCode(null);
     }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     *
     * KRAD Conversion: Performs customization of the sections making fields read only.
     *
     * No use of data dictionary.
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        //If oldMaintainable is null, it means we are trying to get sections for the old part
        //If oldMaintainable is not null, it means we are trying to get sections for the new part
        //Refer to KualiMaintenanceForm lines 288-294
        CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
        if(oldMaintainable==null) {
            return sections;
        }
        if (shouldReviewTypesFieldBeReadOnly(document) == false) {
            return sections;
        }

        //  If the document is new, don't make these fields editable
        if (!KRADConstants.MAINTENANCE_NEW_ACTION.equals(getMaintenanceAction())) {
            for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if (PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE.equals(field.getPropertyName())) {
                            field.setReadOnly(true);
                         }
                        if (PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE.equals(field.getPropertyName())) {
                             field.setReadOnly(true);
                        }
                        if (PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE.equals(field.getPropertyName())) {
                            field.setReadOnly(true);
                        }
                    }
                }
            }
        }
        return sections;
    }


    protected boolean shouldReviewTypesFieldBeReadOnly(MaintenanceDocument document){
        CustomerProfile  customerProfile = (CustomerProfile)document.getNewMaintainableObject().getBusinessObject();
        if(StringUtils.isEmpty(customerProfile.getChartCode())) {
            return false;
        }
        if(StringUtils.isEmpty(customerProfile.getSubUnitCode())) {
            return false;
        }
        if(StringUtils.isEmpty(customerProfile.getUnitCode())) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        CustomerProfile  customerProfile = (CustomerProfile)document.getNewMaintainableObject().getBusinessObject();

       if (ObjectUtils.isNull(customerProfile.getDefaultSubAccountNumber())) {
        customerProfile.setDefaultSubAccountNumber(PdpPropertyConstants.CustomerProfile.CUSTOMER_DEFAULT_SUB_ACCOUNT_NUMBER);
    }
       if (ObjectUtils.isNull(customerProfile.getDefaultSubObjectCode())) {
        customerProfile.setDefaultSubObjectCode(PdpPropertyConstants.CustomerProfile.CUSTOMER_DEFAULT_SUB_OBJECT_CODE);
    }
        super.processAfterPost(document, parameters);
    }
}
