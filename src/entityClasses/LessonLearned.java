package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: LessonLearned Class </p>
 * 
 * <p> Description: This entity class is one lesson learned written by a Contributor.  It is the
 * "lessons learned class" the HW2 instructions ask for.  It holds the "core information" from
 * User Story US-C1 (title, description, tags, category), the lock flags that a future Curator
 * will be able to set (US-R3, US-U1-V, US-D1-V), and the experience entries that get added each
 * time somebody tries to reuse the lesson (US-U2).</p>
 * 
 * <p> I copied the approach of entityClasses.User: private attributes with getters and setters
 * and NO database code in here.  Reading and writing rows is the Database class's job, this is
 * just a plain data holder.</p>
 * 
 * <p> Copyright: ASU CSE 360 HW2 © 2026 </p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 */

public class LessonLearned {
	
	/*
	 * These are the private attributes for this entity object.  They line up one-to-one with the
	 * columns of the lessonsLearnedDB table.
	 */
	private int id;
	private String ownerUserName;
	private String title;
	private String description;
	private String tags;
	private String category;
	
	// One lock flag per core field plus one for the whole lesson.  US-D1-V says a lesson can't be
	// deleted if it "(or any of its fields)" is locked, so I keep both kinds.  The Contributor can
	// only READ these.  Setting them is the future Curator's job (see Database.setLessonLock).
	private boolean lockedTitle;
	private boolean lockedDescription;
	private boolean lockedTags;
	private boolean lockedCategory;
	private boolean lockedEntire;
	
	private long createdAt;
	private long updatedAt;
	
	// The experience log (US-U2).  It starts empty and the Model fills it in when a single lesson
	// is opened for viewing, so a lesson pulled out of a list doesn't cost an extra query each.
	private List<ExperienceEntry> experienceEntries = new ArrayList<ExperienceEntry>();

	
	/*****
	 * <p> Method: LessonLearned() </p>
	 * 
	 * <p> Description: This default constructor is not used in this system. </p>
	 */
	public LessonLearned() {
		
	}

	
	/*****
	 * <p> Method: LessonLearned(String ownerUserName, String title, String description,
	 * 		String tags, String category) </p>
	 * 
	 * <p> Description: This constructor builds a brand new lesson that has never been saved, so
	 * there is no id yet, nothing is locked (only a Curator locks things), and both timestamps
	 * are "now". </p>
	 * 
	 * @param ownerUserName the username of the Contributor who owns this lesson
	 * 
	 * @param title the lesson's title
	 * 
	 * @param description the lesson's description
	 * 
	 * @param tags comma-separated tags (can be an empty string)
	 * 
	 * @param category one of the fixed categories
	 */
	public LessonLearned(String ownerUserName, String title, String description, String tags,
			String category) {
		this.ownerUserName = ownerUserName;
		this.title = title;
		this.description = description;
		this.tags = tags;
		this.category = category;
		long now = System.currentTimeMillis();
		this.createdAt = now;
		this.updatedAt = now;
	}

	
	/*****
	 * <p> Method: LessonLearned(int id, String ownerUserName, String title, String description,
	 * 		String tags, String category, boolean lockedTitle, boolean lockedDescription,
	 * 		boolean lockedTags, boolean lockedCategory, boolean lockedEntire, long createdAt,
	 * 		long updatedAt) </p>
	 * 
	 * <p> Description: This is the "full" constructor Database.java uses to rebuild a lesson from
	 * a row it just read.  Every attribute is passed in as-is because it already existed. </p>
	 * 
	 * @param id the database primary key
	 * @param ownerUserName the owner's username
	 * @param title the title
	 * @param description the description
	 * @param tags the tags
	 * @param category the category
	 * @param lockedTitle is the title locked
	 * @param lockedDescription is the description locked
	 * @param lockedTags are the tags locked
	 * @param lockedCategory is the category locked
	 * @param lockedEntire is the whole lesson locked
	 * @param createdAt epoch milliseconds when it was created
	 * @param updatedAt epoch milliseconds when it was last updated
	 */
	public LessonLearned(int id, String ownerUserName, String title, String description,
			String tags, String category, boolean lockedTitle, boolean lockedDescription,
			boolean lockedTags, boolean lockedCategory, boolean lockedEntire, long createdAt,
			long updatedAt) {
		this.id = id;
		this.ownerUserName = ownerUserName;
		this.title = title;
		this.description = description;
		this.tags = tags;
		this.category = category;
		this.lockedTitle = lockedTitle;
		this.lockedDescription = lockedDescription;
		this.lockedTags = lockedTags;
		this.lockedCategory = lockedCategory;
		this.lockedEntire = lockedEntire;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	
	/*****
	 * <p> Method: boolean isFieldLocked(String field) </p>
	 * 
	 * <p> Description: Can this one field be edited right now?  A field counts as locked if its
	 * own flag is set OR the whole lesson is locked.  I check both in here so no caller has to
	 * remember to check both.  Test Case "View Single Lesson - Locked Field Displayed as
	 * Read-Only" relies on this. </p>
	 * 
	 * @param field "title", "description", "tags", or "category" (case-sensitive on purpose so a
	 * 		typo can't quietly come back as "not locked")
	 * 
	 * @return true if the field can't be edited right now
	 */
	public boolean isFieldLocked(String field) {
		if (lockedEntire) return true;
		switch (field) {
			case "title": return lockedTitle;
			case "description": return lockedDescription;
			case "tags": return lockedTags;
			case "category": return lockedCategory;
			default: return false;
		}
	}

	
	/*****
	 * <p> Method: boolean isLocked() </p>
	 * 
	 * <p> Description: Can this lesson be deleted right now?  Per US-D1-V it can NOT if the whole
	 * lesson is locked or if any single field is locked. </p>
	 * 
	 * @return true if the lesson is protected from deletion
	 */
	public boolean isLocked() {
		return lockedEntire || lockedTitle || lockedDescription || lockedTags || lockedCategory;
	}

	
	/*
	 * Getters and setters, same pattern as User.java.
	 */
	public int getId() { return id; }
	public void setId(int i) { id = i; }
	public String getOwnerUserName() { return ownerUserName; }
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }
	public String getTags() { return tags; }
	public void setTags(String s) { tags = s; }
	public String getCategory() { return category; }
	public void setCategory(String s) { category = s; }
	public boolean isLockedTitle() { return lockedTitle; }
	public boolean isLockedDescription() { return lockedDescription; }
	public boolean isLockedTags() { return lockedTags; }
	public boolean isLockedCategory() { return lockedCategory; }
	public boolean isLockedEntire() { return lockedEntire; }
	public long getCreatedAt() { return createdAt; }
	public long getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(long t) { updatedAt = t; }
	public List<ExperienceEntry> getExperienceEntries() { return experienceEntries; }
	public void setExperienceEntries(List<ExperienceEntry> e) { experienceEntries = e; }
}
