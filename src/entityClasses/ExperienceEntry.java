package entityClasses;

/*******
 * <p> Title: ExperienceEntry Class </p>
 * 
 * <p> Description: This entity class is one "attempt to leverage a lesson" from User Story
 * US-U2: somebody comes back later and records how long it took and what they did.  Each entry
 * belongs to exactly one LessonLearned.  It only stores that lesson's id instead of a reference
 * to the LessonLearned object, so the two classes don't need to know each other's internals.</p>
 * 
 * <p> Copyright: ASU CSE 360 HW2 © 2026 </p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 */

public class ExperienceEntry {
	
	private int id;
	private int lessonId;
	private double timeEffortHours;
	private String description;
	private long createdAt;

	
	/*****
	 * <p> Method: ExperienceEntry() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public ExperienceEntry() {
		
	}

	
	/*****
	 * <p> Method: ExperienceEntry(int lessonId, double timeEffortHours, String description) </p>
	 * 
	 * <p> Description: Builds a new entry that hasn't been saved yet.  It does not check that the
	 * hours are positive, because that is the LessonRecognizer's job.  This class stores whatever
	 * it is handed. </p>
	 * 
	 * @param lessonId the lesson this entry belongs to
	 * 
	 * @param timeEffortHours how many hours the attempt took
	 * 
	 * @param description what was done and how
	 */
	public ExperienceEntry(int lessonId, double timeEffortHours, String description) {
		this.lessonId = lessonId;
		this.timeEffortHours = timeEffortHours;
		this.description = description;
		this.createdAt = System.currentTimeMillis();
	}

	
	/*****
	 * <p> Method: ExperienceEntry(int id, int lessonId, double timeEffortHours,
	 * 		String description, long createdAt) </p>
	 * 
	 * <p> Description: The "full" constructor Database.getExperienceForLesson() uses to rebuild
	 * an entry from a row it just read. </p>
	 * 
	 * @param id the database primary key
	 * @param lessonId the lesson this entry belongs to
	 * @param timeEffortHours how many hours the attempt took
	 * @param description what was done and how
	 * @param createdAt epoch milliseconds when it was recorded
	 */
	public ExperienceEntry(int id, int lessonId, double timeEffortHours, String description,
			long createdAt) {
		this.id = id;
		this.lessonId = lessonId;
		this.timeEffortHours = timeEffortHours;
		this.description = description;
		this.createdAt = createdAt;
	}

	
	public int getId() { return id; }
	public int getLessonId() { return lessonId; }
	public double getTimeEffortHours() { return timeEffortHours; }
	public String getDescription() { return description; }
	public long getCreatedAt() { return createdAt; }
}
