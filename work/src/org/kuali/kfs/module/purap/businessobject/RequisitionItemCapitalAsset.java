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

package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiModuleService;


/**
 * Requisition Item Capital Asset Business Object.
 */
public class RequisitionItemCapitalAsset extends PurchasingItemCapitalAssetBase {

    private CapitalAssetManagementAsset asset;

    /**
     * Default constructor.
     */
    public RequisitionItemCapitalAsset() {
        super();
    }    
    
    public RequisitionItemCapitalAsset(Long capitalAssetNumber) {
        super(capitalAssetNumber);
    }

    public CapitalAssetManagementAsset getAsset() {
        return asset = (CapitalAssetManagementAsset) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAsset.class).retrieveExternalizableBusinessObjectIfNecessary(this, asset, "asset");
    }

}
