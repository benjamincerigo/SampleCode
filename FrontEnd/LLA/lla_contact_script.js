jQuery(document).ready(function(){
	
		
	    // Get the form.
	    var form = jQuery("#ajax_contact");
	    //Find and hide loading
	    var loading = jQuery("#contact_loading");
	    jQuery(loading).hide();

	    // Get the messages div.
	    var formMessages = jQuery("#ajax_response");
	    
	    // TODO: The rest of the code will go here...
	    function reloadCaptcha()
    		{
        	jQuery('#siimage').prop('src', LLA.template + '/securimage/securimage_show.php?sid=' + Math.random());
        	jQuery('.lla_contact_captcha').val('');

    	}

	    jQuery("#ajax_contact").submit(function(event) {
	    // Stop the browser from submitting the form.
	    event.preventDefault();
	    
	    //Show that it is loading
	    jQuery(loading).show();

	    // Serialize the form data.
		var formData = jQuery("#ajax_contact").serialize();
		
		//Make Query
		jQuery.ajax({
	    type: 'POST',
	    url: LLA.ajaxurl,
	    data: formData,
	    dataType: 'json'
		})

		

	    // TODO
	    .fail(function(response) {
		    // Make sure that the formMessages div has the 'error' class.
		    jQuery(formMessages).removeClass('success');
		    jQuery(formMessages).addClass('error');

		    //Process response
		    var json_response = process_error(response);

		    // reset Captcha
		    reloadCaptcha();
		    // Set the message text.
		    if (json_response.message !== '') {

		        jQuery(formMessages).text(json_response.message);
		    } else {
		    	//Error in Java
		        jQuery(formMessages).text('Sorry, An error occured and your message could not be sent. Please try again.');
		    }
		    //Not loading
		    jQuery(loading).hide();
		})
	    .done(function(response) {
		    // Make sure that the formMessages div has the 'success' class.
		   	jQuery(formMessages).removeClass('error');
		    jQuery(formMessages).addClass('success');

		    //reset
		    jQuery('#ajax_contact')[0].reset();
		    reloadCaptcha();
		    // process the the message text.
		    var json_response = process_error(response);

		    //Out put response from server
		    jQuery(formMessages).text(json_response.message);
		    //Hide loading
		  	jQuery(loading).hide();
		})
	});

	function process_error(response){
		//Function for processing error - If has etra stings on the returned text
			//Make return variable
			var json_response = "";

			//If the respose has a porpoty responseText (fail)
			if(response.hasOwnProperty('responseText')){
				res_process = response.responseText;
			}
			else
			{
				res_process = response;
			}
			

		//If string then make into a json object
		if(typeof res_process == 'string'){
			try {
					//make into Jason
			   		json_response =  JSON.parse(res_process);
			   		
			   	}
			   	catch (err) 
			   	{	
			   		//If there is error strin gat the begging split it
			   		var split = res_process.split(/[\{\}]/);

			   		var to_parse = "{" + split[1] + "}";
			   		//make into json
			   		json_response = JSON.parse(to_parse);
			   		
			   	}
		   }
		   else {
		   	//Response is already corret
		   	json_response = res_process;
		   }
	   	return json_response;
	}

	});

	// Set up an event listener for the contact form.


	
