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
package org.kuali.rice.kim.util;

import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kim.api.KimConstants;

/**
 * This is a description of what this class does - bhargavp don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimCommonUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimCommonUtils.class);

    public static String getClosestParentDocumentTypeName( DocumentType documentType, Set<String> potentialParentDocumentTypeNames) {
        if ( potentialParentDocumentTypeNames == null || documentType == null ) {
            return null;
        }
        if (potentialParentDocumentTypeNames.contains(documentType.getName())) {
            return documentType.getName();
        } else {
            if ( StringUtils.isBlank(documentType.getParentId())
                    || StringUtils.equals( documentType.getParentId(), documentType.getId() ) ) {
                return null;
            } else {
                return getClosestParentDocumentTypeName(KewApiServiceLocator.getDocumentTypeService().getDocumentTypeById(documentType.getParentId()), potentialParentDocumentTypeNames);
            }
        }
    }

    public static boolean storedValueNotSpecifiedOrInputValueMatches(Map<String,String> storedValues, Map<String,String> inputValues, String attributeName) {
        return ((storedValues == null)
                || (inputValues == null))
                || storedValues.isEmpty()
                || inputValues.isEmpty()
                ||  !storedValues.containsKey(attributeName)
                || StringUtils.equals( storedValues.get(attributeName), inputValues.get(attributeName));
    }

    public static boolean doesPropertyNameMatch(
            String requestedDetailsPropertyName,
            String permissionDetailsPropertyName) {
        if (StringUtils.isBlank(permissionDetailsPropertyName)) {
            return true;
        }
        return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
                || (StringUtils.startsWith(requestedDetailsPropertyName,permissionDetailsPropertyName+"."));
    }

    public static String getComponentSimpleName(Class<? extends Object> clazz) {
        return clazz.getSimpleName();
    }

    public static String getComponentFullName(Class<? extends Object> clazz) {
        return clazz.getName();
    }

    public static void copyProperties(Object targetToCopyTo, Object sourceToCopyFrom){
        if(targetToCopyTo!=null && sourceToCopyFrom!=null) {
            try{
                PropertyUtils.copyProperties(targetToCopyTo, sourceToCopyFrom);
            } catch(Exception ex){
                throw new RuntimeException("Failed to copy from source object: "+sourceToCopyFrom.getClass()+" to target object: "+targetToCopyTo,ex);
            }
        }
    }

    public static String getKimBasePath(){
        String kimBaseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KimConstants.KimUIConstants.KIM_URL_KEY);
        if (!kimBaseUrl.endsWith(KimConstants.KimUIConstants.URL_SEPARATOR)) {
            kimBaseUrl = kimBaseUrl + KimConstants.KimUIConstants.URL_SEPARATOR;
        }
        return kimBaseUrl;
    }

    public static String getPathWithKimContext(String path, String kimActionName){
        String kimContext = KimConstants.KimUIConstants.KIM_APPLICATION+KimConstants.KimUIConstants.URL_SEPARATOR;
        String kimContextParameterized = KimConstants.KimUIConstants.KIM_APPLICATION+KimConstants.KimUIConstants.PARAMETERIZED_URL_SEPARATOR;
        if(path.contains(kimActionName) && !path.contains(kimContext + kimActionName)
                && !path.contains(kimContextParameterized + kimActionName)) {
            path = path.replace(kimActionName, kimContext+kimActionName);
        }
        return path;
    }

}
