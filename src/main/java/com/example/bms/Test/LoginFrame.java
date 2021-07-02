package com.example.bms.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * 用户登录界面，完成登录后启动客户端
 * @create 2021-01-23 15:42
 */
public class LoginFrame extends JFrame implements ActionListener, KeyListener, FocusListener {
    private Socket socket;    // 客户的socket
    private String userName;    // 客户端的名称

    private JPanel jp;    // 面板
    private JTextField jtf;    // 文本框
    private JButton jb;    // 按键
    private String hintText0 = "请输入用户名";    // 输入框提示内容
    private String hintText1 = "用户名为空，请重新输入";    // 输入内容为空提示

    public LoginFrame(Socket socket) {
        this.socket = socket;
        this.init();
    }

    /**
     * 初始化
     */
    public void init() {
        // 初始化组件
        jp = new JPanel();    // 面板
        jtf = new JTextField(15);    //文本框， 指定文本框大小
        // 设置提示文字
        jtf.setText(hintText0);     // 提示用户输入用户名
        jtf.setForeground(Color.gray);     // 提示文字颜色
        jb = new JButton("确定");    // 按键
        // 将组件添加到面板上
        jp.add(jtf);
        jp.add(jb);
        // 将面板添加到窗体中
        this. add(jp);
        // 设置标题，大小， 位置， 是否可见
        this.setTitle("用户登录");
        this.setSize(400, 100);
        this.setLocation(700, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // 窗口关闭 程序退出
        this.setVisible(true);     // 窗口可见

        jb.addActionListener(this);    // 给“确定”按键绑定一个监听事件， 当前对象监听
        jtf.addKeyListener(this);    // 给文本框内容添加一个键盘监听事件
        jtf.addFocusListener(this);    // 给文本框添加一个焦点监听事件
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        SIGN();    //用户登录
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            SIGN();    //用户登录
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
        // 获取焦点时，清空提示内容
        String temp = jtf.getText();
        if (temp.equals(hintText0) || temp.equals(hintText1)) {
            jtf.setText("");
            jtf.setForeground(Color.black);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // 失去焦点时，没有输入内容，显示提示内容
        String temp = jtf.getText();
        if (temp.equals("")) {
            jtf.setText(hintText0);
            jtf.setForeground(Color.gray);
        }
    }

    /**
     * 用户登录
     */
    public void SIGN() {
        PrintStream printStream = null;
        userName = null;
        userName = jtf.getText();    // 获取用户输入的名称
        if (userName.equals("") || userName.equals(hintText0)) {
            jtf.setText(hintText1);    // 提示用户名不能为空
            jtf.setForeground(Color.gray);     // 提示文字颜色
        } else {
            String temp = "Sign:" + userName;
            try {
                //1.获取服务器端的输出流
                printStream = new PrintStream(socket.getOutputStream());
                if (!userName.equals("")) {
                    printStream.println(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.setVisible(false);    // 登录窗口设为不可见
            // 启动客户端
            Thread client = new Thread(new ClientHandle(userName, socket));
            client.start();
        }
    }
}
