package lessonTests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import applicationMain.FoundationsMain;
import entityClasses.LessonsLearnedList;
import guiAddExperience.ModelAddExperience;
import guiContributorHome.ModelContributorHome;
import guiCreateLesson.ModelCreateLesson;
import guiLessonDetails.ModelLessonDetails;
import guiTools.LessonResult;
import guiUpdateLesson.ModelUpdateLesson;

/*******
 * <p> Title: ContributorFunctionalTest Class. </p>
 * 
 * <p> Description: The functional tests for the Contributor User Stories.  Each named check
 * ("Create Lesson - Valid Input", "Cannot View Another Contributor's Lesson", and so on) calls
 * the Model of the Contributor page that performs the action (ModelCreateLesson,
 * ModelContributorHome, ModelLessonDetails, ModelUpdateLesson, ModelAddExperience) directly,
 * against the real Foundations Database.  The Models need no GUI, so these checks are repeatable
 * and run without starting JavaFX.</p>
 * 
 * <p> Each group of checks uses its own new username, and every lesson created is deleted at the
 * end, so no test data is left in the database.  The tests cannot run while FoundationsMain is
 * running, because the H2 database file allows only one connection at a time.</p>
 * 
 * <p> Like the testbed automation classes, the class prints every result and then ends with an
 * AssertionError if anything failed, so a failure cannot be missed.  The input validation tests
 * are in inputValidation.LessonRecognizerTest.</p>
 * 
 * <p> This class is carried forward from HW2 so the Phase One refactor can be checked against
 * the same 21 behaviors; the full TP2 test suite, organized by requirement, is produced in
 * Tasks 6 and 7.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2026 </p>
 * 
 * @author  Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2 (as guiRole1.ModelRole1HomeTest)
 * @version 1.01		2026-10-14 TP2: moved to lessonTests and pointed at the five Contributor
 * 							page Models
 *  
 */
public class ContributorFunctionalTest {

	/** How many tests have run so far. */
	private static int numberOfTests = 0;
	/** How many of those tests failed. */
	private static int numberFailed = 0;

	/** Every lesson the tests create, remembered so cleanUp() can delete it at the end. */
	private static List<Integer> createdLessonIds = new ArrayList<Integer>();

	/**********
	 * Default constructor is not used; every member is static.
	 */
	public ContributorFunctionalTest() {
	}

	/**********
	 * Connects to the database, runs every functional test group in CRUD order, removes the
	 * test lessons, and prints the totals.  Ends with an AssertionError if any test failed.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			FoundationsMain.database.connectToDatabase();
		} catch (SQLException e) {
			System.out.println("*** ERROR *** Could not connect to the database.  Is FoundationsMain "
					+ "still running?  " + e.getMessage());
			return;
		}

		System.out.println("Contributor Functional Tests (lessons learned)");
		runCreateTests();
		runReadTests();
		runUpdateTests();
		runExperienceTests();
		runDeleteTests();
		cleanUp();
		FoundationsMain.database.closeConnection();

		// Same footer wording as the testbed automation files (EmailAddressTestAutomation and
		// PasswordEvaluationTestingAutomation)
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + (numberOfTests - numberFailed));
		System.out.println("Number of tests failed: " + numberFailed);
		if (numberFailed > 0)
			throw new AssertionError(numberFailed + " Contributor functional check(s) failed.");
	}

	/**********
	 * Create tests: a valid lesson is saved, and an invalid field blocks the whole save.
	 */
	private static void runCreateTests() {
		System.out.println("\n--- Create ---");
		String user = uniqueUser("createTester");

		LessonResult ok = makeLesson(user, "Valid title", "Technical");
		checkNamed("Create Lesson - Valid Input",
				ok.isSuccess() && ok.getMessage().equals("Lesson created")
				&& ModelContributorHome.viewMyLessons(user).size() == 1,
				"expected success with \"Lesson created\" and the lesson in my list");

		// A good title next to a bad category must not save anything, not even the good title
		LessonResult bad = ModelCreateLesson.createLesson(user, "Another valid title",
				"This description is definitely long enough.", "java", "Marketing");
		checkNamed("Create Lesson - Invalid Input Blocks the Save",
				!bad.isSuccess() && bad.getMessage().equals("Please choose a valid category.")
				&& bad.getFailedField().equals("category")
				&& ModelContributorHome.viewMyLessons(user).size() == 1,
				"expected the category error and still only 1 lesson saved");
	}

	/**********
	 * Read tests: listing, searching, viewing, and the privacy of another Contributor's lessons.
	 */
	private static void runReadTests() {
		System.out.println("\n--- Read ---");
		String userA = uniqueUser("readTesterA");
		String userB = uniqueUser("readTesterB");

		LessonsLearnedList none = ModelContributorHome.viewMyLessons(userA);
		checkNamed("View My Lessons - Empty List", none != null && none.isEmpty(),
				"expected an empty (not null) list for a user with no lessons");

		makeLesson(userA, "Sprint retro notes", "Teamwork");
		LessonResult second = makeLesson(userA, "Deploy pipeline fix", "Technical");
		checkNamed("View My Lessons - Populated List", ModelContributorHome.viewMyLessons(userA).size() == 2,
				"expected exactly 2 lessons after creating 2");

		LessonResult zero = ModelContributorHome.searchMyLessons(userA, "nonexistentword");
		checkNamed("Subset List - Returns Correct Sizes (zero matches)",
				zero.isSuccess() && zero.getList() != null && zero.getList().isEmpty(),
				"expected an empty collection, not null and not an error");
		LessonResult one = ModelContributorHome.searchMyLessons(userA, "Deploy");
		checkNamed("Subset List - Returns Correct Sizes (one match)",
				one.isSuccess() && one.getList().size() == 1,
				"expected exactly 1 match for \"Deploy\"");
		LessonResult many = ModelContributorHome.searchMyLessons(userA, "e");
		checkNamed("Subset List - Returns Correct Sizes (multiple matches)",
				many.isSuccess() && many.getList().size() == 2,
				"expected both lessons to match \"e\"");
		LessonResult emptySearch = ModelContributorHome.searchMyLessons(userA, "");
		checkNamed("Subset List - Empty Search Text Finds Nothing",
				emptySearch.isSuccess() && emptySearch.getList().isEmpty(),
				"expected an empty result, not everything and not an error");

		LessonResult tooLong = ModelContributorHome.searchMyLessons(userA, rep("a", 255));
		checkNamed("Search Input - Invalid Text Rejected",
				!tooLong.isSuccess() && tooLong.getMessage().equals("Search text is too long."),
				"expected \"Search text is too long.\"");

		ModelUpdateLesson.updateLesson(userA, second.getLessonId(), "Deploy pipeline fix",
				"This description is definitely long enough.", "java", "Technical");
		FoundationsMain.database.setLessonLock(second.getLessonId(), "title", true);
		LessonResult locked = ModelLessonDetails.viewLesson(userA, second.getLessonId());
		checkNamed("View Single Lesson - Locked Field Displayed as Read-Only",
				locked.isSuccess() && locked.getLesson().isFieldLocked("title")
				&& !locked.getLesson().isFieldLocked("description"),
				"expected the title locked and the description still editable");

		LessonResult other = ModelLessonDetails.viewLesson(userB, second.getLessonId());
		checkNamed("Cannot View Another Contributor's Lesson",
				!other.isSuccess() && other.getMessage().equals("Lesson not found.")
				&& other.getLesson() == null,
				"expected \"Lesson not found.\" and no lesson content");

		LessonResult missing = ModelLessonDetails.viewLesson(userA, -999);
		LessonResult missingUpdate = ModelUpdateLesson.updateLesson(userA, -999, "Title",
				"This description is definitely long enough.", "", "General");
		LessonResult missingDelete = ModelLessonDetails.deleteLesson(userA, -999, true);
		checkNamed("Select Nonexistent Lesson ID",
				missing.getMessage().equals("Lesson not found.")
				&& missingUpdate.getMessage().equals("Lesson not found.")
				&& missingDelete.getMessage().equals("Lesson not found.")
				&& !missing.isSuccess() && !missingUpdate.isSuccess() && !missingDelete.isSuccess(),
				"expected \"Lesson not found.\" for view, update, and delete");
	}

	/**********
	 * Update tests: valid changes are saved, invalid input and locked fields are refused.
	 */
	private static void runUpdateTests() {
		System.out.println("\n--- Update ---");
		String user = uniqueUser("updateTester");
		String longEnough = "This description is definitely long enough.";
		int id = makeLesson(user, "Original title", "General").getLessonId();

		LessonResult ok = ModelUpdateLesson.updateLesson(user, id, "Updated title", longEnough,
				"java", "General");
		checkNamed("Update Lesson - Valid Change to an Unlocked Field",
				ok.isSuccess() && ok.getMessage().equals("Lesson updated")
				&& ModelLessonDetails.viewLesson(user, id).getLesson().getTitle().equals("Updated title"),
				"expected \"Lesson updated\" and the new title saved");

		FoundationsMain.database.setLessonLock(id, "title", true);
		LessonResult locked = ModelUpdateLesson.updateLesson(user, id, "Sneaky new title", longEnough,
				"java", "General");
		checkNamed("Update Lesson - Rejected Because Field Is Locked",
				!locked.isSuccess() && locked.getMessage().equals("This field is locked and cannot be edited.")
				&& ModelLessonDetails.viewLesson(user, id).getLesson().getTitle().equals("Updated title"),
				"expected the locked-field message and the title unchanged");

		LessonResult invalid = ModelUpdateLesson.updateLesson(user, id, "Updated title",
				rep("A", 5001), "java", "General");
		checkNamed("Update Lesson - Rejected Because New Value Is Invalid",
				!invalid.isSuccess()
				&& invalid.getMessage().equals("Description must be between 10 and 5000 characters.")
				&& ModelLessonDetails.viewLesson(user, id).getLesson().getDescription().equals(longEnough),
				"expected the description-length message and the description unchanged");
	}

	/**********
	 * Add Experience tests: a valid entry is linked to its lesson, an invalid one is refused.
	 */
	private static void runExperienceTests() {
		System.out.println("\n--- Add Experience Information ---");
		String user = uniqueUser("experienceTester");
		int id = makeLesson(user, "Reusable lesson", "Research").getLessonId();

		LessonResult ok = ModelAddExperience.addExperience(user, id, "3.5",
				"Reused the checklist from the original lesson and it saved us time.");
		checkNamed("Add Experience Information - Valid Input",
				ok.isSuccess() && ok.getMessage().equals("Experience added")
				&& ModelLessonDetails.viewLesson(user, id).getLesson().getExperienceEntries().size() == 1,
				"expected \"Experience added\" and 1 entry linked to the lesson");

		LessonResult bad = ModelAddExperience.addExperience(user, id, "-2", "Tried the same approach again.");
		checkNamed("Add Experience Information - Rejected on Invalid Input",
				!bad.isSuccess() && bad.getMessage().equals("Time/effort must be a positive number.")
				&& ModelLessonDetails.viewLesson(user, id).getLesson().getExperienceEntries().size() == 1,
				"expected the time/effort message and still only 1 entry saved");
	}

	/**********
	 * Delete tests: "Yes" deletes, "No" keeps, and a locked lesson or field blocks the delete.
	 */
	private static void runDeleteTests() {
		System.out.println("\n--- Delete ---");
		String user = uniqueUser("deleteTester");

		int idA = makeLesson(user, "Lesson to delete", "General").getLessonId();
		LessonResult yes = ModelLessonDetails.deleteLesson(user, idA, true);
		checkNamed("Delete Lesson - Confirmed Deletion of an Unlocked Lesson",
				yes.isSuccess() && yes.getMessage().equals("Lesson deleted")
				&& !ModelLessonDetails.viewLesson(user, idA).isSuccess(),
				"expected \"Lesson deleted\" and the lesson gone");

		int idB = makeLesson(user, "Lesson to keep", "General").getLessonId();
		LessonResult no = ModelLessonDetails.deleteLesson(user, idB, false);
		checkNamed("Delete Lesson - Declined Confirmation Cancels the Deletion",
				!no.isSuccess() && no.getMessage().isEmpty()
				&& ModelLessonDetails.viewLesson(user, idB).isSuccess(),
				"expected no error message and the lesson still there");

		int idC = makeLesson(user, "Locked lesson", "General").getLessonId();
		FoundationsMain.database.setLessonLock(idC, "entire", true);
		LessonResult locked = ModelLessonDetails.deleteLesson(user, idC, true);
		checkNamed("Delete Lesson - Rejected Because Lesson Is Locked",
				!locked.isSuccess() && locked.getMessage().equals("This lesson is locked and cannot be deleted.")
				&& ModelLessonDetails.viewLesson(user, idC).isSuccess(),
				"expected the locked-lesson message and the lesson still there");

		// A single locked FIELD (not the whole lesson) must also block deleting (US-D1-V)
		int idD = makeLesson(user, "Lesson with one locked field", "General").getLessonId();
		FoundationsMain.database.setLessonLock(idD, "tags", true);
		LessonResult fieldLocked = ModelLessonDetails.deleteLesson(user, idD, true);
		checkNamed("Delete Lesson - Rejected Because a Single Field Is Locked",
				!fieldLocked.isSuccess()
				&& fieldLocked.getMessage().equals("This lesson is locked and cannot be deleted."),
				"expected the locked-lesson message when only one field is locked");
	}



	/**********
	 * Prints one line for a named test, using the same names as Test Cases.pdf.
	 *
	 * @param name			the test name from Test Cases.pdf
	 * @param passed		whether the test passed
	 * @param expectation	what was expected, printed only when the test fails
	 */
	private static void checkNamed(String name, boolean passed, String expectation) {
		numberOfTests++;
		if (!passed) numberFailed++;
		System.out.println(name + " - " + (passed ? "Pass." : "FAIL."));
		if (!passed) System.out.println("        " + expectation);
	}

	/**********
	 * Creates a valid lesson for a test user and remembers its id so cleanUp() can delete it.
	 *
	 * @param user		the test user who owns the lesson
	 * @param title		the lesson title
	 * @param category	the lesson category
	 * @return the Model's result, holding the new lesson id on success
	 */
	private static LessonResult makeLesson(String user, String title, String category) {
		LessonResult result = ModelCreateLesson.createLesson(user, title,
				"This description is definitely long enough.", "java", category);
		if (result.isSuccess()) createdLessonIds.add(result.getLessonId());
		return result;
	}

	/**********
	 * The H2 database is a real file that survives between runs, so a fixed username would still
	 * see lessons left from the last run and throw the counts off.  Adding the nano-time makes
	 * every test user brand new.
	 *
	 * @param base	a readable name for the test group
	 * @return a username no earlier run has used
	 */
	private static String uniqueUser(String base) {
		return "hw2test_" + base + "_" + System.nanoTime();
	}

	/**********
	 * Deletes every lesson these tests created (and their experience rows, through the cascade).
	 * This goes straight to the Database so it also removes the locked ones, which the Model
	 * would refuse to delete.
	 */
	private static void cleanUp() {
		for (int id : createdLessonIds) FoundationsMain.database.deleteLesson(id);
		System.out.println("\nCleaned up " + createdLessonIds.size() + " test lesson(s).");
	}

	/**********
	 * Builds a String by repeating one piece, used for the exact-boundary-length inputs.
	 *
	 * @param piece	the text to repeat
	 * @param times	how many times to repeat it
	 * @return the repeated text
	 */
	private static String rep(String piece, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) sb.append(piece);
		return sb.toString();
	}
}
