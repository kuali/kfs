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
package org.kuali.module.chart.lookup.valuefinder;

import java.util.Map;

import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ChartUser;

/**
 * 
 * This class holds utilities to assist with the 
 */
public class ValueFinderUtil {
    
    /**
     * 
     * This method returns the currently logged in Chart User.
     * @return the currently logged in Chart User
     */
    public static ChartUser getCurrentChartUser() {
        UniversalUser currentUser = ValueFinderUtil.getCurrentUniversalUser();
        if (currentUser != null) {
            Map<String, KualiModuleUser> moduleUsers = SpringContext.getBean(UniversalUserService.class).getModuleUsers(currentUser);
            return (ChartUser)moduleUsers.get(ChartUser.MODULE_ID);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * This method returns the currently logged in Universal User.
     * @return the currently logged in Universal User
     */
    private static UniversalUser getCurrentUniversalUser() {
        if (GlobalVariables.getUserSession() != null) {
            return GlobalVariables.getUserSession().getUniversalUser();
        } else {
            return null;
        }
    }
}
