package hospitalsystem.UI;

/**
 * Denotes what basic operations should every menu providing operation with person should implement.
 */
public interface PersonnelMenu {
    /**
     * Provides option to add new personnel.
     */
    void add();

    /**
     * Provides option to edit existing personnel.
     */
    void edit();

    /**
     * Provides option to delete existing personnel.
     */
    void delete();

    /**
     * Provides option to find personnel based on the identification number.
     */
    void findById();

    /**
     * Provides option to display all personnel associated with menu page.
     */
    void all();
}
