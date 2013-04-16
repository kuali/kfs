/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.PersistenceService;
/**
 * Purap Object Utils.
 * Similar to the nervous system ObjectUtils this class contains methods to reflectively set and get values on
 * BusinessObjects that are passed in.
 */
public class PurApObjectUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApObjectUtils.class);

    // ***** following changes are to work around an ObjectUtils bug and are copied from ObjectUtils.java
    /**
     * Compares a business object with a List of BOs to determine if an object with the same key as the BO exists in the list. If it
     * does, the item is returned.
     *
     * @param controlList - The list of items to check
     * @param bo - The BO whose keys we are looking for in the controlList
     */
    public static BusinessObject retrieveObjectWithIdentitcalKey(Collection controlList, BusinessObject bo) {
        BusinessObject returnBo = null;

        for (Iterator i = controlList.iterator(); i.hasNext();) {
            BusinessObject listBo = (BusinessObject) i.next();
            if (equalByKeys(listBo, bo)) {
                returnBo = listBo;
            }
        }

        return returnBo;
    }

    /**
     * Compares two business objects for equality of type and key values.
     *
     * @param bo1
     * @param bo2
     * @return boolean indicating whether the two objects are equal.
     */
    public static boolean equalByKeys(BusinessObject bo1, BusinessObject bo2) {
        boolean equal = true;

        if (bo1 == null && bo2 == null) {
            equal = true;
        }
        else if (bo1 == null || bo2 == null) {
            equal = false;
        }
        else if (!bo1.getClass().getName().equals(bo2.getClass().getName())) {
            equal = false;
        }
        else {
            Map bo1Keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(bo1);
            Map bo2Keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(bo2);
            for (Iterator iter = bo1Keys.keySet().iterator(); iter.hasNext();) {
                String keyName = (String) iter.next();
                if (bo1Keys.get(keyName) != null && bo2Keys.get(keyName) != null) {
                    if (!bo1Keys.get(keyName).toString().equals(bo2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                }
                else {
                    // CHANGE FOR PurapOjbCollectionHelper change if one is null we are likely looking at a new object (sequence) which is definitely
                    // not equal
                    equal = false;
                }
            }
        }


        return equal;
    }
    // ***** END copied from ObjectUtils.java changes
}
