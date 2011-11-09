/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetLocationGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

	private String documentNumber;
    private DocumentHeader documentHeader;
    private List<AssetLocationGlobalDetail> assetLocationGlobalDetails;
    
	/**
	 * Default constructor.
	 */
	public AssetLocationGlobal() {
        assetLocationGlobalDetails = new ArrayList<AssetLocationGlobalDetail>();
	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }
    
    /**
     * Gets the assetLocationGlobalDetails attribute. 
     * @return Returns the assetLocationGlobalDetails.
     */
    public List<AssetLocationGlobalDetail> getAssetLocationGlobalDetails() {
        return assetLocationGlobalDetails;
    }

    /**
     * Sets the assetLocationGlobalDetails attribute value.
     * @param assetLocationGlobalDetails The assetLocationGlobalDetails to set.
     */
    public void setAssetLocationGlobalDetails(List<AssetLocationGlobalDetail> assetLocationGlobalDetails) {
        this.assetLocationGlobalDetails = assetLocationGlobalDetails;
    }

    /**
     * @see org.kuali.rice.krad.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Assets to update
     * 
     * @see org.kuali.rice.krad.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        // the list of persist-ready BOs
        List<PersistableBusinessObject> persistables = new ArrayList();

        // walk over each change detail record
        for (AssetLocationGlobalDetail detail : assetLocationGlobalDetails) {

            // load the object by keys
            Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, detail.getPrimaryKeys());

            // if we got a valid account, do the processing
            if (asset != null) {

                if (StringUtils.isNotBlank(detail.getCampusCode())) {
                    asset.setCampusCode(detail.getCampusCode());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingCode())) {
                    asset.setBuildingCode(detail.getBuildingCode());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingRoomNumber())) {
                    asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingSubRoomNumber())) {
                    asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
                }

                // set tag number to null if no data in field
                if (StringUtils.isNotBlank(detail.getCampusTagNumber()) && !StringUtils.equalsIgnoreCase(detail.getCampusTagNumber(), asset.getCampusTagNumber())) {
                    asset.setOldTagNumber(asset.getCampusTagNumber());
                    asset.setCampusTagNumber(detail.getCampusTagNumber());
                }
                
                updateOffCampusWithOnCampusValues(asset);
                
                asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));

                persistables.add(asset);
            }
        }

        return persistables;
    }
 
    
    /**
     * KFSMI-6695 Location Global allows update to building, room when asest has off campus location fields poplulated, after update
     * assets has both on campus and off campus. An asset should not have both an on campus address ( buidling, and room) and an off
     * campus addresses. If the building and room are updated from location global then the following fields should be set to null
     * WHERE the ast_loc_typ_cd = 'O' cm_ast_loc_t.ast_loc_cntnt_nm cm_ast_loc_t.ast_loc_strt_addr cm_ast_loc_t.ast_loc_city_nm
     * cm_ast_loc_t.ast_loc_state_cd cm_ast_loc_t.ast_loc_cntry_cd cm_ast_loc_t.ast_loc_zip_cd
     * 
     * @param asset
     */
    private void updateOffCampusWithOnCampusValues(Asset asset) {
        List<AssetLocation> toDelete = new ArrayList<AssetLocation>();
        if (asset.getAssetLocations() != null) {
            for (AssetLocation location : asset.getAssetLocations()) {
                boolean offCampus = CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equals(location.getAssetLocationTypeCode());
                boolean buildingOrRoom = StringUtils.isNotBlank(asset.getBuildingCode()) || StringUtils.isNotBlank(asset.getBuildingRoomNumber());
                if (offCampus && buildingOrRoom) {
                  location.setAssetLocationContactName(null); // cm_ast_loc_t.ast_loc_cntnt_nm
                  location.setAssetLocationStreetAddress(null); // cm_ast_loc_t.ast_loc_strt_addr
                  location.setAssetLocationCityName(null); // cm_ast_loc_t.ast_loc_city_nm
                  location.setAssetLocationStateCode(null); // cm_ast_loc_t.ast_loc_state_cd
                  location.setAssetLocationCountryCode(null); // cm_ast_loc_t.ast_loc_cntry_cd
                  location.setAssetLocationZipCode(null); // cm_ast_loc_t.ast_loc_zip_cd
                  
                  // this didn't work
                  //toDelete.add(location);
                }
            }
        }
        
        // this didn't work
        //asset.getAssetLocations().removeAll(toDelete);        
     }
    
    public boolean isPersistable() {
        return true;
    }
    
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetLocationGlobalDetails();
    }
    
    /**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }
}
