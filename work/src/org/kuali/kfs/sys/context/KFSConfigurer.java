/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;
import org.kuali.rice.core.framework.config.module.WebModuleConfiguration;
import org.springframework.beans.factory.InitializingBean;

public class KFSConfigurer extends ModuleConfigurer implements InitializingBean {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSConfigurer.class);

    protected boolean testMode = false;

    public KFSConfigurer() {
        super("KFS");
        LOG.info( "KFSConfigurer instantiated" );
        setValidRunModes(Arrays.asList(RunMode.LOCAL));
    }

    @Override
    protected void doAdditionalModuleStartLogic() throws Exception {
        LOG.info( "*********************************************************" );
        LOG.info( "KFS Starting Module" );
        LOG.info( "*********************************************************" );
        super.doAdditionalModuleStartLogic();
    }

    @Override
    protected void doAdditionalModuleStopLogic() throws Exception {
        LOG.info( "*********************************************************" );
        LOG.info( "KFS Stopping Module" );
        LOG.info( "*********************************************************" );
        super.doAdditionalModuleStopLogic();
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        String files = ConfigContext.getCurrentContextConfig().getProperty("spring.source.files");
        if ( testMode ) {
            files = files + "," + ConfigContext.getCurrentContextConfig().getProperty("spring.test.files");
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "KFS Spring Files Requested.  Returning: " + files );
        }
        return files == null ? Collections.<String>emptyList() : parseFileList(files);
    }

    protected List<String> parseFileList(String files) {
        List<String> parsedFiles = new ArrayList<String>();
        for (String file : Arrays.asList(files.split(","))) {
            String trimmedFile = file.trim();
            if (!trimmedFile.isEmpty()) {
                parsedFiles.add(trimmedFile);
            }
        }

        return parsedFiles;
    }

    @Override
    protected WebModuleConfiguration loadWebModule() {
        return new KfsWebModuleConfiguration();
    }

    @Override
    public boolean hasWebInterface() {
        return true;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
}
