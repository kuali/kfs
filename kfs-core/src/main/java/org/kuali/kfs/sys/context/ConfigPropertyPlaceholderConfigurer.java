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
