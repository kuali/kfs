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
package org.kuali.module.cams.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.web.ui.Section;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;

/**
 * Methods for the Asset maintenance document UI.
 */
public class AssetMaintainableImpl extends KualiMaintainableImpl {

    /**
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterEdit(java.util.Map)
     */
    @Override
    public void processAfterEdit(Map parameters) {
        Asset asset = (Asset) this.getBusinessObject();
        String[] value = (String[]) parameters.get(CamsPropertyConstants.Asset.DOCUMENT_TYPE_CODE);
        if (value != null){
            asset.setDocumentTypeCode(value[0]);
        }    
        super.processAfterEdit(parameters);
     }

    /**
     * Allows customizing the maintenance document interface to hide / show based on priviledge and document
     * type code (@see AssetLookupableHelperServiceImpl).
     * 
     * @param oldMaintainable
     * @return
     */
    @Override
    public List getSections(Maintainable oldMaintainable) {
        Asset asset = (Asset) this.getBusinessObject();
        List<Section> sections = new ArrayList<Section>();

        // Add all the kuali core sections
        List<Section> coreSections = getCoreSections(oldMaintainable);
        
        for (Section section : coreSections) {
            if (StringUtils.isEmpty(asset.getDocumentTypeCode())) {
                sections.add(section);
            } else if (asset.getDocumentTypeCode().equals(CamsConstants.DocumentType.ASSET_RETIREMENT) &&
                    section.getSectionTitle().equalsIgnoreCase(CamsConstants.SectionTitles.ASSET_RETIREMENT)) {
                sections.add(section);
            }
        }

        return sections;
    }
}
