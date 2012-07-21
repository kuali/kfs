/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.util;

import java.util.Comparator;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;

public class GroupTravelerComparator implements Comparator<GroupTraveler> {

    @Override
    public int compare(GroupTraveler groupTraveler1, GroupTraveler groupTraveler2) {
        groupTraveler1.setName(formatName(groupTraveler1.getName()));
        groupTraveler2.setName(formatName(groupTraveler2.getName()));
        String name1 = groupTraveler1.getName();
        String name2 = groupTraveler1.getName();
        return groupTraveler1.getName().compareTo(groupTraveler2.getName());
    }
    
    
    private String formatName(String name){
        if (name.indexOf(",") > 0){
            return name;
        }
        else{
            String[] nameArr = name.split("\\s");
            if (nameArr.length == 1){
                return name;
            }
            else if (nameArr.length == 2){
                return (nameArr[1] + ", " + nameArr[0]);
            }
            else if (nameArr.length == 3){
                return (nameArr[2] + ", " + nameArr[0] + " " + nameArr[1]);
            }
            else{
                return name;
            }
           
        }
        
    }
}
