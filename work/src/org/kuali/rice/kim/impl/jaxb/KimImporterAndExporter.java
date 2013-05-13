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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.framework.impex.xml.XmlLoader;
import org.kuali.rice.kim.api.permission.PermissionContract;
import org.kuali.rice.kim.api.role.RoleContract;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.exception.ExportNotSupportedException;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Imports and exports roles and permissions from/to XML via JAXB.
 * 
 * <p>TODO: Do we need to restrict XML additions or updates based on which user is performing the ingestion?
 * 
 * <p>TODO: It may be best to make this class into a "service" instead.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimImporterAndExporter implements XmlLoader, Exporter {

    private final List<String> supportedFormats = Collections.singletonList(KRADConstants.XML_FORMAT);
    
    /**
     * @see org.kuali.rice.core.framework.impex.xml.XmlLoader#loadXml(java.io.InputStream, java.lang.String)
     */
    @Override
    public void loadXml(InputStream inputStream, String principalId) {
        KimXmlUtil.parseKimXml(inputStream);
    }

    /**
     * @see org.kuali.rice.krad.bo.Exporter#export(java.lang.Class, java.util.List, java.lang.String, java.io.OutputStream)
     */
    @Override
    public void export(Class<?> dataObjectClass, List<? extends Object> dataObjects, String exportFormat,
            OutputStream outputStream) throws IOException, ExportNotSupportedException {
        if (!supportedFormats.contains(exportFormat)) {
            throw new ExportNotSupportedException("The KimImporterAndExporter does not support the \"" + exportFormat + "\" export format");
        }
        
        if (PermissionContract.class.isAssignableFrom(dataObjectClass)) {
            KimXmlUtil.exportKimXml(outputStream, dataObjects, null);
        } else if (RoleContract.class.isAssignableFrom(dataObjectClass)) {
            KimXmlUtil.exportKimXml(outputStream, null, dataObjects);
        } else {
            throw new ExportNotSupportedException("The KimImporterAndExporter cannot export non-permission and non-role objects");
        }
        
    }

    /**
     * @see org.kuali.rice.krad.bo.Exporter#getSupportedFormats(java.lang.Class)
     */
    @Override
    public List<String> getSupportedFormats(Class<?> dataObjectClass) {
        return supportedFormats;
    }
}
