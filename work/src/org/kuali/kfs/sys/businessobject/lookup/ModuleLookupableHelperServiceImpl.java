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
package org.kuali.kfs.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.BatchJobStatus;
import org.kuali.kfs.bo.KualiModuleBO;
import org.kuali.kfs.util.SpringServiceLocator;

public class ModuleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchJobStatusLookupableHelperServiceImpl.class);
    
    private KualiConfigurationService configService;
    private Map fieldConversions;
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        List<KualiModule> modules = SpringServiceLocator.getKualiModuleService().getInstalledModules();
        String codeValue = fieldValues.get("moduleCode");
        String nameValue = fieldValues.get( "moduleName");
        List<KualiModuleBO> boModules = new ArrayList();
        for ( KualiModule mod : modules ) {
            if ( !StringUtils.isEmpty(nameValue) && !StringUtils.containsIgnoreCase(mod.getModuleName(), nameValue) ) {
                continue; 
            }
            if ( !StringUtils.isEmpty(codeValue) && !StringUtils.containsIgnoreCase(mod.getModuleCode(), codeValue) ) {
                continue;
            }
                boModules.add( new KualiModuleBO(mod.getModuleCode(), mod.getModuleId(), mod.getModuleName()));    
        } 
        return boModules;
    }

    
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
   
    public List getReturnKeys() {
        List returnKeys;
        
        if (fieldConversions!= null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList(fieldConversions.keySet());
        }
        else {
            returnKeys = SpringServiceLocator.getBusinessObjectDictionaryService().getLookupFieldNames(org.kuali.kfs.bo.KualiModuleBO.class);
        }

        return returnKeys;
    }
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }
    

}
