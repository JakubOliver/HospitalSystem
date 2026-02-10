package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

abstract class Submenu extends Menu{
    Scanner scanner;

    public Submenu(HospitalAPI api, Scanner scanner) {
        super(api);

        this.scanner = scanner;

        printMenu();
        processMenu();
    }
}
