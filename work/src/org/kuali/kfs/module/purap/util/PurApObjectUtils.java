/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.document.LedgerPostingDocumentBase;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.module.purap.document.PurchasingDocumentBase;

public class PurApObjectUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApObjectUtils.class);

    /**
     * 
     * This method copies based on a class template it does not copy fields in Known Uncopyable Fields
     * Note: this only copies fields actually in the base class, it doesn't copy inherited fields currently
     * @param base the base class
     * @param src the class to copy from
     * @param target the class to copy to
     */
    public static void populateFromBaseClass(Class base,BusinessObject src,BusinessObject target,Map supplementalUncopyable) {
        List<String> fieldNames = new ArrayList<String>();
        Field[] fields = base.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        for (String fieldName : fieldNames) {
            if(!PurapConstants.KNOWN_UNCOPYABLE_FIELDS.containsKey(fieldName) && !supplementalUncopyable.containsKey(fieldName)) { 
                try {
                    ObjectUtils.setObjectProperty(target, fieldName, ObjectUtils.getPropertyValue(src, fieldName));
                }
                catch (Exception e) {
                    //purposefully skip for now 
                    //(I wish objectUtils getPropertyValue threw named errors instead of runtime) so I could
                    //selectively skip
                    LOG.debug("couldn't set field "+fieldName);
                }
            }
        }
    }
    
    /**
     * 
     * This method copies based on a class template it does not copy fields in Known Uncopyable Fields
     * @param base the base class
     * @param src 
     * @param target
     */
    public static void populateFromBaseClass(Class base,BusinessObject src,BusinessObject target) {
        populateFromBaseClass(base,src,target,new HashMap());
    }
    
    /**
     * TODO: FIXME: this needs to be fixed!
     * This method is a temporary place holder until I get this done right
     * @param po
     * @param newPO
     */
    public static void populateFromBaseWithSuper(PurchaseOrderDocument po, PurchaseOrderDocument newPO) {
//        PurApObjectUtils.populateFromBaseClass(DocumentBase.class, po, newPO);
        newPO.getDocumentHeader().setFinancialDocumentDescription(po.getDocumentHeader().getFinancialDocumentDescription());
        newPO.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        PurApObjectUtils.populateFromBaseClass(TransactionalDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(LedgerPostingDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(GeneralLedgerPostingDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(AccountingDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(PurchasingAccountsPayableDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(PurchasingDocumentBase.class, po, newPO);
        PurApObjectUtils.populateFromBaseClass(PurchaseOrderDocument.class, po, newPO);
    }
}
