package socketClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URISyntaxException;
import java.util.Map;
//Видео https://www.youtube.com/watch?v=paFm6xKjqWg

//В этом классе будет основная логика работы (а в классе "Client" написана базовая логика)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebClient {
    private Client client;

    public static WebClient getInstance() {
        return new WebClient();
    }

    public void connectToSocket(SocketContext context) {
        boolean isBodySent = false;

        try {

            //если заголовки не пустые, то добавляем их клиенту
            client = new Client(context);
            if (!context.getRequestHeaders().isEmpty()) {
                final Map<String, String> requestHeaderParams = context.getRequestHeaders();
                requestHeaderParams.forEach((key, value) -> { //заполняем Map
                    client.addHeader(key, value);
                });
            }

            //подключение к сокету
            client.connectBlocking();

            //будем получать сообщение до тех пор, пока клиент не закрыт
            while (!client.isClosed()) {

                //если в "context" есть какая-то задача, которую нужно запустить в отдельном потоке, то мы ее запускаем
                if (context.getRunnable()!= null){
                    context.getRunnable().run();
                }

                //если в "context" есть какое-то тело и оно не отправлено, тогда мы его отправляем 1 раз, хотя находимся в цикле while
                if (context.getBody() != null && !isBodySent) {
                    client.send(context.getBody());
                    isBodySent = true;
                }

                //если в "context" содержится ожидаемое нами сообщение, то сохраняем его и закрываем соединение
                if (context.getExceptedMessage() != null){
                    client.onMessage(context.getExceptedMessage()); //запускаем в работу метод "onMessage"
                    return;
                }

                //если подключение не закрыто, закрываем его по таймауту
                if (client.getAlliveTime() >= context.getTimeOut()) {
                    client.closeConnection(1006, "Time Out");
                }
            }
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
