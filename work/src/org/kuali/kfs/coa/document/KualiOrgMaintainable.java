/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.maintenance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.core.maintenance.KualiMaintainableImpl;

/**
 * 
 * 
 * 
 */

public class KualiOrgMaintainable extends KualiMaintainableImpl {

    private static final long serialVersionUID = -3182120468758958991L;

    public static final String KUALI_ORG_SECTION = "Edit Organization Code";

    /**
     *
     * 
     * @see org.kuali.core.maintenance.Maintainable#getCoreSections()
     */
    public List getCoreSections() {

        boolean fieldFound = false;
        boolean sectionFound = false;

        String orgPostalCodeFieldName = PropertyConstants.ORGANIZATION_ZIP_CODE;

        // walk the sections
        List sections = super.getCoreSections();
        for (Iterator sectionIterator = sections.iterator(); sectionIterator.hasNext();) {
            Section section = (Section) sectionIterator.next();

            // if this is the section we're looking for
            if (section.getSectionTitle().equalsIgnoreCase(KUALI_ORG_SECTION)) {

                // mark that we found the section
                sectionFound = true;

                // walk the rows
                List rows = section.getRows();
                for (Iterator rowIterator = rows.iterator(); rowIterator.hasNext();) {
                    Row row = (Row) rowIterator.next();

                    // walk the fields
                    List fields = row.getFields();
                    for (Iterator fieldIterator = fields.iterator(); fieldIterator.hasNext();) {
                        Field field = (Field) fieldIterator.next();

                        // if this is the field we're looking for ...
                        if (field.getPropertyName().equalsIgnoreCase(orgPostalCodeFieldName)) {

                            // mark that we've found the field
                            fieldFound = true;

                            // build the fieldConversions for the UserID field lookup
                            Map fieldConversions = new HashMap();
                            fieldConversions.put(PropertyConstants.POSTAL_ZIP_CODE, PropertyConstants.ORGANIZATION_ZIP_CODE );
                            fieldConversions.put(PropertyConstants.POSTAL_STATE_CODE, PropertyConstants.ORGANIZATION_STATE_CODE );
                            fieldConversions.put(PropertyConstants.POSTAL_CITY_NAME, PropertyConstants.ORGANIZATION_CITY_NAME );
                            
                            
                            // add the fieldConversions, lookupParameters and the lookup class
                            field.setFieldConversions(fieldConversions);
                            //field.setLookupParameters(lookupParameters);
                            //field.setQuickFinderClassNameImpl(UniversalUser.class.getName());
                        }
                    }
                }
            }

        }

        // if the section no longer exists, fail loudly
        if (!sectionFound) {
            throw new RuntimeException("There is no longer a section titled '" + KUALI_ORG_SECTION + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document " + "will be broken.  The correct name needs to be set in the Constant in this class.");
        }
        // if the field was not found, fail loudly
        else if (!fieldFound) {
            throw new RuntimeException("There is no longer a field titled '" + PropertyConstants.ORGANIZATION_ZIP_CODE + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document " + "will be broken.  The correct name needs to be set in the PropertyConstants class.");
        }

        return sections;
    }

}
