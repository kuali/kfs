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
