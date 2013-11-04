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

import javax.xml.bind.UnmarshalException;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Helper class containing static methods for aiding in parsing parsing XML.
 * 
 * <p>All non-private methods are package-private so that only the KIM-parsing-related code can make use of them. (TODO: Is that necessary?)
 * 
 * <p>TODO: Should this be converted into a service instead?
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class PermissionXmlUtil {
    // Do not allow outside code to instantiate this class.
    private PermissionXmlUtil() {}
    
    /**
     * Validates a new permission and then saves it.
     * 
     * @param newPermission
     * @throws IllegalArgumentException if newPermission is null.
     * @throws UnmarshalException if newPermission contains invalid data.
     */
    static void validateAndPersistNewPermission(PermissionXmlDTO newPermission) throws UnmarshalException {
        if (newPermission == null) {
            throw new IllegalArgumentException("Cannot persist a null permission");
        }
        
        // Validate the new permission.
        validatePermission(newPermission);
        
        // Save the permission.
        Permission.Builder builder = Permission.Builder.create(newPermission.getNamespaceCode(), newPermission.getPermissionName());
        builder.setDescription(newPermission.getPermissionDescription());
        builder.setActive(newPermission.getActive().booleanValue());
        builder.setAttributes(newPermission.getPermissionDetails());
        
        KimApiServiceLocator.getPermissionService().createPermission(builder.build());
    }

    /**
     * Validates a permission to ensure that the required fields have been filled.
     * 
     * @throws UnmarshalException if newPermission contains invalid data.
     */
    private static void validatePermission(PermissionXmlDTO newPermission) throws UnmarshalException {
        // Ensure that the permission name, namespace, template, and description have been filled in.
        if (StringUtils.isBlank(newPermission.getPermissionName()) || StringUtils.isBlank(newPermission.getNamespaceCode())) {
            throw new UnmarshalException("Cannot create a permission with a blank name or namespace");
        } else if (StringUtils.isBlank(newPermission.getPermissionTemplateId())) {
            throw new UnmarshalException("Cannot create a permission without specifying a permission template");
        } else if (StringUtils.isBlank(newPermission.getPermissionDescription())) {
            throw new UnmarshalException("Cannot create a permission with a blank description");
        }
        
        // If another permission with that name and namespace exists, use its ID on the new permission.
        PermissionContract permission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(
                newPermission.getNamespaceCode(), newPermission.getPermissionName());
        if (permission != null) {
            newPermission.setPermissionId(permission.getId());
        }
    }
}
