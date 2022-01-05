package com.p3mail.application.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class MainWindowController {

	@FXML
	private ImageView imgIcon;

	@FXML
	private Label lblEmailAddress;

	@FXML
	private Label lblEmailAddress1;

	@FXML
	private Label lblEmailAddress11;

	@FXML
	private Label lblFrom;

	@FXML
	private Label lblObject;

	@FXML
	private Label lblTo;

	@FXML
	private ListView<?> lstEmails;

	@FXML
	private BorderPane pnlEmailList;

	@FXML
	private BorderPane pnlReadMessage;

}
