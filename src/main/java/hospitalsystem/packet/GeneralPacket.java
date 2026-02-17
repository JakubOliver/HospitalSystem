package hospitalsystem.packet;

/**
 * Wrapper for status information transported via API between hospital system and TUI menu.
 */
public class GeneralPacket {
    /** Denotes whether the API request connected to the packet was successful */
    public final boolean successful;
    /** Error message of the exception that occurs while processing API request */
    protected final String error;

    /**
     * Creates general packet.
     * <p>
     * Constructor does not get any exception therefore object created via this constructor represents successful process.
     */
    public GeneralPacket() {
        this.successful = true;
        this.error = null;
    }

    /**
     * Creates general packet.
     * <p>
     * Constructs general packet that represents unsuccessful process.
     *
     * @param exception Exception that occurs via processing the API request.
     */
    public GeneralPacket(Exception exception){
        this.successful = false;
        this.error = exception.getMessage();
    }

    /**
     * Returns information message about the successfulness of the process.
     *
     * @return information message about the successfulness of the process.
     */
    public String resolveStatus(){
        if (successful) {
            return Msg.successfulPacket;
        }

        return "Error: " + error;
    }

    /**
     * Wrapper class containing messages connected with the packets.
     */
    public static class Msg {
        private Msg(){}

        /** Informative message representing general error with the API request connected to the packet. */
        public static final String invalidPacket = "Invalid packet";
        /** Informative message denoting that the API request connected to the packet was successful. */
        public static final String successfulPacket = "Successful!";
    }

}
