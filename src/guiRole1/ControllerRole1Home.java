package guiRole1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entityClasses.ExperienceEntry;
import entityClasses.LessonCollection;
import entityClasses.LessonLearned;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/*******
 * <p> Title: ControllerRole1Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 1 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * For HW2 this page is the Contributor's home page.  The Controller reacts to the widgets, but it
 * does NOT validate or touch the database itself.  It hands what the user typed to
 * ModelRole1Home and then displays whatever LessonResult comes back, showing each error message
 * right under the field that caused it (InputValidation.pdf: "show the message beneath the
 * related field").
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter (Contributor actions added by Emilia Davey)
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation
 * @version 1.02		2026-09-29 HW2: Contributor lessons learned actions
 */

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRole1Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole1Home.theStage, ViewRole1Home.theUser);
	}	

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}

	
	/*-*******************************************************************************************

	HW2: the Contributor's lesson actions
	
	 */
	
	/**********
	 * <p> Method: showAllMyLessons() </p>
	 * 
	 * <p> Description: Puts the page into its starting state: the search cleared, a blank New
	 * Lesson form, and the full list of this Contributor's lessons (US-R1).  The View calls this
	 * every time the page is displayed, so the list is always current. </p>
	 */
	protected static void showAllMyLessons() {
		ViewRole1Home.text_Search.clear();
		ViewRole1Home.label_SearchError.setText("");
		refreshLessonList(ModelRole1Home.viewMyLessons(currentUser()), null);
		performNewLesson();
	}

	
	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Runs the search box text through the Model (US-R2 and US-R2-V).  If the
	 * text is invalid, the message shows under the search box and the list is left alone, like
	 * the "Search Input - Invalid Text Rejected" test says. </p>
	 */
	protected static void performSearch() {
		ViewRole1Home.label_SearchError.setText("");
		LessonResult result = ModelRole1Home.searchMyLessons(currentUser(), 
				ViewRole1Home.text_Search.getText());
		if (!result.isSuccess()) {
			ViewRole1Home.label_SearchError.setText(result.getMessage());
			return;
		}
		refreshLessonList(result.getCollection(), "matching");
	}

	
	/**********
	 * <p> Method: performClearSearch() </p>
	 * 
	 * <p> Description: Clears the search box and shows every lesson again.  Since an empty search
	 * shows nothing (the team's rule), this button is how the full list comes back. </p>
	 */
	protected static void performClearSearch() {
		ViewRole1Home.text_Search.clear();
		ViewRole1Home.label_SearchError.setText("");
		refreshLessonList(ModelRole1Home.viewMyLessons(currentUser()), null);
	}

	
	/**********
	 * <p> Method: performSelectLesson(int index) </p>
	 * 
	 * <p> Description: Called when the user clicks a lesson in the list (US-R3).  It asks the
	 * Model for the full lesson and fills the form, disabling any locked field. </p>
	 * 
	 * @param index the selected row in the list, or -1 if nothing is selected (which is ignored)
	 */
	protected static void performSelectLesson(int index) {
		if (index < 0 || index >= ViewRole1Home.listedLessonIds.size()) return;
		clearErrors();
		LessonResult result = ModelRole1Home.viewLesson(currentUser(), 
				ViewRole1Home.listedLessonIds.get(index));
		if (!result.isSuccess()) {
			showStatus(result.getMessage(), false);
			return;
		}
		loadLessonIntoForm(result.getLesson());
	}

	
	/**********
	 * <p> Method: performNewLesson() </p>
	 * 
	 * <p> Description: Switches the form to "create" mode with everything blank and unlocked. </p>
	 */
	protected static void performNewLesson() {
		ViewRole1Home.theSelectedLessonId = -1;
		ViewRole1Home.listView_Lessons.getSelectionModel().clearSelection();
		ViewRole1Home.text_Title.clear();
		ViewRole1Home.text_Description.clear();
		ViewRole1Home.text_Tags.clear();
		ViewRole1Home.combobox_Category.getSelectionModel().select(0);
		setFormLocks(null);
		ViewRole1Home.label_FormHeading.setText("New Lesson");
		ViewRole1Home.button_SaveLesson.setText("Create Lesson");
		ViewRole1Home.label_LockNote.setText("");
		ViewRole1Home.text_Experience.setText("Select a lesson to see its experience log.");
		clearErrors();
		showStatus("", true);
	}

	
	/**********
	 * <p> Method: performSave() </p>
	 * 
	 * <p> Description: The one Save button does Create (US-C1) when no lesson is selected and
	 * Update (US-U1) when one is.  Either way it just passes the form values to the Model.  A
	 * failure shows its message under the field named in the LessonResult and nothing is saved.
	 * </p>
	 */
	protected static void performSave() {
		clearErrors();
		String category = ViewRole1Home.combobox_Category.getValue();
		// The placeholder row isn't a real category, so treat it as "nothing chosen"
		if (category == null || category.startsWith("<")) category = "";
		
		boolean creating = (ViewRole1Home.theSelectedLessonId == -1);
		LessonResult result;
		if (creating)
			result = ModelRole1Home.createLesson(currentUser(), 
					ViewRole1Home.text_Title.getText(), ViewRole1Home.text_Description.getText(), 
					ViewRole1Home.text_Tags.getText(), category);
		else
			result = ModelRole1Home.updateLesson(currentUser(), ViewRole1Home.theSelectedLessonId,
					ViewRole1Home.text_Title.getText(), ViewRole1Home.text_Description.getText(), 
					ViewRole1Home.text_Tags.getText(), category);
		
		if (!result.isSuccess()) {
			showFieldError(result.getFailedField(), result.getMessage());
			return;
		}
		
		// It worked.  Reload the list, then re-select the saved lesson so the form shows exactly
		// what was stored.  The status goes last because selecting clears the old messages.
		refreshLessonList(ModelRole1Home.viewMyLessons(currentUser()), null);
		selectLessonById(result.getLessonId());
		showStatus(result.getMessage(), true);
	}

	
	/**********
	 * <p> Method: performDelete() </p>
	 * 
	 * <p> Description: Delete (US-D1 and US-D1-V).  It first asks the Model with confirmed =
	 * false.  If the Model comes back with a message the lesson was refused (locked or not
	 * found), so the message is shown and NO confirmation is asked, because it makes no sense to
	 * ask "Are you sure?" about something that can't happen.  An EMPTY message means it CAN be
	 * deleted, so the "Are you sure?" dialog is shown, and only a "Yes" makes the second call with
	 * confirmed = true.  "No" or closing the dialog just cancels. </p>
	 */
	protected static void performDelete() {
		if (ViewRole1Home.theSelectedLessonId == -1) {
			showStatus("Select a lesson first.", false);
			return;
		}
		int id = ViewRole1Home.theSelectedLessonId;
		LessonResult check = ModelRole1Home.deleteLesson(currentUser(), id, false);
		if (!check.getMessage().isEmpty()) {
			showStatus(check.getMessage(), false);
			return;
		}
		
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Delete Lesson");
		confirm.setHeaderText("Are you sure?");
		confirm.setContentText("Delete \"" + ViewRole1Home.text_Title.getText() 
				+ "\"?  This can't be undone.");
		confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> answer = confirm.showAndWait();
		
		if (answer.isPresent() && answer.get() == ButtonType.YES) {
			LessonResult result = ModelRole1Home.deleteLesson(currentUser(), id, true);
			if (result.isSuccess()) {
				refreshLessonList(ModelRole1Home.viewMyLessons(currentUser()), null);
				performNewLesson();
				showStatus(result.getMessage(), true);
			}
			else
				showStatus(result.getMessage(), false);
		}
	}

	
	/**********
	 * <p> Method: performAddExperience() </p>
	 * 
	 * <p> Description: Add experience information to the selected lesson (US-U2 and US-U2-V).  A
	 * small dialog collects the hours and the description.  The Add button's click is intercepted
	 * (event filter) so that when the Model rejects the input the dialog stays open and shows the
	 * message under the right field, instead of closing and losing what was typed. </p>
	 */
	protected static void performAddExperience() {
		if (ViewRole1Home.theSelectedLessonId == -1) {
			showStatus("Select a lesson first.", false);
			return;
		}
		final int id = ViewRole1Home.theSelectedLessonId;
		
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.setTitle("Add Experience");
		dialog.setHeaderText("Record a new attempt to use this lesson");
		ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);
		
		TextField hours = new TextField();
		hours.setPromptText("e.g. 3.5");
		Label hoursError = errorLabel();
		TextArea what = new TextArea();
		what.setPromptText("What was done and how?");
		what.setWrapText(true);
		what.setPrefRowCount(4);
		Label whatError = errorLabel();
		dialog.getDialogPane().setContent(new VBox(6, new Label("Time / effort (hours)"), hours, 
				hoursError, new Label("What was done"), what, whatError));
		
		final boolean[] added = { false };
		Button addButton = (Button) dialog.getDialogPane().lookupButton(addType);
		addButton.addEventFilter(ActionEvent.ACTION, event -> {
			hoursError.setText("");
			whatError.setText("");
			LessonResult result = ModelRole1Home.addExperience(currentUser(), id, 
					hours.getText(), what.getText());
			if (result.isSuccess()) {
				added[0] = true;			// let the dialog close
				return;
			}
			event.consume();				// keep the dialog open so they can fix it
			if (result.getFailedField().equals("experienceDescription"))
				whatError.setText(result.getMessage());
			else
				hoursError.setText(result.getMessage());
		});
		dialog.showAndWait();
		
		if (added[0]) {
			LessonResult again = ModelRole1Home.viewLesson(currentUser(), id);
			if (again.isSuccess()) loadLessonIntoForm(again.getLesson());
			showStatus("Experience added", true);
		}
	}

	
	/*-*******************************************************************************************

	Private helpers for the actions above
	
	 */
	
	/**********
	 * The username of whoever is logged in.  Every Model call is made with this name, so a
	 * Contributor only ever works with their own lessons.
	 */
	private static String currentUser() {
		return ViewRole1Home.theUser.getUserName();
	}

	
	/**********
	 * Replaces the contents of the list.  Only the ids are kept in a parallel list, so a row in
	 * the ListView can be matched back to its lesson.
	 * 
	 * @param collection	the lessons to show
	 * @param what			null for "all my lessons", or a word like "matching" for the status text
	 */
	private static void refreshLessonList(LessonCollection collection, String what) {
		ViewRole1Home.listedLessonIds.clear();
		List<String> rows = new ArrayList<String>();
		for (LessonLearned lesson : collection.getLessons()) {
			ViewRole1Home.listedLessonIds.add(lesson.getId());
			rows.add(lesson.getTitle() + "   [" + lesson.getCategory() + "]" 
					+ (lesson.isLocked() ? "   (locked)" : ""));
		}
		ViewRole1Home.listView_Lessons.setItems(FXCollections.observableArrayList(rows));
		int n = collection.size();
		ViewRole1Home.label_ListStatus.setText("Showing " + n + ((what == null) ? " " : " " + what + " ")
				+ ((n == 1) ? "lesson" : "lessons"));
	}

	
	/**********
	 * Selects the row for one lesson id, which fires the list's selection listener and loads the
	 * form.
	 */
	private static void selectLessonById(int lessonId) {
		int row = ViewRole1Home.listedLessonIds.indexOf(lessonId);
		if (row >= 0) ViewRole1Home.listView_Lessons.getSelectionModel().select(row);
	}

	
	/**********
	 * Fills the form with one lesson (US-R3), including its experience log and which fields are
	 * locked.
	 */
	private static void loadLessonIntoForm(LessonLearned lesson) {
		ViewRole1Home.theSelectedLessonId = lesson.getId();
		ViewRole1Home.text_Title.setText(lesson.getTitle());
		ViewRole1Home.text_Description.setText(lesson.getDescription());
		ViewRole1Home.text_Tags.setText(lesson.getTags());
		ViewRole1Home.combobox_Category.setValue(lesson.getCategory());
		setFormLocks(lesson);
		ViewRole1Home.label_FormHeading.setText("Lesson Details");
		ViewRole1Home.button_SaveLesson.setText("Save Changes");
		
		// Tell the user what is locked instead of leaving them wondering why a field is grayed out
		String note = "";
		if (lesson.isLockedEntire())
			note = "Whole lesson is locked";
		else {
			List<String> names = new ArrayList<String>();
			for (String f : new String[] { "title", "description", "tags", "category" })
				if (lesson.isFieldLocked(f)) names.add(f);
			if (!names.isEmpty()) note = "Locked: " + String.join(", ", names);
		}
		ViewRole1Home.label_LockNote.setText(note);
		
		StringBuilder log = new StringBuilder();
		for (ExperienceEntry e : lesson.getExperienceEntries())
			log.append(e.getTimeEffortHours()).append(" h - ").append(e.getDescription()).append("\n");
		if (log.length() == 0) log.append("No experience entries yet.");
		ViewRole1Home.text_Experience.setText(log.toString());
	}

	
	/**********
	 * Disables the fields that are locked.  Passing null (a brand new lesson) enables everything.
	 */
	private static void setFormLocks(LessonLearned lesson) {
		ViewRole1Home.text_Title.setDisable(lesson != null && lesson.isFieldLocked("title"));
		ViewRole1Home.text_Description.setDisable(lesson != null && lesson.isFieldLocked("description"));
		ViewRole1Home.text_Tags.setDisable(lesson != null && lesson.isFieldLocked("tags"));
		ViewRole1Home.combobox_Category.setDisable(lesson != null && lesson.isFieldLocked("category"));
	}

	
	/**********
	 * Puts a Model error under the field it belongs to.  A failure that isn't about one field
	 * (like "Lesson not found.") goes to the status line.
	 */
	private static void showFieldError(String field, String message) {
		switch (field) {
			case "title": ViewRole1Home.label_TitleError.setText(message); break;
			case "description": ViewRole1Home.label_DescriptionError.setText(message); break;
			case "tags": ViewRole1Home.label_TagsError.setText(message); break;
			case "category": ViewRole1Home.label_CategoryError.setText(message); break;
			default: showStatus(message, false);
		}
	}

	
	private static void clearErrors() {
		ViewRole1Home.label_TitleError.setText("");
		ViewRole1Home.label_DescriptionError.setText("");
		ViewRole1Home.label_TagsError.setText("");
		ViewRole1Home.label_CategoryError.setText("");
	}

	
	/**********
	 * The status line under the form: green for good news, red for a problem.
	 */
	private static void showStatus(String message, boolean good) {
		ViewRole1Home.label_SaveStatus.setTextFill(good ? Color.web("#2e7d32") : Color.RED);
		ViewRole1Home.label_SaveStatus.setText(message);
	}

	
	private static Label errorLabel() {
		Label l = new Label("");
		l.setTextFill(Color.RED);
		return l;
	}
}