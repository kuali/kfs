/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.util;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
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

    public static String getClosestParentDocumentTypeName(
            DocumentType documentType,
            Set<String> potentialParentDocumentTypeNames) {
        if ( potentialParentDocumentTypeNames == null || documentType == null ) {
            return null;
        }
        if (potentialParentDocumentTypeNames.contains(documentType.getName())) {
            return documentType.getName();
        } else {
            if ((documentType.getDocTypeParentId() == null)
                    || documentType.getDocTypeParentId().equals(
                            documentType.getDocumentTypeId())) {
                return null;
            } else {
                return getClosestParentDocumentTypeName(documentType
                        .getParentDocType(), potentialParentDocumentTypeNames);
            }
        }
    }

    public static boolean storedValueNotSpecifiedOrInputValueMatches(Map<String,String> storedValues, Map<String,String> inputValues, String attributeName) {
        return ((storedValues == null) || (inputValues == null)) || !storedValues.containsKey(attributeName) || storedValues.get(attributeName).equals(inputValues.get(attributeName));
    }

    public static boolean doesPropertyNameMatch(
            String requestedDetailsPropertyName,
            String permissionDetailsPropertyName) {
        if (StringUtils.isBlank(permissionDetailsPropertyName)) {
            return true;
        }
        if ( requestedDetailsPropertyName == null ) {
            requestedDetailsPropertyName = ""; // prevent NPE
        }
        return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
                || (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName+"."));
    }

//    public static Map<String,String> getNamespaceAndComponentSimpleName( Class<? extends Object> clazz) {
//        Map<String,String> attributeSet = new HashMap<String,String>();
//        attributeSet.put(KimConstants.AttributeConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
//        attributeSet.put(KimConstants.AttributeConstants.COMPONENT_NAME, getComponentSimpleName(clazz));
//        return attributeSet;
//    }
//
//    public static Map<String,String> getNamespaceAndComponentFullName( Class<? extends Object> clazz) {
//        Map<String,String> attributeSet = new HashMap<String,String>();
//        attributeSet.put(KimConstants.AttributeConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
//        attributeSet.put(KimConstants.AttributeConstants.COMPONENT_NAME, getComponentFullName(clazz));
//        return attributeSet;
//    }
//
//    public static Map<String,String> getNamespaceAndActionClass( Class<? extends Object> clazz) {
//        Map<String,String> attributeSet = new HashMap<String,String>();
//        attributeSet.put(KimConstants.AttributeConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
//        attributeSet.put(KimConstants.AttributeConstants.ACTION_CLASS, clazz.getName());
//        return attributeSet;
//    }
//
//    public static String getNamespaceCode(Class<? extends Object> clazz) {
//        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
//        if (moduleService == null) {
//            return KimApiConstants.KIM_TYPE_DEFAULT_NAMESPACE;
//        }
//        return moduleService.getModuleConfiguration().getNamespaceCode();
//    }

    public static String getComponentSimpleName(Class<? extends Object> clazz) {
        return clazz.getSimpleName();
    }

    public static String getComponentFullName(Class<? extends Object> clazz) {
        return clazz.getName();
    }

//    public static boolean isAttributeSetEntryEquals( Map<String,String> map1, Map<String,String> map2, String key ) {
//        return StringUtils.equals( map1.get( key ), map2.get( key ) );
//    }

    /**
     * Resolves the given kim type service name represented as a String to the appropriate QName.
     * If the value given is empty or null, then it will resolve to the default KimTypeInfoService name.
     */
    public static QName resolveKimTypeInfoServiceName(String kimTypeServiceName) {
        if (StringUtils.isBlank(kimTypeServiceName)) {
            return resolveKimTypeInfoServiceName(KimApiConstants.DEFAULT_KIM_TYPE_SERVICE);
        }
        return QName.valueOf(kimTypeServiceName);
    }

//    /**
//     * @deprecated Please use KIMServiceLocator.getKimTypeInfoService(KimType) instead
//     */
//    @Deprecated
//    public static KimTypeInfoService getKimTypeInfoService(KimType kimType){
//        return KIMServiceLocator.getKimTypeInfoService(kimType);
//    }
//
//    /**
//     * @deprecated Please use KIMServiceLocator.getKimTypeInfoService(QName) instead
//     */
//    @Deprecated
//    public static KimTypeInfoService getKimTypeInfoService( String serviceName ) {
//        return KIMServiceLocator.getKimTypeInfoService(resolveKimTypeInfoServiceName(serviceName));
//    }

    public static void copyProperties(Object targetToCopyTo, Object sourceToCopyFrom){
        if(targetToCopyTo!=null && sourceToCopyFrom!=null)
        try{
            PropertyUtils.copyProperties(targetToCopyTo, sourceToCopyFrom);
        } catch(Exception ex){
            throw new RuntimeException("Failed to copy from source object: "+sourceToCopyFrom.getClass()+" to target object: "+targetToCopyTo,ex);
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
                && !path.contains(kimContextParameterized + kimActionName))
            path = path.replace(kimActionName, kimContext+kimActionName);
        return path;
    }

    public static String stripEnd(String toStripFrom, String toStrip){
        String stripped;
        if(toStripFrom==null) return null;
        if(toStrip==null) return toStripFrom;
        if(toStrip.length() > toStripFrom.length()) return toStripFrom;
        if(toStripFrom.endsWith(toStrip)){
            StringBuffer buffer = new StringBuffer(toStripFrom);
            buffer.delete(buffer.length()-toStrip.length(), buffer.length());
            stripped = buffer.toString();
        } else stripped = toStripFrom;
        return stripped;
    }

//    protected static boolean canOverrideEntityPrivacyPreferences( String principalId ){
//        return getIdentityManagementService().isAuthorized(
//                GlobalVariables.getUserSession().getPrincipalId(),
//                KimConstants.NAMESPACE_CODE,
//                KimApiConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
//                null,
//                new HashMap<String,String>(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId) );
//    }
//
//    public static boolean isSuppressName(String entityId) {
//        EntityPrivacyPreferences privacy = null;
//        EntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
//        if (entityInfo != null) {
//            privacy = entityInfo.getPrivacyPreferences();
//        }
//        UserSession userSession = GlobalVariables.getUserSession();
//
//        boolean suppressName = false;
//        if (privacy != null) {
//            suppressName = privacy.isSuppressName();
//        }
//        return suppressName
//                && userSession != null
//                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
//                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
//    }
//
//    public static boolean isSuppressEmail(String entityId) {
//        EntityPrivacyPreferences privacy = null;
//        EntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
//        if (entityInfo != null) {
//            privacy = entityInfo.getPrivacyPreferences();
//        }
//        UserSession userSession = GlobalVariables.getUserSession();
//
//        boolean suppressEmail = false;
//        if (privacy != null) {
//            suppressEmail = privacy.isSuppressEmail();
//        }
//        return suppressEmail
//                && userSession != null
//                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
//                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
//    }
//
//    public static boolean isSuppressAddress(String entityId) {
//        EntityPrivacyPreferences privacy = null;
//        EntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
//        if (entityInfo != null) {
//            privacy = entityInfo.getPrivacyPreferences();
//        }
//        UserSession userSession = GlobalVariables.getUserSession();
//
//        boolean suppressAddress = false;
//        if (privacy != null) {
//            suppressAddress = privacy.isSuppressAddress();
//        }
//        return suppressAddress
//                && userSession != null
//                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
//                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
//    }
//
//    public static boolean isSuppressPhone(String entityId) {
//        EntityPrivacyPreferences privacy = null;
//        EntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
//        if (entityInfo != null) {
//            privacy = entityInfo.getPrivacyPreferences();
//        }
//        UserSession userSession = GlobalVariables.getUserSession();
//
//        boolean suppressPhone = false;
//        if (privacy != null) {
//            suppressPhone = privacy.isSuppressPhone();
//        }
//        return suppressPhone
//                && userSession != null
//                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
//                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
//    }
//
//    public static boolean isSuppressPersonal(String entityId) {
//        EntityPrivacyPreferences privacy = null;
//        EntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
//        if (entityInfo != null) {
//            privacy = entityInfo.getPrivacyPreferences();
//        }
//        UserSession userSession = GlobalVariables.getUserSession();
//
//        boolean suppressPersonal = false;
//        if (privacy != null) {
//            suppressPersonal = privacy.isSuppressPersonal();
//        }
//        return suppressPersonal
//                && userSession != null
//                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
//                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
//    }
//
//    public static String encryptExternalIdentifier(String externalIdentifier, String externalIdentifierType){
//        Map<String, String> criteria = new HashMap<String, String>();
//        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE, externalIdentifierType);
//        ExternalIdentifierType externalIdentifierTypeObject = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
//        if( externalIdentifierTypeObject!= null && externalIdentifierTypeObject.isEncryptionRequired()){
//            if(StringUtils.isNotEmpty(externalIdentifier)){
//                try{
//                    return SpringContext.getBean(EncryptionService.class).encrypt(externalIdentifier);
//                }catch (GeneralSecurityException e) {
//                    LOG.info("Unable to encrypt value : " + e.getMessage() + " or it is already encrypted");
//                }
//            }
//        }
//        return externalIdentifier;
//    }
//
//    public static String decryptExternalIdentifier(String externalIdentifier, String externalIdentifierType){
//        Map<String, String> criteria = new HashMap<String, String>();
//        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE, externalIdentifierType);
//        ExternalIdentifierType externalIdentifierTypeObject = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
//        if( externalIdentifierTypeObject!= null && externalIdentifierTypeObject.isEncryptionRequired()){
//            if(StringUtils.isNotEmpty(externalIdentifier)){
//                try{
//                    return SpringContext.getBean(EncryptionService.class).decrypt(externalIdentifier);
//                }catch (GeneralSecurityException e) {
//                    LOG.info("Unable to decrypt value : " + e.getMessage() + " or it is already decrypted");
//                }
//            }
//        }
//        return externalIdentifier;
//    }
//
//    public static IdentityManagementService getIdentityManagementService() {
//        if ( identityManagementService == null ) {
//            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
//        }
//        return identityManagementService;
//    }
//
//
//    public static GroupImpl copyInfoToGroup(Group info, GroupImpl group) {
//        group.setActive(info.isActive());
//        group.setGroupDescription(info.getGroupDescription());
//        group.setGroupId(info.getGroupId());
//        group.setGroupName(info.getGroupName());
//        group.setKimTypeId(info.getKimTypeId());
//        group.setNamespaceCode(info.getNamespaceCode());
//
//        return group;
//    }
//
//    /**
//     *
//     * @param infoMap Containing the Info Attribute objects.
//     * @param groupId for the group of attributes
//     * @param kimTypeId for the group of attributes
//     * @return a list of group attributes
//     */
//
//    public static List<GroupAttributeDataImpl> copyInfoAttributesToGroupAttributes(Map<String, String> infoMap, String groupId, String kimTypeId) {
//        List<GroupAttributeDataImpl> attrList = new ArrayList<GroupAttributeDataImpl>(infoMap.size());
//        List<KimTypeAttribute> attributeInfoList = SpringContext.getBean(KimTypeInfoService.class).getKimType(kimTypeId).getAttributeDefinitions();
//
//        for (String key : infoMap.keySet()) {
//            KimTypeAttribute typeAttributeInfo = getAttributeInfo(attributeInfoList, key);
//
//            if (typeAttributeInfo != null) {
//                GroupAttributeDataImpl groupAttribute = new GroupAttributeDataImpl();
//                groupAttribute.setKimAttributeId(typeAttributeInfo.getKimAttributeId());
//                groupAttribute.setAttributeValue(infoMap.get(typeAttributeInfo.getAttributeName()));
//                groupAttribute.setGroupId(groupId);
//                groupAttribute.setKimTypeId(kimTypeId);
//                attrList.add(groupAttribute);
//            } else {
//                throw new IllegalArgumentException("KimAttribute not found: " + key);
//            }
//        }
//        return attrList;
//    }
//
//    private static KimTypeAttribute getAttributeInfo(List<KimTypeAttribute> attributeInfoList, String attributeName) {
//        KimTypeAttribute kRet = null;
//        for (KimTypeAttribute attributeInfo : attributeInfoList) {
//            if (attributeInfo.getAttributeName().equals(attributeName)) {
//                kRet = attributeInfo;
//                break;
//            }
//        }
//        return kRet;
//    }

}
