/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.dao.ojb;

import java.util.Arrays;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.ojb.KualiModuleUserDaoOjb;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.kra.KraConstants;

public class KraUserDaoOjb extends KualiModuleUserDaoOjb {

	KualiConfigurationService configService;
	
	@Override
	public Object getActiveUserQueryCriteria(String moduleId) {
        String[] values = getConfigService().getParameterValues( KFSConstants.KRA_NAMESPACE, KFSConstants.Components.ALL, KraConstants.ALLOWED_EMPLOYEE_STATUS_RULE  );

		Criteria criteria = new Criteria();
		criteria.addEqualTo("staff", "Y");
		Criteria isFacultyCriteria = new Criteria();
		isFacultyCriteria.addEqualTo("faculty", "Y");
		criteria.addOrCriteria(isFacultyCriteria);
		criteria.addIn("employeeStatusCode", Arrays.asList(values));
		return criteria; 
        
	}

	public KualiConfigurationService getConfigService() {
		return configService;
	}

	public void setConfigService(KualiConfigurationService configService) {
		this.configService = configService;
	}
	
}
