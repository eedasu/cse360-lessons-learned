package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: LessonCollection Class </p>
 * 
 * <p> Description: This is the second class the HW2 instructions ask for: "a class that supports
 * storing all current lessons learned as well as any subset of them (e.g., a subset reflecting
 * the results of a search)".  A subset can be empty, can have one lesson, or can have many, with
 * no upper limit, so it is backed by an ArrayList.  User Story US-R2 and the Test Case "Subset
 * List - Returns Correct Sizes" check all three sizes.</p>
 * 
 * <p> The filter methods never change the collection they are called on.  They hand back a NEW
 * LessonCollection, so filtering "all my lessons" can't accidentally throw lessons away.</p>
 * 
 * <p> Copyright: ASU CSE 360 HW2 © 2026 </p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 */

public class LessonCollection {
	
	private List<LessonLearned> lessons;

	
	/*****
	 * <p> Method: LessonCollection() </p>
	 * 
	 * <p> Description: Builds an empty collection.  This is what a contributor with zero lessons
	 * (US-R1) or a search that matched nothing (US-R2) gets: an empty collection, never null. </p>
	 */
	public LessonCollection() {
		lessons = new ArrayList<LessonLearned>();
	}

	
	/*****
	 * <p> Method: LessonCollection(List&lt;LessonLearned&gt; lessons) </p>
	 * 
	 * <p> Description: Builds a collection around an existing list, e.g. right after the
	 * Database reads a batch of rows. </p>
	 * 
	 * @param lessons the lessons to hold; null is treated like an empty list so nothing
	 * 		downstream has to null-check this class
	 */
	public LessonCollection(List<LessonLearned> lessons) {
		this.lessons = (lessons != null) ? lessons : new ArrayList<LessonLearned>();
	}

	
	/*****
	 * <p> Method: int size() </p>
	 * 
	 * <p> Description: How many lessons are in this collection. </p>
	 * 
	 * @return the number of lessons held
	 */
	public int size() { return lessons.size(); }

	
	/*****
	 * <p> Method: boolean isEmpty() </p>
	 * 
	 * <p> Description: Does this collection have zero lessons? </p>
	 * 
	 * @return true if it is empty
	 */
	public boolean isEmpty() { return lessons.isEmpty(); }

	
	/*****
	 * <p> Method: List&lt;LessonLearned&gt; getLessons() </p>
	 * 
	 * <p> Description: The lessons in this collection.  Both constructors guarantee the list is a
	 * real list, so this never returns null, even for an empty subset. </p>
	 * 
	 * @return the list of lessons, never null
	 */
	public List<LessonLearned> getLessons() { return lessons; }

	
	/*****
	 * <p> Method: LessonCollection filterByCategory(String category) </p>
	 * 
	 * <p> Description: A new collection with only the lessons whose category matches exactly.
	 * This is a handy general-purpose filter, but the search box does NOT use it. </p>
	 * 
	 * @param category the category to keep
	 * 
	 * @return a new collection (possibly empty)
	 */
	public LessonCollection filterByCategory(String category) {
		List<LessonLearned> matches = new ArrayList<LessonLearned>();
		for (LessonLearned lesson : lessons)
			if (lesson.getCategory().equals(category)) matches.add(lesson);
		return new LessonCollection(matches);
	}

	
	/*****
	 * <p> Method: LessonCollection filterByTag(String tag) </p>
	 * 
	 * <p> Description: A new collection with only the lessons whose tags contain the given text
	 * (case-insensitive).  Like filterByCategory, this is a general-purpose filter. </p>
	 * 
	 * @param tag the tag text to look for
	 * 
	 * @return a new collection (possibly empty)
	 */
	public LessonCollection filterByTag(String tag) {
		List<LessonLearned> matches = new ArrayList<LessonLearned>();
		if (tag == null || tag.trim().isEmpty()) return new LessonCollection(matches);
		String needle = tag.trim().toLowerCase();
		for (LessonLearned lesson : lessons)
			if (lesson.getTags() != null && lesson.getTags().toLowerCase().contains(needle))
				matches.add(lesson);
		return new LessonCollection(matches);
	}

	
	/*****
	 * <p> Method: LessonCollection filterByTitleContains(String text) </p>
	 * 
	 * <p> Description: This is the filter the search box actually uses (US-R2), so it is the one
	 * behind the Search/Filter Text tests and "Subset List - Returns Correct Sizes".</p>
	 * 
	 * <p> One rule in here is a little unusual: an EMPTY (or all-spaces) search gives back an
	 * EMPTY collection, NOT everything.  Java's String.contains("") is true for every string, so
	 * left alone it would match everything.  But our team's InputValidation.pdf says the Users
	 * search box should "return no results on an empty or non-matching search rather than showing
	 * a field error", so I applied that same rule to lessons on purpose.  The Clear button on the
	 * screen is how a user gets the full list back. </p>
	 * 
	 * @param text the text to look for in each title (case-insensitive)
	 * 
	 * @return a new collection with the matches; empty if the text is empty or nothing matched
	 */
	public LessonCollection filterByTitleContains(String text) {
		List<LessonLearned> matches = new ArrayList<LessonLearned>();
		if (text == null || text.trim().isEmpty()) return new LessonCollection(matches);
		String needle = text.trim().toLowerCase();
		for (LessonLearned lesson : lessons)
			if (lesson.getTitle().toLowerCase().contains(needle)) matches.add(lesson);
		return new LessonCollection(matches);
	}
}
