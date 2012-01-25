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
package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CommodityContractManager extends PersistableBusinessObjectBase implements MutableInactivatable {

	private String purchasingCommodityCode;
	private String campusCode;
	private Integer contractManagerCode;

    private CampusParameter campus;
    private CommodityCode commodityCode;
    private ContractManager contractManager;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public CommodityContractManager() {

	}

	public String getPurchasingCommodityCode() { 
		return purchasingCommodityCode;
	}

	public void setPurchasingCommodityCode(String purchasingCommodityCode) {
		this.purchasingCommodityCode = purchasingCommodityCode;
	}

	public String getCampusCode() { 
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public Integer getContractManagerCode() { 
		return contractManagerCode;
	}

	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}

	public CampusParameter getCampus() { 
        return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(CampusParameter campus) {
		this.campus = campus;
	}


    public CommodityCode getCommodityCode() {
        return commodityCode;
    }

    /**
     * Sets the commodity attribute value.
     * @param commodityCode The commodityCode to set.
     * @deprecated
     */
    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }


    public ContractManager getContractManager() {
        return contractManager;
    }

    /**
     * Sets the contractManager attribute value.
     * @param contractManager The contractManager to set.
     * @deprecated
     */
    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    /**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, this.purchasingCommodityCode);
        m.put(VendorPropertyConstants.CAMPUS_CODE, this.campusCode);
        if (this.contractManagerCode != null) {
            m.put(VendorPropertyConstants.CONTRACT_MANAGER_CODE, this.contractManagerCode.toString());
        }
	    return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
