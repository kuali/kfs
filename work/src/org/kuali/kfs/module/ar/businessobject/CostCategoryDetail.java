/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * An interface for all details which may be children of CostCategories
 */
public interface CostCategoryDetail extends MutableInactivatable {
    /**
     * @return the category code of the cost category detail
     */
    public String getCategoryCode();

    /**
     * @return the chart of accounts of a CostCategoryDetail
     */
    public String getChartOfAccountsCode();
}