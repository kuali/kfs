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
import java.util.Set;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.Document;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;

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
//    public static void populateFromBaseClass(Class base,BusinessObject src,BusinessObject target,Map supplementalUncopyable) {
//        List<String> fieldNames = new ArrayList<String>();
//        Field[] fields = base.getDeclaredFields();
//        for (Field field : fields) {
//            fieldNames.add(field.getName());
//        }
//        for (String fieldName : fieldNames) {
//            if(!PurapConstants.KNOWN_UNCOPYABLE_FIELDS.containsKey(fieldName) && !supplementalUncopyable.containsKey(fieldName)) { 
//                try {
//                    ObjectUtils.setObjectProperty(target, fieldName, ObjectUtils.getPropertyValue(src, fieldName));
//                }
//                catch (Exception e) {
//                    //purposefully skip for now 
//                    //(I wish objectUtils getPropertyValue threw named errors instead of runtime) so I could
//                    //selectively skip
//                    LOG.debug("couldn't set field '"+fieldName+"' due to exception with class name '"+e.getClass().getName()+"'");
//                }
//            }
//        }
//    }
    public static void populateFromBaseClass(Class base,BusinessObject src,BusinessObject target,Map supplementalUncopyable) {
        List<String> fieldNames = new ArrayList<String>();
        Field[] fields = base.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        for (String fieldName : fieldNames) {
            if ( (isProcessableField(base, fieldName, PurapConstants.KNOWN_UNCOPYABLE_FIELDS)) && (isProcessableField(base, fieldName, supplementalUncopyable)) ) {
                attemptCopyOfFieldName(base.getName(), fieldName, src, target);
            }
        }
    }
    
    private static boolean isProcessableField(Class baseClass, String fieldName, Map excludedFieldNames) {
        if (excludedFieldNames.containsKey(fieldName)) {
            Class potentialClassName = (Class) excludedFieldNames.get(fieldName);
            if ( (ObjectUtils.isNull(potentialClassName)) || (potentialClassName.equals(baseClass)) ) {
                return false;
            }
        }
        return true;
    }
    
    private static void attemptCopyOfFieldName(String baseClassName, String fieldName, BusinessObject sourceObject, BusinessObject targetObject) {
        try {
            ObjectUtils.setObjectProperty(targetObject, fieldName, ObjectUtils.getPropertyValue(sourceObject, fieldName));
        }
        catch (Exception e) {
            //purposefully skip for now 
            //(I wish objectUtils getPropertyValue threw named errors instead of runtime) so I could
            //selectively skip
            LOG.debug("couldn't set field '"+fieldName+"' using base class '"+baseClassName+"' due to exception with class name '"+e.getClass().getName()+"'");
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
     * FIXME: this needs to be fixed!
     * This method is a temporary place holder until I get this done right
     * @param po
     * @param newPO
     */
    public static void populateFromBaseWithSuper(Document po, Document newPO, Map supplementalUncopyableFieldNames, Set<Class> classesToExclude) {
        List<Class> classesToCopy = new ArrayList<Class>();
        Class sourceObjectClass = po.getClass();
        classesToCopy.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            if (!classesToExclude.contains(sourceObjectClass)) {
                classesToCopy.add(sourceObjectClass);
            }
        }
        for (int i = (classesToCopy.size() - 1); i >= 0; i--) {
            Class temp = classesToCopy.get(i);
            populateFromBaseClass(temp, po, newPO, supplementalUncopyableFieldNames);
        }
    }
//    public static void populateFromBaseWithSuper(PurchaseOrderDocument po, PurchaseOrderDocument newPO) {
//        List<Class> classesToCopy = new ArrayList<Class>();
//        Class sourceObjectClass = po.getClass();
//        classesToCopy.add(sourceObjectClass);
//        while (sourceObjectClass.getSuperclass() != null) {
//            sourceObjectClass = sourceObjectClass.getSuperclass();
//            classesToCopy.add(sourceObjectClass);
//        }
//        for (int i = (classesToCopy.size() - 1); i > 0; i--) {
//            Class temp = classesToCopy.get(i);
//            populateFromBaseClass(temp, po, newPO);
//        }
////        PurApObjectUtils.populateFromBaseClass(DocumentBase.class, po, newPO);
//        newPO.getDocumentHeader().setFinancialDocumentDescription(po.getDocumentHeader().getFinancialDocumentDescription());
//        newPO.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
//        PurApObjectUtils.populateFromBaseClass(TransactionalDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(LedgerPostingDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(GeneralLedgerPostingDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(AccountingDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(PurchasingAccountsPayableDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(PurchasingDocumentBase.class, po, newPO);
//        PurApObjectUtils.populateFromBaseClass(PurchaseOrderDocument.class, po, newPO);
//    }
}
