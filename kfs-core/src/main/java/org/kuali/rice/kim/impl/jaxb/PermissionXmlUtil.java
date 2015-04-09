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
