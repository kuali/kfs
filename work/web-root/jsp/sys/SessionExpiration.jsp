<%@ page import="org.kuali.rice.krad.util.KRADConstants"%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>


<kul:page showDocumentInfo="false"
	headerTitle="KFS - Session Timeout" docTitle="KFS - Session Timeout" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="SessionInvalidateAction" errorKey="foo">

    <div style="margin-top: 10px; text-align: center; font-size: 1.2em;">
	    <strong>Your session has timed out.</strong><br/>
	    <a href="${Constants.PORTAL_ACTION}">Return to Main Page</a>
	</div>
</kul:page> 