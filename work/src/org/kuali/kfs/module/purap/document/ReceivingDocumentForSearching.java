/*
 * Copyright 2009 The Kuali Foundation
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

/* 
 * This is a dummy class used to create a DataDictionary entry for the Receiving parent type (RCV) for searching.
 */
public class ReceivingDocumentForSearching extends ReceivingDocumentBase{

    /**
     * It's not needed to implement PurapItemOperations methods in this dummy class
     */
    @Override
    public Class getItemClass() {
        return null;
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this dummy class
     */
    public <T> T getItem(int pos) {
        return null;
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this dummy class 
     */
    public List getItems() {
        return null;
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this dummy class
     */
    public void setItems(List items) {
    }

}
