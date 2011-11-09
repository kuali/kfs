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
package org.kuali.rice.krad.util;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OjbMetadataLoader implements InitializingBean {
    
    private static final Logger LOG = Logger.getLogger(OjbMetadataLoader.class);

    private List<String> repositoryDescriptors = new ArrayList<String>();
    private List<String> connectionDescriptors = new ArrayList<String>(); 
    
    public List<String> getConnectionDescriptors() {
        return connectionDescriptors;
    }

    public void setConnectionDescriptors(List<String> connectionDescriptors) {
        this.connectionDescriptors = connectionDescriptors;
    }

    public List<String> getRepositoryDescriptors() {
        return repositoryDescriptors;
    }

    public void setRepositoryDescriptors(List<String> repositoryDescriptors) {
        this.repositoryDescriptors = repositoryDescriptors;
    }

    public void afterPropertiesSet() throws Exception {
        
        MetadataManager mm = MetadataManager.getInstance();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        
        for (String repositoryDescriptor : repositoryDescriptors) {
            InputStream is = resourceLoader.getResource(repositoryDescriptor).getInputStream();
            DescriptorRepository dr = mm.readDescriptorRepository(is);
            mm.mergeDescriptorRepository(dr);
            if (LOG.isDebugEnabled()) {
                LOG.debug("--------------------------------------------------------------------------");
                LOG.debug("Merging repository descriptor: " + repositoryDescriptor);
                LOG.debug("--------------------------------------------------------------------------");
            }
            try {
                is.close();
            } catch (Exception e) {
                LOG.warn("Failed to close stream to file " + repositoryDescriptor, e);
            }
        }
        
        for (String connectionDesciptor : connectionDescriptors) {
            InputStream is = resourceLoader.getResource(connectionDesciptor).getInputStream();
            ConnectionRepository cr = mm.readConnectionRepository(is);
            mm.mergeConnectionRepository(cr);
            if (LOG.isDebugEnabled()) {
                LOG.debug("--------------------------------------------------------------------------");
                LOG.debug("Merging connection descriptor: " + connectionDesciptor);
                LOG.debug("--------------------------------------------------------------------------");
            }
            try {
                is.close();
            } catch (Exception e) {
                LOG.warn("Failed to close stream to file " + connectionDesciptor, e);
            }
        }
        
    }

}
