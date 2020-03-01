package com.writer;

import com.writer.audio.RecordHelper;
import com.writer.audio.TimeListener;
import com.writer.audio.WordUtil;
import com.writer.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordFrame {
    private static final String RECORD = "    请讲话";
    private static final String PLAY = "      --------";
    private static boolean isSpeaking = false;
    private static long msDuration = 5000;
    private static boolean newLine = false;
    private static String lang = "普通话";

    private static JButton recordBtn;
    private static JLabel recordLbl;
    private static JTextArea textArea;
    private static TimeListener recorderListener;

    static {
        recordBtn = new JButton("开始录入");
        recordLbl = new JLabel(PLAY);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(true);

        Font font=new Font("微软雅黑",Font.PLAIN,18);
        textArea.setFont(font);

        recordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (WordFrame.class) {
                    isSpeaking = !isSpeaking;
                    recordBtn.setText(isSpeaking ? "结束录入" : "开始录入");

                    RecordHelper recordHelper = RecordHelper.getInst();
                    if (isSpeaking) {
                        recordHelper.record(recorderListener, msDuration);
                    } else {
                        recordHelper.stop();
                    }
                }
            }
        });

        recorderListener = new TimeListener() {
            @Override
            public void timeUpdated(long seconds) {
                synchronized (WordFrame.class) {
                    recordLbl.setText(String.format("%s(%d秒)", RECORD, msDuration / 1000 - seconds));
                }
            }

            @Override
            public void stopped(long seconds) {
                recordLbl.setText(PLAY);
                String text = WordUtil.getWord(lang);
                if (!StrUtil.isEmpty(text)) {
                    textArea.append(text);
                    if (newLine) {
                        textArea.append("\n");
                    }
                }

                synchronized (WordFrame.class) {
                    if (isSpeaking) {
                        recordLbl.setText(String.format("%s(%d秒)", RECORD, msDuration / 1000));
                        RecordHelper recordHelper = RecordHelper.getInst();
                        recordHelper.record(recorderListener, msDuration);
                    }
                }
            }
        };
    }

    public static JFrame showFrame() {
        // create frame
        final JFrame frame = new JFrame("Writer - AI语音转换文字");
        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(Box.createVerticalStrut(50));

        // buttons
        Box configBox = Box.createHorizontalBox();
        addConfig(configBox);

        configBox.add(Box.createHorizontalStrut(10));
        topPanel.add(configBox, BorderLayout.WEST);

        Box recordBox = Box.createVerticalBox();
        recordBox.add(recordBtn);
        recordBox.add(recordLbl);
        topPanel.add(recordBox, BorderLayout.CENTER);

        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // show panel
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // do work
        frame.getRootPane().setDefaultButton(recordBtn);
        recordBtn.doClick();

        return frame;
    }

    private static void addConfig(Box configBox) {
        configBox.add(Box.createHorizontalStrut(5));

        configBox.add(new JLabel("请选择语言: "));
        final JComboBox lanComboBox = new JComboBox();
        configBox.add(lanComboBox);

        configBox.add(Box.createHorizontalStrut(10));
        configBox.add(new JLabel("请选择录音时长(秒): "));
        final JComboBox msComboBox = new JComboBox();
        configBox.add(msComboBox);

        configBox.add(Box.createHorizontalStrut(10));
        final JCheckBox lineChkBox = new JCheckBox("增加新行");
        configBox.add(lineChkBox);

        // new line
        lineChkBox.setSelected(newLine);
        lineChkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.printf("New line selected: %s\n", lineChkBox.isSelected());
                synchronized (WordFrame.class) {
                    newLine = lineChkBox.isSelected();
                }
            }
        });

        // language
        lanComboBox.addItem("普通话");
        lanComboBox.addItem("English");

        lanComboBox.setSelectedItem(lang);
        lanComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String langStr = (String) lanComboBox.getSelectedItem();
                System.out.printf("Language selected: %s\n", langStr);
                synchronized (WordFrame.class) {
                    lang = langStr;
                }
            }
        });

        // duration
        msComboBox.addItem("3");
        msComboBox.addItem("5");
        msComboBox.addItem("10");
        msComboBox.addItem("15");
        msComboBox.addItem("20");
        msComboBox.addItem("30");

        msComboBox.setSelectedItem(String.valueOf(msDuration / 1000));
        msComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemStr = (String) msComboBox.getSelectedItem();
                System.out.printf("Duration selected: %s\n", itemStr);
                if (itemStr != null) {
                    synchronized (WordFrame.class) {
                        msDuration = Integer.valueOf(itemStr) * 1000;
                    }
                }
            }
        });
    }
}
