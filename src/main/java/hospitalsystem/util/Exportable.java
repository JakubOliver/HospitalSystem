package hospitalsystem.util;

/**
 * Requires classes to implement export methods that could be use while exporting content of the classes into files.
 */
public interface Exportable {
    /**
     * Text format that will be used for exporting object.
     *
     * @return text that will be used for exporting object.
     */
    String export();
}
