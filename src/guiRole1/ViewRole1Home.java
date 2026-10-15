package guiRole1;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import inputValidation.LessonRecognizer;
import database.Database;
import entityClasses.User;


/*******
 * <p> Title: ViewRole1Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role1 Home Page.  In the Foundations code this page was a
 * stub with an empty middle section.  For HW2, Role1 plays the Contributor role, so GUI Area 2
 * now holds the Contributor's lessons learned screen: a search box and a list of the user's
 * lessons on the left, and a form to create, view, edit, and add experience to a lesson on the
 * right.  All the widgets are still just declared and positioned here.  What they DO lives in
 * ControllerRole1Home, and the data work lives in ModelRole1Home.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter (Contributor screen added by Emilia Davey)
 * 
 * @version 1.00		2025-08-20 Initial version
 * @version 1.01		2026-09-29 HW2: Contributor lessons learned screen in GUI Area 2
 *  
 */

public class ViewRole1Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2 (HW2): the Contributor's lessons learned screen.
	
	// Left column: search box, the list of this user's lessons, and the list's buttons
	protected static TextField text_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static Button button_ClearSearch = new Button("Clear");
	protected static Label label_SearchError = new Label();
	protected static ListView<String> listView_Lessons = new ListView<String>();
	protected static Button button_NewLesson = new Button("New Lesson");
	protected static Button button_DeleteLesson = new Button("Delete");
	protected static Button button_AddExperience = new Button("Add Experience");
	protected static Label label_ListStatus = new Label();
	
	// Right column: the form.  Every field has its own error label directly under it.
	protected static Label label_FormHeading = new Label("New Lesson");
	private static Label label_TitleCaption = new Label("Title");
	protected static TextField text_Title = new TextField();
	protected static Label label_TitleError = new Label();
	private static Label label_DescriptionCaption = new Label("Description");
	protected static TextArea text_Description = new TextArea();
	protected static Label label_DescriptionError = new Label();
	private static Label label_TagsCaption = new Label("Tags (comma-separated, optional)");
	protected static TextField text_Tags = new TextField();
	protected static Label label_TagsError = new Label();
	private static Label label_CategoryCaption = new Label("Category");
	protected static ComboBox <String> combobox_Category = new ComboBox <String>();
	protected static Label label_CategoryError = new Label();
	protected static Button button_SaveLesson = new Button("Create Lesson");
	protected static Label label_LockNote = new Label();
	protected static Label label_SaveStatus = new Label();
	private static Label label_ExperienceHeading = new Label("Experience log");
	protected static TextArea text_Experience = new TextArea();
	
	// Which lesson the form is showing (-1 means the form is creating a new one), and the ids of
	// the lessons in the list, in the same order as the rows, so a clicked row maps to a lesson
	protected static int theSelectedLessonId = -1;
	protected static List<Integer> listedLessonIds = new ArrayList<Integer>();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole1Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewRole1HomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayRole1Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role1 Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayRole1Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole1Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		
		// HW2: this page is reused for every login, so reload THIS user's lessons every time
		// instead of showing whoever used it last
		ControllerRole1Home.showAllMyLessons();
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Contributor Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewRole1Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole1Home() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Contributor Home Page (Role1)");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {ControllerRole1Home.performUpdate(); });
		
		// GUI Area 2, left column: search and the list of lessons
		setupTextFieldUI(text_Search, 215, 20, 105);
		text_Search.setPromptText("Search my lessons by title");
		text_Search.setOnAction((_) -> {ControllerRole1Home.performSearch(); });
		
		setupButtonUI(button_Search, "Dialog", 13, 60, Pos.CENTER, 243, 105);
		button_Search.setOnAction((_) -> {ControllerRole1Home.performSearch(); });
		
		setupButtonUI(button_ClearSearch, "Dialog", 13, 61, Pos.CENTER, 309, 105);
		button_ClearSearch.setOnAction((_) -> {ControllerRole1Home.performClearSearch(); });
		
		setupErrorLabelUI(label_SearchError, 350, 20, 137);
		
		listView_Lessons.setLayoutX(20);
		listView_Lessons.setLayoutY(160);
		listView_Lessons.setPrefSize(350, 290);
		// Clicking a row loads that lesson into the form (US-R3).  The Controller ignores the -1
		// that fires whenever the list is cleared or refilled.
		listView_Lessons.getSelectionModel().selectedIndexProperty().addListener(
				(observable, oldValue, newValue) -> 
				{ControllerRole1Home.performSelectLesson(newValue.intValue()); });
		
		setupButtonUI(button_NewLesson, "Dialog", 13, 110, Pos.CENTER, 20, 460);
		button_NewLesson.setOnAction((_) -> {ControllerRole1Home.performNewLesson(); });
		
		setupButtonUI(button_DeleteLesson, "Dialog", 13, 110, Pos.CENTER, 137, 460);
		button_DeleteLesson.setOnAction((_) -> {ControllerRole1Home.performDelete(); });
		
		setupButtonUI(button_AddExperience, "Dialog", 13, 116, Pos.CENTER, 254, 460);
		button_AddExperience.setOnAction((_) -> {ControllerRole1Home.performAddExperience(); });
		
		setupLabelUI(label_ListStatus, "Arial", 12, 350, Pos.BASELINE_LEFT, 20, 497);
		
		// GUI Area 2, right column: the lesson form
		setupLabelUI(label_FormHeading, "Arial", 18, 385, Pos.BASELINE_LEFT, 395, 100);
		label_FormHeading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		
		setupLabelUI(label_TitleCaption, "Arial", 12, 385, Pos.BASELINE_LEFT, 395, 127);
		setupTextFieldUI(text_Title, 385, 395, 143);
		setupErrorLabelUI(label_TitleError, 385, 395, 173);
		
		setupLabelUI(label_DescriptionCaption, "Arial", 12, 385, Pos.BASELINE_LEFT, 395, 191);
		text_Description.setLayoutX(395);
		text_Description.setLayoutY(207);
		text_Description.setPrefSize(385, 75);
		text_Description.setWrapText(true);
		setupErrorLabelUI(label_DescriptionError, 385, 395, 284);
		
		setupLabelUI(label_TagsCaption, "Arial", 12, 205, Pos.BASELINE_LEFT, 395, 302);
		setupTextFieldUI(text_Tags, 205, 395, 318);
		setupErrorLabelUI(label_TagsError, 205, 395, 348);
		
		setupLabelUI(label_CategoryCaption, "Arial", 12, 168, Pos.BASELINE_LEFT, 612, 302);
		// Same "<Select ...>" first item the other ComboBoxes in this project use, so nothing is
		// chosen until the user chooses
		List<String> categories = new ArrayList<String>();
		categories.add("<Select one>");
		for (String c : LessonRecognizer.VALID_CATEGORIES) categories.add(c);
		combobox_Category.setItems(FXCollections.observableArrayList(categories));
		combobox_Category.getSelectionModel().select(0);
		setupComboBoxUI(combobox_Category, "Dialog", 13, 168, 612, 318);
		setupErrorLabelUI(label_CategoryError, 168, 612, 348);
		
		setupButtonUI(button_SaveLesson, "Dialog", 14, 190, Pos.CENTER, 395, 382);
		button_SaveLesson.setOnAction((_) -> {ControllerRole1Home.performSave(); });
		setupLabelUI(label_LockNote, "Arial", 12, 185, Pos.BASELINE_LEFT, 595, 388);
		
		setupLabelUI(label_SaveStatus, "Arial", 13, 385, Pos.BASELINE_LEFT, 395, 414);
		
		setupLabelUI(label_ExperienceHeading, "Arial", 12, 385, Pos.BASELINE_LEFT, 395, 434);
		label_ExperienceHeading.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		text_Experience.setLayoutX(395);
		text_Experience.setLayoutY(451);
		text_Experience.setPrefSize(385, 64);
		text_Experience.setWrapText(true);
		text_Experience.setEditable(false);		// the log is read-only, new entries use the dialog
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((_) -> {ControllerRole1Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> {ControllerRole1Home.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
         theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			text_Search, button_Search, button_ClearSearch, label_SearchError, listView_Lessons,
			button_NewLesson, button_DeleteLesson, button_AddExperience, label_ListStatus,
			label_FormHeading, label_TitleCaption, text_Title, label_TitleError,
			label_DescriptionCaption, text_Description, label_DescriptionError,
			label_TagsCaption, text_Tags, label_TagsError,
			label_CategoryCaption, combobox_Category, label_CategoryError,
			button_SaveLesson, label_LockNote, label_SaveStatus,
			label_ExperienceHeading, text_Experience,
	        line_Separator4, button_Logout, button_Quit);
}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize an error label: small, red, and wrapped so a longer
	 * message drops to a second line instead of running off the page.  (HW2)
	 * 
	 * @param l		The Label object to be initialized
	 * @param w		The width of the Label
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupErrorLabelUI(Label l, double w, double x, double y){
		l.setFont(Font.font("Arial", 12));
		l.setTextFill(Color.RED);
		l.setMinWidth(w);
		l.setMaxWidth(w);
		l.setWrapText(true);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a text field.  (HW2)
	 * 
	 * @param t		The TextField object to be initialized
	 * @param w		The width of the TextField
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupTextFieldUI(TextField t, double w, double x, double y){
		t.setFont(Font.font("Arial", 14));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox, same as the helper of
	 * the same name in ViewMultipleRoleDispatch.  (HW2)
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, 
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setPrefWidth(w);		// also cap the width, otherwise the ComboBox grows past the right margin
		c.setMaxWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}