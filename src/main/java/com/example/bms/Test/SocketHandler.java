package com.example.bms.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 套接字处理器，用来管理聊天的客户的的行为，比如找谁聊天，是否群发信息。上线下线
 * 一个客户端对应一个套接字处理器

 * @create 2021-01-23 9:56
 */

class SocketHandler implements Runnable{
    // 关联用户的名称和socket端口，借此统计用户在线的情况
    private static Map<String,Socket> map = new ConcurrentHashMap<String, Socket>();
    private Socket socket;    // 对应处理的socket

    /**
     * 构造函数
     * @param socket 对应客户端的套接字
     */
    public SocketHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());    // 获取客户端的输入流
            String msg = null;    // 接收用户的信息
            Server.resetText(msg);
            while(true){
                if(scanner.hasNextLine()){
                    msg = scanner.nextLine();    // 读取客户端传来的数据信息
                    // 用户登录
                    if(msg.startsWith("Sign:")){
                        // 将用户名保存在userName中
                        String userName = msg.split("\\:")[1];    // 获取用户名
                        // 注册该用户
                        userRegist(userName,socket);
                        continue;
                    } else if(msg.startsWith("@") && msg.contains("-")){ // 用户选择私聊, 私聊的格式为:@userName-私聊信息
                        // 用户必须先注册
                        firstStep(socket);
                        // 保存需要私聊的用户名
                        String userName = msg.split("@")[1].split("-")[0];
                        // 保存私聊的信息
                        String str = msg.split("@")[1].split("-")[1];
                        // 发送私聊信息
                        privateChat(socket,userName,str);
                        continue;
                    } else if(msg.contains("exit")){// 用户退出聊天, 用户退出格式为:包含exit
                        // 用户必须先注册
                        firstStep(socket);
                        // 执行退出流程
                        userExit(socket);
                        break;
                    } else{// 群聊信息
                        // 用户必须先注册
                        firstStep(socket);
                        // 执行群聊流程
                        groupChat(socket, msg);
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第一步必须先注册!
     * @param socket 当前客户端
     */
    private void firstStep(Socket socket) throws IOException {
        Set<Map.Entry<String,Socket>> set = map.entrySet();
        for(Map.Entry<String,Socket> entry : set){
            if(entry.getValue().equals(socket)){
                if(entry.getKey() == null){
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    printStream.println("请先进行注册操作！");
                    printStream.println("注册格式为:[用户名]");
                }
            }
        }
    }

    /**
     * 注册用户信息
     * @param userName 用户名
     * @param socket 用户客户端Socket对象
     */
    private void userRegist(String userName, Socket socket){
        map.put(userName, socket);
        Server.resetText("[用户: " + userName + "] 上线了，他的[客户端为: " + socket + "]!");
        Server.resetText("当前在线人数为:" + map.size() + "人");
    }

    /**
     * 群聊流程(将Map集合转换为Set集合,从而取得每个客户端Socket,将群聊信息发送给每个客户端)
     * @param socket 发出群聊的客户端
     * @param msg 群聊信息
     */
    private void groupChat(Socket socket,String msg) throws IOException {
        Set<Map.Entry<String,Socket>> set = map.entrySet();    // 将Map集合转换为Set集合
        String userName = null;    // 遍历Set集合找到发起群聊信息的用户
        for(Map.Entry<String,Socket> entry : set){
            if(entry.getValue().equals(socket)){
                userName = entry.getKey();
                break;
            }
        }
        Server.resetText(userName + "群聊说:" + msg);    // 在服务器上显示，用于调试
        // 遍历Set集合将群聊信息发给每一个客户端（除了自己以外）
        for(Map.Entry<String,Socket> entry : set){
            //取得客户端的Socket对象
            if (!entry.getValue().equals(socket)) {
                Socket client = entry.getValue();
                PrintStream printStream = new PrintStream(client.getOutputStream());    //取得client客户端的输出流
                printStream.println(userName + "群聊说:" + msg);
            }
        }
    }
    /**
     * 私聊流程(利用userName取得客户端的Socket对象,从而取得对应输出流,将私聊信息发送到指定客户端)
     * @param socket 当前客户端
     * @param userName 私聊的用户名
     * @param msg 私聊的信息
     */
    private void privateChat(Socket socket, String userName, String msg) throws IOException {

        String curUser = null;    // 取得当前客户端的用户名
        Set<Map.Entry<String,Socket>> set=map.entrySet();
        for(Map.Entry<String,Socket> entry : set){
            if(entry.getValue().equals(socket)){
                curUser=entry.getKey();
                break;
            }
        }
        Socket client = map.get(userName);    // 取得私聊用户名对应的客户端
        PrintStream printStream = new PrintStream(client.getOutputStream());    // 获取私聊客户端的输出流,将私聊信息发送到指定客户端
        printStream.println(curUser + "@你说:" + msg);
        Server.resetText(curUser + "私聊" + userName + "说:" + msg);    // 服务器端显示，用于调试
    }

    /**
     * 用户退出
     * @param socket
     */
    private void userExit(Socket socket){
        String userName = null;    //利用socket取得对应的Key值
        for(String key:map.keySet()){
            if(map.get(key).equals(socket)){
                userName=key;
                break;
            }
        }
        map.remove(userName,socket);    // 将userName,Socket元素从map集合中删除
        // 提醒服务器该客户端已下线
        Server.resetText("用户:"+ userName +"已下线!");
        Server.resetText("当前在线人数为:" + map.size() + "人");
    }
}
