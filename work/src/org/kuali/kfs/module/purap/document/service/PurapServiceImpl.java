/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/service/PurapServiceImpl.java,v $
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
package org.kuali.module.purap.service.impl;

import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.service.PurapService;

public class PurapServiceImpl implements PurapService {
// TODO: make the boService available, also need to add to the PurapService section of KualiSpringBeansPurap.xml
//    private BusinessObjectService boService;
//
//    protected BusinessObjectService getBOService() {
//        if( ObjectUtils.isNull( this.boService ) ) {
//            this.boService = SpringServiceLocator.getBusinessObjectService();
//        }
//        return this.boService;
//    }
    
    /**
     * This method updates the status and status history for a purap document.
     *
    */
    public boolean updateStatusAndStatusHistory(PurchasingAccountsPayableDocument document, String statusToSet) {
        
        boolean success = false; // TODO: is this needed?
        
        if ( ObjectUtils.isNull(document) || ObjectUtils.isNull(statusToSet) ) {
            return success;
        }

        document.setStatusCode(statusToSet);
        
        // TODO: update the status history.
        
        // TODO: if/else that checks to see if the document's status and status history is correct,
        //   then set success = true if so.
        
        success = true;
        return success;
    }
    
}
