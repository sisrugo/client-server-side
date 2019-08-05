package bgu.spl181.net.api;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer ,ConnectionHandler> clientList;

    public ConnectionsImpl() {
        clientList = new ConcurrentHashMap <>();
    }

    public boolean send(int connectionId, T msg){
        ConnectionHandler handler = clientList.get(connectionId);
        if(handler != null) {
            synchronized (handler) {
                try {
                    handler.send(msg);
                } catch (Exception e) {
                    return false;
                }

            }
            return true;
        }
        else
            return false;
    }

    public void broadcast(T msg){
        for(Map.Entry<Integer, ConnectionHandler> entry : clientList.entrySet()) {
            ConnectionHandler currentCH = entry.getValue();
            currentCH.send(msg);
        }
    }

    public void disconnect(int connectionId, T msg){
        send(connectionId, msg);
        ConnectionHandler handler = clientList.get(connectionId);
        if (handler != null){
            synchronized (handler){
                clientList.remove(connectionId);
            }
        }
    }

    public void addClient(Integer id, ConnectionHandler handler) {
        clientList.put(id, handler);
    }
}
