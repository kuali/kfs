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
