/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.derviedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;

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
	    if (StringUtils.isNotBlank(organizationZipCode)) {
	        PostalCode postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryId(organizationZipCode);
	        if (ObjectUtils.isNotNull(postalZipCode)) {
	            newOrg.setOrganizationCityName(postalZipCode.getPostalCityName());
	            newOrg.setOrganizationStateCode(postalZipCode.getPostalStateCode());
	        }
	        else {
	            newOrg.setOrganizationCityName(KNSConstants.EMPTY_STRING);
                newOrg.setOrganizationStateCode(KNSConstants.EMPTY_STRING);
	        }
	    }
	    else {
            newOrg.setOrganizationCityName(KNSConstants.EMPTY_STRING);
            newOrg.setOrganizationStateCode(KNSConstants.EMPTY_STRING);
	    }
	}
}
