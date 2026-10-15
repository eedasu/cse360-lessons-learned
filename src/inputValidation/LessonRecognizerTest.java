package inputValidation;

/*******
 * <p> Title: LessonRecognizerTest Class. </p>
 * 
 * <p> Description: This class exercises the valid and invalid input cases for every field
 * LessonRecognizer checks.  These are the "Automated Input Validation Tests", Tests 1 to 45, from
 * Test Cases.pdf, numbered exactly like the PDF and printed in the same "Test N: ... - Pass."
 * style as TP1_Test_Cases.pdf.  For every "Fail" test the exact error message is compared, not
 * just "it failed", because the assignment wants helpful, specific messages.</p>
 * 
 * <p> Like UserNameRecognizerTest, this is a plain console program with its own main method (no
 * JUnit).  One difference: UserNameRecognizerTest stops at the first failure, but this one runs
 * every test and prints all the results first, so a screencast can show the whole run.  It still
 * ends with an AssertionError if anything failed, so a failure can't be missed.</p>
 * 
 * <p> The functional (database) tests are in guiRole1.ModelRole1HomeTest.</p>
 * 
 * @author Emilia Davey
 * 
 * @version 1.00		2026-09-29 Initial version for HW2
 *  
 */
public class LessonRecognizerTest {

	private static int numberOfTests = 0;
	private static int numberFailed = 0;

	public static void main(String[] args) {
		System.out.println("HW2 Automated Input Validation Tests - LessonRecognizer\n");
		runTitleTests();
		runDescriptionTests();
		runTagsTests();
		runCategoryTests();
		runSearchTextTests();
		runExperienceValueTests();
		runExperienceDescriptionTests();
		
		// Same footer wording as the testbed automation files (EmailAddressTestAutomation and
		// PasswordEvaluationTestingAutomation)
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + (numberOfTests - numberFailed));
		System.out.println("Number of tests failed: " + numberFailed);
		if (numberFailed > 0)
			throw new AssertionError(numberFailed + " LessonRecognizer test case(s) failed.");
	}

	private static void runTitleTests() {
		System.out.println("\n--- Title Recognizer Tests ---");
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

	private static void runDescriptionTests() {
		System.out.println("\n--- Description Recognizer Tests ---");
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

	private static void runTagsTests() {
		System.out.println("\n--- Tags Recognizer Tests ---");
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

	private static void runCategoryTests() {
		System.out.println("\n--- Category Recognizer Tests ---");
		String msg = "Please choose a valid category.";
		expectValid(21, LessonRecognizer.checkForValidCategory("Technical"), "\"Technical\"");
		expectValid(22, LessonRecognizer.checkForValidCategory("General"), "\"General\"");
		expectError(23, LessonRecognizer.checkForValidCategory("Marketing"), msg, 
				"\"Marketing\" - not in the fixed list");
		expectError(24, LessonRecognizer.checkForValidCategory(""), msg, "blank category");
		expectError(25, LessonRecognizer.checkForValidCategory("TECHNICAL"), msg, 
				"\"TECHNICAL\" - matching is case-sensitive");
	}

	private static void runSearchTextTests() {
		System.out.println("\n--- Search/Filter Text Recognizer Tests ---");
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

	private static void runExperienceValueTests() {
		System.out.println("\n--- Experience Value Recognizer Tests ---");
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

	private static void runExperienceDescriptionTests() {
		System.out.println("\n--- Experience Description Recognizer Tests ---");
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
	 * Prints one "Test N: ... - Pass." line for a test whose input should be VALID, which means
	 * the recognizer returns an empty String.
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
	 * exact message.  Comparing the exact text is how these tests check the "helpful,
	 * context-relevant error message" requirement.
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
	 */
	private static String rep(String piece, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) sb.append(piece);
		return sb.toString();
	}
}
