/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.impl.config.module.ModuleConfigurer;
import org.kuali.rice.krad.config.KRADConfigurer;

public class KFSConfigurer extends ModuleConfigurer {

    /**
     * Overridden to do nothing, since the default path from Rice is not one we want to use.
     * 
     * @see org.kuali.rice.core.impl.config.module.ModuleConfigurer#getPrimarySpringFiles()
     */
    @Override
    public List<String> getPrimarySpringFiles() {
        return Collections.emptyList();
    }
    
    @Override
    public String getWebModuleConfigName() {
        return "config";
    }
    
    @Override
    public String getWebModuleConfigurationFiles() {
        return "WEB-INF/struts-config.xml";
    }
    @Override
    public List<String> getAdditionalSpringFiles() {        
        return super.getAdditionalSpringFiles();
    }
}
