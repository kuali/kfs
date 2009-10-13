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
package org.kuali.kfs.module.cg.businessobject;

/**
 * Encapsulates the result of scraping the government CFDA web-page.
 */
public class CfdaUpdateResults {
    private int numberOfRecordsRetrievedFromWebSite;
    private int numberOfRecordsInKfsDatabase;
    private int numberOfRecrodsNotUpdatedForHistoricalPurposes;
    private int numberOfRecordsDeactivatedBecauseNoLongerOnWebSite;
    private int numberOfRecordsReActivated;
    private int numberOfRecordsNotUpdatedBecauseManual;
    private int numberOfRecordsUpdatedBecauseAutomatic;
    private int numberOfRecordsNewlyAddedFromWebSite;
    private String message;

    /**
     * @return
     */
    public int getNumberOfRecordsNewlyAddedFromWebSite() {
        return numberOfRecordsNewlyAddedFromWebSite;
    }

    /**
     * @param numberOfRecordsNewlyAddedFromWebSite
     */
    public void setNumberOfRecordsNewlyAddedFromWebSite(int numberOfRecordsNewlyAddedFromWebSite) {
        this.numberOfRecordsNewlyAddedFromWebSite = numberOfRecordsNewlyAddedFromWebSite;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsReActivated() {
        return numberOfRecordsReActivated;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsReActivated
     */
    public void setNumberOfRecordsReActivated(int numberOfRecordsReActivated) {
        this.numberOfRecordsReActivated = numberOfRecordsReActivated;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite() {
        return numberOfRecordsDeactivatedBecauseNoLongerOnWebSite;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsDeactivatedBecauseNoLongerOnWebSite
     */
    public void setNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite(int numberOfRecordsDeactivatedBecauseNoLongerOnWebSite) {
        this.numberOfRecordsDeactivatedBecauseNoLongerOnWebSite = numberOfRecordsDeactivatedBecauseNoLongerOnWebSite;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecrodsNotUpdatedForHistoricalPurposes() {
        return numberOfRecrodsNotUpdatedForHistoricalPurposes;
    }

    /**
     * This method...
     * 
     * @param numberOfRecrodsNotUpdatedForHistoricalPurposes
     */
    public void setNumberOfRecrodsNotUpdatedForHistoricalPurposes(int numberOfRecrodsNotUpdatedForHistoricalPurposes) {
        this.numberOfRecrodsNotUpdatedForHistoricalPurposes = numberOfRecrodsNotUpdatedForHistoricalPurposes;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsRetrievedFromWebSite() {
        return numberOfRecordsRetrievedFromWebSite;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsRetrievedFromWebSite
     */
    public void setNumberOfRecordsRetrievedFromWebSite(int numberOfRecordsRetrievedFromWebSite) {
        this.numberOfRecordsRetrievedFromWebSite = numberOfRecordsRetrievedFromWebSite;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsInKfsDatabase() {
        return numberOfRecordsInKfsDatabase;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsInKfsDatabase
     */
    public void setNumberOfRecordsInKfsDatabase(int numberOfRecordsInKfsDatabase) {
        this.numberOfRecordsInKfsDatabase = numberOfRecordsInKfsDatabase;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsUpdatedBecauseAutomatic() {
        return numberOfRecordsUpdatedBecauseAutomatic;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsUpdatedBecauseAutomatic
     */
    public void setNumberOfRecordsUpdatedBecauseAutomatic(int numberOfRecordsUpdatedBecauseAutomatic) {
        this.numberOfRecordsUpdatedBecauseAutomatic = numberOfRecordsUpdatedBecauseAutomatic;
    }

    /**
     * This method...
     * 
     * @return
     */
    public int getNumberOfRecordsNotUpdatedBecauseManual() {
        return numberOfRecordsNotUpdatedBecauseManual;
    }

    /**
     * This method...
     * 
     * @param numberOfRecordsNotUpdatedBecauseManual
     */
    public void setNumberOfRecordsNotUpdatedBecauseManual(int numberOfRecordsNotUpdatedBecauseManual) {
        this.numberOfRecordsNotUpdatedBecauseManual = numberOfRecordsNotUpdatedBecauseManual;
    }

    /**
     * This method...
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method...
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
