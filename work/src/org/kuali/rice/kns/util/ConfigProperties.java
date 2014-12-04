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
