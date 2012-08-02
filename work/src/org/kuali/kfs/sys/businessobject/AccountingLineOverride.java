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
package org.kuali.kfs.sys.businessobject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.AccountPresenceService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class helps implement AccountingLine overrides. It is not persisted itself, but it simplifies working with the persisted
 * codes. Instances break the code into components. Static methods help with the AccountingLine.
 */
public class AccountingLineOverride {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineOverride.class);

    /**
     * These codes are the way the override is persisted in the AccountingLine.
     */
    public static final class CODE { // todo: use JDK 1.5 enum
        public static final String NONE = "NONE";
        public static final String EXPIRED_ACCOUNT = "EXPIRED_ACCOUNT";
        public static final String NON_BUDGETED_OBJECT = "NON_BUDGETED_OBJECT";
        public static final String TRANSACTION_EXCEEDS_REMAINING_BUDGET = "TRANSACTION_EXCEEDS_REMAINING_BUDGET";
        public static final String EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT = "EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT";
        public static final String NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET = "NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET";
        public static final String EXPIRED_ACCOUNT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET = "EXPIRED_ACCOUNT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET";
        public static final String EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET = "EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET";
        public static final String NON_FRINGE_ACCOUNT_USED = "NON_FRINGE_ACCOUNT_USED";
        public static final String EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED = "EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED";
    }

    /**
     * These are the somewhat independent components of an override.
     */
    public static final class COMPONENT { // todo: use JDK 1.5 enum
        public static final Integer EXPIRED_ACCOUNT = new Integer(1);
        public static final Integer NON_BUDGETED_OBJECT = new Integer(2);
        public static final Integer TRANSACTION_EXCEEDS_REMAINING_BUDGET = new Integer(3);
        public static final Integer NON_FRINGE_ACCOUNT_USED = new Integer(8);
    }

    /**
     * The names of the AccountingLine properties that the processForOutput() and determineNeededOverrides() methods use. Callers of
     * those methods may need to refresh these fields from OJB.
     */
    public static final List<String> REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "account", "objectCode" }));

    /**
     * This holds an instance of every valid override, mapped by code.
     */
    private static final Map<String, AccountingLineOverride> codeToOverrideMap = new HashMap<String, AccountingLineOverride>();

    /**
     * This holds an instance of every valid override, mapped by components.
     */
    private static final Map componentsToOverrideMap = new HashMap();

    static {
        // populate the code map
        new AccountingLineOverride(CODE.NONE, new Integer[] {});
        new AccountingLineOverride(CODE.EXPIRED_ACCOUNT,
        // todo: use JDK 1.5 ... args
            new Integer[] { COMPONENT.EXPIRED_ACCOUNT });
        new AccountingLineOverride(CODE.NON_BUDGETED_OBJECT, new Integer[] { COMPONENT.NON_BUDGETED_OBJECT });
        new AccountingLineOverride(CODE.TRANSACTION_EXCEEDS_REMAINING_BUDGET, new Integer[] { COMPONENT.TRANSACTION_EXCEEDS_REMAINING_BUDGET });
        new AccountingLineOverride(CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT, new Integer[] { COMPONENT.EXPIRED_ACCOUNT, COMPONENT.NON_BUDGETED_OBJECT });
        new AccountingLineOverride(CODE.NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, new Integer[] { COMPONENT.NON_BUDGETED_OBJECT, COMPONENT.TRANSACTION_EXCEEDS_REMAINING_BUDGET });
        new AccountingLineOverride(CODE.EXPIRED_ACCOUNT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, new Integer[] { COMPONENT.EXPIRED_ACCOUNT, COMPONENT.TRANSACTION_EXCEEDS_REMAINING_BUDGET });
        new AccountingLineOverride(CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, new Integer[] { COMPONENT.EXPIRED_ACCOUNT, COMPONENT.NON_BUDGETED_OBJECT, COMPONENT.TRANSACTION_EXCEEDS_REMAINING_BUDGET });
        new AccountingLineOverride(CODE.NON_FRINGE_ACCOUNT_USED, new Integer[] { COMPONENT.NON_FRINGE_ACCOUNT_USED });
        new AccountingLineOverride(CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, new Integer[] { COMPONENT.EXPIRED_ACCOUNT, COMPONENT.NON_FRINGE_ACCOUNT_USED });
    }

    private final String code;
    private final Set components;

    /**
     * This private constructor is for the static initializer.
     *
     * @param myCode
     * @param myComponents
     */
    private AccountingLineOverride(String myCode, Integer[] myComponents) {
        code = myCode;
        components = componentsAsSet(myComponents);
        codeToOverrideMap.put(code, this);
        componentsToOverrideMap.put(components, this);
    }

    /**
     * Checks whether this override contains the given component.
     *
     * @param component
     * @return whether this override contains the given component.
     */
    public boolean hasComponent(Integer component) {
        return components.contains(component);
    }

    /**
     * Gets the code of this override.
     *
     * @return the code of this override.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the components of this override.
     *
     * @return the components of this override.
     */
    private Set getComponents() {
        return components;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AccountingLineOverride (code " + code + ", components " + components + ")";
    }

    /**
     * Returns the AccountingLineOverride that has the components of this AccountingLineOverride minus any components not in the
     * given mask. This is like <code>&amp;</code>(a bit-wise and), if the components were bits.
     *
     * @param mask
     * @return the AccountingLineOverride that has the components of this AccountingLineOverride minus any components not in the
     *         given mask.
     * @throws IllegalArgumentException if there is no such valid combination of components
     */
    public AccountingLineOverride mask(AccountingLineOverride mask) {
        Set key = maskComponents(mask);
        if (!isValidComponentSet(key)) {
            throw new IllegalArgumentException("invalid component set " + key);
        }
        return valueOf(key);
    }

    /**
     * Returns the Set of components that this override and the given override have in common.
     *
     * @param mask
     * @return the Set of components that this override and the given override have in common.
     */
    private Set maskComponents(AccountingLineOverride mask) {
        Set retval = new HashSet(components);
        retval.retainAll(mask.getComponents());
        return retval;
    }

    /**
     * Returns whether this override, when masked by the given override, is valid. Some combinations of components have no override
     * code defined.
     *
     * @param mask
     * @return whether this override, when masked by the given override, is valid.
     */
    public boolean isValidMask(AccountingLineOverride mask) {
        return isValidComponentSet(maskComponents(mask));
    }

    /**
     * Returns whether the given String is a valid override code.
     *
     * @param code
     * @return whether the given String is a valid override code.
     */
    public static boolean isValidCode(String code) {
        return codeToOverrideMap.containsKey(code);
    }

    /**
     * Returns whether the given Integers are a valid set of components. Some combinations of components are invalid and have no
     * code defined.
     *
     * @param components
     * @return whether the given Integers are a valid set of components.
     */
    public static boolean isValidComponentSet(Integer[] components) {
        return isValidComponentSet(componentsAsSet(components));
    }

    private static boolean isValidComponentSet(Set components) { // todo: JDK 1.5 generic Set
        return componentsToOverrideMap.containsKey(components);
    }

    /**
     * Factory method from code.
     *
     * @param code the override code
     * @return the AccountingLineOverride instance corresponding to the given code.
     * @throws IllegalArgumentException if the given code is not valid
     */
    public static AccountingLineOverride valueOf(String code) {
        if (!isValidCode(code)) {
            throw new IllegalArgumentException("invalid code " + code);
        }
        return codeToOverrideMap.get(code); // todo: JDK 1.5 generic Map instead of cast
    }

    /**
     * Factory method from components.
     *
     * @param components the override components, treated as a set
     * @return the AccountingLineOverride instance corresponding to the given component set.
     * @throws IllegalArgumentException if the given set of components is not valid
     */
    public static AccountingLineOverride valueOf(Integer[] components) {
        Set key = componentsAsSet(components);
        if (!isValidComponentSet(key)) {
            throw new IllegalArgumentException("invalid component set " + key);
        }
        return valueOf(key);
    }

    public static AccountingLineOverride valueOf(Set components) {
        return (AccountingLineOverride) componentsToOverrideMap.get(components); // todo: JDK 1.5 generic Map instead of cast
    }

    private static Set componentsAsSet(Integer[] components) {
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(components)));
    }

    /**
     * On the given AccountingLine, converts override input checkboxes from a Struts Form into a persistable override code.
     *
     * @param line
     */
    public static void populateFromInput(AccountingLine line) {
        // todo: this logic won't work if a single account checkbox might also stands for NON_FRINGE_ACCOUNT_USED; needs thought

        Set overrideInputComponents = new HashSet();
        if (line.getAccountExpiredOverride()) {
            overrideInputComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        if (line.isObjectBudgetOverride()) {
            overrideInputComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }
        if (!isValidComponentSet(overrideInputComponents)) {
            // todo: error for invalid override checkbox combinations, for which there is no override code
        }
        line.setOverrideCode(valueOf(overrideInputComponents).getCode());
    }

    /**
     * Prepares the given AccountingLine in a Struts Action for display by a JSP. This means converting the override code to
     * checkboxes for display and input, as well as analysing the accounting line and determining which override checkboxes are
     * needed.
     *
     * @param line
     */
    public static void processForOutput(AccountingDocument document ,AccountingLine line) {
        AccountingLineOverride fromCurrentCode = valueOf(line.getOverrideCode());
        AccountingLineOverride needed = determineNeededOverrides(document,line);
        line.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setObjectBudgetOverride(fromCurrentCode.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
        line.setObjectBudgetOverrideNeeded(needed.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
    }

    /**
     * Determines what overrides the given line needs.
     *
     * @param line
     * @return what overrides the given line needs.
     */
    public static AccountingLineOverride determineNeededOverrides(AccountingDocument document ,AccountingLine line) {
        boolean isDocumentFinalOrProcessed = false;
       if(ObjectUtils.isNotNull(document)) {
           AccountingDocument accountingDocument = document;
           isDocumentFinalOrProcessed = accountingDocument.isDocumentFinalOrProcessed();
       }

        Set neededOverrideComponents = new HashSet();
        if (needsExpiredAccountOverride(line, isDocumentFinalOrProcessed)) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        if (needsObjectBudgetOverride(line.getAccount(), line.getObjectCode())) {
            neededOverrideComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }

        if (!isValidComponentSet(neededOverrideComponents)) {
            // todo: error for invalid override checkbox combinations, for which there is no override code
        }
        return valueOf(neededOverrideComponents);
    }

    /**
     * Returns whether the given account needs an expired account override.
     *
     * @param account
     * @return whether the given account needs an expired account override.
     */
    public static boolean needsExpiredAccountOverride(Account account) {
        return !ObjectUtils.isNull(account) && account.isActive() && account.isExpired();
    }

    /**
     * Returns whether the given account needs an expired account override.
     *
     * @param account
     * @return whether the given account needs an expired account override.
     */
    public static boolean needsExpiredAccountOverride(AccountingLine line, boolean isDocumentFinalOrProcessed ) {
        if(isDocumentFinalOrProcessed && CODE.EXPIRED_ACCOUNT.equals(line.getOverrideCode()) ) {
            return true;
        }
        else {
            return !ObjectUtils.isNull(line.getAccount()) && line.getAccount().isActive() && line.getAccount().isExpired();
        }
    }

    /**
     * Returns whether the given account needs an expired account override.
     *
     * @param account
     * @return whether the given account needs an expired account override.
     */
    public static boolean needsNonFringAccountOverride(Account account) {
        return !ObjectUtils.isNull(account) && account.isActive() && !account.isAccountsFringesBnftIndicator();
    }

    /**
     * Returns whether the given object code needs an object budget override
     *
     * @param account
     * @return whether the given object code needs an object budget override
     */
    public static boolean needsObjectBudgetOverride(Account account, ObjectCode objectCode) {
        return !ObjectUtils.isNull(account) && !ObjectUtils.isNull(objectCode) && account.isActive() && !SpringContext.getBean(AccountPresenceService.class).isObjectCodeBudgetedForAccountPresence(account, objectCode);
    }

    public static Document getDocument(AccountingLine line) {
        Document document = null;
        try {
            document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(line.getDocumentNumber());

       }catch(WorkflowException exception) {
           LOG.error("Unable to locate document for documentId :: " + line.getDocumentNumber() );
       }

       return document;
    }
}
