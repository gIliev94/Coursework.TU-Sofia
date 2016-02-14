package warehouse.database;

/**
 * Validates a String input from the user.
 * 
 * @author Georgi Iliev
 *
 */
public class QueryValidator {

    /**
     * Client name validation - allowing ONLY letters and numbers present in the
     * name.
     * 
     * @param clientName
     * @return
     */
    public static boolean validateClientNameString(String clientName) {
	for (char c : clientName.toCharArray()) {
	    if (!Character.isLetterOrDigit(c)) {
		return false;
	    }
	}

	return true;
    }

}
