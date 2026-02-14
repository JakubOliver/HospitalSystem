package hospitalsystem.packet;

public class TextPacket extends GeneralPacket{
    public final String text;

    public TextPacket(String text) {
        this.text = text;
    }
}
