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

    @Override
    public RunMode getRunMode() {
        return RunMode.LOCAL;
    }
}
