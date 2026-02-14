package hospitalsystem.packet;

public class GeneralPacket {
    public final boolean successful;
    protected final String error;

    public GeneralPacket(boolean successful, String error) {
        this.successful = successful;
        this.error = error;
    }

    public GeneralPacket() {
        this.successful = true;
        this.error = null;
    }

    public GeneralPacket(Exception exception){
        this.successful = false;
        this.error = exception.getMessage();
    }

    public String resolveStatus(){
        if (successful) {
            return Msg.successfulPacket;
        }

        return "Error: " + error;
    }

    public static class Msg {
        public static final String invalidPacket = "Invalid packet";
        public static final String successfulPacket = "Successful!";
    }

}
