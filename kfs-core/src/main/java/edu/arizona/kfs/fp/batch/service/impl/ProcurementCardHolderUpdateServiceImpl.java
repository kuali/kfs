package edu.arizona.kfs.fp.batch.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.defaultvalue.NextProcurementCardDefaultIdFinder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolderLoad;
import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderUpdateService;
import edu.arizona.kfs.sys.KFSPropertyConstants;

/**
 * This is the default implementation of the ProcurementCardHolderUpdateService interface.
 */
@Transactional
public class ProcurementCardHolderUpdateServiceImpl implements ProcurementCardHolderUpdateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderUpdateServiceImpl.class);
         
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private Person cardholderUser;
    
    private static final String GO_LIVE_DATE = "2011-10-01";
    private static final String UA = "UA";
    private static final String OPEN = "Open";
    private static final String DEFAULT_SYS_ID = "0";
   
    private static final String ACTIVE = "Active";
    private static final String STATUS_CODE_A = "A";
    private static final String CARD_ACTIVATION = "Card activation";
    private static final String STATUS_CODE_T = "T";
    private static final String CARD_ACTIVATION_MONITORING = "Card activation - Monitoring";
    private static final String STATUS_CODE_V = "V";
    private static final String MONITORING = "Monitoring";
    private static final String STATUS_CODE_M = "M";
    private static final String CARD_HELD = "Card held";
    private static final String STATUS_CODE_H = "H";
    private static final String CHARGEOFF_ACCOUNT = "Chargeoff Account ";
    private static final String STATUS_CODE_E = "E";
    private static final String NEW = "New";
    private static final String STATUS_CODE_N = "N";
    private static final String OVERLIMIT = "Overlimit";
    private static final String STATUS_CODE_O = "O";
    private static final String PENDING = "Pending";
    private static final String STATUS_CODE_P = "P";
    private static final String TEMPORARY_LOST_STOLEN = "Temporary lost/stolen";
    private static final String STATUS_CODE_L = "L";
    private static final String TEMPORARILY_BLOCKED_STOLEN = "Temporarily blocked stolen";
    private static final String STATUS_CODE_B = "B";
    private static final String STATUS_CODE_C = "C";
    
     private static final String CANCELED = "Canceled";
     private static final String CANCEL_CODE_E = "E";
     private static final String INSTITUTION_SPECIFIC = "Canceled - institution specific";
     private static final String CANCEL_CODE_I = "I";
     private static final String TEMPORARY = "Canceled - temporary";
     private static final String CANCEL_CODE_P = "P";
     private static final String PERMANENT = "Canceled - permanent";
     private static final String CANCEL_CODE_M = "M";
     private static final String BANK_REQUEST = "Canceled - bank request";
     private static final String CANCEL_CODE_B = "B";
     private static final String CARDHOLDER_TERMINATED = "Canceled - cardholder terminated";
     private static final String CANCEL_CODE_T = "T";
     private static final String CUSTOMER_REQUEST = "Canceled - customer request";
     private static final String CANCEL_CODE_R = "R";
     private static final String COLLECTIONS = "Canceled - collections";
     private static final String CANCEL_CODE_C = "C";
     private static final String NO_LONGER_NEEDED = "Canceled - no longer needed";
     private static final String CANCEL_CODE_N = "N";
     private static final String CARDHOLDER_LEFT_COMPANY = "Canceled - PA Requests to have card closed, cardholder left company";
     private static final String CANCEL_CODE_A = "A";
     private static final String CANCELED_WITHIN_FIVE_DAYS = "Canceled within five days of opening";
     private static final String CANCEL_CODE_O = "O";
     private static final String LOST_STOLEN = "Lost/stolen";
     private static final String CANCEL_CODE_L = "L";
     private static final String LOST_STOLEN_FRAUD = "Lost/stolen - fraud";
     private static final String CANCEL_CODE_D = "D";
     private static final String CANCEL_CODE_X = "X";


        
    /**
     * This method retrieves a collection of temporary procurement card holder records and traverses through this list, updating
     * or inserting procurement card holder records.
     * 
     * @return True if the procurement card holder records were created successfully.  If any problem occur while creating the
     * documents, a runtime exception will be thrown.
     */
    @Override
    public boolean updateProcurementCardHolderRecords() {
        
        //load procurement cardholder records from temp table
        List<ProcurementCardHolderLoad> loadedPcardHolders = new ArrayList<ProcurementCardHolderLoad>();
        loadedPcardHolders = retrievePcardHolders();
        LOG.info("Total Procurement Cardholders loaded: " + loadedPcardHolders.size());
        
        //list for saving records
        List<ProcurementCardDefault> procurementCardHolderDetails = new ArrayList<ProcurementCardDefault>();
        ProcurementCardDefault procurementCardHolderDetail;
        int insertedRecords = 0;
        int updatedRecords = 0;
        int excludedRecords = 0;
        
        for (ProcurementCardHolderLoad procurementCardHolderLoad : loadedPcardHolders) {
            //check for exclusion
            if (!excludeRecord(procurementCardHolderLoad.getCardCancelDate())) {
                //check for existing record - expire date is mmdd
                procurementCardHolderDetail = getExistingProcurementCardHolder(procurementCardHolderLoad.getCreditCardNumber());
                if (ObjectUtils.isNull(procurementCardHolderDetail)) {
                    //insert
                    procurementCardHolderDetail = new ProcurementCardDefault();
                    procurementCardHolderDetail.setCreditCardNumber(procurementCardHolderLoad.getCreditCardNumber());
                    procurementCardHolderDetail.setCreditCardLastFour(procurementCardHolderLoad.getCreditCardNumber().substring(12));
                    procurementCardHolderDetail.setChartOfAccountsCode(UA);
                    procurementCardHolderDetail.setActive(true);
                    insertedRecords = insertedRecords + 1;
                }
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Updated record " + procurementCardHolderDetail);
                    }
                    updatedRecords = updatedRecords + 1;
                }
                procurementCardHolderDetail.setCardHolderSystemId(determineSystemId(procurementCardHolderLoad.getCardHolderSystemId(), procurementCardHolderLoad.getCardStatusCode()));
                procurementCardHolderDetail.setCardHolderName(procurementCardHolderLoad.getCardHolderName());
                procurementCardHolderDetail.setCardHolderAlternateName(procurementCardHolderLoad.getCardHolderAlternateName());
                procurementCardHolderDetail.setCardHolderLine1Address(procurementCardHolderLoad.getCardHolderLine1Address());
                procurementCardHolderDetail.setCardHolderLine2Address(procurementCardHolderLoad.getCardHolderLine2Address());
                procurementCardHolderDetail.setCardHolderCityName(procurementCardHolderLoad.getCardHolderCityName());
                procurementCardHolderDetail.setCardHolderStateCode(procurementCardHolderLoad.getCardHolderStateCode());
                procurementCardHolderDetail.setCardHolderZipCode(procurementCardHolderLoad.getCardHolderZipCode());
                procurementCardHolderDetail.setCardHolderWorkPhoneNumber(procurementCardHolderLoad.getCardHolderWorkPhoneNumber());
                procurementCardHolderDetail.setCardLimit(procurementCardHolderLoad.getCardLimit());
                procurementCardHolderDetail.setCardCycleAmountLimit(procurementCardHolderLoad.getCardCycleAmountLimit());
                procurementCardHolderDetail.setCardCycleVolLimit(procurementCardHolderLoad.getCardCycleVolumeLimit());
                procurementCardHolderDetail.setCardMonthlyNumber(procurementCardHolderLoad.getCardMonthlyNumber());
                procurementCardHolderDetail.setCardStatusCode(determineStatusCode(procurementCardHolderLoad.getCardStatusCode()));
                procurementCardHolderDetail.setCardCancelCode(determineCancelCode(procurementCardHolderLoad.getCardStatusCode()));
                procurementCardHolderDetail.setCardNoteText(procurementCardHolderLoad.getCardNoteText());
                procurementCardHolderDetail.setCardOpenDate(procurementCardHolderLoad.getCardOpenDate());
                procurementCardHolderDetail.setCardCancelDate(procurementCardHolderLoad.getCardCancelDate());
                if (ObjectUtils.isNotNull(procurementCardHolderLoad.getCardExpireDate())) {
                    procurementCardHolderDetail.setCardExpireDate(determineExpireDate(procurementCardHolderLoad.getCardExpireDate()));
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding ProcurementCardDefault to list to save " + procurementCardHolderDetail);
                }
                procurementCardHolderDetails.add(procurementCardHolderDetail);
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Excluded procurementCardHolderLoad " + procurementCardHolderLoad);
                }
                excludedRecords = excludedRecords + 1;
            }
        }
        
        try {
            for(ProcurementCardDefault procurementCardDefault : procurementCardHolderDetails) {
                if(ObjectUtils.isNull(procurementCardDefault.getId())) {
                    procurementCardDefault.setId(Long.valueOf(new NextProcurementCardDefaultIdFinder().getValue()));
                }
            }
            
            businessObjectService.save(procurementCardHolderDetails);
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Total Procurement Cardholders inserted: " + insertedRecords);
                LOG.info("Total Procurement Cardholders updated: " + updatedRecords);
                LOG.info("Total Procurement Cardholders excluded: " + excludedRecords);
            }
        }
        catch (Exception e) {
            LOG.error("Error persisting " + " " + e.getMessage(), e);
            throw new RuntimeException("Error persisting " + " " + e.getMessage(),e);
        }
       
        return true;
    }
   
    /**
     * This method retrieves a list of procurement card holder records from a temporary table.
     * 
     * @return List containing procurement card holder records.
     */
    protected List<ProcurementCardHolderLoad> retrievePcardHolders() {
        
        // retrieve Procurement Card holder records from Procurement Card holder table order by card number
        List<ProcurementCardHolderLoad> loadedPcardHolders = (List<ProcurementCardHolderLoad>) businessObjectService.findMatchingOrderBy(ProcurementCardHolderLoad.class, new HashMap<String, String>(), KFSPropertyConstants.CREDIT_CARD_NUMBER, true);
        return loadedPcardHolders;
    }
   
    /**
     * Gets the matching record from the custom Procurement Cardholder table.
     */
    private ProcurementCardDefault getExistingProcurementCardHolder(String creditCardNumber) {
                
        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, creditCardNumber);
        ProcurementCardDefault procurementCardHolderDetail = (ProcurementCardDefault) businessObjectService.findByPrimaryKey(ProcurementCardDefault.class, pkMap);
        
        return procurementCardHolderDetail;
    }
    
    /**
     * Check to see if this record is excluded based on the cancel date
     */
    private boolean excludeRecord (Date cancelDate) {
        //exclude records with a cancel date more than 120 days before the go live date 
        if (ObjectUtils.isNotNull(cancelDate)) {
            //create go live date
            Date goLiveDate = Date.valueOf(GO_LIVE_DATE);
            Timestamp goLiveDateTS = new Timestamp(goLiveDate.getTime());
            Timestamp cancelDateTS = new Timestamp(cancelDate.getTime());
            //check difference in days
            if (goLiveDateTS.after(cancelDateTS) && dateTimeService.dateDiff(cancelDateTS, goLiveDateTS, false) > 150) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    /**
     * Determines the status code from the status text
     */
    private String determineStatusCode (String statusText) {
        //determine valid status code from status text
        String statusCode;
        
        if (statusText.equals(ACTIVE)) {
            statusCode = STATUS_CODE_A;
        } else if (statusText.equals(CARD_ACTIVATION)) {
            statusCode = STATUS_CODE_T;
        } else if (statusText.equals(CARD_ACTIVATION_MONITORING)) {
            statusCode = STATUS_CODE_V;
        } else if (statusText.equals(MONITORING)) {
            statusCode = STATUS_CODE_M;
        } else if (statusText.equals(CARD_HELD)) {
            statusCode = STATUS_CODE_H;
        } else if (statusText.equals(CHARGEOFF_ACCOUNT)) {
            statusCode = STATUS_CODE_E;
        } else if (statusText.equals(NEW)) {
            statusCode = STATUS_CODE_N;
        } else if (statusText.equals(OVERLIMIT)) {
            statusCode = STATUS_CODE_O;
        } else if (statusText.equals(PENDING)) {
            statusCode = STATUS_CODE_P;
        } else if (statusText.equals(TEMPORARY_LOST_STOLEN)) {
            statusCode = STATUS_CODE_L;
        } else if (statusText.equals(TEMPORARILY_BLOCKED_STOLEN)) {
            statusCode = STATUS_CODE_B;
        } else {
            statusCode = STATUS_CODE_C;
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("statusCode " + statusCode);
        }
        return statusCode;
    }
    
    /**
     * Determines the cancel code from the cancel text
     */
    private String determineCancelCode (String statusText) {
        //determine valid cancel code from status text
        String cancelCode;
        
        if (statusText.equals(CANCELED)) {
            cancelCode = CANCEL_CODE_E;
        } else if (statusText.equals(INSTITUTION_SPECIFIC)) {
            cancelCode = CANCEL_CODE_I;
        } else if (statusText.equals(TEMPORARY)) {
            cancelCode = CANCEL_CODE_P;
        } else if (statusText.equals(PERMANENT)) {
            cancelCode = CANCEL_CODE_M;
        } else if (statusText.equals(BANK_REQUEST)) {
            cancelCode = CANCEL_CODE_B;
        } else if (statusText.equals(CARDHOLDER_TERMINATED)) {
            cancelCode = CANCEL_CODE_T;
        } else if (statusText.equals(CUSTOMER_REQUEST)) {
            cancelCode = CANCEL_CODE_R;
        } else if (statusText.equals(COLLECTIONS)) {
            cancelCode = CANCEL_CODE_C;
        } else if (statusText.equals(NO_LONGER_NEEDED)) {
            cancelCode = CANCEL_CODE_N;
        } else if (statusText.equals(CARDHOLDER_LEFT_COMPANY)) {
            cancelCode = CANCEL_CODE_A;
        } else if (statusText.equals(CANCELED_WITHIN_FIVE_DAYS)) {
            cancelCode = CANCEL_CODE_O;
        } else if (statusText.equals(LOST_STOLEN)) {
            cancelCode = CANCEL_CODE_L;
        } else if (statusText.equals(LOST_STOLEN_FRAUD)) {
            cancelCode = CANCEL_CODE_D;
        } else {
            cancelCode = CANCEL_CODE_X;
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelCode " + cancelCode);
        }
        return cancelCode;
    }
    
    /**
     * Determines the system ID from the employee ID and status test
     */
    private String determineSystemId (String employeeId, String statusText) {
        //determine valid system id (net id) from employee id and status text
        String systemId;
        if (statusText.equals(ACTIVE) || statusText.equals(CARD_ACTIVATION) || statusText.equals(MONITORING) || statusText.equals(OPEN)) {
            systemId = DEFAULT_SYS_ID;
        }
        else {
            systemId = null;
        }
        
        if (ObjectUtils.isNotNull(employeeId)) {
            cardholderUser = getCardholderUser(employeeId.substring(1));
            if (ObjectUtils.isNotNull(cardholderUser)) {
                //employee found
                systemId = cardholderUser.getPrincipalId();
            }
        }
        return systemId;
        
    }
      
    /**
     * Determines the expiration date
     */
    private Date determineExpireDate (String expireDate) {
        //determine valid expiration date from string of mmdd
        int expireMonth = Integer.parseInt(expireDate.substring(0, 2));
        int expireDay = 31;
        int expireYear = Integer.parseInt(expireDate.substring(2));
        expireYear = expireYear + 2000;
        if (expireMonth == 2) {
            if (expireYear % 4 == 0)
                expireDay = 29;
            else
                expireDay = 28;
        }
        if (expireMonth == 4 || expireMonth == 6 || expireMonth == 9 || expireMonth == 11) {
            expireDay = 30;
        }
        
        Date expDate = Date.valueOf(String.valueOf(expireYear) + "-" + expireDate.substring(0, 2) + "-" + String.valueOf(expireDay));
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("expDate " + expDate);
        }
        return expDate;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }  
    
    public Person getCardholderUser(String employeeId) {
        cardholderUser = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(employeeId);
        return cardholderUser;
    }
}
