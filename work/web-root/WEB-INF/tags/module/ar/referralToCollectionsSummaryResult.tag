<%--
 Copyright 2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="invoiceAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="propertyName" required="true" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="tabTitle" required="true"%>
<%@ attribute name="useTabTop" required="true"%>
<c:set var="tabErrorKey" value="${propertyName}.*" />

<div id="workarea">
	<c:choose>
		<c:when test="${useTabTop}">
			<kul:tabTop tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}">
				<ar:referralToCollectionsSummaryResultContent invoiceAttributes="${invoiceAttributes}" propertyName="${propertyName}" />
			</kul:tabTop>
		</c:when>
		<c:otherwise>
			<kul:tab tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}">
				<ar:referralToCollectionsSummaryResultContent invoiceAttributes="${invoiceAttributes}" propertyName="${propertyName}" />
			</kul:tab>
		</c:otherwise>
	</c:choose>
</div>
