/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.io.IOException;
import java.util.Properties;

import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.property.ConfigLogger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Configures a property placeholder in Spring which will allow access to the properties configured in the workflow configuration.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ConfigPropertyPlaceholderConfigurer.class);

    public ConfigPropertyPlaceholderConfigurer() {
        setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        // perform standard property resource file loading
        super.loadProperties(props);
        // put the properties into the Rice configuration context
        ConfigContext.getCurrentContextConfig().putProperties(props);
        // load the Rice properties
        if ( LOG.isDebugEnabled() ) {
            Config config = ConfigContext.getCurrentContextConfig();
            if (config != null) {
                LOG.debug("Replacing parameters in Spring using config:\r\n" + config);
                ConfigLogger.logConfig(config);
            }
        }
    }
}
