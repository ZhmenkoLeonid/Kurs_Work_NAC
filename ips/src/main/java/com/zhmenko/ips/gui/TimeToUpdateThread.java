package com.zhmenko.ips.gui;

import com.zhmenko.ips.traffic_analyze.AnalyzeProperties;
import com.zhmenko.ips.traffic_analyze.AnalyzeThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;
import java.util.TimeZone;

public class TimeToUpdateThread extends Thread {
    private SimpleDateFormat simpleDateFormat;
    private JFrame consoleFrame;
    private long delay;
    TimeToUpdateThread(JFrame consoleFrame) {
        this.consoleFrame = consoleFrame;

        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.delay = 1000;
    }

    @Override
    public void run() {
        try {
            while (true) {
                long currentTimer = AnalyzeThread.getTimeLeftBfrUpdateMillis();
                if (currentTimer < 0) continue;
                consoleFrame.setTitle("Время до обновления статистики: " +
                        simpleDateFormat.format(new Date(currentTimer)));
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
