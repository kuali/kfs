package edu.arizona.kfs.pdp.util;

import edu.arizona.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSConstants;

import edu.arizona.kfs.pdp.PdpConstants;

public class PayeeACHAcctFlatFileConverter {
	
	public static PayeeACHAccount convert(String fileLine) {
		PayeeACHAccount payeeAcct = new PayeeACHAccount();
		String active = KFSConstants.EMPTY_STRING;
		
		payeeAcct.setBankRoutingNumber(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_BANK_ROUTING_NUMBER, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_BANK_ROUTING_NUMBER));
		payeeAcct.setBankAccountNumber(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_BANK_ACCOUNT_NUMBER, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_BANK_ACCOUNT_NUMBER));
		payeeAcct.setPayeeName(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_PAYEE_NAME, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_PAYEE_NAME));
		payeeAcct.setPayeeEmailAddress(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_PAYEE_EMAIL_ADDRESS, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_PAYEE_EMAIL_ADDRESS));
		payeeAcct.setPayeeIdentifierTypeCode(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_PAYEE_IDENTIFIER_TYPE_CODE, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_PAYEE_IDENTIFIER_TYPE_CODE));
		payeeAcct.setAchTransactionType(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_ACH_TRANSACTION_TYPE, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_ACH_TRANSACTION_TYPE));
		active = getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_ACTIVE_INDICATOR, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_ACTIVE_INDICATOR);
		
		if (active == null || active.equals(KFSConstants.EMPTY_STRING) ||active .equals(KFSConstants.ACTIVE_INDICATOR)) {
			payeeAcct.setActive(true);
		}
		else {
			payeeAcct.setActive(false);
		}
		
		payeeAcct.setBankAccountTypeCode(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_BANK_ACCOUNT_TYPE_CODE, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_BANK_ACCOUNT_TYPE_CODE));
		payeeAcct.setPayeeIdNumber(getField(fileLine, PdpConstants.ACHFilePropertyPositions.START_OF_PAYEE_ID_NUMBER, PdpConstants.ACHFilePropertyPositions.LENGTH_OF_PAYEE_ID_NUMBER));
		
		return payeeAcct;
	}
	
	private static String getField(String data, int startChar, int length) {
		String field = data.substring(startChar -1, startChar + length - 1).trim();
		return field;
	}
}
