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
package org.kuali.rice.kim.impl.jaxb;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.kuali.rice.core.impl.jaxb.DataXmlDTO;

/**
 * Helper class for importing and exporting KIM XML.
 * 
 * <p>TODO: Should this be converted into a service instead?
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class KimXmlUtil {
    // Do not allow outside code to instantiate this class.
    private KimXmlUtil() {}
    
    /**
     * Parses permissions and/or roles from XML.
     * 
     * @param inputStream The input stream to read the XML from.
     */
    public static void parseKimXml(InputStream inputStream) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataXmlDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Exports one or more sets of KIM objects to XML.
     * 
     * @param outputStream The output stream to write the XML to.
     * @param permissions The KIM permissions to export; set to a null or empty list to prevent exportation of a &lt;permissionData&gt; element.
     * @param roles The KIM roles to export; set to a null or empty list to prevent exportation of a &lt;roleData&gt; element.
     */
    public static void exportKimXml(OutputStream outputStream, List<? extends Object> permissions, List<? extends Object> roles) {
        PermissionDataXmlDTO permissionData = (permissions != null && !permissions.isEmpty()) ?
                new PermissionDataXmlDTO(new PermissionsXmlDTO(permissions)) : null;
        RoleDataXmlDTO roleData = (roles != null && !roles.isEmpty()) ?
                new RoleDataXmlDTO(new RolesXmlDTO(roles)) : null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataXmlDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(new DataXmlDTO(permissionData, roleData), outputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
