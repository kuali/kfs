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
package org.kuali.kfs.module.purap.document;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * This class allows for the use of items by multiple purap type docs
 */
public interface PurapItemOperations {
    
    public List getItems();

    public void setItems(List<PurApItem> items);

    public <T extends Object>T getItem(int pos);

    public abstract Class getItemClass();
}
