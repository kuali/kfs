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
package org.kuali.kfs.coa.document.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeService;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OrgDerivedValuesSetter implements DerivedValuesSetter {
	public void setDerivedValues(KualiForm form, HttpServletRequest request){
	    KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
	    MaintenanceDocumentBase maintenanceDocument = (MaintenanceDocumentBase) maintenanceForm.getDocument();
	    Organization newOrg = (Organization) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
	    String organizationZipCode = newOrg.getOrganizationZipCode();
	    String organizationCountryCode = newOrg.getOrganizationCountryCode();
	    if (StringUtils.isNotBlank(organizationZipCode) && StringUtils.isNotBlank(organizationCountryCode)) {
	        PostalCode postalZipCode = SpringContext.getBean(PostalCodeService.class).getPostalCode(organizationCountryCode, organizationZipCode);
	        if (ObjectUtils.isNotNull(postalZipCode)) {
	            newOrg.setOrganizationCityName(postalZipCode.getCityName());
	            newOrg.setOrganizationStateCode(postalZipCode.getStateCode());
	        }
	        else {
	            newOrg.setOrganizationCityName(KRADConstants.EMPTY_STRING);
                newOrg.setOrganizationStateCode(KRADConstants.EMPTY_STRING);
	        }
	    }
	    else {
            newOrg.setOrganizationCityName(KRADConstants.EMPTY_STRING);
            newOrg.setOrganizationStateCode(KRADConstants.EMPTY_STRING);
	    }
	}
}
