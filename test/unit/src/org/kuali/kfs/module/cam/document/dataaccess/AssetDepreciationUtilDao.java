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
package org.kuali.kfs.module.cam.document.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.rice.kns.bo.PersistableBusinessObject;

public interface AssetDepreciationUtilDao {
    public String getMaxDocumentNumber();
    
    public Collection<AssetPayment> getAssetPayments(List<Asset> assets);
    
    public void deleteAssetPayment(List<Asset> assets);
    
    public void deleteGLPEs();
    
    /**
     * 
     * Deletes any asset number with the same number of the testing asset numbers
     * @param assetPayments
     */
    public void deleteAssets(List<Asset> assets);
    
    public void save(List businessObjects);
}
