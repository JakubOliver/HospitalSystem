package hospitalsystem.packet;

public class DataPacket<T> extends GeneralPacket{
    public final T data;

    public DataPacket(boolean successful, String error) {
        super(successful, error);
        data = null;
    }

    public DataPacket(T data) {
        this.data = data;

        super();
    }

    public DataPacket(Exception exception){
        super(exception);
        data = null;
    }
}
