package org.vfs.client;

import org.vfs.client.network.Client;

/**
 * Entry point
 * @author Lipatov Nikita
 * TODO: 1) SocketWriter слушает очередь от пользователя, пишет в socket
 * TODO: 2) SocketReader слушает сервер, пишет в очередь на вывод
 * TODO: 3) QueueWriter слушает сообщения от пользователя, формирует message, пихает message в очередь для SocketWriter
 * TODO: 4) QueueReader слушает сообщения из очереди на вывод, парсит message, выводит на экран
 * TODO: 5) Main слушает пользователя и выполняет команды, команды асинхронны.
 * FixedThreadPoolExecutor, submit-> анонимный класс(4).
 * те кто пишет не блокируют
 * те кто читает - блокируют
 */
public class ClientMain
{

    /**
     * @param args no arguments
     */
    public static void main(String[] args)
    {
        Client client = new Client();
        client.run();
    }
}
