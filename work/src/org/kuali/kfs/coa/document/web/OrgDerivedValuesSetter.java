/*
 * Copyright 2007-2009 The Kuali Foundation
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
