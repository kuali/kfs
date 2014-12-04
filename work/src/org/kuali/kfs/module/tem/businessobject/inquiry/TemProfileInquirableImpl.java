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
package org.kuali.kfs.module.tem.businessobject.inquiry;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.FieldRestriction;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class TemProfileInquirableImpl extends KfsInquirableImpl {

    private static final Logger LOG = Logger.getLogger(TemProfileInquirableImpl.class);

	/**
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        TemProfile profile = (TemProfile)super.getBusinessObject(fieldValues);
        SpringContext.getBean(TemProfileService.class).updateACHAccountInfo(profile);
        return profile;
    }

    /**
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public List<Section> getSections(BusinessObject bo) {

		List<Section> sections = super.getSections(bo);
		Person currUser = GlobalVariables.getUserSession().getPerson();
        InquiryRestrictions inquiryRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService().getInquiryRestrictions(bo, currUser);

        // This is to allow users to view their own profile. If the principalId from the profile
        // matches, the current user then fields are unmasked. Otherwise the other roles will handle the appropriate
        // masking/unmasking of fields (see TemProfileOrganizationHierarchyRoleTypeServiceImpl).
        TemProfile profile = (TemProfile) bo;
        String principalId = profile.getPrincipalId();
        FieldRestriction restriction;

        if (StringUtils.isNotBlank(principalId) && StringUtils.equalsIgnoreCase(currUser.getPrincipalId().trim(), principalId)) {
            for(Section section : sections) {
            	List<Row> rows = section.getRows();
            	for (Row row : rows) {
            		List<Field> rowFields = row.getFields();
            		for (Field field : rowFields) {
            			if(field.getFieldType().equalsIgnoreCase(Field.CONTAINER)) {
            				List<Row> containerRows = field.getContainerRows();
            				for(Row containerRow : containerRows) {
            					List<Field> containerRowFields = containerRow.getFields();
            					for (Field containerRowField : containerRowFields) {
            						restriction = inquiryRestrictions.getFieldRestriction(containerRowField.getPropertyName());
        	            			if(restriction.isMasked() || restriction.isPartiallyMasked()) {
        	            				containerRowField.setSecure(false);
        	            			}
            					}
            				}
            			}
            			restriction = inquiryRestrictions.getFieldRestriction(field.getPropertyName());
	            		if(restriction.isMasked() || restriction.isPartiallyMasked()) {
	            			field.setSecure(false);
	            		}
            		}
            	}
            }
        }

        return sections;
	}

}
