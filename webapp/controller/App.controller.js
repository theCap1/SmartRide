sap.ui.define([
		"sap/ui/core/mvc/Controller",
		"sap/ui/model/json/JSONModel",
		"sap/m/MessageToast"
	],

	function(Controller, JSONModel, Toast) {
		"use strict";

		return Controller.extend("storm.controller.App", {
			onShowHello : function () {
				// show a native JavaScript alert
				Toast.show("blabla");
			},
			afterRender: function() {
			}
		});
	});