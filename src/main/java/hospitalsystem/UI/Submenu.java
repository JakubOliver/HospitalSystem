package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

abstract class Submenu extends Menu{
    Scanner scanner;

    Submenu(HospitalAPI api, Scanner scanner) {
        super(api);

        this.scanner = scanner;
    }
}
