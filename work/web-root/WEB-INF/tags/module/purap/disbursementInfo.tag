<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="sourceDocumentNumber" required="true"
              description="Document number to get disbursement info for" %>
<%@ attribute name="sourceDocumentType" required="true"
              description="Document type to get disbursement info for" %>   
              
<c:url var="page" value="${KualiForm.disbursementInfoUrl}">
  <c:param name="custPaymentDocNbr" value="${sourceDocumentNumber}"/>
  <c:param name="financialDocumentTypeCode" value="${sourceDocumentType}"/>
</c:url>
<c:url var="image" value="${ConfigProperties.externalizable.images.url}tinybutton-disbursinfo.gif"/>
							  
&nbsp;<a href="${page}" target="_pdp"><img src="${image}" border="0"/></a>              
