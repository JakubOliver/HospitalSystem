package hospitalsystem.UI;

import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

/**
 * Abstract ancestor for submenu pages. Extending general menu with custom constructor for submenus.
 */
abstract class Submenu extends Menu{
    Scanner scanner;
    UIState state = UIState.RUN;

    /**
     * Abstract constructor used in the chain of construction of submenu pages.
     *
     * @param api HospitalAPI providing the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public Submenu(HospitalAPI api, Scanner scanner) {
        super(api);

        this.scanner = scanner;

        while (state == UIState.RUN){
            printMenu();
            processMenu();
        }
    }

    protected void end(){
        state =  UIState.END;
    }
}
