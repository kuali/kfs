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
