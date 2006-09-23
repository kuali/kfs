/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 *
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 *
 * You may obtain a copy of the License at:
 *
 * http://kualiproject.org/license.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.maintenance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.web.uidraw.Field;
import org.kuali.core.web.uidraw.Row;
import org.kuali.core.web.uidraw.Section;
import org.kuali.core.maintenance.KualiMaintainableImpl;

/**
 * This class is the Maintainable implementation for support of Kuali User.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class KualiOrgMaintainable extends KualiMaintainableImpl {

    private static final long serialVersionUID = -3182120468758958991L;

    public static final String KUALI_ORG_SECTION = "Edit Organization Code";

    /**
     * Overrides super so we can display user lookup only on new user.
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
