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
package org.kuali.kfs.sys.document.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * An interface that describes the methods used by a service that transforms AccountingLineViewRenderableElements 
 */
public interface AccountingLineRenderingTransformation {

    /**
     * Transforms the given tree of elements into the transformed tree of elements
     * @param elements a List of renderable elements to transform
     * @param accountingLine the accounting line that will be rendered
     */
    public abstract void transformElements(List<TableJoining> elements, AccountingLine accountingLine);
}
