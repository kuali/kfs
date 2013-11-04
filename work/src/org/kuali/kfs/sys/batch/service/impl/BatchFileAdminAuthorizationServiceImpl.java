/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

public class BatchFileAdminAuthorizationServiceImpl implements BatchFileAdminAuthorizationService {

    private static final String RESEARCH_PARTICIPANTS_PERMISSION = "Download Research Participant Check File(s)";
    private IdentityManagementService identityManagementService;
    private KualiModuleService kualiModuleService;

    @Override
    public boolean canDownload(BatchFile batchFile, Person user) {

        boolean isAuthorized = false;
        if (batchFile.getFileName().indexOf(PdpConstants.RESEARCH_PARTICIPANT_FILE_PREFIX) >= 0) {
              isAuthorized = getIdentityManagementService().hasPermissionByTemplateName(user.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name, generateDownloadCheckPermissionDetails(batchFile, user));
        }
        else {
            isAuthorized = getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.namespace, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
        }
        return isAuthorized;
    }

    @Override
    public boolean canDelete(BatchFile batchFile, Person user) {
        boolean isAuthorized = false;
        if (batchFile.getFileName().indexOf(PdpConstants.RESEARCH_PARTICIPANT_FILE_PREFIX) >= 0) {
            isAuthorized = getIdentityManagementService().hasPermissionByTemplateName(user.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name, generateDownloadCheckPermissionDetails(batchFile, user));
        }
        else {
            isAuthorized = getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.namespace, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
        }
        return isAuthorized;
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

    protected Map<String,String> generateDownloadCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }

    protected Map<String,String> generateDownloadCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    protected Map<String,String> generateDeleteCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }

    protected Map<String,String> generateDeleteCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    protected Map<String,String> generatePermissionDetails(BatchFile batchFile, Person user) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, determineNamespaceCode(batchFile));
        permissionDetails.put(KfsKimAttributes.FILE_PATH, replaceSlashes(batchFile.getPath() + File.separator + batchFile.getFileName()));
        return permissionDetails;
    }

    /**
     * The permissions for the filePath will be added using '/' directory separators.
     * This method will replace any '\\' directory separators with '/'
     *
     * @param filePath
     * @return
     */
    private String replaceSlashes(String filePath) {

        if (File.separatorChar == '\\') {
            filePath = filePath.replace(File.separatorChar, '/');
        }

        return filePath;
    }

    protected Map<String,String> generateRoleQualifiers(BatchFile batchFile, Person user) {
        return new HashMap<String,String>();
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
