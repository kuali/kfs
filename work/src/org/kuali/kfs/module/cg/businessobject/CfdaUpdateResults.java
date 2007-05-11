package org.kuali.module.cg.service;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: May 11, 2007
 * Time: 2:33:36 PM
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

    public int getNumberOfRecordsNewlyAddedFromWebSite() {
        return numberOfRecordsNewlyAddedFromWebSite;
    }

    public void setNumberOfRecordsNewlyAddedFromWebSite(int numberOfRecordsNewlyAddedFromWebSite) {
        this.numberOfRecordsNewlyAddedFromWebSite = numberOfRecordsNewlyAddedFromWebSite;
    }

    public int getNumberOfRecordsReActivated() {
        return numberOfRecordsReActivated;
    }

    public void setNumberOfRecordsReActivated(int numberOfRecordsReActivated) {
        this.numberOfRecordsReActivated = numberOfRecordsReActivated;
    }

    public int getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite() {
        return numberOfRecordsDeactivatedBecauseNoLongerOnWebSite;
    }

    public void setNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite(int numberOfRecordsDeactivatedBecauseNoLongerOnWebSite) {
        this.numberOfRecordsDeactivatedBecauseNoLongerOnWebSite = numberOfRecordsDeactivatedBecauseNoLongerOnWebSite;
    }

    public int getNumberOfRecrodsNotUpdatedForHistoricalPurposes() {
        return numberOfRecrodsNotUpdatedForHistoricalPurposes;
    }

    public void setNumberOfRecrodsNotUpdatedForHistoricalPurposes(int numberOfRecrodsNotUpdatedForHistoricalPurposes) {
        this.numberOfRecrodsNotUpdatedForHistoricalPurposes = numberOfRecrodsNotUpdatedForHistoricalPurposes;
    }

    public int getNumberOfRecordsRetrievedFromWebSite() {
        return numberOfRecordsRetrievedFromWebSite;
    }

    public void setNumberOfRecordsRetrievedFromWebSite(int numberOfRecordsRetrievedFromWebSite) {
        this.numberOfRecordsRetrievedFromWebSite = numberOfRecordsRetrievedFromWebSite;
    }

    public int getNumberOfRecordsInKfsDatabase() {
        return numberOfRecordsInKfsDatabase;
    }

    public void setNumberOfRecordsInKfsDatabase(int numberOfRecordsInKfsDatabase) {
        this.numberOfRecordsInKfsDatabase = numberOfRecordsInKfsDatabase;
    }

    public int getNumberOfRecordsUpdatedBecauseAutomatic() {
        return numberOfRecordsUpdatedBecauseAutomatic;
    }

    public void setNumberOfRecordsUpdatedBecauseAutomatic(int numberOfRecordsUpdatedBecauseAutomatic) {
        this.numberOfRecordsUpdatedBecauseAutomatic = numberOfRecordsUpdatedBecauseAutomatic;
    }

    public int getNumberOfRecordsNotUpdatedBecauseManual() {
        return numberOfRecordsNotUpdatedBecauseManual;
    }

    public void setNumberOfRecordsNotUpdatedBecauseManual(int numberOfRecordsNotUpdatedBecauseManual) {
        this.numberOfRecordsNotUpdatedBecauseManual = numberOfRecordsNotUpdatedBecauseManual;
    }
}
