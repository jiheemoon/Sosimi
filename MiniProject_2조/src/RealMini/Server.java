package RealMini;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
	
	// vector배열로 client list로 관리, 중복가능
	Vector<ServiceThread> Clients;

	public int agree ; //찬성
	public int disagree ; //반대 
	// Server 생성자
	public Server() {
		Clients = new Vector<>();
	}

	public void addClient(ServiceThread st) {
		Clients.addElement(st); 	// 쓰레드로 여러 clinet 
	}

	public void removeClient(ServiceThread st) {
		Clients.removeElement(st);
	}
	// 메세지 보내는 메서드
	public void sendMessageAll(String msg) {
		try {
			System.out.println(msg);

			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread st = ((ServiceThread) Clients.elementAt(i));
				st.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 귓말
	public void sendMessageOne(String msg, String wisper) { 
		try {

			String[] msgArr = msg.split(" ");
			String wMsg = "";
			for (int i = 2; i < msgArr.length; i++) {
				wMsg += (msgArr[i] + " ");
			}

			System.out.println("[" + wisper + ">>" + msgArr[1] + "]" + wMsg);

			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread one = ((ServiceThread) Clients.elementAt(i));

				if (one.getUserName().equals(msgArr[1])) {
					one.sendMessage("[" + wisper + ">>" + msgArr[1] + "]" + wMsg);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 공지를 입력하는 메서드
	public void sendGongji(String msg) {
		try {
			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread st = ((ServiceThread) Clients.elementAt(i));
				st.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 투표기능 : 문자(agree)를 받아서 결과 출력 
	public void agreevote() { 	
		System.out.println("찬성표: "+agree); //서버에서만 현재 득표수 알수있
		System.out.println("반대표: "+disagree);	
		if(agree+disagree == 1 && !(Clients.size()==1)) {
			sendMessageAll("<<<< 투표가 진행되었습니다 다들 투표해주세요 ! >>>>");
		}else if((Clients.size()==1)) {
			sendMessageAll("<<<< 투표는 최소 두명이상일때 가능합니다! >>>>");
		}
		if(agree+disagree == Clients.size()&&!(Clients.size()==1)) { //사람수만큼투표가완료되면 공지판에 출력 
			if(agree>disagree) {
				sendGongji("<<< 투 표 결 과 >>> "+"   [찬성]"+"gongji");
			}else if(agree<disagree) {
				sendGongji("<<< 투 표 결 과 >>> "+"   [반대]"+"gongji");
			}else if(agree==disagree){
				sendGongji("<<< 투 표 결 과 >>> "+"   [반반(재투표)]"+"gongji");
			}
			agree = 0; //새로운투표를 위해 찬성반대표 초기화
			disagree = 0;
		}	
		for(int i = 0 ; i < Clients.size(); i++) { //중복방지한거 다시 true로 
			ServiceThread st = ((ServiceThread) Clients.elementAt(i));
			st.votting = true;
		}
	}
	//  client와 server를 thread를 통해  연결 
	public static void main(String[] args) {
		Server server;
		ServerSocket serverSocket = null;
		int port = 8888;
		server = new Server();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("연결 실패");
			System.exit(1);
		}
		System.out.println("서버 \n" + serverSocket + "\n에서 연결을 기다립니다.");
		try {
			while (true) {
				Socket serviceSocket = serverSocket.accept();
				ServiceThread thread = new ServiceThread(server, serviceSocket);
				thread.start(); // run 실행
				server.addClient(thread);
			}
		} catch (Exception e) {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
