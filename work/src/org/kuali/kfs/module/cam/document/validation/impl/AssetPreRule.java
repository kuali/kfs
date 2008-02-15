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
package org.kuali.module.cams.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.rules.MaintenancePreRulesBase;

/**
 * Business Prerules applicable to Asset documents. These PreRules checks for the Asset that needs to occur while
 * still in the Struts processing. This includes setting the .... field using the values from .... and
 * ..., and could be used for many other purposes.<br>
 * This class (unlike AssetRule) does not delegate responsibility due to limited number of PreRules.
 */
public class AssetPreRule extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPreRule.class);

    private Asset newAsset;
    private Asset copyAsset;
    private String universalUserId;

    public AssetPreRule() {
    }

    /**
     * Returns the Universal User Id of the current logged-in user
     * 
     * @return String the UniversalUserId
     */

    public String getUniversalUserId() {
        if (ObjectUtils.isNull(universalUserId)) {
            this.universalUserId = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        }
        return this.universalUserId;
    }

    /**
     * Sets up a convenience object and few other Asset attributes
     * 
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
  
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        setFederalContribution(document);
        return true;
    }

    /**
     * Sets the convenience objects like newAsset and oldAsset, so you have short and easy handles to the new and old
     * objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        copyAsset = (Asset) ObjectUtils.deepCopy(newAsset);
        copyAsset.refresh();
    }
    
    private void setFederalContribution(MaintenanceDocument document) {
        //newAsset.setFederalContributionAmount(new KualiDecimal(1000.00));
        
    }

    
}
