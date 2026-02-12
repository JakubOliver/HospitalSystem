package hospitalsystem.packet;

import java.util.Optional;

public class GeneralPacket {
    public final boolean successful;
    public final Optional<String> error;

    public GeneralPacket(boolean successful, String error) {
        this.successful = successful;
        this.error = Optional.of(error);
    }

    public GeneralPacket() {
        this.successful = true;
        this.error = Optional.empty();
    }
}
