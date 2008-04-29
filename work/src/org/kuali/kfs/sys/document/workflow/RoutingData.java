/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.core.util.Guid;

/**
 * This class encapsulates the information needed by the generic routing 
 * framework. The routing attributes that a particular piece
 * of routing data can be used by are listed as elements of a String 
 * array: routingTypes. This array has an additional element that
 * is populated with a Guid. This is just to prevent XStream from creating 
 * a reference if two RoutingData objects have identical
 * routingTypes data.  Each RoutingTypes element is the simple name of the 
 * class of the routing attribute that will use the routingSet information of
 * the RoutingData object to calculate routing.
 * 
 * The resulting XML is generally more readable if each RoutingData object
 * has a unique RoutingTypes and the RoutingSet contains multiple RoutingObjects.
 */
public class RoutingData {
    List <String> routingTypes;
    Set routingSet;
    public Set getRoutingSet() {
        return routingSet;
    }
    public void setRoutingSet(Set routingSet) {
        routingSet.add(new RoutingGuid());
        this.routingSet = routingSet;
    }
    public List getRoutingTypes() {
        return routingTypes;
    }
    public void setRoutingTypes(List <String> routingTypes) {
        this.routingTypes = routingTypes;
        this.routingTypes.add(new Guid().toString());
        
   }
   public void setRoutingType(String routingType){
        this.routingTypes = new ArrayList();
        this.routingTypes.add(routingType);
        this.routingTypes.add(new Guid().toString());//to prevent an XStream reference
   }
}
