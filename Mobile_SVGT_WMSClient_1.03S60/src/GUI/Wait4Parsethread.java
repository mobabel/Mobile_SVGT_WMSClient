package GUI;

import javax.microedition.lcdui.*;

import GUI.MODEL.SETTING;

public class Wait4Parsethread extends Thread {
	Alert Alert_wait4parse;

	public Wait4Parsethread(Alert Alert_wait4parse) {
		this.Alert_wait4parse = Alert_wait4parse;
	}

	public void run() {

		Gauge indicator = Alert_wait4parse.getIndicator();
		for (int i = 0; i < 11; i++) {
			indicator.setValue(i);
			try {
				Thread.sleep(SETTING.ParseWaitThreadSleep_Time);
			} catch (Exception e) {
			}
		}
	}

}
