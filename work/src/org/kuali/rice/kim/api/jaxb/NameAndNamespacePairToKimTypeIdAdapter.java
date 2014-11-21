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

import javax.xml.bind.MarshalException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePair;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeContract;

/**
 * An XML adapter that converts between a NameAndNamespacePair and a KIM type ID.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NameAndNamespacePairToKimTypeIdAdapter extends XmlAdapter<NameAndNamespacePair,String> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public String unmarshal(NameAndNamespacePair v) throws Exception {
        if (v != null) {
            KimTypeContract kimType = KimApiServiceLocator.getKimTypeInfoService().findKimTypeByNameAndNamespace(
                    v.getNamespaceCode(), new NormalizedStringAdapter().unmarshal(v.getName()));
            if (kimType == null) {
                throw new UnmarshalException("Cannot find KIM Type with namespace \"" + v.getNamespaceCode() + "\" and name \"" + v.getName() + "\"");
            }
            return kimType.getId();
        }
        return null;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public NameAndNamespacePair marshal(String v) throws Exception {
        if (v != null) {
            KimTypeContract kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(StringUtils.trim(v));
            if (kimType == null) {
                throw new MarshalException("Cannot find KIM Type with ID \"" + v + "\"");
            }
            return new NameAndNamespacePair(kimType.getNamespaceCode(), kimType.getName());
        }
        return null;
    }

}
