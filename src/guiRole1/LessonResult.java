package guiRole1;

import entityClasses.LessonCollection;
import entityClasses.LessonLearned;

/*******
 * <p> Title: LessonResult Class </p>
 * 
 * <p> Description: Every operation in ModelRole1Home hands back one of these instead of a bare
 * true/false.  The reason is that Test Cases.pdf wants a specific, helpful error message for each
 * failure, and a boolean can't carry a message.  This bundles up: did it work, what to tell the
 * user, which field caused the problem (so the screen can show the message under that field like
 * InputValidation.pdf says), and whatever data the caller asked for.</p>
 * 
 * <p> The constructor is private on purpose.  Callers use passed(), passedCollection(), or
 * failed(), so it is always obvious at the call site which one they mean.</p>
 * 
 * <p> Copyright: ASU CSE 360 HW2 © 2026 </p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 */

public class LessonResult {
	
	private boolean success;
	private String message = "";
	private String failedField = "";
	private int lessonId = -1;
	private LessonLearned lesson = null;
	private LessonCollection collection = null;

	private LessonResult() {
		
	}

	
	/*****
	 * <p> Method: LessonResult passed(String message, int lessonId, LessonLearned lesson) </p>
	 * 
	 * <p> Description: A successful result about one lesson. </p>
	 * 
	 * @param message the confirmation text for the user (e.g. "Lesson created")
	 * @param lessonId the id of the lesson involved
	 * @param lesson the lesson itself, or null if there isn't one to hand back
	 * @return a result whose isSuccess() is true
	 */
	public static LessonResult passed(String message, int lessonId, LessonLearned lesson) {
		LessonResult r = new LessonResult();
		r.success = true;
		r.message = message;
		r.lessonId = lessonId;
		r.lesson = lesson;
		return r;
	}

	
	/*****
	 * <p> Method: LessonResult passedCollection(String message, LessonCollection collection) </p>
	 * 
	 * <p> Description: A successful result that carries a whole collection (used by search). </p>
	 * 
	 * @param message the text for the user
	 * @param collection the collection to hand back
	 * @return a result whose isSuccess() is true
	 */
	public static LessonResult passedCollection(String message, LessonCollection collection) {
		LessonResult r = new LessonResult();
		r.success = true;
		r.message = message;
		r.collection = collection;
		return r;
	}

	
	/*****
	 * <p> Method: LessonResult failed(String message, String failedField) </p>
	 * 
	 * <p> Description: A failed result.  The message should be one of the exact messages in
	 * Test Cases.pdf.  An empty message is used on purpose for a declined delete confirmation,
	 * since backing out isn't an error. </p>
	 * 
	 * @param message the specific error message
	 * @param failedField "title", "description", "tags", "category", "experienceValue",
	 * 		"experienceDescription", or "searchText"; an empty string if the failure isn't about
	 * 		one field (like "Lesson not found.")
	 * @return a result whose isSuccess() is false
	 */
	public static LessonResult failed(String message, String failedField) {
		LessonResult r = new LessonResult();
		r.success = false;
		r.message = message;
		r.failedField = failedField;
		return r;
	}

	
	public boolean isSuccess() { return success; }
	public String getMessage() { return message; }
	public String getFailedField() { return failedField; }
	public int getLessonId() { return lessonId; }
	public LessonLearned getLesson() { return lesson; }
	public LessonCollection getCollection() { return collection; }
}
