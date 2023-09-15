package networking;

public interface  NetworkClientListener {
    void onNetworkClientListener(NetworkClient networkClient);
    void onNetworkClientDisconnected();
}
