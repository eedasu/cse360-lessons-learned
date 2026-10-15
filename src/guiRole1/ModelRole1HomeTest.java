package guiRole1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import applicationMain.FoundationsMain;
import entityClasses.LessonCollection;

/*******
 * <p> Title: ModelRole1HomeTest Class. </p>
 * 
 * <p> Description: This class runs the "Manual / Functional Tests" from Test Cases.pdf, the 17
 * named tests (Create Lesson - Valid Input and so on).  In TP1 the manual tests were click-through
 * procedures, but ModelRole1Home doesn't need the GUI, so these run it directly against the real
 * Foundations Database.  That makes them repeatable and gives Screencast 6 real output.</p>
 * 
 * <p> Each test uses its own brand new username, and every lesson it creates is deleted at the
 * end, so it doesn't leave test data in the real database.  It can't run while FoundationsMain
 * is running, because the H2 database file only allows one connection.</p>
 * 
 * <p> Like the other test class, it prints every result, then ends with an AssertionError if
 * anything failed.  The input validation tests are in applicationMain.LessonRecognizerTest.</p>
 * 
 * @author  Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 *  
 */
public class ModelRole1HomeTest {

	private static int numberOfTests = 0;
	private static int numberFailed = 0;
	
	// Every lesson the tests create is remembered here so cleanUp() can delete it at the end
	private static List<Integer> createdLessonIds = new ArrayList<Integer>();

	public static void main(String[] args) {
		try {
			FoundationsMain.database.connectToDatabase();
		} catch (SQLException e) {
			System.out.println("*** ERROR *** Could not connect to the database.  Is FoundationsMain "
					+ "still running?  " + e.getMessage());
			return;
		}
		
		System.out.println("HW2 Functional Tests - ModelRole1Home (Contributor lessons learned)");
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
			throw new AssertionError(numberFailed + " ModelRole1Home check(s) failed.");
	}

	private static void runCreateTests() {
		System.out.println("\n--- Create ---");
		String user = uniqueUser("createTester");
		
		LessonResult ok = makeLesson(user, "Valid title", "Technical");
		checkNamed("Create Lesson - Valid Input", 
				ok.isSuccess() && ok.getMessage().equals("Lesson created")
				&& ModelRole1Home.viewMyLessons(user).size() == 1, 
				"expected success with \"Lesson created\" and the lesson in my list");
		
		// A good title next to a bad category must not save anything, not even the good title
		LessonResult bad = ModelRole1Home.createLesson(user, "Another valid title",
				"This description is definitely long enough.", "java", "Marketing");
		checkNamed("Create Lesson - Invalid Input Blocks the Save", 
				!bad.isSuccess() && bad.getMessage().equals("Please choose a valid category.")
				&& bad.getFailedField().equals("category")
				&& ModelRole1Home.viewMyLessons(user).size() == 1,
				"expected the category error and still only 1 lesson saved");
	}

	private static void runReadTests() {
		System.out.println("\n--- Read ---");
		String userA = uniqueUser("readTesterA");
		String userB = uniqueUser("readTesterB");
		
		LessonCollection none = ModelRole1Home.viewMyLessons(userA);
		checkNamed("View My Lessons - Empty List", none != null && none.isEmpty(),
				"expected an empty (not null) list for a user with no lessons");
		
		makeLesson(userA, "Sprint retro notes", "Teamwork");
		LessonResult second = makeLesson(userA, "Deploy pipeline fix", "Technical");
		checkNamed("View My Lessons - Populated List", ModelRole1Home.viewMyLessons(userA).size() == 2,
				"expected exactly 2 lessons after creating 2");
		
		LessonResult zero = ModelRole1Home.searchMyLessons(userA, "nonexistentword");
		checkNamed("Subset List - Returns Correct Sizes (zero matches)", 
				zero.isSuccess() && zero.getCollection() != null && zero.getCollection().isEmpty(),
				"expected an empty collection, not null and not an error");
		LessonResult one = ModelRole1Home.searchMyLessons(userA, "Deploy");
		checkNamed("Subset List - Returns Correct Sizes (one match)", 
				one.isSuccess() && one.getCollection().size() == 1,
				"expected exactly 1 match for \"Deploy\"");
		LessonResult many = ModelRole1Home.searchMyLessons(userA, "e");
		checkNamed("Subset List - Returns Correct Sizes (multiple matches)", 
				many.isSuccess() && many.getCollection().size() == 2,
				"expected both lessons to match \"e\"");
		LessonResult emptySearch = ModelRole1Home.searchMyLessons(userA, "");
		checkNamed("Subset List - Empty Search Text Finds Nothing", 
				emptySearch.isSuccess() && emptySearch.getCollection().isEmpty(),
				"expected an empty result, not everything and not an error");
		
		LessonResult tooLong = ModelRole1Home.searchMyLessons(userA, rep("a", 255));
		checkNamed("Search Input - Invalid Text Rejected", 
				!tooLong.isSuccess() && tooLong.getMessage().equals("Search text is too long."),
				"expected \"Search text is too long.\"");
		
		ModelRole1Home.updateLesson(userA, second.getLessonId(), "Deploy pipeline fix", 
				"This description is definitely long enough.", "java", "Technical");
		FoundationsMain.database.setLessonLock(second.getLessonId(), "title", true);
		LessonResult locked = ModelRole1Home.viewLesson(userA, second.getLessonId());
		checkNamed("View Single Lesson - Locked Field Displayed as Read-Only", 
				locked.isSuccess() && locked.getLesson().isFieldLocked("title")
				&& !locked.getLesson().isFieldLocked("description"),
				"expected the title locked and the description still editable");
		
		LessonResult other = ModelRole1Home.viewLesson(userB, second.getLessonId());
		checkNamed("Cannot View Another Contributor's Lesson", 
				!other.isSuccess() && other.getMessage().equals("Lesson not found.")
				&& other.getLesson() == null,
				"expected \"Lesson not found.\" and no lesson content");
		
		LessonResult missing = ModelRole1Home.viewLesson(userA, -999);
		LessonResult missingUpdate = ModelRole1Home.updateLesson(userA, -999, "Title", 
				"This description is definitely long enough.", "", "General");
		LessonResult missingDelete = ModelRole1Home.deleteLesson(userA, -999, true);
		checkNamed("Select Nonexistent Lesson ID", 
				missing.getMessage().equals("Lesson not found.") 
				&& missingUpdate.getMessage().equals("Lesson not found.")
				&& missingDelete.getMessage().equals("Lesson not found.")
				&& !missing.isSuccess() && !missingUpdate.isSuccess() && !missingDelete.isSuccess(),
				"expected \"Lesson not found.\" for view, update, and delete");
	}

	private static void runUpdateTests() {
		System.out.println("\n--- Update ---");
		String user = uniqueUser("updateTester");
		String longEnough = "This description is definitely long enough.";
		int id = makeLesson(user, "Original title", "General").getLessonId();
		
		LessonResult ok = ModelRole1Home.updateLesson(user, id, "Updated title", longEnough, 
				"java", "General");
		checkNamed("Update Lesson - Valid Change to an Unlocked Field", 
				ok.isSuccess() && ok.getMessage().equals("Lesson updated")
				&& ModelRole1Home.viewLesson(user, id).getLesson().getTitle().equals("Updated title"),
				"expected \"Lesson updated\" and the new title saved");
		
		FoundationsMain.database.setLessonLock(id, "title", true);
		LessonResult locked = ModelRole1Home.updateLesson(user, id, "Sneaky new title", longEnough, 
				"java", "General");
		checkNamed("Update Lesson - Rejected Because Field Is Locked", 
				!locked.isSuccess() && locked.getMessage().equals("This field is locked and cannot be edited.")
				&& ModelRole1Home.viewLesson(user, id).getLesson().getTitle().equals("Updated title"),
				"expected the locked-field message and the title unchanged");
		
		LessonResult invalid = ModelRole1Home.updateLesson(user, id, "Updated title", 
				rep("A", 5001), "java", "General");
		checkNamed("Update Lesson - Rejected Because New Value Is Invalid", 
				!invalid.isSuccess() 
				&& invalid.getMessage().equals("Description must be between 10 and 5000 characters.")
				&& ModelRole1Home.viewLesson(user, id).getLesson().getDescription().equals(longEnough),
				"expected the description-length message and the description unchanged");
	}

	private static void runExperienceTests() {
		System.out.println("\n--- Add Experience Information ---");
		String user = uniqueUser("experienceTester");
		int id = makeLesson(user, "Reusable lesson", "Research").getLessonId();
		
		LessonResult ok = ModelRole1Home.addExperience(user, id, "3.5", 
				"Reused the checklist from the original lesson and it saved us time.");
		checkNamed("Add Experience Information - Valid Input", 
				ok.isSuccess() && ok.getMessage().equals("Experience added")
				&& ModelRole1Home.viewLesson(user, id).getLesson().getExperienceEntries().size() == 1,
				"expected \"Experience added\" and 1 entry linked to the lesson");
		
		LessonResult bad = ModelRole1Home.addExperience(user, id, "-2", "Tried the same approach again.");
		checkNamed("Add Experience Information - Rejected on Invalid Input", 
				!bad.isSuccess() && bad.getMessage().equals("Time/effort must be a positive number.")
				&& ModelRole1Home.viewLesson(user, id).getLesson().getExperienceEntries().size() == 1,
				"expected the time/effort message and still only 1 entry saved");
	}

	private static void runDeleteTests() {
		System.out.println("\n--- Delete ---");
		String user = uniqueUser("deleteTester");
		
		int idA = makeLesson(user, "Lesson to delete", "General").getLessonId();
		LessonResult yes = ModelRole1Home.deleteLesson(user, idA, true);
		checkNamed("Delete Lesson - Confirmed Deletion of an Unlocked Lesson", 
				yes.isSuccess() && yes.getMessage().equals("Lesson deleted")
				&& !ModelRole1Home.viewLesson(user, idA).isSuccess(),
				"expected \"Lesson deleted\" and the lesson gone");
		
		int idB = makeLesson(user, "Lesson to keep", "General").getLessonId();
		LessonResult no = ModelRole1Home.deleteLesson(user, idB, false);
		checkNamed("Delete Lesson - Declined Confirmation Cancels the Deletion", 
				!no.isSuccess() && no.getMessage().isEmpty()
				&& ModelRole1Home.viewLesson(user, idB).isSuccess(),
				"expected no error message and the lesson still there");
		
		int idC = makeLesson(user, "Locked lesson", "General").getLessonId();
		FoundationsMain.database.setLessonLock(idC, "entire", true);
		LessonResult locked = ModelRole1Home.deleteLesson(user, idC, true);
		checkNamed("Delete Lesson - Rejected Because Lesson Is Locked", 
				!locked.isSuccess() && locked.getMessage().equals("This lesson is locked and cannot be deleted.")
				&& ModelRole1Home.viewLesson(user, idC).isSuccess(),
				"expected the locked-lesson message and the lesson still there");
		
		// A single locked FIELD (not the whole lesson) must also block deleting (US-D1-V)
		int idD = makeLesson(user, "Lesson with one locked field", "General").getLessonId();
		FoundationsMain.database.setLessonLock(idD, "tags", true);
		LessonResult fieldLocked = ModelRole1Home.deleteLesson(user, idD, true);
		checkNamed("Delete Lesson - Rejected Because a Single Field Is Locked", 
				!fieldLocked.isSuccess() 
				&& fieldLocked.getMessage().equals("This lesson is locked and cannot be deleted."),
				"expected the locked-lesson message when only one field is locked");
	}

	

	/**********
	 * Prints one line for a named test, using the same names as Test Cases.pdf.
	 */
	private static void checkNamed(String name, boolean passed, String expectation) {
		numberOfTests++;
		if (!passed) numberFailed++;
		System.out.println(name + " - " + (passed ? "Pass." : "FAIL."));
		if (!passed) System.out.println("        " + expectation);
	}

	/**********
	 * Creates a valid lesson for a test user and remembers its id so cleanUp() can delete it.
	 */
	private static LessonResult makeLesson(String user, String title, String category) {
		LessonResult result = ModelRole1Home.createLesson(user, title,
				"This description is definitely long enough.", "java", category);
		if (result.isSuccess()) createdLessonIds.add(result.getLessonId());
		return result;
	}

	/**********
	 * The H2 database is a real file that survives between runs, so a fixed username would still
	 * see lessons left from the last run and throw the counts off.  Adding the nano-time makes
	 * every test user brand new.
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
	 */
	private static String rep(String piece, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) sb.append(piece);
		return sb.toString();
	}
}
