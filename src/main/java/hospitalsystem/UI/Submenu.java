package hospitalsystem.UI;

import hospitalsystem.Hospital;

import java.util.Scanner;

/**
 * Abstract ancestor for submenu pages. Extending general menu with custom constructor for submenus.
 */
abstract class Submenu extends Menu{
    /**
     * Abstract constructor used in the chain of construction of submenu pages.
     *
     * @param api HospitalAPI providing the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public Submenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    public Submenu(Hospital api, Scanner scanner, boolean dummy) {
        super(api, scanner, dummy);
    }
}
