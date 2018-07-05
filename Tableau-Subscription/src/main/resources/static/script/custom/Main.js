var myCron = '';
var myUrlParams = {};
var dashboardParams = {};
var notTableauParams = [];

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

	// INIT NOT TABLEAU PARAMS OBJECT
	$.get("dashboard/getNotTableauParams", function(data, status){
		notTableauParams = data;
	});

	//INIT cronstrue LIB TO GET CRON EXP AS READABLE FORM
	var cronstrue = window.cronstrue;

	//INIT DATE PICKER
	$('.datepicker').datepicker();

	//SET TOOLTIP
	$('[data-toggle="tooltip"]').tooltip(); 

	//INIT CRON CREATOR FORM
	$('#selector').cron({initial:"0 0 7 * * ?"});


	//NAVIGATION TO SCREENS 
	$('a[href="#subscriptions"]').click(function(){

		showSubscriptionsScreen();

	}); 
	$('a[href="#newSubscription"]').click(function(){

		showNewSubscriptionScreen();

	}); 
	


});