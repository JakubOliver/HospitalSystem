package hospitalsystem.packet;

/**
 * Extension of the general packet that also provides caller with the data output connected to the API request.
 *
 * @param <T> Type of the data that will be attached to the API response.
 */
public class DataPacket<T> extends GeneralPacket{
    /** Data wrapper inside the data packet */
    public final T data;

    /**
     * Creates successful data packet based on provided data.
     *
     * @param data Data that will be stored in data packet (representing API data response).
     */
    public DataPacket(T data) {
        this.data = data;

        super();
    }

    /**
     * Creates unsuccessful data packet.
     *
     * @param exception Exception that occurs while processing the API request.
     */
    public DataPacket(Exception exception){
        super(exception);
        data = null;
    }
}
