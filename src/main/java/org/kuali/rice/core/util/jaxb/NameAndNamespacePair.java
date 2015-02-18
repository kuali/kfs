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
package org.kuali.rice.core.util.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * An XML element that stores a name and namespace pair in its simple content and "namespaceCode" attribute.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="NameAndNamespaceType")
public class NameAndNamespacePair implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name="namespaceCode", required=true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String namespaceCode;
    
    @XmlValue
    private String name;
    
    public NameAndNamespacePair() {}
    
    public NameAndNamespacePair(String namespaceCode, String name) {
        this.namespaceCode = namespaceCode;
        this.name = name;
    }
    
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
