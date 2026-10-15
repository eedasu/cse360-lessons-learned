package inputValidation;

/*******
 * <p> Title: LessonRecognizer Class. </p>
 * 
 * <p> Description: This class recognizes valid input for everything a Contributor can type into a
 * lesson learned: the title, description, tags, category, search text, and the two experience
 * fields.  It works like UserNameRecognizer: every method returns an empty String when the input
 * is valid, and otherwise returns a specific message that can be displayed to the user.  That
 * empty-String-means-valid convention keeps the Model code simple.</p>
 * 
 * <p> The messages are exactly the ones written in my HW2's Test Cases.pdf, because the automated tests
 * compare against them word for word.</p>
 * 
 * <p> Every method checks the cheap things first (blank, then length) and only scans the
 * characters after that, so an oversized input is rejected right away without being walked
 * through, which is the size-limit idea from InputValidation.pdf.</p>
 * 
 * <p> The limits for Title (100) and Search text (254) reuse rules our team already wrote in
 * InputValidation.pdf.  The limits for Description, Tags, the time/effort value, and the
 * experience description are my own proposed values because TP1 has no field like them.  They
 * are marked below.</p>
 * 
 * <p> Copyright: ASU CSE 360 HW2 © 2026 </p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2, modeled on UserNameRecognizer
 *  
 */
public class LessonRecognizer {

	// The categories a lesson is allowed to have.  I borrowed this starting list from the
	// CATEGORIES list in the LessonShare sample app.  The Lessons Learned user stories don't
	// specify categories, so this list is my own choice and is not settled by the team.
	public static final String[] VALID_CATEGORIES = { "General", "Technical", "Teamwork",
			"Project Management", "Communication", "Research", "Career" };

	// The limits from the Field Validation Policy table in Test Cases.pdf
	private static final int TITLE_MAX = 100;				// reused: the team's free-text rule
	private static final int DESCRIPTION_MIN = 10;			// my own proposed value (not team-settled)
	private static final int DESCRIPTION_MAX = 5000;		// my own proposed value (not team-settled)
	private static final int TAG_MAX = 30;					// my own proposed value (not team-settled)
	private static final int TAGS_TOTAL_MAX = 200;			// my own proposed value (not team-settled)
	private static final int SEARCH_MAX = 254;				// reused: the team's Users-search rule
	private static final int EXPERIENCE_VALUE_SIZE_MAX = 20;	// keeps a huge digit string from being parsed
	private static final double EXPERIENCE_VALUE_MAX = 10000;	// my own proposed value (not team-settled)
	private static final int EXPERIENCE_DESCRIPTION_MIN = 5;	// my own proposed value (not team-settled)
	private static final int EXPERIENCE_DESCRIPTION_MAX = 2000;	// my own proposed value (not team-settled)

	/**********
	 * <p> Method: checkForValidTitle(String title) </p>
	 * 
	 * <p> Description: This method checks a title for User Stories US-C1-V and US-U1-V.  A title
	 * must not be blank, must be 100 characters or fewer, and can't contain control characters.
	 * There is no separate minimum beyond "not blank", which is the team's rule for free-text
	 * fields (Test 2 is a one-letter title).  The position in the message is 1-based so it reads
	 * naturally to a person.</p>
	 * 
	 * @param title specifies the title to be checked
	 * 
	 * @return an empty String if the title is valid; otherwise, an error message
	 * 
	 */
	public static String checkForValidTitle(String title) {
		if (title == null || title.trim().isEmpty())
			return "Title is required.";

		if (title.length() > TITLE_MAX)
			return "Title must be " + TITLE_MAX + " characters or fewer.";

		int badIndex = indexOfControlCharacter(title, false);
		if (badIndex >= 0)
			return "Title contains an invalid character at position " + (badIndex + 1) + ".";

		return "";
	}

	/**********
	 * <p> Method: checkForValidDescription(String description) </p>
	 * 
	 * <p> Description: This method checks a description.  Unlike the title, it has a real minimum
	 * (10 characters), because a couple of words doesn't explain a lesson to anybody.  A blank
	 * description is reported as "required" instead of "too short" so the message matches what
	 * the person actually did.  Line breaks are allowed, because a TextArea produces them and
	 * without them nobody could type two paragraphs.</p>
	 * 
	 * @param description specifies the description to be checked
	 * 
	 * @return an empty String if the description is valid; otherwise, an error message
	 * 
	 */
	public static String checkForValidDescription(String description) {
		if (description == null || description.trim().isEmpty())
			return "Description is required.";

		if (description.length() < DESCRIPTION_MIN || description.length() > DESCRIPTION_MAX)
			return "Description must be between " + DESCRIPTION_MIN + " and " + DESCRIPTION_MAX
					+ " characters.";

		int badIndex = indexOfControlCharacter(description, true);
		if (badIndex >= 0)
			return "Description contains an invalid character at position " + (badIndex + 1) + ".";

		return "";
	}

	/**********
	 * <p> Method: checkForValidTags(String tags) </p>
	 * 
	 * <p> Description: This method checks the tags field.  Tags are optional, so an empty String
	 * is valid.  The field can be at most 200 characters, each tag (the pieces between commas)
	 * can be at most 30 characters, and control characters are rejected.  Extra commas such as
	 * "java,,css" are just skipped instead of being an error, since an empty piece isn't really a
	 * tag and Test Cases.pdf doesn't ask for that rule.</p>
	 * 
	 * @param tags specifies the comma-separated tags to be checked
	 * 
	 * @return an empty String if the tags are valid; otherwise, an error message
	 * 
	 */
	public static String checkForValidTags(String tags) {
		if (tags == null || tags.isEmpty())
			return "";

		if (tags.length() > TAGS_TOTAL_MAX)
			return "Tags are too long.";

		if (indexOfControlCharacter(tags, false) >= 0)
			return "Tags contain an invalid character.";

		// trim() so "java, css" isn't penalized for the space after the comma
		for (String tag : tags.split(","))
			if (tag.trim().length() > TAG_MAX)
				return "Tags are too long.";

		return "";
	}

	/**********
	 * <p> Method: checkForValidCategory(String category) </p>
	 * 
	 * <p> Description: This method checks a category.  It must be an exact, case-sensitive match
	 * for one of VALID_CATEGORIES.  This mirrors how InputValidation.pdf handles roles ("accept
	 * only supported application roles").  There are no characters to walk through here, it is
	 * only a lookup in a fixed list.</p>
	 * 
	 * @param category specifies the category to be checked
	 * 
	 * @return an empty String if the category is valid; otherwise, an error message
	 * 
	 */
	public static String checkForValidCategory(String category) {
		if (category != null)
			for (String validCategory : VALID_CATEGORIES)
				if (validCategory.equals(category))
					return "";

		return "Please choose a valid category.";
	}

	/**********
	 * <p> Method: checkForValidSearchText(String searchText) </p>
	 * 
	 * <p> Description: This method checks search text for User Story US-R2-V.  Empty is VALID
	 * here, because per the team's Users-search rule an empty search isn't a field error, it just
	 * finds nothing (LessonCollection.filterByTitleContains makes that happen).  Only text that
	 * is too long or has a control character is rejected.</p>
	 * 
	 * @param searchText specifies the search text to be checked
	 * 
	 * @return an empty String if the search text is valid (even if empty); otherwise, an error
	 * message
	 * 
	 */
	public static String checkForValidSearchText(String searchText) {
		if (searchText == null)
			return "";

		if (searchText.length() > SEARCH_MAX)
			return "Search text is too long.";

		if (indexOfControlCharacter(searchText, false) >= 0)
			return "Search text contains an invalid character.";

		return "";
	}

	/**********
	 * <p> Method: checkForValidExperienceValue(String timeEffort) </p>
	 * 
	 * <p> Description: This method checks the time/effort hours for User Story US-U2-V.  The text
	 * must be a plain decimal number: digits with at most one decimal point, starting and ending
	 * with a digit.  That rules out a minus sign, letters, "5." and ".5".  The number must then
	 * be more than 0 and no more than 10,000.  Every kind of failure uses the same message,
	 * because to the person typing it all of them mean "type a positive number".</p>
	 * 
	 * @param timeEffort specifies the text typed into the time/effort field
	 * 
	 * @return an empty String if it is a valid positive number in range; otherwise, an error
	 * message
	 * 
	 */
	public static String checkForValidExperienceValue(String timeEffort) {
		final String message = "Time/effort must be a positive number.";

		String text = (timeEffort == null) ? "" : timeEffort.trim();
		if (text.isEmpty() || text.length() > EXPERIENCE_VALUE_SIZE_MAX)
			return message;

		if (!isDigit(text.charAt(0)) || !isDigit(text.charAt(text.length() - 1)))
			return message;

		int decimalPoints = 0;
		for (int ndx = 0; ndx < text.length(); ndx++) {
			char currentCharacter = text.charAt(ndx);
			if (currentCharacter == '.')
				decimalPoints++;
			else if (!isDigit(currentCharacter))
				return message;
		}
		if (decimalPoints > 1)
			return message;

		// The loop above proved this is a plain number, so parsing it is safe
		double value = Double.parseDouble(text);
		if (value <= 0 || value > EXPERIENCE_VALUE_MAX)
			return message;

		return "";
	}

	/**********
	 * <p> Method: checkForValidExperienceDescription(String description) </p>
	 * 
	 * <p> Description: This method checks the "what was done" text on an experience entry for
	 * User Story US-U2-V.  It must be 5 to 2000 characters, not blank, and free of control
	 * characters (line breaks are allowed).  Every kind of failure uses the one message Test
	 * Cases.pdf gives for this field.</p>
	 * 
	 * @param description specifies the experience description to be checked
	 * 
	 * @return an empty String if it is valid; otherwise, an error message
	 * 
	 */
	public static String checkForValidExperienceDescription(String description) {
		final String message = "Please describe what was done.";

		if (description == null || description.trim().isEmpty())
			return message;

		if (description.length() < EXPERIENCE_DESCRIPTION_MIN
				|| description.length() > EXPERIENCE_DESCRIPTION_MAX)
			return message;

		if (indexOfControlCharacter(description, true) >= 0)
			return message;

		return "";
	}

	/**********
	 * <p> Method: indexOfControlCharacter(String text, boolean allowLineBreaks) </p>
	 * 
	 * <p> Description: This method finds the first control character (tabs and other non-printing
	 * characters) in a String.  Java counts a newline as a control character, so the multi-line
	 * fields pass allowLineBreaks = true to let newlines through.  Single-line fields (title,
	 * tags, search) reject them like any other control character.</p>
	 * 
	 * @param text specifies the String to be scanned
	 * 
	 * @param allowLineBreaks true if newline characters are acceptable in this field
	 * 
	 * @return the 0-based index of the first control character, or -1 if there isn't one
	 * 
	 */
	private static int indexOfControlCharacter(String text, boolean allowLineBreaks) {
		for (int ndx = 0; ndx < text.length(); ndx++) {
			char currentCharacter = text.charAt(ndx);
			boolean isLineBreak = (currentCharacter == '\n' || currentCharacter == '\r');
			if (Character.isISOControl(currentCharacter) && !(allowLineBreaks && isLineBreak))
				return ndx;
		}
		return -1;
	}

	/**********
	 * <p> Method: isDigit(char character) </p>
	 * 
	 * <p> Description: This method determines whether a character is 0-9.  I did not use
	 * Character.isDigit() because it also accepts digits from other alphabets, which
	 * Double.parseDouble() would not necessarily understand.</p>
	 * 
	 * @param character specifies the character to be checked
	 * 
	 * @return true if the character is 0-9; otherwise, false
	 * 
	 */
	private static boolean isDigit(char character) {
		return character >= '0' && character <= '9';
	}
}
