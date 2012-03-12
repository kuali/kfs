/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch.service;

import java.io.InputStream;
import java.util.Map;

import org.kuali.kfs.module.cam.batch.AssetBarcodeInventoryInputFileType;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.kfs.sys.batch.service.BatchInputFileSetService;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;
public interface AssetBarcodeInventoryInputFileService extends BatchInputFileSetService {
    public Map<String, String> save(Person user, AssetBarcodeInventoryInputFileType inputType, String fileUserIdentifer, Map<String, InputStream> typeToStreamMap, AssetBarCodeInventoryInputFileForm form) throws AuthorizationException, FileStorageException;    
}

