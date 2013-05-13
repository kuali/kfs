/*
 * Copyright 2011 The Kuali Foundation
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
