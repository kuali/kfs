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
package org.kuali.kfs.module.cam.document.workflow;

import org.kuali.kfs.sys.document.workflow.RoutingObject;
/**
 * SubFundGroup info for routing
 */
public class RoutingAssetNumber extends RoutingObject {
    private String assetNumber;
    
    /**
     * Constructs a RoutingAssetNumber
     * @param assetNumber
     */
    public RoutingAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    /**
     * Gets the subFundGroup attribute. 
     * @return Returns the subFundGroup.
     */
    public String getAssetNumber() {
        return assetNumber;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((assetNumber == null) ? 0 : assetNumber.hashCode());
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
        final RoutingAssetNumber other = (RoutingAssetNumber) obj;
        if (assetNumber == null) {
            if (other.assetNumber != null)
                return false;
        }
        else if (!assetNumber.equals(other.assetNumber))
            return false;
        return true;
    }
}
