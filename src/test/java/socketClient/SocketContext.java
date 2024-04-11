package socketClient;

//Класс для хранения используемых при работе с WebSocket-ом переменных

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketContext {
    //обязательные поля
    private String URI; //ссылка куда подключаться
    private String exceptedMessage; //ожидаемое сообщение, т.к. все почти WebSocket-тесты  делаются на проверку получения от сервера ожидаемого сообщения сообщения

    //собственные поля
    private Map<String ,String > requestHeaders = new HashMap<>(); //для создания заголовков в которых мы будем указывать данные для прохожденя безопасности/авторизации (например токены подключения)
    private List<String > messageList = new ArrayList<>(); //для хранения сообщений, которые присылает Websocket
    private int statusCode; //
    private int timeOut = 5; //
    private int timeTaken; //информация нужная для отчетов (например Allure)
    private String body; //для возможности получения сообщений от сервера мы вначале должны подписаться на какую-то тему. Для осуществления подписки мы должны отправить сообщение с телом "body" и в этом теле мы указываем на, что конкретно мы хотим подписаться (например если это криптобиржа, то в теле мы указываем валюту, обновление курса которой мы хотим получать))
    private Runnable runnable; //используется для передачи какого-то действия, которое должно быть выполнено чаще всего над UI-частью тестов

}
