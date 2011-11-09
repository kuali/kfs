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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.OjbCollectionAware;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Helper object to deal with persisting collections.
 */
public class PurApOjbCollectionHelper {
    public final static int MAX_DEPTH = 2;

    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     * 
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        processCollectionsRecurse(template, orig, copy, MAX_DEPTH);
    }

    /**
     * This method processes collections recursively up to the depth level specified
     * 
     * @param template
     * @param orig
     * @param copy
     */
    private void processCollectionsRecurse(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy, int depth) {
        if (copy == null || depth < 1) {
            return;
        }

        List originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection<PersistableBusinessObject> origSource = (Collection<PersistableBusinessObject>) originalCollections.get(i);
                    Collection<PersistableBusinessObject> copySource = (Collection<PersistableBusinessObject>) copyCollections.get(i);
                    List list = findUnwantedElements(copySource, origSource, template, depth - 1);
                    cleanse(template, origSource, list);

                }
            }
            catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }

    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     * 
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections2(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        // if copy is null this is the first time we are saving the object, don't have to worry about updating collections
        if (copy == null) {
            return;
        }

        List originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection origSource = (Collection) originalCollections.get(i);
                    Collection copySource = (Collection) copyCollections.get(i);
                    List list = findUnwantedElements(copySource, origSource, null, 0);
                    cleanse(template, origSource, list);

                }
            }
            catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }

    /**
     * This method deletes unwanted objects from the database as well as from the given input List
     * 
     * @param origSource - list containing unwanted business objects
     * @param unwantedItems - business objects to be permanently removed
     * @param template
     */
    private void cleanse(OjbCollectionAware template, Collection origSource, List unwantedItems) {
        if (unwantedItems.size() > 0) {
            Iterator iter = unwantedItems.iterator();
            while (iter.hasNext()) {
                template.getPersistenceBrokerTemplate().delete(iter.next());
            }
        }

    }

    /**
     * This method identifies items in the first List that are not contained in the second List. It is similar to the (optional)
     * java.util.List retainAll method.
     * 
     * @param fromList list from the database
     * @param controlList list from the object
     * @return true iff one or more items were removed
     */
    private List findUnwantedElements(Collection fromList, Collection controlList, OjbCollectionAware template, int depth) {
        List toRemove = new ArrayList();

        Iterator iter = fromList.iterator();
        while (iter.hasNext()) {
            PersistableBusinessObject copyLine = (PersistableBusinessObject) iter.next();
            
            PersistableBusinessObject line = (PersistableBusinessObject) PurApObjectUtils.retrieveObjectWithIdentitcalKey(controlList, copyLine);
            if (ObjectUtils.isNull(line)) {
                toRemove.add(copyLine);
            }
            else { // since we're not deleting try to recurse on this element
                processCollectionsRecurse(template, line, copyLine, depth);
            }
        }
        return toRemove;
    }
}
