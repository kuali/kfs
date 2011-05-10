/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service;

/**
 * Image Service Interface.
 */
public interface ImageService {

    /**
     * Get the purchasing director signature image. This will get the image from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Campus code for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getPurchasingDirectorImage(String key, String campusCode, String location);

    /**
     * Get the contract manager signature image. This will get the image from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Contract manager ID for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getContractManagerImage(String key, Integer contractManagerId, String location);

    /**
     * Get the campus logo image. This will get the image from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Campus code for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getLogo(String key, String campusCode, String location);

}
