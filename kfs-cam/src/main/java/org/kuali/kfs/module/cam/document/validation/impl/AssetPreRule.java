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
package org.kuali.kfs.module.cam.document.validation.impl;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business Prerules applicable to Asset documents. These PreRules checks for the Asset that needs to occur while
 * still in the Struts processing. This includes setting the .... field using the values from .... and
 * ..., and could be used for many other purposes.<br>
 * This class (unlike AssetRule) does not delegate responsibility due to limited number of PreRules.
 */
public class AssetPreRule extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPreRule.class);

    protected Asset newAsset;
    protected Asset copyAsset;
    protected String personId;

    public AssetPreRule() {
    }

    /**
     * Returns the Universal User Id of the current logged-in user
     * 
     * @return String the PersonId
     */

    public String getPersonId() {
        if (ObjectUtils.isNull(personId)) {
            this.personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        }
        return this.personId;
    }

    /**
     * Sets up a convenience object and few other Asset attributes
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
    protected void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        copyAsset = (Asset) ObjectUtils.deepCopy(newAsset);
        copyAsset.refresh();
    }
    
    protected void setFederalContribution(MaintenanceDocument document) {
    }

    
}

