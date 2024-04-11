package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import socketClient.SocketContext;
import socketClient.WebClient;

import java.time.Duration;
import java.util.List;

import org.java_websocket.client.WebSocketClient;

import static com.codeborne.selenide.Selenide.$x;

//!!!! Почему-то когда запускаю на Selenium в окне чата не появляется мое сообщение т.к. пишет, что disconnect
// (даже если в файлах "WebClient" и "Client" закомментирую все закрывалки соединения). А если на Selenide, то все
// в порядке, ни каких disconnect чата и нормально появляются все сообщения??????

public class SocketTests {
    private SocketContext context;

    @Test
    public void socketUI_SendText(){

        String expectedMessage = "threadqa message";

        //на Selenium
//        System.setProperty("webdriver.chrome.driver", "D:\\Java\\Selenium\\chromedriver.exe");
//		WebDriver driver = new ChromeDriver();
//        // Устанавливаем неявное время ожидания перед выбрасыванием ошибки о не нахождении элемента на странице на 10 секунд
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        try {
//            driver.get("https://www.piesocket.com/websocket-tester");
//            Thread.sleep(2000);
//            WebElement input = driver.findElement(By.xpath("//input[@id='email']"));
//            WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
//            button.click();



        //на Selenide
        Selenide.open("https://www.piesocket.com/websocket-tester");
        SelenideElement input = $x("//input[@id='email']");
        SelenideElement button = $x("//button[@type='submit']");
        $x("//button[@type='submit']").click();

        String url = input.getAttribute("value"); //получаем адрес WebSocket-а к которому нужно подключиться "wss://****"

        Runnable sendUIMessage = new Runnable() {
            @Override
            public void run() {
                input.clear();
                input.sendKeys(expectedMessage);
                button.click();
            }
        };

        context = new SocketContext();
        context.setURI(url);
        context.setExceptedMessage(expectedMessage);
        context.setTimeOut(10);
        context.setRunnable(sendUIMessage);
        //!!! Когда пытаемся отправить сообщение на сервер не через ввод сообщения в поле на странице сайта, а методом "send"
        // класса "WebSocketClient" из заполненного нами поля "body", то сервер на этом конкретном сайте начал выводить в общее поле(где видна вся переписка)
        // не это наше сообщение, а "error":"Unknown api key". И это ломает нашу последующую проверку на появление нашего сообщения в общем поле. Нормально
        // получается только через Runnable, где заполняем на странице поле и отправляем его на сервер нажатием кнопки !!!
//        context.setBody(expectedMessage);

        //Запускаем процесс отправки/получения
        WebClient.getInstance().connectToSocket(context);


        //Проверка на наличие "threadqa message" на Selenide
        SelenideElement x = $x("//*[@id='consoleLog']");
        String log = x.getText();
        int i = 0;
        $x("//*[@id='consoleLog']").shouldHave(Condition.partialText(expectedMessage));

        //Проверка на наличие "threadqa message" на Selenium
        //1. Так не могу получить инфу для проверки, т.к. чат уже почему-то disconnet
// //       WebElement consoleLog = driver.findElement(By.xpath("//*[@id='consoleLog']"));
// //       String log = consoleLog.getText();
        //2. Могу получить инфу для проверки только так, из своей переменной а не из поля чата. 
//        List<String > messageList = context.getMessageList();
//
//        Assertions.assertTrue(messageList.toString().contains(expectedMessage));
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
