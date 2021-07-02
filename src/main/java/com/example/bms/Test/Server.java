package com.example.bms.Test;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端管理器
 * @create 2021-01-23 9:56
 */
public class Server extends JFrame{
    private static JTextArea jta; //文本域
    private JScrollPane jsp; //滚动条
    private int serverPort;  //服务端的端口号

    //构造方法
    public Server(int serverPort) {
        this.serverPort = serverPort;
        this.init();    // 初始化
        this.excute();    // 执行
    }

    /**
     * 初始化
     */
    public void init() {
        jta = new JTextArea();    //文本域 注意：需要将文本域添加到滚动条中，实现滚动效果
        jsp = new JScrollPane(jta);    //滚动条

        //将滚动条和面板添加到窗体中
        this.add(jsp);
        //设置 标题、大小、位置、关闭、是否可见
        this.setTitle("聊天小程序服务端");    //标题
        this.setSize(400,400);    // 宽 高
        this.setLocation(0,300);    // 水平 垂直
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //窗体关闭 程序退出
        this.setVisible(true);    //设为可见
    }

    /**
     * 执行
     */
    public void excute() {
        ServerSocket serverSocket = null;    // 创建服务端套接字
        try {
            serverSocket = new ServerSocket(serverPort);
            // 创建线程池,从而可以处理多个客户端
            ExecutorService executorService= Executors.newFixedThreadPool(20);
            resetText("聊天室小程序已启动");    // 输出提示信息
            while (true) {
                Socket socket = serverSocket.accept();    // 监听客户端的情况，等待客户端连接
                resetText("有新的朋友加入");
                executorService.execute(new SocketHandler(socket));    // 对每一个客户端，创建一个套接字处理器线程
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();    // 关闭serverSocket通道
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void resetText(String info) {
        jta.append(info + "\n");
    }
    public static void main(String[] args){
        Server server = new Server(8888);
    }
}
