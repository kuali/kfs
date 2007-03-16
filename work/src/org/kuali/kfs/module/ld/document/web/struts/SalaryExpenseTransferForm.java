/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.form;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

/**
 * This class is the form class for the Salary Expense Transfer document. This method extends the parent
 * KualiTransactionalDocumentFormBase class which contains all of the common form methods and form attributes needed by the
 * Salary Expense Transfer document. It adds a new method which is a convenience method for getting at the Salary Expense Transfer document easier.
 * 
 * 
 */
public class SalaryExpenseTransferForm extends LaborDocumentFormBase {
    private UniversalUser user;
    private String userId;
    private String emplid;

    /**
     * Constructs a SalaryExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public SalaryExpenseTransferForm() {
        super();
        setDocument(new SalaryExpenseTransferDocument());
    }

    /**
     * 
     * This method returns a refernce to the Salary Expense Transfer Document
     * @return
     */
    
    public SalaryExpenseTransferDocument getSalaryExpenseTransferDocument() {
        return (SalaryExpenseTransferDocument) getDocument();
    }
    
    //  todo: Make use of this on the salaryExpenserTransfer.jsp    
    /**
     * 
     * This method sets the User ID retrieved from the universal user service
     * @param emplid
     * @throws UserNotFoundException
     */
    public void setUserId(String uid) throws UserNotFoundException {
        if (uid != null) {
            //  This may happen during populate when there is no initial user
            user = SpringServiceLocator.getUniversalUserService().getUniversalUser(uid);
        }
    }
        
//  todo: Make use of this on the salaryExpenserTransfer.jsp
    public String getUserId() {
        String retval = null;
        if (user != null) {
            retval = user.getPersonUniversalIdentifier();
        }
        return retval;
    }

    //  todo: Make use of this on the salaryExpenserTransfer.jsp
    /**
     * 
     * This method sets the Person Name retrieved from the universal user service
     * @param emplid
     * @throws UserNotFoundException
     */
    public void setPersonName(String personName) throws UserNotFoundException {
        if (personName != null) {
            //  This may happen during populate when there is no initial user
            user = SpringServiceLocator.getUniversalUserService().getUniversalUser(personName);
        }
    }
    
    //  todo: Make use of this on the salaryExpenserTransfer.jsp
    /**
     * 
     * This method returns the Person Name from the UniversalUser table.
     * @return
     */
    public String getPersonName() {
        String retval = null;
        if (user != null) {
            retval = user.getPersonName();
        }
        return retval;
    }  
    
   /**
    * 
    * This method sets the employee ID retrieved from the universal user service
    * @param emplid
    * @throws UserNotFoundException
    */
    public void setEmplid(String emplid) throws UserNotFoundException {
        if (emplid != null) {
            //  This may happen during populate when there is no initial user
            user = SpringServiceLocator.getUniversalUserService().getUniversalUser(emplid);
        }
    }

/**
 * 
 * This method returns the employee ID from the UniversalUser table.
 * @return
 */
    public String getEmplid() {
        String retval = null;
        if (user != null) {
            retval = user.getPersonPayrollIdentifier();
        }
        return retval;
    }   
}