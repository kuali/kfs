/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.util.List;

import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.KNSConstants;

public class BatchFileAdminAuthorizationServiceImpl implements BatchFileAdminAuthorizationService {

    private IdentityManagementService identityManagementService;
    private KualiModuleService kualiModuleService;
    
    public boolean canDownload(BatchFile batchFile, Person user) {
        return getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KNSConstants.KNS_NAMESPACE, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
    }

    public boolean canDelete(BatchFile batchFile, Person user) {
        return getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KNSConstants.KNS_NAMESPACE, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
    }
    
    protected String determineNamespaceCode(BatchFile batchFile) {
        for (ModuleService moduleService : getKualiModuleService().getInstalledModuleServices()) {
            ModuleConfiguration moduleConfiguration = moduleService.getModuleConfiguration();
            if (moduleConfiguration instanceof FinancialSystemModuleConfiguration) {
                List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) moduleConfiguration).getBatchFileDirectories();
                for (String batchFileDirectoryName : batchFileDirectories) {
                    File directory = new File(batchFileDirectoryName).getAbsoluteFile();
                    if (BatchFileUtils.isSuperDirectoryOf(directory, batchFile.retrieveFile())) {
                        return moduleConfiguration.getNamespaceCode();
                    }
                }
            }
        }
        return null;
    }

    protected AttributeSet generateDownloadCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }
    
    protected AttributeSet generateDownloadCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    protected AttributeSet generateDeleteCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }
    
    protected AttributeSet generateDeleteCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    private AttributeSet generatePermissionDetails(BatchFile batchFile, Person user) {
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.NAMESPACE_CODE, determineNamespaceCode(batchFile));
        permissionDetails.put("filePath", batchFile.retrieveFile().getAbsolutePath());
        return permissionDetails;
    }
    
    private AttributeSet generateRoleQualifiers(BatchFile batchFile, Person user) {
        return new AttributeSet();
    }
    
    protected IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    public KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        }
        return kualiModuleService;
    }
}
