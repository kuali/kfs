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
