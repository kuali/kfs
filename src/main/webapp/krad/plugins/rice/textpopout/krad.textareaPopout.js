/*
 * Copyright 2005-2014 The Kuali Foundation
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
(function($) {
	$.fn.initPopoutText = function(options, imageUrl){
		return this.each(function(){
			
			options = options || {};
			//default setting
			options = $.extend({
				label: "",
				summary: "",
				constraint: ""
			}, options);
			
			var id= $(this).attr("id");
			var obj = $(this);
			var context;
			if (top == self) {
				context = jq;
			}
			else{
				context = parent.$;
			}
			
			var labelHtml="";
			var summaryHtml="";
			var constraintHtml="";
			if(options.label){
				labelHtml = "<label for='textarea_popout_control'>"+options.label+"</label>";
			}
			if(options.summary){
				summaryHtml="<span class='summary'>" + options.summary + "</span>";
			}
			if(options.constraint){
				constraintHtml="<span class='constraint'>" + options.constraint + "</span>";
			}
			
			$(this).after('<a id="expand_btn_' + id + '" title="Expand"><img src="' + imageUrl + 'pencil_add.png" alt="Expand"/></a>');
			$(document).ready(function()
			{
				$("a#expand_btn_" + id).click(function(e){
					e.preventDefault();
					
					var value = obj.val();
					if(!value){
						value = "";
					}
					var options = {
						content: "<div class='textarea_popout'>"+labelHtml+summaryHtml
							+"<textarea id='textarea_popout_control'>"+value+"</textarea>"
							+constraintHtml
							+"<input id='done_btn' class='done' type='button' value='Done'/></div>",
						onComplete: function(){
							context("textarea#textarea_popout_control").focus();
			    			context("#done_btn").click(function(e){
			    				e.preventDefault();
			    				obj.val(context("textarea#textarea_popout_control").val());
			    				context.fancybox.close();
			    				obj.valid();
			    				obj.focus();
			    			});
						},
						autoDimensions: true,
						hideOnOverlayClick: false,
						centerOnScroll: true
					};

	        		context.fancybox(options);

				});
			});
		});
	};
})(jQuery);