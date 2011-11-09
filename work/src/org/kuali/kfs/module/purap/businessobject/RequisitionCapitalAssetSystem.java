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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class RequisitionCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private Integer purapDocumentIdentifier;
	
	/**
	 * Default constructor.
	 */
	public RequisitionCapitalAssetSystem() {
	    super();
	}

    
    public Class getCapitalAssetLocationClass() {
        return RequisitionCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return RequisitionItemCapitalAsset.class;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    @Override
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purapDocumentIdentifier != null) {
            m.put("purapDocumentIdentifier", this.purapDocumentIdentifier.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
	    return m;
    }

}
