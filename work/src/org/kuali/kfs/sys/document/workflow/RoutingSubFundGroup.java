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

/**
 * SubFundGroup info for routing
 */
public class RoutingSubFundGroup extends RoutingObject {
    private String subFundGroup;
    
    /**
     * Constructs a RoutingSubFundGroup
     * @param subFundGroup
     */
    public RoutingSubFundGroup(String subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the subFundGroup attribute. 
     * @return Returns the subFundGroup.
     */
    public String getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((subFundGroup == null) ? 0 : subFundGroup.hashCode());
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
        final RoutingSubFundGroup other = (RoutingSubFundGroup) obj;
        if (subFundGroup == null) {
            if (other.subFundGroup != null)
                return false;
        }
        else if (!subFundGroup.equals(other.subFundGroup))
            return false;
        return true;
    }
}
