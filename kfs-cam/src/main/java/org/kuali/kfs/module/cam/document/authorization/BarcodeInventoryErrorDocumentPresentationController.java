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
package org.kuali.kfs.module.cam.document.authorization;

import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

/**
 * Presentation Controller for Barcode Error Documents
 */
public class BarcodeInventoryErrorDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    protected AssetBarcodeInventoryLoadService assetBarcodeInventoryLoadService;
    
    public BarcodeInventoryErrorDocumentPresentationController() {
        assetBarcodeInventoryLoadService = SpringContext.getBean(AssetBarcodeInventoryLoadService.class); 
    }    
    @Override
    public boolean canSave(Document document) {
        return false;
    }

    @Override
    public boolean canRoute(Document document) {
        return false;
    }

    @Override
    public boolean canBlanketApprove(Document document) {
        return false;
    }
    
    @Override
    public boolean canAddAdhocRequests(Document document) {
        return SpringContext.getBean(AssetService.class).isDocumentEnrouting(document);        
    }

    @Override
    public boolean canCancel(Document document) {
        return assetBarcodeInventoryLoadService.isCurrentUserInitiator(document);
    }
    
    @Override
    public boolean canDisapprove(Document document) {
        return assetBarcodeInventoryLoadService.isCurrentUserInitiator(document);
    }
}
