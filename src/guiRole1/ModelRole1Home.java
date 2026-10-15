package guiRole1;

import java.sql.SQLException;

import inputValidation.LessonRecognizer;
import database.Database;
import entityClasses.ExperienceEntry;
import entityClasses.LessonCollection;
import entityClasses.LessonLearned;

/*******
 * <p> Title: ModelRole1Home Class. </p>
 * 
 * <p> Description: The Role1Home Page Model.  In the Foundations code this class was an empty
 * stub because the page had no data of its own to manipulate.  For HW2, Role1 plays the
 * Contributor role, so this is where the Contributor's Create, Read, Update, and Delete
 * operations on lessons learned live.  That fits the MVC pattern the rest of the project uses:
 * the View draws the widgets, the Controller reacts to them, and the Model does the actual data
 * work and talks to the Foundations Database.</p>
 * 
 * <p> Every operation runs its checks in the same order as the "validation stage" table in
 * InputValidation.pdf:
 * <br> 1. Authorization: does the lesson exist, and does it belong to the person asking?
 * <br> 2. Field validation: is every submitted value valid (LessonRecognizer)?
 * <br> 3. Final write: save everything at once, or nothing.
 * <br> The order really matters.  For example, updateLesson() checks locks BEFORE validating the
 * new value, so a locked field is refused for being locked even if the value was also junk (Test
 * Case "Update Lesson - Rejected Because Field Is Locked").</p>
 * 
 * <p> Unlike the Controller classes, the methods here are public, because the automated tests in
 * lessonTests need to call them directly without starting JavaFX.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter (Contributor CRUD added by Emilia Davey)
 * 
 * @version 1.00		2025-08-15 Initial version
 * @version 1.01		2025-09-13 Updated JavaDoc description
 * @version 1.02		2026-09-29 HW2: Contributor lessons learned CRUD
 */

public class ModelRole1Home {
	
	// Same idea as every View and Controller in this project: use the one shared Database
	// instance from FoundationsMain instead of making a second one.
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	// The exact messages from Test Cases.pdf for the failures that aren't about one field
	private static final String NOT_FOUND = "Lesson not found.";
	private static final String FIELD_LOCKED = "This field is locked and cannot be edited.";
	private static final String LESSON_LOCKED = "This lesson is locked and cannot be deleted.";

	
	/**********
	 * <p> Method: LessonResult createLesson(String requestingUser, String title,
	 * 		String description, String tags, String category) </p>
	 * 
	 * <p> Description: CREATE.  Implements US-C1 and US-C1-V.  All four fields are validated
	 * first and the lesson is only saved if every one of them passes.  This is what makes "Create
	 * Lesson - Invalid Input Blocks the Save" work: a good title does NOT get saved by itself when
	 * the category next to it was bad.</p>
	 * 
	 * @param requestingUser	the username of the Contributor creating the lesson
	 * @param title				the title
	 * @param description		the description
	 * @param tags				comma-separated tags (can be empty)
	 * @param category			one of the fixed categories
	 * @return					on success the new lesson and "Lesson created", otherwise the
	 * 							specific error message and the name of the field that caused it
	 */
	public static LessonResult createLesson(String requestingUser, String title, 
			String description, String tags, String category) {
		
		String error = LessonRecognizer.checkForValidTitle(title);
		if (!error.isEmpty()) return LessonResult.failed(error, "title");
		
		error = LessonRecognizer.checkForValidDescription(description);
		if (!error.isEmpty()) return LessonResult.failed(error, "description");
		
		error = LessonRecognizer.checkForValidTags(tags);
		if (!error.isEmpty()) return LessonResult.failed(error, "tags");
		
		error = LessonRecognizer.checkForValidCategory(category);
		if (!error.isEmpty()) return LessonResult.failed(error, "category");
		
		// Everything passed.  Trim the outer whitespace before storing, like InputValidation.pdf
		// says to for free text.
		LessonLearned lesson = new LessonLearned(requestingUser, title.trim(), 
				description.trim(), (tags == null) ? "" : tags.trim(), category);
		try {
			int newId = theDatabase.createLesson(lesson);
			lesson.setId(newId);
			return LessonResult.passed("Lesson created", newId, lesson);
		} catch (SQLException e) {
			// ControllerFirstAdmin just prints the error and calls System.exit(0) when register()
			// fails.  I didn't want one failed lesson save to close the whole application, so I
			// return a normal failure and let the Contributor try again.
			System.err.println("*** ERROR *** Could not save a lesson: " + e.getMessage());
			return LessonResult.failed("Could not save the lesson due to a database error.", "");
		}
	}

	
	/**********
	 * <p> Method: LessonCollection viewMyLessons(String requestingUser) </p>
	 * 
	 * <p> Description: READ (list).  Implements US-R1 and US-R4.  It can't really fail (a
	 * Contributor with no lessons just gets an empty collection), so it returns the collection
	 * directly instead of a LessonResult.</p>
	 * 
	 * @param requestingUser	whose lessons to list
	 * @return					every lesson that user owns; empty, never null, if there are none
	 */
	public static LessonCollection viewMyLessons(String requestingUser) {
		return theDatabase.getLessonsByOwner(requestingUser);
	}

	
	/**********
	 * <p> Method: LessonResult searchMyLessons(String requestingUser, String searchText) </p>
	 * 
	 * <p> Description: READ (subset).  Implements US-R2 and US-R2-V.  It is separate from
	 * viewMyLessons() because search text needs its own validation.  Only the requesting user's
	 * own lessons are ever searched, so the search can't leak somebody else's lesson.</p>
	 * 
	 * @param requestingUser	whose lessons to search
	 * @param searchText		what to look for in the titles; empty is valid but finds nothing
	 * @return					on success the matching collection (possibly empty), otherwise the
	 * 							specific search-text error message
	 */
	public static LessonResult searchMyLessons(String requestingUser, String searchText) {
		String error = LessonRecognizer.checkForValidSearchText(searchText);
		if (!error.isEmpty()) return LessonResult.failed(error, "searchText");
		
		LessonCollection matches = theDatabase.getLessonsByOwner(requestingUser)
				.filterByTitleContains(searchText);
		return LessonResult.passedCollection("Search complete", matches);
	}

	
	/**********
	 * <p> Method: LessonResult viewLesson(String requestingUser, int lessonId) </p>
	 * 
	 * <p> Description: READ (one).  Implements US-R3, US-R3-V, and US-R4.  "Doesn't exist" and
	 * "belongs to somebody else" both give the same "Lesson not found." on purpose.  If they
	 * gave different messages, a Contributor guessing ids could learn which ids other people are
	 * using, which would break US-R4's privacy promise (Test Case "Cannot View Another
	 * Contributor's Lesson").</p>
	 * 
	 * @param requestingUser	who is asking
	 * @param lessonId			the lesson they want to see
	 * @return					on success the lesson with its experience entries loaded,
	 * 							otherwise "Lesson not found."
	 */
	public static LessonResult viewLesson(String requestingUser, int lessonId) {
		LessonLearned lesson = findOwnedLesson(requestingUser, lessonId);
		if (lesson == null) return LessonResult.failed(NOT_FOUND, "");
		
		lesson.setExperienceEntries(theDatabase.getExperienceForLesson(lessonId));
		return LessonResult.passed("", lessonId, lesson);
	}

	
	/**********
	 * <p> Method: LessonResult updateLesson(String requestingUser, int lessonId, String title,
	 * 		String description, String tags, String category) </p>
	 * 
	 * <p> Description: UPDATE.  Implements US-U1 and US-U1-V.  Order of checks:
	 * <br> 1. The lesson exists and belongs to requestingUser.
	 * <br> 2. A locked field may not be CHANGED.  If the submitted value is the same as what is
	 * already stored, that's fine, because the screen submits every field even when only one was
	 * edited.
	 * <br> 3. Every unlocked field's new value passes its recognizer.
	 * <br> 4. One UPDATE statement saves everything, or nothing does.</p>
	 * 
	 * @param requestingUser	who is making the change
	 * @param lessonId			the lesson to change
	 * @param title				the new title
	 * @param description		the new description
	 * @param tags				the new tags
	 * @param category			the new category
	 * @return					on success "Lesson updated", otherwise the specific error message
	 */
	public static LessonResult updateLesson(String requestingUser, int lessonId, String title,
			String description, String tags, String category) {
		
		LessonLearned lesson = findOwnedLesson(requestingUser, lessonId);
		if (lesson == null) return LessonResult.failed(NOT_FOUND, "");
		
		String newTitle = (title == null) ? "" : title.trim();
		String newDescription = (description == null) ? "" : description.trim();
		String newTags = (tags == null) ? "" : tags.trim();
		String newCategory = (category == null) ? "" : category;
		
		// Lock check first, before validation (see the class comment)
		if (lesson.isFieldLocked("title") && !newTitle.equals(lesson.getTitle()))
			return LessonResult.failed(FIELD_LOCKED, "title");
		if (lesson.isFieldLocked("description") && !newDescription.equals(lesson.getDescription()))
			return LessonResult.failed(FIELD_LOCKED, "description");
		if (lesson.isFieldLocked("tags") && !newTags.equals(lesson.getTags()))
			return LessonResult.failed(FIELD_LOCKED, "tags");
		if (lesson.isFieldLocked("category") && !newCategory.equals(lesson.getCategory()))
			return LessonResult.failed(FIELD_LOCKED, "category");
		
		// Validate only the unlocked fields.  A locked field was just proven unchanged, so it is
		// whatever was already stored and already valid.
		String error;
		if (!lesson.isFieldLocked("title")) {
			error = LessonRecognizer.checkForValidTitle(title);
			if (!error.isEmpty()) return LessonResult.failed(error, "title");
		}
		if (!lesson.isFieldLocked("description")) {
			error = LessonRecognizer.checkForValidDescription(description);
			if (!error.isEmpty()) return LessonResult.failed(error, "description");
		}
		if (!lesson.isFieldLocked("tags")) {
			error = LessonRecognizer.checkForValidTags(tags);
			if (!error.isEmpty()) return LessonResult.failed(error, "tags");
		}
		if (!lesson.isFieldLocked("category")) {
			error = LessonRecognizer.checkForValidCategory(category);
			if (!error.isEmpty()) return LessonResult.failed(error, "category");
		}
		
		// Every check passed, so change the object and save all four fields in one statement
		lesson.setTitle(newTitle);
		lesson.setDescription(newDescription);
		lesson.setTags(newTags);
		lesson.setCategory(newCategory);
		lesson.setUpdatedAt(System.currentTimeMillis());
		if (!theDatabase.updateLesson(lesson)) return LessonResult.failed(NOT_FOUND, "");
		
		return LessonResult.passed("Lesson updated", lessonId, lesson);
	}

	
	/**********
	 * <p> Method: LessonResult addExperience(String requestingUser, int lessonId,
	 * 		String timeEffortText, String description) </p>
	 * 
	 * <p> Description: UPDATE (experience).  Implements US-U2 and US-U2-V.  This does NOT look at
	 * the lock flags, on purpose: locks protect the CORE fields from edits, they were never meant
	 * to stop somebody from logging a new attempt at using a lesson.</p>
	 * 
	 * @param requestingUser	who is adding the entry
	 * @param lessonId			the lesson it belongs to
	 * @param timeEffortText	the hours, exactly as typed
	 * @param description		what was done and how
	 * @return					on success "Experience added", otherwise the specific message
	 */
	public static LessonResult addExperience(String requestingUser, int lessonId,
			String timeEffortText, String description) {
		
		LessonLearned lesson = findOwnedLesson(requestingUser, lessonId);
		if (lesson == null) return LessonResult.failed(NOT_FOUND, "");
		
		String error = LessonRecognizer.checkForValidExperienceValue(timeEffortText);
		if (!error.isEmpty()) return LessonResult.failed(error, "experienceValue");
		
		error = LessonRecognizer.checkForValidExperienceDescription(description);
		if (!error.isEmpty()) return LessonResult.failed(error, "experienceDescription");
		
		// The recognizer already proved this text is a plain number, so parsing is safe.  Both
		// fields were checked before anything was written, so a bad one can't leave half an
		// entry behind.
		double hours = Double.parseDouble(timeEffortText.trim());
		theDatabase.createExperience(new ExperienceEntry(lessonId, hours, description.trim()));
		return LessonResult.passed("Experience added", lessonId, lesson);
	}

	
	/**********
	 * <p> Method: LessonResult deleteLesson(String requestingUser, int lessonId,
	 * 		boolean confirmed) </p>
	 * 
	 * <p> Description: DELETE.  Implements US-D1 and US-D1-V.  Order of checks:
	 * <br> 1. The lesson exists and belongs to requestingUser.
	 * <br> 2. It is not locked.  This comes BEFORE the confirmation on purpose, so a locked
	 * lesson never gets as far as an "Are you sure?" prompt (Test Case "Delete Lesson - Rejected
	 * Because Lesson Is Locked").
	 * <br> 3. The Contributor answered "Yes".</p>
	 * 
	 * <p> The screen uses that ordering: it first calls this with confirmed = false.  A
	 * non-empty message means it was refused (not found or locked) and is shown as an error.  An
	 * EMPTY message means "this can be deleted, I'm just waiting for a Yes", so the screen shows
	 * its "Are you sure?" dialog and, if the answer is Yes, calls again with confirmed = true.
	 * </p>
	 * 
	 * @param requestingUser	who is deleting
	 * @param lessonId			the lesson to delete
	 * @param confirmed			true only if the Contributor answered "Yes"
	 * @return					on success "Lesson deleted".  If confirmed is false and the lesson
	 * 							is deletable, a failed result with an EMPTY message (backing out
	 * 							isn't an error).  Otherwise the specific refusal message.
	 */
	public static LessonResult deleteLesson(String requestingUser, int lessonId, 
			boolean confirmed) {
		
		LessonLearned lesson = findOwnedLesson(requestingUser, lessonId);
		if (lesson == null) return LessonResult.failed(NOT_FOUND, "");
		
		if (lesson.isLocked()) return LessonResult.failed(LESSON_LOCKED, "");
		
		if (!confirmed) return LessonResult.failed("", "");
		
		if (!theDatabase.deleteLesson(lessonId)) return LessonResult.failed(NOT_FOUND, "");
		return LessonResult.passed("Lesson deleted", lessonId, null);
	}

	
	/**********
	 * <p> Method: LessonLearned findOwnedLesson(String requestingUser, int lessonId) </p>
	 * 
	 * <p> Description: Private helper for step 1 of every operation above.  It returns the lesson
	 * only if it exists AND belongs to requestingUser, otherwise null.  Having this in one place
	 * means the ownership rule can't get forgotten in one of the operations.</p>
	 * 
	 * @param requestingUser	who is asking
	 * @param lessonId			the lesson they want
	 * @return					the lesson, or null if it is missing or not theirs
	 */
	private static LessonLearned findOwnedLesson(String requestingUser, int lessonId) {
		LessonLearned lesson = theDatabase.getLessonById(lessonId);
		if (lesson == null || !lesson.getOwnerUserName().equals(requestingUser)) return null;
		return lesson;
	}
}