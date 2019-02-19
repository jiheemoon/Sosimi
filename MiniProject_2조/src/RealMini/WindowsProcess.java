package RealMini;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class WindowsProcess // 감시프로그램
{
	private String processName;

	public WindowsProcess(String processName) {
		this.processName = processName;
	}
	// window 명령어로 프로세스를 강제로 끄는 메소드
	public void kill() throws Exception {
		if (isRunning()) {
			getRuntime().exec("taskkill /F /IM " + processName);
		}
	}
	// 
	private boolean isRunning() throws Exception {
		Process listTasksProcess = getRuntime().exec("tasklist");
		BufferedReader tasksListReader = new BufferedReader(new InputStreamReader(listTasksProcess.getInputStream()));

		String tasksLine;
		while ((tasksLine = tasksListReader.readLine()) != null) {
			// System.out.println(tasksLine); // out에 실행중인 프로그램 출력
			if (tasksLine.contains(processName)) {
				return true;
			}
		}
		return false;
	}

	private Runtime getRuntime() {
		return Runtime.getRuntime();
	}

	public static void watch() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					WindowsProcess wp = new WindowsProcess("chrome.exe");
					WindowsProcess wp2 = new WindowsProcess("iexplore.exe");
					wp.kill();
					wp2.kill();
				} catch (Exception e) {
//               e.printStackTrace();
				}
			}
		};
		timer.schedule(task, 0, 1000);// 1초마다 실행
	}
}