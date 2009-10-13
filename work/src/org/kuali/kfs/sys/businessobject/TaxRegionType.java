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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TaxRegionType extends PersistableBusinessObjectBase implements Inactivateable {
	
	private String taxRegionTypeCode;
	private String taxRegionTypeName;
	private boolean active;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTaxRegionTypeCode() {
		return taxRegionTypeCode;
	}

	public void setTaxRegionTypeCode(String taxRegionTypeCode) {
		this.taxRegionTypeCode = taxRegionTypeCode;
	}

	public String getTaxRegionTypeName() {
		return taxRegionTypeName;
	}

	public void setTaxRegionTypeName(String taxRegionTypeName) {
		this.taxRegionTypeName = taxRegionTypeName;
	}

	@Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }
}
