/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Section;

public class TaxRegionMaintainableImpl extends FinancialSystemMaintainable {

    
    /**
     * This method hides particular tax region sections based on tax region type code
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.rice.kns.maintenance.Maintainable)
     * 
     * KRAD Conversion: Maintainable customizes the hiding/showing of the sections
     * No Use of data dictionary
     */
    @Override
    public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(document, oldMaintainable);

        TaxRegion taxRegion = (TaxRegion) getBusinessObject();

        //have to check if type code is empty, because on the oldMaintainable on a NEW action, none of the old Maintainable BO's values are set
        if( StringUtils.isNotEmpty( taxRegion.getTaxRegionTypeCode() ) ) {
            String sectionIdToDisplay = getSectionIdToDisplay(taxRegion.getTaxRegionTypeCode());
            for (Section section : sections) {
                if (!isMainOrRateSection(section.getSectionId()) && !sectionIdToDisplay.equals(section.getSectionId())) {
                    section.setHidden(true);
                }
            }            
        }
        
        return sections;
    }

    /**
     * This method returns the appropriate section ID that should NOT be hidden based on a specific tax region type code
     * 
     * @param taxRegionTypeCode
     * @return
     */
    protected String getSectionIdToDisplay(String taxRegionTypeCode) {
        if (KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE_STATE.equals(taxRegionTypeCode)) {
            return KFSConstants.TaxRegionConstants.TAX_REGION_STATES_SECTION_ID;
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE_COUNTY.equals(taxRegionTypeCode)) {
            return KFSConstants.TaxRegionConstants.TAX_REGION_COUNTIES_SECTION_ID;
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE_POSTAL_CODE.equals(taxRegionTypeCode)) {
            return KFSConstants.TaxRegionConstants.TAX_REGION_POSTAL_CODES_SECTION_ID;
        }
        else {
            throw new RuntimeException("No section is set up for tax region type code " + taxRegionTypeCode);
        }
    }

    /**
     * This method returns true if section is main or tax region rate section
     * 
     * @param sectionId
     * @return
     */
    protected boolean isMainOrRateSection(String sectionId) {
        return KFSConstants.TaxRegionConstants.TAX_REGION_RATES_SECTION_ID.equals(sectionId) || KFSConstants.TaxRegionConstants.TAX_REGION_CREATE_SECTION_ID.equals(sectionId);
    }
}
