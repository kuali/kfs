/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.workflow;

public final class OrgReviewRoutingData extends RoutingObject{
    
    String routingChart;
    String routingOrg;
    String routingOverrideCode;
    
    public String getRoutingOverrideCode() {
        return routingOverrideCode;
    }

    public void setRoutingOverrideCode(String routingOverrideCode) {
        this.routingOverrideCode = routingOverrideCode;
    }

    public OrgReviewRoutingData (String routingChart, String routingOrg){
        this.routingChart=routingChart;
        this.routingOrg=routingOrg;
    }
    
    public OrgReviewRoutingData (String routingChart, String routingOrg, String routingOverrideCode){
        this.routingChart=routingChart;
        this.routingOrg=routingOrg;
        this.routingOverrideCode= routingOverrideCode;
    }
    
    public String getRoutingChart(){
        return routingChart;
    }

    public void setRoutingChart(String routingChart){
        this.routingChart=routingChart;
    }
    
    public String getRoutingOrg(){
        return routingOrg;
    }

    public void setRoutingOrg(String routingOrg){
        this.routingOrg=routingOrg;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((routingChart == null) ? 0 : routingChart.hashCode());
        result = PRIME * result + ((routingOrg == null) ? 0 : routingOrg.hashCode());
        result = PRIME * result + ((routingOverrideCode == null) ? 0 : routingOverrideCode.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OrgReviewRoutingData other = (OrgReviewRoutingData) obj;
        if (routingChart == null) {
            if (other.routingChart != null)
                return false;
        }
        else if (!routingChart.equals(other.routingChart))
            return false;
        if (routingOrg == null) {
            if (other.routingOrg != null)
                return false;
        }
        else if (!routingOrg.equals(other.routingOrg))
            return false;
        if (routingOverrideCode == null) {
            if (other.routingOverrideCode != null)
                return false;
        }
        else if (!routingOverrideCode.equals(other.routingOverrideCode))
            return false;
        return true;
    }
}
