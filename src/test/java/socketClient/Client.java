package socketClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
//Видео https://www.youtube.com/watch?v=paFm6xKjqWg

//Тут реализуем Базовый Клиент (в классе "WebClient" есть еще Основной клиент с основной логикой)

//Создаем предложенные интерфейсом "WebSocketClient" все методы, 1-й из конструкторов + создаем
// метод получения времени существования подключения
public class Client extends WebSocketClient {

    private final SocketContext context;
    private Date openedTime;

    public Client(SocketContext context) throws URISyntaxException {
        super(new URI(context.getURI()));
        this.context = context;
    }


    //Что происходит когда открываем соединение
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        openedTime = new Date();
        System.out.println("Opened Connection " + context.getURI());
    }

    //Что происходит когда получаем сообщение
    @Override
    public void onMessage(String message) {
        System.out.println("Received new message " + message);
        context.getMessageList().add(message);

        //завершаем соединение при получении ожидаемого сообщения
        if (message.equals(context.getExceptedMessage())) {
            System.out.println("Except message received!!!");
            closeConnection(1000, "Received expected message"); //тут "1000" это аналог кода "200"
        }
    }

    //Что происходит когда, закрывается (по любым причинам) соединение
    @Override
    public void onClose(int code, String reason, boolean b) {
        System.out.println("Close socket with code " + code + ", reason is " + reason);
        context.setStatusCode(code); //для возможных тестов
    }

    //Что происходит когда, появляется сообщение об ошибке
    @Override
    public void onError(Exception e) {

    }

    //Получаем в секундах время существования подключения
    public int getAlliveTime() {
        Date closeDate = new Date();
        int timeInSenconds = (int) (closeDate.getTime() - openedTime.getTime()) / 1000;
        context.setTimeTaken(timeInSenconds);
        return timeInSenconds;
    }
}
