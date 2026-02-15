package hospitalsystem.packet;

public class GeneralTypePacket<T> extends GeneralPacket{
    public final T data;

    public GeneralTypePacket(boolean successful, String error) {
        super(successful, error);
        data = null;
    }

    public GeneralTypePacket(T data) {
        this.data = data;

        super();
    }

    public GeneralTypePacket(Exception exception){
        super(exception);
        data = null;
    }
}
