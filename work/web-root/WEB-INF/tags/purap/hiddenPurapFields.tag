<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%-- PURCHASING ACCOUNTS PAYABLE DOCUMENT FIELDS --%>
<html:hidden property="document.identifier" />
<html:hidden property="document.statusCode" />
<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
<html:hidden property="document.vendorDetailAssignedIdentifier" />

<%-- PURCHASING DOCUMENT FIELDS --%>
<html:hidden property="document.requisitionSourceCode" />
<html:hidden property="document.billingPhoneNumber" />
