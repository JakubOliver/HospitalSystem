package cz.cuni.kubinja.hospitalsystem.menu;

/**
 * Denotes what every menu page has to offer.
 */
public interface Page {
    /**
     * Defines which options should menu provide and how will be mapped options to the functions. 
     */
    void defineMenu();

    /**
     * Shows menu options in terminal.
     */
    void printMenu();

    /**
     * Processes the user option based on page menu.
     */
    void processMenu();
}
