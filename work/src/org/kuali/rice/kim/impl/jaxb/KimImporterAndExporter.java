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
