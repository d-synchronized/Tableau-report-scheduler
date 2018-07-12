var selectedRow = null;

//FUNCTION ADD PARAM ROW
function addParamRow(name, value){

	//SET AN ID TO NAME AND VALUE TEXTBOX
	var nameID = "_" + new Date().valueOf() + Math.random().toFixed(16).substring(2);
	var valueID = "_" + new Date().valueOf() + Math.random().toFixed(16).substring(2);

	var newRow = '<tr>';
	newRow += '<td><div class="form-group"><input type="text" id="'+ nameID +'" class="name form-control" required readonly></div></td>';
	newRow += '<td><div class="form-group"><input type="text" id="'+ valueID +'" class="value form-control" required readonly></div></td>';
	newRow += '<td><button class="deleteRow btn btn-primary">Remove</button><button class="editRow btn btn-primary">Edit</button></td>';
	newRow += '</tr>';
	$('.table-main-form tbody').append(newRow);

	//IF NAME AND VALUE IS AVAILABLE SET THEM
	if (typeof name != 'undefined'){

		$('#'+ nameID).val(name);
		$('#'+ valueID).val(value);
	}
}

//FUNCTION EDIT PARAM ROW
function editParamRow(name, value){
	$(selectedRow).closest("tr").find('.name').val(name);
	$(selectedRow).closest("tr").find('.value').val(value);
}

//FUNCTION TO REMOVE PARAM ROW
function removeParamRow(target){
	$(target).closest('tr').remove();
}

//FUNCTION TO ENABLE/DISABLE ADD PARAM FORM FIELDS
function enableDisableAddParamFormFields(value){
	if (value == 'NORMAL') {
		$('#textParamContainer').show();
		$('#dateParamContainer').hide();

		$('#modalInputParamName').prop('disabled', false);
		$('#modalInputParamValue').prop('disabled', false);

		$('#modalInputParamNameDateEnd').prop('disabled', true);
		$('#modalInputParamNameDateStart').prop('disabled', true);
		$('#modalInputParamValueDateEnd').prop('disabled', true);
		$('#modalInputParamValueDateStart').prop('disabled', true); 
	}
	else if (value == 'RANGE') {
		$('#textParamContainer').hide();
		$('#dateParamContainer').show();

		$('#modalInputParamName').prop('disabled', true);
		$('#modalInputParamValue').prop('disabled', true);

		$('#modalInputParamNameDateEnd').prop('disabled', false);
		$('#modalInputParamNameDateStart').prop('disabled', false);
		$('#modalInputParamValueDateEnd').prop('disabled', false);
		$('#modalInputParamValueDateStart').prop('disabled', false); 
	}
}

//SUBMIT FORM INSERT/EDIT SUBSCRIPTION
function submitSubscriptionForm(target){

	myUrlParams = {};

	//GET FORM VALUES
	var formValues = $("#newSubscriptionForm").serializeArray();

	//SET FORM VALUES IN myUrlParams OBJECT
	$.each(formValues, function(_, kv) {
		if (kv.name.indexOf("cron-") == -1){
			myUrlParams[kv.name] = kv.value;
		}
	});

	//ADD ADDITIONAL URL PARAMETERS TO myUrlParams OBJECT
	$( ".table-main-form tbody tr" ).each(function() {
		myUrlParams[$( this ).find( ".name" ).val()] = $( this ).find( ".value" ).val();
	});

	//SET STARTDATE/ENDDATE
	myUrlParams['startScheduleDate'] = $('#startScheduleDate').val();
	myUrlParams['endScheduleDate'] = $('#endScheduleDate').val();

	//BUILD RESOURCE URL
	var actionUrl = $(target).attr('action');
	if('' !== $('#fileNameId').val()){
		actionUrl += '?name=' + $('#fileNameId').val();	
		myUrlParams['activate'] = dashboardParams[$('#fileNameId').val()].activate;

	}else{
		myUrlParams['activate'] = 'true';
	}

	//SET CRON VALUE
	myUrlParams['cronVal'] = $('#selector').cron('value');


	//POST FORM VALUES
	$.ajax({
		type: 'post',
		url: actionUrl,
		data: JSON.stringify(myUrlParams),
		contentType: "application/json; charset=utf-8",
		traditional: true,
		success: function (data) {

			bootbox.alert("<h2>Action was successful!</h2>", function(result) {});

			$('#newSubscriptionForm').trigger("reset");
			if('' !== $('#fileNameId').val()){
				showSubscriptionsScreen();
			}			

		}
	});
}

$(document).ready(function() {

	// ADD PARAMETER TO FORM
	$('.addRow').click(function(event) {
		event.preventDefault();
		selectedRow = null;
		$('#myAddParamModal').modal('show');
		$('#myAddParamForm').trigger("reset");
		enableDisableAddParamFormFields($('input[type=radio][name=paramRadios]:CHECKED').val());
	});

	// REMOVE PARAMETER TO FORM
	$('.table-main-form tbody').on("click", ".deleteRow", function() {
		removeParamRow(this);
	});

	// RADIO BUTTON ONCHANGE EVENTS
	$('input[type=radio][name=paramRadios]').change(function() {
		enableDisableAddParamFormFields(this.value);
	});

	// SUBMIT MODAL PARAMETER TO FORM
	$('#myAddParamForm').submit(function(event) {
		event.preventDefault();

		var paramName='';
		var paramValue='';

		if ($('input[type=radio][name=paramRadios]:CHECKED').val() == 'NORMAL') {			

			paramName = $('#modalInputParamName').val();
			paramValue = $('#modalInputParamValue').val(); 

		}
		else {			

			paramName = "[RANGE]:" + $('#modalInputParamNameDateEnd').val()+ ":" +$('#modalInputParamNameDateStart').val();
			paramValue = $('#modalInputParamValueDateEnd').val()+ ":" + $('#modalInputParamValueDateStart').val(); 
		}
		if(selectedRow != null){
			editParamRow(paramName, paramValue);
		}else{
			addParamRow(paramName, paramValue);
		}
		
		$('#myAddParamModal').modal('hide');

	});

	//	SUBMIT MAIN FORM
	$( "#newSubscriptionForm" ).submit(function( event ) {

		event.preventDefault();

		submitSubscriptionForm(this);


	});

	// EDIT PARAMETER
	$('#newSubscriptionScreen').on("click", ".editRow", function(event) {

		event.preventDefault();
		selectedRow = this;
		var name = $(this).closest("tr").find('.name').val();
		var value = $(this).closest("tr").find('.value').val();

		$('#myAddParamForm').trigger("reset");

		if(name.indexOf("[RANGE]:") >= 0){
			$('input[type=radio][name=paramRadios][value=RANGE]').prop('checked',true);
			enableDisableAddParamFormFields('RANGE');

			var dateStartName = name.split(':')[2];
			var dateEndName = name.split(':')[1];			

			var dateStartValue = value.split(':')[1];
			var dateEndValue = value.split(':')[0];			

			$('#modalInputParamNameDateEnd').val(dateEndName);
			$('#modalInputParamNameDateStart').val(dateStartName);
			$('#modalInputParamValueDateEnd').val(dateEndValue);
			$('#modalInputParamValueDateStart').val(dateStartValue);

		}else{
			$('input[type=radio][name=paramRadios][value=NORMAL]').prop('checked',true);
			enableDisableAddParamFormFields('NORMAL');

			$('#modalInputParamName').val(name);
			$('#modalInputParamValue').val(value);
		}

		$('#myAddParamModal').modal('show');

	});

});