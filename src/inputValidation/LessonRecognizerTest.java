package inputValidation;

/*******
 * <p> Title: LessonRecognizerTest Class. </p>
 *
 * <p> Description: The semi-automated input validation tests for every field LessonRecognizer
 * checks.  Tests 1 to 45 are the "Automated Input Validation Tests" from HW2 Test Cases.pdf,
 * kept with the same numbers; Tests 46 to 58 were added in TP2 for the fields added in TP2: a
 * team member's name (requirement E7) and the root cause, recommendation, and Curator search
 * terms (requirement E6).</p>
 *
 * <p> <b>Requirements covered.</b>  Each group method below names the requirements it
 * demonstrates.  The field rules here are what US-C1-V, US-R2-V, US-U1-V, and US-U2-V mean by
 * input that is "missing, too short, too long, or contains invalid characters", and each test
 * checks the exact "specific, helpful error message" those stories require.  The requirement
 * tests in the lessonTests package then prove that each page's Model applies these rules
 * before anything is saved.</p>
 *
 * <p> <b>How the tests are organized.</b>  Boundary tests for one field are short and only make
 * sense side by side (the last valid length next to the first invalid length), so each field's
 * tests are in one method, and that method's Javadoc lists every test in it with its input and
 * expected result.  The screenshot of each method therefore shows the code of every test it
 * contains.</p>
 *
 * <p> <b>How to read the output.</b>  Each test prints one line, "Test N: [input] - [what it
 * checks] - Pass.".  For a valid input the test passes only if the recognizer returns an
 * empty String (no error).  For an invalid input the test passes only if the recognizer
 * returns exactly the expected message; when a test fails, both the expected and the actual
 * message are printed under the line.  Like UserNameRecognizerTest, this is a plain console
 * program with a main method; it runs every test before reporting, and ends with an
 * AssertionError if anything failed, so a failure cannot be missed.</p>
 *
 * @author Emilia Davey
 *
 * @version 1.00		2026-09-29 Initial version for HW2
 * @version 1.01		2026-10-16 TP2 Task 6: Tests 46-58 for the member name, root cause,
 * 							recommendation, and search terms; requirement mapping in the Javadoc
 */
public class LessonRecognizerTest {

	/** How many tests have run so far. */
	private static int numberOfTests = 0;
	/** How many of those tests failed. */
	private static int numberFailed = 0;

	/**********
	 * Default constructor is not used; every member is static.
	 */
	public LessonRecognizerTest() {
	}

	/**********
	 * Runs Tests 1 to 58 and prints the totals.  Ends with an AssertionError if any test failed.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("TP2 Automated Input Validation Tests - LessonRecognizer\n");
		System.out.println("A valid input passes when no error is returned; an invalid input "
				+ "passes only\nwhen the exact expected error message is returned.");
		runTitleTests();
		runDescriptionTests();
		runTagsTests();
		runCategoryTests();
		runSearchTextTests();
		runExperienceValueTests();
		runExperienceDescriptionTests();
		runMemberNameTests();
		runCoachedTextTests();
		runSearchTermsTests();

		// Same footer wording as the testbed automation files (EmailAddressTestAutomation and
		// PasswordEvaluationTestingAutomation)
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + (numberOfTests - numberFailed));
		System.out.println("Number of tests failed: " + numberFailed);
		if (numberFailed > 0)
			throw new AssertionError(numberFailed + " LessonRecognizer test case(s) failed.");
	}

	/**********
	 * <p> Tests 1-7: lesson title. </p>
	 *
	 * <p> <b>Requirements:</b> US-C1 and US-U1 (a valid title is accepted), US-C1-V and US-U1-V
	 * (a missing, too long, or invalid title gets a specific message). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidTitle requires at least one
	 * non-blank character, at most 100 characters, and no control characters, and names the
	 * position of the first invalid character. </p>
	 *
	 * <p> Tests: 1 normal title (valid); 2 one character (valid, minimum); 3 100 characters
	 * (valid, maximum); 4 101 characters (too long); 5 only spaces (required); 6 contains a tab
	 * (invalid character at position 7); 7 empty (required). </p>
	 */
	private static void runTitleTests() {
		System.out.println("\n--- Title Recognizer Tests (US-C1, US-C1-V, US-U1-V) ---");
		expectValid(1, LessonRecognizer.checkForValidTitle("Fixed the deployment pipeline bug"),
				"\"Fixed the deployment pipeline bug\" - valid title");
		expectValid(2, LessonRecognizer.checkForValidTitle("X"),
				"\"X\" (1 character) - meets the minimum, any non-blank value");
		expectValid(3, LessonRecognizer.checkForValidTitle(rep("A", 100)),
				"100-character title - meets the maximum boundary");
		expectError(4, LessonRecognizer.checkForValidTitle(rep("A", 101)),
				"Title must be 100 characters or fewer.", "101-character title - over the maximum");
		expectError(5, LessonRecognizer.checkForValidTitle("   "), "Title is required.",
				"whitespace-only title");
		expectError(6, LessonRecognizer.checkForValidTitle("Sprint\tRecap"),
				"Title contains an invalid character at position 7.",
				"\"Sprint[TAB]Recap\" - control character");
		expectError(7, LessonRecognizer.checkForValidTitle(""), "Title is required.",
				"empty title");
	}

	/**********
	 * <p> Tests 8-15: lesson description. </p>
	 *
	 * <p> <b>Requirements:</b> US-C1 and US-U1 (a valid description is accepted), US-C1-V and
	 * US-U1-V (a missing, too short, too long, or invalid description gets a specific
	 * message). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidDescription requires 10 to 5000
	 * characters with no control characters other than line breaks, and reports a blank value
	 * as required. </p>
	 *
	 * <p> Tests: 8 normal (valid); 9 10 characters (valid, minimum); 10 5000 characters (valid,
	 * maximum); 11 9 characters (range message); 12 5001 characters (range message); 13 only
	 * spaces (required); 14 contains a tab (invalid character at position 9); 15 empty
	 * (required). </p>
	 */
	private static void runDescriptionTests() {
		System.out.println("\n--- Description Recognizer Tests (US-C1, US-C1-V, US-U1-V) ---");
		String range = "Description must be between 10 and 5000 characters.";
		expectValid(8, LessonRecognizer.checkForValidDescription(
				"We underestimated integration time and shipped two days late."),
				"normal description");
		expectValid(9, LessonRecognizer.checkForValidDescription(rep("A", 10)),
				"10-character description - meets the minimum boundary");
		expectValid(10, LessonRecognizer.checkForValidDescription(rep("A", 5000)),
				"5000-character description - meets the maximum boundary");
		expectError(11, LessonRecognizer.checkForValidDescription(rep("A", 9)), range,
				"9-character description - under the minimum");
		expectError(12, LessonRecognizer.checkForValidDescription(rep("A", 5001)), range,
				"5001-character description - over the maximum");
		expectError(13, LessonRecognizer.checkForValidDescription("     "),
				"Description is required.", "whitespace-only description");
		expectError(14, LessonRecognizer.checkForValidDescription("Line one\tline two"),
				"Description contains an invalid character at position 9.",
				"description containing a tab (control character)");
		expectError(15, LessonRecognizer.checkForValidDescription(""),
				"Description is required.", "empty description");
	}

	/**********
	 * <p> Tests 16-20: tags. </p>
	 *
	 * <p> <b>Requirements:</b> US-C1 and US-U1 (tags are optional core information), US-C1-V
	 * and US-U1-V (too long or invalid tags get a specific message). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidTags accepts an empty value, limits
	 * the whole field to 200 characters and each comma-separated tag to 30, and rejects control
	 * characters. </p>
	 *
	 * <p> Tests: 16 normal tags (valid); 17 empty (valid, optional); 18 201 characters in total
	 * (too long); 19 one 31-character tag (too long); 20 contains a tab (invalid
	 * character). </p>
	 */
	private static void runTagsTests() {
		System.out.println("\n--- Tags Recognizer Tests (US-C1, US-C1-V, US-U1-V) ---");
		expectValid(16, LessonRecognizer.checkForValidTags("java, debugging, sprint-3"),
				"normal comma-separated tags");
		expectValid(17, LessonRecognizer.checkForValidTags(""),
				"empty tags - accepted, the field is optional");
		expectError(18, LessonRecognizer.checkForValidTags(rep("a", 201)), "Tags are too long.",
				"tags field over 200 characters total");
		expectError(19, LessonRecognizer.checkForValidTags(rep("a", 31)), "Tags are too long.",
				"a single 31-character tag - over the 30-character limit");
		expectError(20, LessonRecognizer.checkForValidTags("java, deb\tug"),
				"Tags contain an invalid character.", "tags containing a tab (control character)");
	}

	/**********
	 * <p> Tests 21-25: category. </p>
	 *
	 * <p> <b>Requirements:</b> US-C1 and US-U1 (a listed category is accepted), US-C1-V and
	 * US-U1-V (any other value gets a specific message). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidCategory accepts only an exact,
	 * case-sensitive match with one of LessonRecognizer.VALID_CATEGORIES, the values the
	 * Category drop-down offers. </p>
	 *
	 * <p> Tests: 21 "Technical" (valid); 22 "General" (valid); 23 "Marketing" (not in the
	 * list); 24 blank (not in the list); 25 "TECHNICAL" (wrong case). </p>
	 */
	private static void runCategoryTests() {
		System.out.println("\n--- Category Recognizer Tests (US-C1, US-C1-V, US-U1-V) ---");
		String msg = "Please choose a valid category.";
		expectValid(21, LessonRecognizer.checkForValidCategory("Technical"), "\"Technical\"");
		expectValid(22, LessonRecognizer.checkForValidCategory("General"), "\"General\"");
		expectError(23, LessonRecognizer.checkForValidCategory("Marketing"), msg,
				"\"Marketing\" - not in the fixed list");
		expectError(24, LessonRecognizer.checkForValidCategory(""), msg, "blank category");
		expectError(25, LessonRecognizer.checkForValidCategory("TECHNICAL"), msg,
				"\"TECHNICAL\" - matching is case-sensitive");
	}

	/**********
	 * <p> Tests 26-30: search and filter text. </p>
	 *
	 * <p> <b>Requirements:</b> US-R2 (valid search text is accepted) and US-R2-V (too long or
	 * invalid search text gets a specific message; empty text is not an error). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidSearchText accepts empty text,
	 * allows up to 254 characters, and rejects control characters. </p>
	 *
	 * <p> Tests: 26 normal (valid); 27 empty (valid, finds nothing); 28 254 characters (valid,
	 * maximum); 29 255 characters (too long); 30 contains a tab (invalid character). </p>
	 */
	private static void runSearchTextTests() {
		System.out.println("\n--- Search/Filter Text Recognizer Tests (US-R2, US-R2-V) ---");
		expectValid(26, LessonRecognizer.checkForValidSearchText("java debugging"),
				"normal search text");
		expectValid(27, LessonRecognizer.checkForValidSearchText(""),
				"empty search text - valid input, it just finds nothing");
		expectValid(28, LessonRecognizer.checkForValidSearchText(rep("a", 254)),
				"254-character search - meets the maximum boundary");
		expectError(29, LessonRecognizer.checkForValidSearchText(rep("a", 255)),
				"Search text is too long.", "255-character search - over the maximum");
		expectError(30, LessonRecognizer.checkForValidSearchText("java\tdebug"),
				"Search text contains an invalid character.",
				"search text containing a tab (control character)");
	}

	/**********
	 * <p> Tests 31-38: experience time/effort value. </p>
	 *
	 * <p> <b>Requirements:</b> US-U2 (a positive number of hours is accepted) and US-U2-V (a
	 * negative, zero, too large, or non-numeric value gets a specific message).  The same rule
	 * is used for each team member's hours (E7). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidExperienceValue checks that the text
	 * is a plain decimal number (digits and at most one point, starting and ending with a digit)
	 * before parsing it, so no input can cause an exception, and then requires more than 0 and
	 * at most 10000. </p>
	 *
	 * <p> Tests: 31 "4.5" (valid); 32 "0.1" (valid, smallest above zero); 33 "10000" (valid,
	 * maximum); 34 "0"; 35 "-3"; 36 "10000.01"; 37 "abc"; 38 empty.  Tests 34-38 all expect
	 * "Time/effort must be a positive number.". </p>
	 */
	private static void runExperienceValueTests() {
		System.out.println("\n--- Experience Value Recognizer Tests (US-U2, US-U2-V, E7) ---");
		String msg = "Time/effort must be a positive number.";
		expectValid(31, LessonRecognizer.checkForValidExperienceValue("4.5"), "\"4.5\"");
		expectValid(32, LessonRecognizer.checkForValidExperienceValue("0.1"),
				"\"0.1\" - smallest value above zero");
		expectValid(33, LessonRecognizer.checkForValidExperienceValue("10000"),
				"\"10000\" - meets the maximum boundary");
		expectError(34, LessonRecognizer.checkForValidExperienceValue("0"), msg,
				"\"0\" - must be greater than zero");
		expectError(35, LessonRecognizer.checkForValidExperienceValue("-3"), msg,
				"\"-3\" - negative");
		expectError(36, LessonRecognizer.checkForValidExperienceValue("10000.01"), msg,
				"\"10000.01\" - over the maximum");
		expectError(37, LessonRecognizer.checkForValidExperienceValue("abc"), msg,
				"\"abc\" - not a number");
		expectError(38, LessonRecognizer.checkForValidExperienceValue(""), msg, "empty input");
	}

	/**********
	 * <p> Tests 39-45: experience description. </p>
	 *
	 * <p> <b>Requirements:</b> US-U2 (a description of what was done is accepted) and US-U2-V
	 * (a missing, too short, or too long description gets a specific message). </p>
	 *
	 * <p> <b>How the code satisfies them:</b> checkForValidExperienceDescription requires 5 to
	 * 2000 characters that are not all blank. </p>
	 *
	 * <p> Tests: 39 normal (valid); 40 5 characters (valid, minimum); 41 2000 characters
	 * (valid, maximum); 42 4 characters; 43 2001 characters; 44 only spaces; 45 empty.  Tests
	 * 42-45 all expect "Please describe what was done.". </p>
	 */
	private static void runExperienceDescriptionTests() {
		System.out.println("\n--- Experience Description Recognizer Tests (US-U2, US-U2-V) ---");
		String msg = "Please describe what was done.";
		expectValid(39, LessonRecognizer.checkForValidExperienceDescription(
				"Paired with two teammates and reused the earlier fix."), "normal description");
		expectValid(40, LessonRecognizer.checkForValidExperienceDescription(rep("A", 5)),
				"5-character description - meets the minimum boundary");
		expectValid(41, LessonRecognizer.checkForValidExperienceDescription(rep("A", 2000)),
				"2000-character description - meets the maximum boundary");
		expectError(42, LessonRecognizer.checkForValidExperienceDescription("Fixd"), msg,
				"\"Fixd\" (4 characters) - under the minimum");
		expectError(43, LessonRecognizer.checkForValidExperienceDescription(rep("A", 2001)), msg,
				"2001-character description - over the maximum");
		expectError(44, LessonRecognizer.checkForValidExperienceDescription("   "), msg,
				"whitespace-only description");
		expectError(45, LessonRecognizer.checkForValidExperienceDescription(""), msg,
				"empty description");
	}

	/**********
	 * <p> Tests 46-49: team member name (added in TP2). </p>
	 *
	 * <p> <b>Requirement:</b> E7, each team member listed on an experience entry needs a valid
	 * name. </p>
	 *
	 * <p> <b>How the code satisfies it:</b> checkForValidMemberName requires a non-blank name of
	 * at most 100 characters with no control characters, and names the position of the first
	 * invalid character. </p>
	 *
	 * <p> Tests: 46 "Virgil Jones" (valid); 47 101 characters (too long); 48 only spaces
	 * (required); 49 contains a tab (invalid character at position 4). </p>
	 */
	private static void runMemberNameTests() {
		System.out.println("\n--- Team Member Name Recognizer Tests (E7) ---");
		expectValid(46, LessonRecognizer.checkForValidMemberName("Virgil Jones"),
				"\"Virgil Jones\" - valid name");
		expectError(47, LessonRecognizer.checkForValidMemberName(rep("A", 101)),
				"Team member name must be 100 characters or fewer.",
				"101-character name - over the maximum");
		expectError(48, LessonRecognizer.checkForValidMemberName("   "),
				"Team member name is required.", "whitespace-only name");
		expectError(49, LessonRecognizer.checkForValidMemberName("Ana\tB"),
				"Team member name contains an invalid character at position 4.",
				"\"Ana[TAB]B\" - control character");
	}

	/**********
	 * <p> Tests 50-54: root cause and recommendation (added in TP2). </p>
	 *
	 * <p> <b>Requirement:</b> E6, the optional coached fields from AICoaching.pdf (Root Cause
	 * and Recommendation) accept an empty value or up to 2000 characters, and longer text gets a
	 * message that names the field. </p>
	 *
	 * <p> <b>How the code satisfies it:</b> checkForValidRootCause and
	 * checkForValidRecommendation share checkOptionalCoachedText, which accepts empty text,
	 * limits the length to 2000, and rejects control characters other than line breaks. </p>
	 *
	 * <p> Tests: 50 empty root cause (valid, optional); 51 2000-character root cause (valid,
	 * maximum); 52 2001-character root cause (too long); 53 normal recommendation (valid); 54
	 * 2001-character recommendation (too long). </p>
	 */
	private static void runCoachedTextTests() {
		System.out.println("\n--- Root Cause and Recommendation Recognizer Tests (E6) ---");
		expectValid(50, LessonRecognizer.checkForValidRootCause(""),
				"empty root cause - accepted, the field is optional");
		expectValid(51, LessonRecognizer.checkForValidRootCause(rep("R", 2000)),
				"2000-character root cause - meets the maximum boundary");
		expectError(52, LessonRecognizer.checkForValidRootCause(rep("R", 2001)),
				"Root cause must be 2000 characters or fewer.",
				"2001-character root cause - over the maximum");
		expectValid(53, LessonRecognizer.checkForValidRecommendation(
				"Schedule integration testing in the first week."), "normal recommendation");
		expectError(54, LessonRecognizer.checkForValidRecommendation(rep("R", 2001)),
				"Recommendation must be 2000 characters or fewer.",
				"2001-character recommendation - over the maximum");
	}

	/**********
	 * <p> Tests 55-58: Curator search terms (added in TP2). </p>
	 *
	 * <p> <b>Requirement:</b> E6, the search terms a Phase Three Curator adds are limited to 500
	 * characters in total and 50 per comma-separated term, with no control characters. </p>
	 *
	 * <p> <b>How the code satisfies it:</b> checkForValidSearchTerms accepts empty text, checks
	 * the total length and invalid characters, then checks each term after splitting on
	 * commas. </p>
	 *
	 * <p> Tests: 55 normal terms (valid); 56 501 characters in total (too long); 57 one
	 * 51-character term (too long); 58 contains a tab (invalid character). </p>
	 */
	private static void runSearchTermsTests() {
		System.out.println("\n--- Curator Search Terms Recognizer Tests (E6) ---");
		expectValid(55, LessonRecognizer.checkForValidSearchTerms("ci, pipelines, release"),
				"normal comma-separated search terms");
		expectError(56, LessonRecognizer.checkForValidSearchTerms(rep("a", 501)),
				"Search terms are too long.", "501 characters in total - over the maximum");
		expectError(57, LessonRecognizer.checkForValidSearchTerms("ok, " + rep("a", 51)),
				"Search terms are too long.", "a single 51-character term - over the 50 limit");
		expectError(58, LessonRecognizer.checkForValidSearchTerms("ci\tcd"),
				"Search terms contain an invalid character.",
				"search terms containing a tab (control character)");
	}



	/**********
	 * Prints one "Test N: ... - Pass." line for a test whose input should be VALID, which means
	 * the recognizer returns an empty String.
	 *
	 * @param testNumber	the test number
	 * @param actualMessage	what the recognizer returned
	 * @param description	what the test checks
	 */
	private static void expectValid(int testNumber, String actualMessage, String description) {
		numberOfTests++;
		boolean passed = actualMessage.isEmpty();
		if (!passed) numberFailed++;
		System.out.println("Test " + testNumber + ": " + description + " - "
				+ (passed ? "Pass." : "FAIL."));
		if (!passed)
			System.out.println("        expected valid but got: '" + actualMessage + "'");
	}

	/**********
	 * Prints one "Test N: ... - Pass." line for a test whose input should be REJECTED with one
	 * exact message.  Comparing the exact text is how these tests check the "specific, helpful
	 * error message" requirement.
	 *
	 * @param testNumber		the test number
	 * @param actualMessage		what the recognizer returned
	 * @param expectedMessage	the exact message the test expects
	 * @param description		what the test checks
	 */
	private static void expectError(int testNumber, String actualMessage, String expectedMessage,
			String description) {
		numberOfTests++;
		boolean passed = expectedMessage.equals(actualMessage);
		if (!passed) numberFailed++;
		System.out.println("Test " + testNumber + ": " + description + " - "
				+ (passed ? "Pass." : "FAIL."));
		if (!passed)
			System.out.println("        expected: '" + expectedMessage + "'  actual: '"
					+ actualMessage + "'");
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
