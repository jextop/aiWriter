package com.writer;

import com.writer.audio.RecordHelper;
import com.writer.audio.TimeListener;
import com.writer.audio.WordUtil;
import com.writer.util.FontUtil;
import com.writer.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordFrame {
    private static final int SIZE_MAX = 20;
    private static final int SIZE_MIN = 10;

    private static String service = WordUtil.AI_BAIDU;
    private static String lang = WordUtil.LANG_CN;
    private static long msDuration = 5000;
    private static boolean newLine = true;

    private static final String RECORD = "    请讲话";
    private static final String PLAY = "      --------";
    private static boolean isSpeaking = false;

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

        Font font = new Font("微软雅黑", Font.PLAIN, 18);
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
                String text = WordUtil.getWord(service, lang);
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
        final JFrame frame = new JFrame("aiWriter - 智能录音笔");
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(Box.createVerticalStrut(50));

        // configs
        Box configBox = Box.createHorizontalBox();
        addConfig(configBox);

        configBox.add(Box.createHorizontalStrut(10));
        topPanel.add(configBox, BorderLayout.WEST);

        // buttons
        Box recordBox = Box.createVerticalBox();
        recordBox.add(recordBtn);
        recordBox.add(recordLbl);
        topPanel.add(recordBox, BorderLayout.CENTER);

        // control behaviors
        Box controlBox = Box.createHorizontalBox();
        addButtons(controlBox);
        topPanel.add(controlBox, BorderLayout.EAST);

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

        // 云服务
        configBox.add(new JLabel("请选择: "));
        final JComboBox srvComboBox = new JComboBox();
        configBox.add(srvComboBox);

        srvComboBox.addItem(service);
        srvComboBox.setSelectedItem(service);

        srvComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String srvStr = (String) srvComboBox.getSelectedItem();
                System.out.printf("Service selected: %s\n", srvStr);
                synchronized (WordFrame.class) {
                    service = srvStr;
                }
            }
        });

        // language
        configBox.add(new JLabel("语言: "));
        final JComboBox lanComboBox = new JComboBox();
        configBox.add(lanComboBox);

        lanComboBox.addItem(lang);
        lanComboBox.addItem(WordUtil.LANG_EN);

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
        configBox.add(Box.createHorizontalStrut(10));
        configBox.add(new JLabel("录音时长(秒): "));
        final JComboBox msComboBox = new JComboBox();
        configBox.add(msComboBox);

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

        // new line
        configBox.add(Box.createHorizontalStrut(10));
        final JCheckBox lineChkBox = new JCheckBox("增加新行");
        configBox.add(lineChkBox);

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
    }

    private static void addButtons(Box controlBox) {
        // font
        controlBox.add(new JLabel("请选择: "));

        final JComboBox fontComboBox = new JComboBox();
        controlBox.add(fontComboBox, BorderLayout.EAST);

        Font font = textArea.getFont();
        fontComboBox.addItem(font.getName());
        String[] fontArr = FontUtil.getFonts();
        for (String fontStr : fontArr) {
            fontComboBox.addItem(fontStr);
        }

        fontComboBox.setSelectedItem(font.getName());
        fontComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fontStr = (String) fontComboBox.getSelectedItem();
                System.out.printf("Font selected: %s\n", fontStr);
                synchronized (WordFrame.class) {
                    Font font = textArea.getFont();
                    font = new Font(fontStr, font.getStyle(), font.getSize());
                    textArea.setFont(font);
                }
            }
        });

        // font size
        controlBox.add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        final JComboBox sizeComboBox = new JComboBox();
        controlBox.add(sizeComboBox, BorderLayout.EAST);

        for (int i = SIZE_MAX; i >= SIZE_MIN; i--) {
            sizeComboBox.addItem(String.valueOf(i));
        }

        String defaultSize = String.valueOf(font.getSize());
        sizeComboBox.setSelectedItem(defaultSize);

        sizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sizeStr = (String) sizeComboBox.getSelectedItem();
                System.out.printf("Font size selected: %s\n", sizeStr);
                synchronized (WordFrame.class) {
                    Font font = textArea.getFont();
                    font = new Font(font.getName(), font.getStyle(), Integer.valueOf(sizeStr));
                    textArea.setFont(font);
                }
            }
        });
    }
}
