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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

/**
 * An XML adapter that converts between PermissionDetailList objects and Map<String, String> objects.
 * Unmarshalled keys and values will automatically be trimmed if non-null.
 * 
 * <p>This adapter will throw an exception during unmarshalling if blank or duplicate keys are encountered.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PermissionDetailListAdapter extends XmlAdapter<PermissionDetailList,Map<String, String>> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(PermissionDetailList v) throws Exception {
        if (v != null) {
            NormalizedStringAdapter normalizedStringAdapter = new NormalizedStringAdapter();
            Map<String, String> map = new HashMap<String, String>();
            for (MapStringStringAdapter.StringMapEntry stringMapEntry : v.getPermissionDetails()) {
                String tempKey = normalizedStringAdapter.unmarshal(stringMapEntry.getKey());
                if (StringUtils.isBlank(tempKey)) {
                    throw new UnmarshalException("Cannot create a permission detail entry with a blank key");
                } else if (map.containsKey(tempKey)) {
                    throw new UnmarshalException("Cannot create more than one permission detail entry with a key of \"" + tempKey + "\"");
                }
                map.put(tempKey, normalizedStringAdapter.unmarshal(stringMapEntry.getValue()));
            }
        }
        return null;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public PermissionDetailList marshal(Map<String, String> v) throws Exception {
        return (v != null) ? new PermissionDetailList(v) : null;
    }

}
