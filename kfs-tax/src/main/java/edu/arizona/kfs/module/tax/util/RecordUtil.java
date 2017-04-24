package edu.arizona.kfs.module.tax.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

public class RecordUtil {

    public static boolean isValidStateCode(String state) {
        if (StringUtils.isBlank(state)) {
            return false;
        }
        StateService stateService = SpringContext.getBean(StateService.class);
        State dbState = stateService.getState(KFSConstants.COUNTRY_CODE_UNITED_STATES, state);
        return !ObjectUtils.isNull(dbState);
    }

    @SuppressWarnings("unused")
    public static boolean isValidZipCode(String zipCode) {
        boolean test = false;

        if (zipCode != null && zipCode.length() == 5) {
            test = true;
        } else if (zipCode != null && zipCode.length() == 9) {
            test = true;
        }
        try {
            Integer temp = new Integer(zipCode);
        } catch (NumberFormatException err) {
            test = false;
        }
        return test;
    }

    public static String removeChar(String value, char c) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        String temp = KFSConstants.EMPTY_STRING;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != c) {
                temp += value.charAt(i);
            }
        }
        return temp;
    }

    public static boolean isValidDecimal(String value, int size) {
        boolean test = false;
        if (value != null && value.length() == size) {
            if (value.charAt(0) == '+' || value.charAt(0) == '-') {
                try {
                    @SuppressWarnings("unused")
                    Long temp = new Long(value.substring(1));
                    return true;
                } catch (NumberFormatException err) {
                    return false;
                }
            }
        }
        return test;
    }

    public static String formatKualiDecimal(KualiDecimal dec, int size) {
        String temp = dec.toString();
        int index = temp.indexOf('.');
        int length = temp.length();
        // Strip decimal
        if (index != -1) {
            temp = temp.substring(0, index) + temp.substring(index + 1, length);
        }
        // Get difference
        int delta = size - 1 - temp.length();
        return (dec.isPositive() ? "+" : "-") + getZeroes(delta) + temp;
    }

    public static boolean isValidCountryCode(String cCode) {
        boolean test = false;
        if (cCode != null && cCode.length() == 2) {
            test = true;
        }
        return test;

    }

    public static boolean isValidCharacter(Character c, List<Character> list) {
        if (c == null) {
            return false;
        }
        int index = list.indexOf(c);
        return index == -1 ? false : true;
    }

    public static boolean hasLength(String value, int length) {
        boolean test = false;
        if (value != null && value.length() == length) {
            test = true;
        }
        return test;
    }

    public static boolean hasCharacterEmpty(String value, int max) {
        boolean test = false;
        if (value == null) {
            test = true;
        } else if (value != null && value.length() <= max) {
            test = true;
        }
        return test;
    }

    public static boolean hasCharacter(String value, int max) {
        boolean test = false;
        if (value != null && value.length() > 0 && value.length() <= max) {
            test = true;
        }
        return test;
    }

    public static String rightJustifyInteger(Integer value, int size) {
        String temp = value.toString();
        int length = temp.length();
        int delta = size - length;
        return getZeroes(delta) + temp;
    }

    public static String leftJustifyString(String value, int size) {
        int length = value == null ? 0 : value.length();
        int delta = size - length;
        if (delta > 0) {
            return (value == null ? KFSConstants.EMPTY_STRING : value) + getBlanks(delta);
        } else {
            return value;
        }
    }

    public static String getZeroes(int cnt) {
        String temp = KFSConstants.EMPTY_STRING;
        for (int i = 0; i < cnt; i++) {
            temp += "0";
        }
        return temp;
    }

    public static String getBlanks(int cnt) {
        String temp = KFSConstants.EMPTY_STRING;
        for (int i = 0; i < cnt; i++) {
            temp += " ";
        }
        return temp;
    }

}
