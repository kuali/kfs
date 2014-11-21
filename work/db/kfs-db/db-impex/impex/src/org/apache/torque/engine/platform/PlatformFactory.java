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
package org.apache.torque.engine.platform;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class responsible to create Platform objects that
 * define RDBMS platform specific behaviour.
 *
 * @author Thomas Mahler
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformFactory.java,v 1.1 2007-10-21 07:57:27 abyrne Exp $
 */
public class PlatformFactory
{
    private static HashMap platforms = new HashMap();
    private static Log log = LogFactory.getLog(PlatformFactory.class);

    /**
     * Returns the Platform for a platform name.
     *
     * @param dbms name of the platform
     */
    public static Platform getPlatformFor(String dbms)
    {
        Platform result = null;
        String platformName = null;

        result = (Platform) getPlatforms().get(dbms);
        if (result == null)
        {
            try
            {
                platformName = getClassnameFor(dbms);
                Class platformClass = Class.forName(platformName);
                result = (Platform) platformClass.newInstance();

            }
            catch (Throwable t)
            {
                log.warn("problems with platform " + platformName
                        + ": " + t.getMessage());
                log.warn("Torque will use PlatformDefaultImpl instead");

                result = new PlatformDefaultImpl();
            }
            getPlatforms().put(dbms, result); // cache the Platform
        }
        return result;
    }

    /**
     * compute the name of the concrete Class representing the Platform
     * specified by <code>platform</code>
     *
     * @param platform the name of the platform as specified in the repository
     */
    private static String getClassnameFor(String platform)
    {
        String pf = "Default";
        if (platform != null)
        {
            pf = platform;
        }
        return "org.apache.torque.engine.platform.Platform" + pf.substring(0, 1).toUpperCase() + pf.substring(1) + "Impl";
    }

    /**
     * Gets the platforms.
     *
     * @return Returns a HashMap
     */
    private static HashMap getPlatforms()
    {
        return platforms;
    }
}
