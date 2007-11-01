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
package org.kuali.workflow.attribute;

/**
 * This is a marker interface. It is used to flag the class as having used the routingChart and routingOrg fields for
 * OrgHierarchyRouting. Routing is driven by the field names. THESE METHODS MUST GET AND SET THE FIELDS NAMED routingChart and
 * routingOrg IN THE IMPLEMENTING CLASS.
 */

public interface AlternateOrgReviewRouting {

    public String getRoutingChart();

    public void setRoutingChart(String routingChart);

    public String getRoutingOrg();

    public void setRoutingOrg(String routingOrg);

}
