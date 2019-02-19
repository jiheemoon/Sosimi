package RealMini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceThread extends Thread {

	private Server server;
	private Socket socket;

	private String UserName;
	private PrintWriter out;
	private BufferedReader in;

	private static int cnt;
	private boolean b1 = true; // 말못하게
	public boolean votting = true;

	public String getUserName() {
		return UserName;
	}
	// 출력기능
	public ServiceThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}
	// 쓰레드 내부에서 욕설 필터링 
	public void sendMessage(String msg) throws IOException {
		StringBuffer sb = new StringBuffer(msg);
		String[] yok = { "새끼", "시발", "ㅅㅂ", "씨발", "개새끼", "존나", "병신", "씹", "좃", "미친", "개", "꺼져" };
		String abc = msg;
		for (int i = 0; i < yok.length; i++) {

			while (msg.contains(yok[i])) {
				int start = sb.indexOf(yok[i]);
				int end = start + yok[i].length();
				sb.replace(start, end, "**"); // 욕설필터링
				msg = sb.toString();
			}
		}
		if (out != null) {
			out.println(msg);
		}
	}
	// 도배 벙어리모드 감지 //0.5초마다 3번연속치면 벙어리 --> 타이머로
	public void dobe(int num) { 
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (num == 3) { //3번연속 0.5초안에 다음메시지를 보내면
					server.sendMessageAll(UserName + "님이 도배로인해 30초간  벙어리가 되었습니다.");
					b1 = false;
					Timer timer1 = new Timer();
					TimerTask task1 = new TimerTask() {
						@Override
						public void run() {
							server.sendMessageAll(UserName + "님이 벙어리모드가 풀렸습니다");
							b1 = true;
						}
					};
					timer1.schedule(task1, 30000);// 30초

				} else { // 한번이라도 0.5초만에 안보내면 0으로 초기화
					cnt = 0;
				}
			}
		};
		timer.schedule(task, 500); // 0.5초마다 실행
	}
	// server에 있는 메소드로 출력
	public void run() {
		try {
			System.out.println("클라이언트 \n" + socket + "\n에서 접속하였습니다.");
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			UserName = in.readLine();
			server.sendMessageAll(UserName + " 입장.");
			String inputLine;
			int num = 0;
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("/s")) {
					server.sendMessageOne(inputLine, UserName);
				} else if ((inputLine.contains(".png"))
						|| (inputLine.contains(".gif") || (inputLine.contains(".jpg")))) {
					if (b1) {
						server.sendMessageAll("[" + UserName + "]");
						server.sendMessageAll(inputLine);
					}
					cnt = cnt + 1;
					num = cnt;
					dobe(num);
				} else if (inputLine.contains("gongji")) {
					server.sendGongji("[  공지  ]" + inputLine);
				} else if (b1&&!inputLine.contains("agree")&&!inputLine.contains("disdisdis")) {
					server.sendMessageAll("[" + UserName + "]" + inputLine + "\t" + Client.time() );
					cnt = cnt + 1;
					num = cnt;
					dobe(num);
				} else if(inputLine.contains("*파일 업로드 완료*")) {
					server.sendMessageAll("[" + UserName + "]" + inputLine + "\t" + Client.time());
				}				
				//투표한거 서버로 보내주기 
				if(inputLine.contains("agree")&&votting) { //votting 중복방지 
					server.agree ++;
					server.agreevote();
					
					votting = false;
				}else if(inputLine.contains("disdisdis")&&votting) { //disagree 하면 agree에서 잡힘
					server.disagree ++;
					server.agreevote();
					
					votting = false;
				}
			}
			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			server.removeClient(this);
			server.sendMessageAll("#" + UserName + "님이 나가셨습니다.");
			System.out.println("클라이언트 \n" + socket + "\n에서 접속이 끊겼습니다...");
		}
	}

}
