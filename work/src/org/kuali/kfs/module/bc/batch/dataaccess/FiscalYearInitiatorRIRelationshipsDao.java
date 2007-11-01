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
package org.kuali.module.budget.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.kuali.core.service.PersistenceStructureService;

public interface FiscalYearInitiatorRIRelationshipsDao {

    /*
     * some kuali objects represent reference tables keyed by fiscal period (usually a fiscal year). when a new fiscal period is
     * created, these objects are copied from the predecessor period so that it is not necessary for someone to type literally
     * thousands of rows into a maintenance document, changing only the fiscal period key. if there is referential integrity in the
     * data base, these objects must be copied in a certain order. the implementation of this interface allows us to "inject" that
     * order into a spring bean, and access and manipulate it during the copy process (informally known as "fiscal year makers").
     * this object makes adding a new object to the process simpler, by allowing the RI relationships to be expressed in XML. ALL
     * "fiscal year makers" objects should be included in this bean, even if they have no RI relationship with any other "fiscal
     * year maker" object. The object list from this bean is used to trigger the copying of every fiscal year maker object.
     */

    // fetch and set a map of child classes involved in fiscal year makers
    // (ALL such classes should be included, even those which have no parent(s)
    // in the RI tree.)
    public HashMap<String, Class> getMakerObjectsList();

    public void setMakerObjectsList(HashMap<String, Class> makerObjectList);

    // fetch and set a map of child classes involved in fiscal year makers
    // and a list of their RI parents
    public HashMap<String, ArrayList> getChildParentMap();

    public void setChildParentMap(HashMap<String, ArrayList> childParentMap);

    // when the reference objects for a base fiscal period are copied into the
    // next fiscal period, there is an option to delete all of the next period
    // objects that already exit and replace them with their base period counterparts
    // this method provides a list of the delete order which satisfies RI (i.e.,
    // the dependent object must be deleted before the parent object).
    public ArrayList getDeleteOrder();

    // this map specifies the copy order (i.e., parents must be copied before children)
    public LinkedHashMap getCopyOrder();


    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService);

    // this class exposes a method that, for each row of the child, will verify
    // that the child row has the proper RI relationship with a corresponding row
    // in each parent
    public class ParentKeyChecker<C> {
    };

}
