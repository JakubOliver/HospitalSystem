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
            return "Successful!";
        }

        return "Error: " + error;
    }
}
