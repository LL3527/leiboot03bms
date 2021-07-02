package com.example.bms.Test;

import java.io.IOException;
import java.net.Socket;

public class Client3 {
    public static void main(String[] args) throws IOException, InterruptedException {
        //1.客户端连接服务器端,返回套接字Socket对象
        Socket socket = new Socket("127.0.0.1",8888);
        // 用户登录
        new LoginFrame(socket);
        //socket.close();    // 关闭套接字
    }
}
