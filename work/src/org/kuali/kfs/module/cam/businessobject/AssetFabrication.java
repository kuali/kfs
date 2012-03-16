/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
