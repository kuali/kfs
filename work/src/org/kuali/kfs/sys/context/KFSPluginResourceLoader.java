/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.core.workflow.KFSResourceLoader;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.BaseResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;

/**
 * A ResourceLoader for the KFS plugin for KEW.  Essentially, this will load the KFS
 * Spring Context and then delegate calls to the KFSResourceLoader which is loaded
 * by Spring.
 *
 * @author Eric Westfall
 */
public class KFSPluginResourceLoader extends BaseResourceLoader {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSPluginResourceLoader.class);

    private ResourceLoader delegateResourceLoader;

    public KFSPluginResourceLoader() {
        super(new QName("KFSPluginResourceLoader"));
    }

    @Override
    public void start() throws Exception {
        if (isStarted()) {
            LOG.warn("KFSPluginResourceLoader already started.");
            return;
        }
        SpringContext.initializePluginApplicationContext();
        ResourceLoader kfsResourceLoader = SpringContext.getBean(KFSResourceLoader.class);
        if (kfsResourceLoader == null) {
            throw new RuntimeException("Could not locate the KFSResourceLoader");
        }
        delegateResourceLoader = kfsResourceLoader;
        super.start();
    }

    @Override
    public void stop() throws Exception {
        SpringContext.close();
        delegateResourceLoader = null;
        super.stop();
    }

    public void addResourceLoader(ResourceLoader arg0) {
        delegateResourceLoader.addResourceLoader(arg0);
    }

    public void addResourceLoaderFirst(ResourceLoader arg0) {
        delegateResourceLoader.addResourceLoaderFirst(arg0);
    }

    public String getContents(String arg0, boolean arg1) {
        return delegateResourceLoader.getContents(arg0, arg1);
    }

    public Object getObject(ObjectDefinition arg0) {
        return delegateResourceLoader.getObject(arg0);
    }

    public ResourceLoader getResourceLoader(QName arg0) {
        return delegateResourceLoader.getResourceLoader(arg0);
    }

    public List<QName> getResourceLoaderNames() {
        return delegateResourceLoader.getResourceLoaderNames();
    }

    public List<ResourceLoader> getResourceLoaders() {
        return delegateResourceLoader.getResourceLoaders();
    }

    public Object getService(QName arg0) {
        if (delegateResourceLoader == null) {
            return null;
        }
        return delegateResourceLoader.getService(arg0);
    }

    public void removeResourceLoader(QName arg0) {
        delegateResourceLoader.removeResourceLoader(arg0);
    }

}
