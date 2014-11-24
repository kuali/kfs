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
package org.kuali.rice.kim.api.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

/**
 * An XML element that can have zero or more StringMapEntry elements. This is similar
 * to the StringMapEntryList, except this element's children are &lt;qualification&gt; elements.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="QualificationListType", propOrder={"qualifications"})
public class QualificationList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="qualification")
    private List<MapStringStringAdapter.StringMapEntry> qualifications;
    
    public QualificationList () {
        qualifications = new ArrayList<MapStringStringAdapter.StringMapEntry>();
    }
    
    public QualificationList(Map<String, String> map) {
        this();
        for (Map.Entry<String,String> tempEntry : map.entrySet()) {
            qualifications.add(new MapStringStringAdapter.StringMapEntry(tempEntry));
        }
    }

    /**
     * @return the qualifications
     */
    public List<MapStringStringAdapter.StringMapEntry> getQualifications() {
        return this.qualifications;
    }

    /**
     * @param qualifications the qualifications to set
     */
    public void setQualifications(List<MapStringStringAdapter.StringMapEntry> qualifications) {
        this.qualifications = qualifications;
    }

    
}
