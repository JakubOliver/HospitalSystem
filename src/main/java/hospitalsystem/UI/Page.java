package hospitalsystem.UI;

/**
 * Denotes what every menu page has to offer.
 */
public interface Page {
    /**
     * Shows menu options in terminal.
     */
    public void printMenu();

    /**
     * Processes the user option based on page menu.
     */
    public void processMenu();
}
