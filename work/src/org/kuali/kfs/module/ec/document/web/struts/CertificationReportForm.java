/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.form;

import org.kuali.module.effort.util.DynamicCollectionComparator;
import org.kuali.module.effort.util.DynamicCollectionComparator.SortOrder;

/**
 * Action form for Effort Certification Document.
 */
public class CertificationReportForm extends EffortCertificationForm {

    private String sortOrder = SortOrder.ASC.name();

    /**
     * Gets the sortOrder attribute.
     * 
     * @return Returns the sortOrder.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sortOrder attribute value.
     * 
     * @param sortOrder The sortOrder to set.
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Toggles the sort order between ascending and descending. If the current order is ascending, then the sort order will be set
     * to descending, and vice versa.
     */
    public void toggleSortOrder() {
        if (SortOrder.ASC.name().equals(this.getSortOrder())) {
            this.setSortOrder(SortOrder.DESC.name());
        }
        else {
            this.setSortOrder(SortOrder.ASC.name());
        }
    }

    /**
     * sort the detail lines based on the values of the sort order and sort column
     */
    public void sortDetailLine(String... sortColumn) {
        String sortOrder = this.getSortOrder();
        DynamicCollectionComparator.sort(this.getDetailLines(), SortOrder.valueOf(sortOrder), sortColumn);
    }
}
