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

import javax.xml.bind.MarshalException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;

/**
 * An XML adapter that simply validates the NameAndNamespacePair to ensure that the name and namespace are non-blank
 * and that the namespace code maps to a valid namespace in the system. This adapter will also pass the name to
 * a NormalizedStringAdapter instance for marshalling/unmarshalling.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NameAndNamespacePairValidatingAdapter extends XmlAdapter<NameAndNamespacePair,NameAndNamespacePair> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public NameAndNamespacePair unmarshal(NameAndNamespacePair v) throws Exception {
        if (v != null) {
            
            if (StringUtils.isBlank(v.getName())) {
                throw new UnmarshalException("Cannot import a name-and-namespace pair with a blank name");
            } else if (StringUtils.isBlank(v.getNamespaceCode())) {
                throw new UnmarshalException("Cannot import a name-and-namespace pair with a blank namespace code");
            } if (CoreServiceApiServiceLocator.getNamespaceService().getNamespace(v.getNamespaceCode()) == null) {
                throw new UnmarshalException("Cannot import a name-and-namespace pair with invalid or unknown namespace \"" +
                        v.getNamespaceCode() + "\"");
            }
            
            v.setName(new NormalizedStringAdapter().unmarshal(v.getName()));
            v.setNamespaceCode(v.getNamespaceCode());
        }
        return v;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public NameAndNamespacePair marshal(NameAndNamespacePair v) throws Exception {
        if (v != null) {
            if (StringUtils.isBlank(v.getName())) {
                throw new MarshalException("Cannot export a name-and-namespace pair with a blank name");
            } else if (StringUtils.isBlank(v.getNamespaceCode())) {
                throw new MarshalException("Cannot export a name-and-namespace pair with a blank namespace code");
            } else if (CoreServiceApiServiceLocator.getNamespaceService().getNamespace(v.getNamespaceCode()) == null) {
                throw new MarshalException("Cannot export a name-and-namespace pair with invalid or unknown namespace \"" + v.getNamespaceCode() + "\"");
            }
            
            v.setName(new NormalizedStringAdapter().marshal(v.getName()));
            v.setNamespaceCode(v.getNamespaceCode());
        }
        return v;
    }

}
