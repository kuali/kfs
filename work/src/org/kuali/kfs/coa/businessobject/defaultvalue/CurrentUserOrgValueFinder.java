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

import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.module.chart.bo.ChartUser;

/**
 * A value finder that returns the current user's default organization code.
 */
public class CurrentUserOrgValueFinder implements ValueFinder {

    /**
     * returns the current user's default organization code
     * 
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        if (currentUser != null) {
            return currentUser.getOrganizationCode();
        }
        else {
            return "";
        }
    }

}
