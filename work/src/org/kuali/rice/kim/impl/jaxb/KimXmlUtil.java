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
