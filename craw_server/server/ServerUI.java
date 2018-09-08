package server;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class SevUI extends JFrame {

    JTextField t_duan = new JTextField("23333");
    JLabel l_duan = new JLabel("端口号");

    JTextField t_net = new JTextField("http://www.163.com");
    JLabel l_net = new JLabel("网址");

    JTextField t_num = new JTextField("10");
    JLabel l_num = new JLabel("爬取个数");

    // JTextField t_keyWord = new JTextField();
    // JLabel l_keyWord = new JLabel("关键字");
    //
    JButton start = new JButton("开始监听");

    JButton key = new JButton("确认关键字");

    SevUI() {

	super("服务器端");

	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setLocation(500, 300);

	this.setSize(300, 200);
	this.setLayout(null);

	l_duan.setBounds(5, 5, 50, 25);
	t_duan.setBounds(60, 5, 100, 25);

	l_net.setBounds(5, 35, 50, 25);
	t_net.setBounds(60, 35, 100, 25);

	l_num.setBounds(5, 65, 60, 25);
	t_num.setBounds(60, 65, 100, 25);

	// l_keyWord.setBounds(5,95,60,25);
	// t_keyWord.setBounds(60,95,100,25);

	this.add(l_duan);
	this.add(t_duan);
	this.add(l_net);
	this.add(t_net);
	this.add(l_num);
	this.add(t_num);
	// this.add(l_keyWord);
	// this.add(t_keyWord);

	start.setBounds(180, 100, 100, 40);
	key.setBounds(60, 125, 100, 25);

	this.add(start);

	this.setVisible(true);

    }

//    public static void main(String[] args) throws Exception {
//
//	final SevUI sev = new SevUI();
//	// JOptionPane.showMessageDialog(null, "请输入网址（http://开头）,端口号,爬取数目");
//
//	sev.start.addActionListener(new ActionListener() {
//
//	    @Override
//	    public void actionPerformed(ActionEvent arg0) {
//		// TODO Auto-generated method stub
//
//		int port = Integer.parseInt(sev.t_duan.getText());
//		String url = sev.t_net.getText().trim();
//		int num = Integer.parseInt(sev.t_num.getText());
//
//		try {
//		    MultiServer s = new MultiServer(port, url, num,10);
//		    s.startLink();
//		} catch (Exception e) {
//		    // TODO Auto-generated catch block
//		    e.printStackTrace();
//		}
//		return;
//
//	    }
//	});
//
//    }
}
