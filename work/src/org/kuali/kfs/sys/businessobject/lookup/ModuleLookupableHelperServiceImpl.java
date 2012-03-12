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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.KualiModuleBO;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

public class ModuleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchJobStatusLookupableHelperServiceImpl.class);

    private ConfigurationService configService;
    private Map fieldConversions;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        List<ModuleService> modules = SpringContext.getBean(KualiModuleService.class).getInstalledModuleServices();
        String codeValue = fieldValues.get("moduleCode");
        String nameValue = fieldValues.get("moduleName");
        List<KualiModuleBO> boModules = new ArrayList();
        String tempNamespaceName;
        for (ModuleService mod : modules) {
            if (!StringUtils.isEmpty(nameValue) && !StringUtils.containsIgnoreCase(mod.getModuleConfiguration().getNamespaceCode(), nameValue)) {
                continue;
            }
            tempNamespaceName = SpringContext.getBean(KualiModuleService.class).getNamespaceName(mod.getModuleConfiguration().getNamespaceCode());
            if (!StringUtils.isEmpty(codeValue) && !StringUtils.containsIgnoreCase(tempNamespaceName, codeValue)) {
                continue;
            }
            boModules.add(new KualiModuleBO(mod.getModuleConfiguration().getNamespaceCode(), 
                    mod.getModuleConfiguration().getNamespaceCode(), tempNamespaceName));
        }
        return boModules;
    }


    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    public List getReturnKeys() {
        List returnKeys;

        if (fieldConversions != null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList(fieldConversions.keySet());
        }
        else {
            returnKeys = SpringContext.getBean(BusinessObjectDictionaryService.class).getLookupFieldNames(org.kuali.kfs.sys.businessobject.KualiModuleBO.class);
        }

        return returnKeys;
    }

    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }


}
