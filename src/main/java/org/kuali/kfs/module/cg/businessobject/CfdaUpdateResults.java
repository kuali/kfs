/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
