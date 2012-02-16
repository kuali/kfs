/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.sys.Message;

/**
 * define the validation methods against the given per diem
 */
public interface PerDiemLoadValidationService {
    
    /**
     * validate the given per diem list
     * 
     * @param perDiemList the given per diem
     */
    public <T extends PerDiem> boolean validate(List<T> perDiemList);    

    /**
     * validate the given per diem
     * 
     * @param perDiem the given per diem
     * @return a list of messages if there is any error with the given per diem
     */
    public <T extends PerDiem> List<Message> validate(T perDiem);
}
