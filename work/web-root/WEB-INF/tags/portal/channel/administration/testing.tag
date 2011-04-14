<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<channel:portalChannelTop channelTitle="Testing" />
<div class="body">
    <c:if test="${!KualiConfigurationService.isProductionEnvironment && ConfigProperties.module.purchasing.enabled == 'true'}">
        <!-- THIS FUNCTIONALITY SHOULD ONLY BE AVAILABLE IN TEST ENVIRONMENTS -->
		<strong>Electronic Invoice</strong><br/>
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Generate/Upload eInvoice Files" url="purapElectronicInvoiceTest.do" /></li>				
		</ul>
    </c:if>
</div>
<channel:portalChannelBottom />
                
