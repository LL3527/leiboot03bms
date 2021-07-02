package com.example.bms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingDemo {
    public static void main(String[] args) throws Exception {
        Gui.create();
    }
}

class Gui {
    public static void create() {
        // 创建面板容器
        JPanel panel = new JPanel();
        // 创建基本组件
        JLabel label = new JLabel("请输入网址或 ip 地址：");
        JTextField textField = new JTextField(25);
        JTextArea textArea = new JTextArea(13, 44);
        textArea.setLineWrap(true);
        JButton btn = new JButton("Ping");
        // 将组件添加到面板
        panel.add(label);
        panel.add(textField);
        panel.add(btn);
        panel.add(textArea);

        // 创建一个顶层容器（窗口）
        JFrame jf = new JFrame("PING 程序的设计与实现");
        // 把 面板容器 作为窗口的内容面板 设置到 窗口
        jf.setContentPane(panel);
        // 设置窗口大小
        jf.setSize(550, 320);
        // 把窗口位置设置到屏幕中心
        jf.setLocationRelativeTo(null);
        // 当点击窗口的关闭按钮时退出程序（没有这一句，程序不会退出）
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 禁用最大化窗口
        jf.setResizable(false);
        // 显示面板
        jf.setVisible(true);


        //监听按钮
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = textField.getText();
                textArea.setText(null);
                System.out.println("ip 地址：" + ip);
                int index = ip.trim().indexOf(" ");
                String command = "ping " + ip;
                if (index != -1) {
                    String ipAddress = ip.substring(0, index);
                    String param = ip.substring(index + 1);
                    command = "ping " + ipAddress + " " + param;
                }
                String line = null;

                try {
                    Process pro = Runtime.getRuntime().exec(command);
                    BufferedReader buf = new BufferedReader(new InputStreamReader(
                            pro.getInputStream()));
                    while ((line = buf.readLine()) != null) {
                        textArea.append(line + "\n");
                        textArea.paintImmediately(textArea.getBounds());
                        System.out.println(line);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

            }
        });
    }
}