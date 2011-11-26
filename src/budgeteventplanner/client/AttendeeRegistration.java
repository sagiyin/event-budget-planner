//<<<<<<< .mine
/*
 *
 needs to make sure that the phone number is not letter
 * clear everything after jump
 * enter the information that already have in the data base after the attendee reenter the registration code.
 * 
 */

//=======
// can cancel their attending anytime.
// in event view page the canceled attendee needs another color.
//>>>>>>> .r187
package budgeteventplanner.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AttendeeRegistration implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final AttendeeServiceAsync attendeeService = GWT
			.create(AttendeeService.class);
	final Label emailLabel = new Label("*Email:");
	final TextBox emailBox = new TextBox();
	final Label firstNameLabel = new Label("*First Name:");
	final TextBox firstNameBox = new TextBox();
	final Label midNameLabel = new Label("Mid Name:");
	final TextBox midNameBox = new TextBox();
	final Label lastNameLabel = new Label("*Last Name:");
	final TextBox lastNameBox = new TextBox();
	final Label jobTitleLabel = new Label("Job Title:");
	final TextBox jobTitleBox = new TextBox();
	final Label companyLabel = new Label("Company:");
	final TextBox companyBox = new TextBox();
	final Label address1Label = new Label("*Address 1:");
	final TextBox address1Box = new TextBox();
	final Label address2Label = new Label("Address 2:");
	final TextBox address2Box = new TextBox();
	final Label cityLabel = new Label("*City:");
	final TextBox cityBox = new TextBox();
	final Label stateLabel = new Label("*State:");
	final TextBox stateBox = new TextBox();
	final Label zipLabel = new Label("*Zip:");
	final TextBox zipBox = new TextBox();
	final Label phoneLabel = new Label("*Phone Number:");
	final TextBox phoneBox = new TextBox();
	final Button submit = new Button("submit");
	final DialogBox dialogBox = new DialogBox();
	final Button closeButton = new Button("Close");
	VerticalPanel dialogVPanel = new VerticalPanel();
	private static String attendeeID = null;
	final ListBox status = new ListBox();
	Label statusLabel=new Label("Are You Going To Attendee:");
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//status 0 yes 1 no 2 maybe
		status.addItem("yes");
		status.addItem("no");
		status.addItem("maybe");
		RootPanel.get("status").add(statusLabel);
		RootPanel.get("statusBox").add(status);

		RootPanel.get("firstName").add(firstNameLabel);
		RootPanel.get("firstNameBox").add(firstNameBox);

		RootPanel.get("midName").add(midNameLabel);
		RootPanel.get("midNameBox").add(midNameBox);

		RootPanel.get("lastName").add(lastNameLabel);
		RootPanel.get("lastNameBox").add(lastNameBox);

		RootPanel.get("jobTitle").add(jobTitleLabel);
		RootPanel.get("jobTitleBox").add(jobTitleBox);

		RootPanel.get("company").add(companyLabel);
		RootPanel.get("companyBox").add(companyBox);

		RootPanel.get("address1").add(address1Label);
		RootPanel.get("address1Box").add(address1Box);

		RootPanel.get("address2").add(address2Label);
		RootPanel.get("address2Box").add(address2Box);

		RootPanel.get("city").add(cityLabel);
		RootPanel.get("cityBox").add(cityBox);

		RootPanel.get("state").add(stateLabel);
		RootPanel.get("stateBox").add(stateBox);

		RootPanel.get("zip").add(zipLabel);
		RootPanel.get("zipBox").add(zipBox);

		RootPanel.get("phone").add(phoneLabel);
		RootPanel.get("phoneBox").add(phoneBox);

		RootPanel.get("submit").add(submit);

		// dialog box

		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Submit Successful!</b>"));

		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		dialogBox.setAnimationEnabled(true);
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				submit.setEnabled(true);
				submit.setFocus(true);
				RootPanel.get("XuXuan2").setVisible(false);
				RootPanel.get("main").setVisible(true);
			}
		});
		// emailBox.setFocus(true);

		class MyHandler implements ClickHandler {

			public void onClick(ClickEvent event) {

				if (!checkForm())
					return;

				attendeeService.updateAttendeeInfo(
						attendeeID,
						firstNameBox.getText() + " " + midNameBox.getText(),
						emailBox.getText(),
						jobTitleBox.getText(),
						companyBox.getText(),
						address1Box.getText() + "\n" + address2Box.getText()
								+ "\n" + cityBox.getText() + "\n"
								+ stateBox.getText() + "\n" + zipBox.getText(),
						phoneBox.getText(),status.getSelectedIndex(), new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
							}

							public void onSuccess(Void result) {

								((HTML) dialogVPanel.getWidget(0))
										.setHTML("<b>Submit Successful!</b>");
								dialogBox.center();
								closeButton.setFocus(true);
								// RootPanel.get("attendeeManager").setVisible(true);
								emailBox.setText("");
								firstNameBox.setText("");
								midNameBox.setText("");
								jobTitleBox.setText("");
								lastNameBox.setText("");
								companyBox.setText("");
								address1Box.setText("");
								address2Box.setText("");
								cityBox.setText("");
								stateBox.setText("");
								zipBox.setText("");
								phoneBox.setText("");
								status.setItemSelected(0, true);
								//RootPanel.get("XuXuan2").setVisible(false);
								//RootPanel.get("main").setVisible(true);
							}

						}

				);

			}
		}
		MyHandler handler = new MyHandler();
		submit.addClickHandler(handler);
	}

	// used by BudgetEventPlanner UI to set the current attendeeID
	public static void setAttendeeID(String attendeeIDc) {
		attendeeID = attendeeIDc;
		
	}

	// check if the form is legal
	boolean checkForm() {
		if (emailBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input your email address</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (!(emailBox.getText().contains("@") && emailBox.getText().contains(
				"."))) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input a legal email address</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (firstNameBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input your first name</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (address1Box.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input your address</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (cityBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input the city you live in</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (stateBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input the state you live in</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (zipBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input the ZIP code</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (phoneBox.getText().compareTo("") == 0) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input your phone number</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		if (phoneBox.getText().length()!=10) {
			((HTML) dialogVPanel.getWidget(0))
					.setHTML("<b>Be sure input ten digits phone number</b>");
			dialogBox.center();
			closeButton.setFocus(true);
			return false;
		}
		

		return true;
	}

}
