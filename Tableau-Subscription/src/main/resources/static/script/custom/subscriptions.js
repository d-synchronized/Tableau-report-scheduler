//DELETE SUBSCRIPTION BY NAME
function deleteSubscriptionById(id, target){
	//DELETE CALL
	bootbox.confirm("<h2>Want to delete?</h2>", function(result) {
		if(result){
			$.ajax({
				url: 'dashboard/deleteDashboard?name='+id,
				type: 'DELETE',
				success: function(result) {
					$(target).closest('.panel').remove();
					bootbox.alert("<h2>Action was successful!</h2>", function(result) {});
				}
			});
		}
	});

}

//ACTIVATE / DEACTIVATE SUBSCRIPTION
function deactivateSubscription(id, isActivate){

	myUrlParams = {};

	$.each(dashboardParams[id], function(key, value) {
		myUrlParams[key] = value;
	});
	myUrlParams['activate'] = isActivate;

	$.ajax({
		type: 'post',
		url: 'dashboard/deactivateDashboard?name='+id,
		data: JSON.stringify(myUrlParams),
		contentType: "application/json; charset=utf-8",
		traditional: true,
		success: function (data) {

			bootbox.alert("<h2>Action was successful!</h2>", function(result) {});
			showSubscriptionsScreen();			

		}
	});


}


//POPULATE NEW SUBSCRIPTION SCREEN BY FILE NAME
function populateNewSubscriptionScreen(name){

	var obj = dashboardParams[name];

	//SET PARAMETERS
	$.each(obj , function( key, value ) {
		//console.log(key+': '+value);
		$('input[name="'+ key +'"]').val(value);

		if ($.inArray(key, notTableauParams) === -1){

			addParamRow(key, value);
		}

	});

	//SET CRON SCHEDULE TIME
	$('#selector').cron("value", obj.cronVal);
	// SET HIDDEN FILENAME
	$('#fileNameId').val(name);

}

//BUILD ERROR DASHBOARD FOR LIST
function buildErrorDashboardForList(index, fields){
	var div = '<div class="panel panel-default">';
	div += '<div class="content-box-header" style="background-color:#f9d6d6">';
	div += '<div class="panel-title">';
	div += fields;
	div += '</div>';
	div += '<div class="panel-options">';
	div += '<div id="selector'+index+'"></div>';
	div += '<a class="deleteSubscription" id="'+ index +'"><i class="glyphicon glyphicon-remove" data-toggle="tooltip" title="Delete Subscription"></i></a>';
	div += '</div>';
	div += '</div>';
	div += '</div>';
	return div;
}

//BUILD DASHBOARD ELEMENT FOR DASHBOARDS LIST
function buildDashboardElementForList(index, obj){

	if(cronstrue.toString(obj['cronVal']).indexOf('undefined') >= 0){
		throw "Error: wrong cron value";
	}

	var div = '<div class="panel panel-default">';
	div += '<div class="content-box-header">';
	div += '<div class="panel-title">';
	div += obj.subscriptionName;
	div += '</div>';
	div += '<div class="panel-options">';
	div += '<div id="selector'+index+'"></div>';
	div += '<span >'+cronstrue.toString(obj['cronVal'])+'</span>';
	div += '<a class="editDashboardParam" id="'+ index +'"><i class="glyphicon glyphicon-pencil" data-toggle="tooltip" title="Edit Subscription"></i></a>';

	var dateObj = obj.endScheduleDate.split('/');
	var endDateVal = new Date(dateObj[2], dateObj[1]-1 , dateObj[0]);
	var currentDate = new Date();

	if(obj.activate === 'true' && endDateVal >= currentDate){
		div += '<a class="deactivateSubscription" id="'+ index +'"><i class="glyphicon glyphicon-stop" data-toggle="tooltip" title="Stop Subscription"></i></a>';
	}else{
		div += '<a class="deactivateSubscription" id="'+ index +'"><i class="glyphicon glyphicon-play" data-toggle="tooltip" title="Start Subscription"></i></a>';
	}	

	div += '<a class="deleteSubscription" id="'+ index +'"><i class="glyphicon glyphicon-remove" data-toggle="tooltip" title="Delete Subscription"></i></a>';
	div += '</div>';
	div += '</div>';
	div += '<div class="panel-body" style="display:none;">';
	$.each( obj, function( key, value ) {
		div += '<div class="row">' + key + ": " + value + '</div>' ;
	});
	div += '</div>';
	div += '</div>';
	return div;
}

//FUNCTION GET DASHBOARDS
function getSubscriptions(){

	$('#viewDashboardScreen').html('');
	dashboardParams = {};

	$.get("dashboard/getDashboards", function(data, status){
		//SET DATA TO MAP USING FILENAME AS KEY
		dashboardParams = data;

		var allDiv = '';

		$.each(data, function(index, obj) {			

			try {
				allDiv += buildDashboardElementForList(index, obj);	
			}
			catch(err) {

				allDiv += buildErrorDashboardForList(index, err);
			}


		});

		//APPEND ALL SUBSCRIPTIONS TO CONTAINER
		$("#viewSubscriptionScreen").append(allDiv);

	});

}

//FUNCTION SHOW ALL SUBSCRIPTION SCREEN
function showSubscriptionsScreen(){
	$('#viewSubscriptionScreen').html('');

	$('#newSubscriptionForm').trigger("reset");

	$('.myContainer').hide();
	$('.myContainerTitle').html('View Subscriptions');

	$('.nav li').removeClass( "current" );
	$('a[href="#subscriptions"]').parent().addClass( "current" );

	$('#viewSubscriptionScreen').show();
	getSubscriptions();
}

//FUNCTION SHOW NEW SUBSCRIPTION SCREEN
function showNewSubscriptionScreen(){

	$('#fileNameId').val('');

	$('.table-main-form tbody').html('');
	$('#viewSubscriptionScreen').html('');

	$('.myContainer').hide();

	$('.myContainerTitle').html('New Subscription');

	$('.nav li').removeClass( "current" );
	$('a[href="#newSubscription"]').parent().addClass( "current" );

	$('#newSubscriptionScreen').show();
}

$(document).ready(function() {




	// TOGGLE SUBCSRIPTIONS CONTAINERS
	$('#viewSubscriptionScreen').on("click", ".panel-title", function() {

		$( this ).parent().next().toggle( "slow" );

	});

	// EDIT SUBSCRIPTION PARAMETERS
	$('#viewSubscriptionScreen').on("click", ".editDashboardParam", function() {

		var id = $(this).attr('id');

		showNewSubscriptionScreen();

		populateNewSubscriptionScreen(id);

	});

	// DELETE SUBSCRIPTION
	$('#viewSubscriptionScreen').on("click", ".deleteSubscription", function() {

		var id = $(this).attr('id');

		deleteSubscriptionById(id, this);

	});

	// DEACTIVATE SUBSCRIPTION
	$('#viewSubscriptionScreen').on("click", ".deactivateSubscription", function() {

		var thisComponent = $(this);
		var id = thisComponent.attr('id');
		var message = '';

		if('glyphicon glyphicon-stop' === thisComponent.find('i').attr('class')){
			message ="<h2>Want to deactivate?</h2>";
		}else{
			message ="<h2>Want to activate?</h2>";
		}		

		bootbox.confirm(message, function(result) {
			if (result) {

				if('glyphicon glyphicon-stop' === thisComponent.find('i').attr('class')){

					thisComponent.find('i').attr('class','glyphicon glyphicon-play');
					deactivateSubscription(id,'false');

				}else {
					thisComponent.find('i').attr('class','glyphicon glyphicon-stop');
					deactivateSubscription(id,'true');
				}

			}
		});

	});

});



