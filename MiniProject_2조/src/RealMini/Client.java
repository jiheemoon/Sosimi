package RealMini;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Client extends JFrame implements Runnable, ActionListener {
	private JPanel p1, p2;
	private GridBagLayout gb;
	private GridBagConstraints gbc;
	private JLabel notice;

	private static JTextPane tp1;
	private static JTextPane tp2;
	private JTextField tf;
	private JScrollPane sp;

	private static StyledDocument doc;
	private StyledDocument doc2;

	private JButton bt, bt1, bt2, bt3, bt4;
	private JButton gongjibt;

	private JMenu menu;
	private JMenuBar mb;
	private JMenuItem m1, m3, m4;

	private Dialog d1;
	private JFileChooser fc;

	private JTextField gongjitf;

	private JPopupMenu popupMenu;
	private JButton btE1, btE2, btE3, btE4, btE5, btE6, btE7, btE8, btE9, btE10; // 이모티콘 btn

	private JFrame votf; // 투표창
	private JButton btn1;
	private JButton btn2;

	// 서버통신소켓
	private Socket clientsocket;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private FileOutputStream fout;
	private BufferedInputStream bi;

	public Client() {
		p1 = new JPanel();
		p2 = new JPanel();

		gb = new GridBagLayout();
		gbc = new GridBagConstraints();

		setLayout(gb);
		gbc.fill = GridBagConstraints.BOTH;

		// menu
		menu = new JMenu("메뉴");
		mb = new JMenuBar();
		m1 = new JMenuItem("파일업로드");
		// m2 = new JMenuItem("Q&A 다운로드");
		m3 = new JMenuItem("찬성반대 투표");
		m4 = new JMenuItem("일정등록");

		m4.addActionListener(new MyEvent()); // 공지 이벤트

		d1 = new Dialog(this, "공지등록", true); // 공지 레이아웃
		d1.setSize(500, 100);
		d1.addWindowListener(new MyEvent());
		gongjitf = new JTextField();
		gongjibt = new JButton("공지등록");
		d1.add(gongjitf, BorderLayout.CENTER);
		d1.add(gongjibt, BorderLayout.EAST);

		fc = new JFileChooser("./"); // 파일업로드
		m1.addActionListener(this);

		m3.addActionListener(new MyEvent()); // 투표기능

		// TextArea
		tp1 = new JTextPane();
		tp2 = new JTextPane();
		doc = tp1.getStyledDocument();
		doc2 = tp2.getStyledDocument();

		notice = new JLabel("< Notice >");
		notice.setFont(new Font("맑은 고딕", Font.ITALIC, 15));
		tp2.insertComponent(notice);

		tp1.setBackground(Color.WHITE);
		tp2.setBackground(Color.LIGHT_GRAY);
		tp2.setPreferredSize(new Dimension(200, 375));

		tp1.setEditable(false);
		tp2.setEditable(false);

		tf = new JTextField();
		tf.setPreferredSize(new Dimension(200, 30));
		tf.addActionListener(new InputListener());

		// JButton
		bt = new JButton("전송");
		bt.setBackground(Color.WHITE);
		bt1 = new JButton("모르겠어요");
		bt2 = new JButton("스크롤up");
		bt3 = new JButton("에러나요..ㅠ");
		bt1.setBackground(Color.WHITE);
		bt2.setBackground(Color.WHITE);
		bt3.setBackground(Color.WHITE);
		bt4 = new JButton("♥");
		bt4.setBackground(Color.PINK);
		bt4.setForeground(Color.WHITE);

		popupMenu = new JPopupMenu();

		addPopup(bt4, popupMenu);

		// 이모티콘 버튼 생성
		// btE1
		btE1 = new JButton("릴렉스..");
		btE1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("Relax.gif");
			}
		});
		popupMenu.add(btE1);
		// btE2
		btE2 = new JButton("슬픔");
		btE2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("쏴리.gif");
			}
		});
		popupMenu.add(btE2);
		// btE3
		btE3 = new JButton("가즈아!"); // 버튼 생성후 이벤트 걸기
		btE3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("기기.gif");
			}
		});
		popupMenu.add(btE3);
		// btE4
		btE4 = new JButton("축하해"); // 버튼 생성후 이벤트 걸기
		btE4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("축축.gif");
			}
		});
		popupMenu.add(btE4);
		// btE5
		btE5 = new JButton("한조대기중.."); // 버튼 생성후 이벤트 걸기
		btE5.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("Heart.gif");
			}
		});
		popupMenu.add(btE5);
		// btE6
		btE6 = new JButton("따봉!"); // 버튼 생성후 이벤트 걸기
		btE6.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("좋아.gif");
			}
		});
		popupMenu.add(btE6);
		// btE7
		btE7 = new JButton("따봉2"); // 버튼 생성후 이벤트 걸기
		btE7.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("GoodJob.gif");
			}
		});
		popupMenu.add(btE7);
		// btE8
		btE8 = new JButton("브이~"); // 버튼 생성후 이벤트 걸기
		btE8.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("V.gif");
			}
		});
		popupMenu.add(btE8);
		// btE9
		btE9 = new JButton("메롱"); // 버튼 생성후 이벤트 걸기
		btE9.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("008.png");
			}
		});
		popupMenu.add(btE9);
		// btE10
		btE10 = new JButton("땀땀.."); // 버튼 생성후 이벤트 걸기
		btE10.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				out.println("재시험.gif");
			}
		});
		popupMenu.add(btE10);

		// JScorollPane
		sp = new JScrollPane(tp1);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setPreferredSize(new Dimension(400, 300));
		sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());

		// layout 배치 //
		mb.add(menu);
		menu.add(m1);
		// menu.add(m2);
		menu.add(m3);
		menu.add(m4);
		this.setJMenuBar(mb);

		p1.add(bt1);
		p1.add(bt2);
		p1.add(bt3);

		p2.add(bt4);
		p2.add(tf);
		p2.add(bt);

		// 1번째줄
		gb(sp, 0, 0, 5, 5);
		gb(tp2, 5, 0, 2, 8);

		// 2번째줄
		gb(p1, 0, 5, 5, 1);

		// 3번째줄
		gb(p2, 0, 6, 5, 1);

		bt.addMouseListener(new MouseAdapter() { // 전송버튼
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == bt) {
					if (tf.getText().equals("")) {
						tf.setText("");
					} else {
						String msg = tf.getText();
						out.println(msg);
						tf.setText("");
					}
				}
			}
		});
		bt1.addMouseListener(new MouseAdapter() { // 메크로
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == bt1) {
					out.println("모르겠습니다~~!!");
				}
			}
		});
		bt2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == bt2) {
					out.println("스크롤 올려주세요 !!~");
				}
			}
		});
		bt3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == bt3) {
					out.println("에러나요ㅠ");
				}
			}
		});
		gongjibt.addMouseListener(new MouseAdapter() { // 공지등록 버튼
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == gongjibt) {
					if (gongjitf.getText().equals("")) {
						gongjitf.setText("");
					} else {
						String msg = gongjitf.getText();
						out.println(msg + "gongji");
						gongjitf.setText("");
						// tp1.setCaretPosition(doc.getLength()); //자동스크롤
					}
				}
			}
		});
	}

	// gridbag 레이아웃
	private void gb(Component obj, int x, int y, int w, int h) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;

		add(obj, gbc);
	}

	// Emoji Popup 버튼 이벤트
	private void addPopup(JButton component, JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if ((e.getSource() == bt4) && (e.getButton() == 1)) { // 마우스왼쪽을 눌렀을 경우에만 작동
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});

	}

	// 채팅창에 문자열 출력
	public static void addMessage(String msg) {

		StyledDocument doc = (StyledDocument) tp1.getDocument(); // 텍스트판 ( 채팅 )
		StyledDocument doc2 = (StyledDocument) tp2.getDocument();// 텍스트판 ( 공지 )

		try {
			if (msg.contains("gongji")) { // 공지창붙이지

				StringBuffer sb = new StringBuffer(msg);
				int start = sb.indexOf("gongji");
				int end = start + sb.length();
				sb.replace(start, end, "");
				String msg1 = sb.toString();

				doc2.insertString(doc2.getLength(), "\n" + msg1 + "\n", null);
//				tp2.setCaretPosition(doc.getLength()); --> null이 역할 대신함..!
			} else {
				doc.insertString(doc.getLength(), msg, null);
				tp1.setCaretPosition(doc.getLength()); // 자동스크롤 (남이보냈을때)
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// 아이콘 삽입메서드
	public static void addIcon(String img) { // 아이콘 삽입 메서드
		StyledDocument doc = (StyledDocument) tp1.getDocument();
		ImageIcon Icon = new ImageIcon(Client.class.getResource("../img/" + img));
		Image Img = Icon.getImage();
		Image newImg = Img.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		ImageIcon newIcon = new ImageIcon(newImg);

		tp1.insertIcon(newIcon); // 이미지 삽입!!!!
		tp1.setCaretPosition(doc.getLength());
	}

	// 최종적으로 출력된 문자가 메세지인지 아이콘인지 확인하여 출력
	public static void view(String msg) {
		if ((msg.contains(".png")) || (msg.contains(".gif") || (msg.contains(".jpg")))) {
			addIcon(msg);
			addMessage("\t" + time() + "\n");
		} else if (msg.contains("*파일 업로드 완료*")) {
			addMessage(msg + "\n");
			folder();
		} else {
			addMessage(msg + "\n");
		}
	}

	// 입력된 시간 출력
	public static String time() {
		Calendar cal = Calendar.getInstance(); // calendar 객체를 얻는다.
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm"); // 출력형태를 지정한다.
		String datetime = sdf.format(cal.getTime());
		String APM = null;
		if (cal.get(Calendar.AM_PM) == 0) { // 오전
			APM = "오전 ";
		} else if (cal.get(Calendar.AM_PM) == 1) { // 오후
			APM = "오후 ";
		}
		return (APM + datetime);
	}

	// 쉬는시간 점심시간알림
	public static void Ring(int h, int m) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Calendar cal = Calendar.getInstance();
				int H = cal.get(Calendar.HOUR);
				int M = cal.get(Calendar.MINUTE);

				if ((H == h) && (M == m)) {
					addMessage("\n");
					ImageIcon Icon = new ImageIcon(Client.class.getResource("../img/breaktime.png"));
					tp1.insertIcon(Icon);
					tp1.setCaretPosition(doc.getLength());
					addMessage("\n");
					timer.cancel();
				}
			}
		};
		timer.schedule(task, 0, 1000); // 1초마다 실행
	}

	// 메뉴아이템 이벤트
	class MyEvent extends WindowAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().trim().equals("일정등록")) {
				d1.setVisible(true);
			}
			if (e.getActionCommand().trim().equals("찬성반대 투표")) {

				votf = new JFrame("찬반 투표");
				votf.setBounds(900, 500, 100, 100);
				;
				btn1 = new JButton("찬성");
				btn2 = new JButton("반대");
				votf.add(btn1, BorderLayout.WEST);
				votf.add(btn2, BorderLayout.EAST);
				votf.setVisible(true);

				btn1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						out.println("agree");

						votf.dispose();
					}
				});

				btn2.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						out.println("disdisdis");

						votf.dispose();
					}
				});
			}
		}

		@Override
		public void windowClosing(WindowEvent e) { // 메뉴아이템 창 따로닫기 기능
			if (e.getSource().getClass() == Client.class) {
				System.exit(0);
			}
			if (e.getSource().getClass() == Dialog.class) {
				d1.dispose();
			}
		}
	}

	// 텍스트필드에 액션리스너 자동엔터키 감지
	class InputListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			if (tf.getText().equals("")) {
				tf.setText("");
			} else {
				String msg = tf.getText();
				out.println(msg);
				tf.setText("");
				tp1.setCaretPosition(doc.getLength()); // 자동스크롤
			}
		}
	}

	// 이름 설정 layout
	public void nameDialog() {
		String nameInput = JOptionPane.showInputDialog(this, "닉네임을 입력해주세요");

		if (nameInput == null) {
			System.exit(0);
		}
		if (nameInput.trim().equals("")) {
			while (nameInput.trim().equals("")) {
				JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요", "경고", JOptionPane.WARNING_MESSAGE);
				nameInput = JOptionPane.showInputDialog("닉네임을 입력하세요");
				if (nameInput == null) {
					System.exit(0);
				}
			}
			out.println(nameInput);
			tf.requestFocus();
		} else {
			out.println(nameInput);
			tf.requestFocus();
		}
	}

	// 파일입출력 이벤트
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().trim().equals("파일업로드")) {
			fc.setDialogTitle("업로드 할 파일을 선택하세요");
			fc.setCurrentDirectory(new File(System.getProperty("user.home") + "\\" + "Desktop")); // System.getProperty("user.home")
																									// : 사용자 홈 디렉토리
			int ret = fc.showOpenDialog(this);

			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.", " ", JOptionPane.WARNING_MESSAGE);

			} else {
				try {
					bi = new BufferedInputStream(new FileInputStream(fc.getSelectedFile()));
					fout = new FileOutputStream(new File("\\\\192.168.10.33\\공유\\" + fc.getSelectedFile().getName()));
					int a;
					byte[] readBy = new byte[1024];

					while ((a = bi.read(readBy)) != -1) {
						fout.write(readBy, 0, a);
					}

					try {
						if (bi != null) {
							fout.flush(); // 현재 버퍼에 저장되어있는 내용을 클라이언트로 전송하고 버퍼를 비운다
							bi.close();
							fout.close();
						}

						JOptionPane.showMessageDialog(null, "업로드가 완료되었습니다", "전송완료", JOptionPane.INFORMATION_MESSAGE);

						String msg = fc.getSelectedFile().getName();
						out.println(msg + "  *파일 업로드 완료*");

					} catch (IOException e1) {
						e1.printStackTrace();
					}

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// 폴더열기
	public static void folder() {
		JButton fopen = new JButton();
		fopen.setText("<HTML><font color=gray>▶ 폴더열기 </font></HTML>");
		fopen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		fopen.setBackground(Color.LIGHT_GRAY);

		tp1.insertComponent(fopen);
		try {
			doc.insertString(doc.getLength(), "\n", null);
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
		fopen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (e.getSource() == fopen) {
					fopen.setBackground(Color.PINK);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (e.getSource() == fopen) {
					fopen.setBackground(Color.LIGHT_GRAY);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				File file = new File("\\\\192.168.10.33\\공유");
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		Client l = new Client();

		l.setVisible(true);
		// l.setSize(700, 500);
		l.setResizable(false);
		l.pack(); // 꽉채움
		l.setLocationRelativeTo(null); // 정중앙에 창뜨기
		l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		l.connect("localhost", 8888);

		Thread thread = new Thread(l);
		thread.start();

		Ring(4, 25); // 쉬는시간
		Ring(5, 20);
		Ring(7, 30);
		Ring(8, 30);
		// 감시기능
		WindowsProcess.watch();
	}

	@Override
	public void run() {
		nameDialog();
		try {
			while (true) {
				view(in.readLine());
			}
		} catch (Exception e) {
			disconnect();
		}
	}

	public void connect(String host, int port) {
		try {
			clientsocket = new Socket(host, port);
			out = new PrintWriter(clientsocket.getOutputStream(), true);

			in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
		} catch (Exception e) {
			System.err.println("입출력 에러입니다");
			System.exit(1);
		}
	}

	public void disconnect() {
		try {
			in.close();
			out.close();
			clientsocket.close();
		} catch (IOException e) {
		}
	}

}
