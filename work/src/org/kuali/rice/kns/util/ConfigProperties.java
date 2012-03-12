/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.util.Map;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * This class Exports all properties available from the Configuration service as constants, usable from jstl.
 * 
 * 
 */
public class ConfigProperties extends JstlPropertyHolder {
    /**
     * Default constructor.
     */
    public ConfigProperties() {
        super();

        ConfigurationService k = KRADServiceLocator.getKualiConfigurationService();
        Map<String,String> p = k.getAllProperties();
        setProperties(p);
    }
}
