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
package org.kuali.kfs.module.cam.businessobject;

/**
 * This BO subclasses Asset and is the same table. We have it so that the maintenance document framework lets us set a different
 * document type code and title for this document.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetFabrication extends Asset {
    private AssetOrganization assetOrganization;

    public AssetOrganization getAssetOrganization() {
        if (this.assetOrganization == null) {
            this.assetOrganization = new AssetOrganization();
        }
        return assetOrganization;
    }

    public void setAssetOrganization(AssetOrganization assetOrganization) {
        this.assetOrganization = assetOrganization;
    }


}
