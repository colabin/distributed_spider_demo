//package client;
//
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JTextField;
//
//
//public class ClientUI extends JFrame{
//	
//	JButton start = new JButton("开始爬取");
//
//	JTextField t_duan = new JTextField("23333");
//	JLabel l_duan = new JLabel("端口");
//	
//	
//	JTextField t_net = new JTextField("127.0.0.1");
//	JLabel l_net = new JLabel("IP");
//	
//	JTextField t_key = new JTextField();
//	JLabel l_key = new JLabel("网页关键字");
//
//	
//	public ClientUI() {
//		// TODO Auto-generated constructor stub
//		
//         super("客户端");
//     	
//		   this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		   this.setLocation(500,300);
//		  
//			this.setSize(300, 200);
//			this.setLayout(null);
//			
//			
//			
//			l_net.setBounds(5,5,50,25);
//			t_net.setBounds(60,5,100,25);
//			
//
//			l_duan.setBounds(5,35,50,25);
//			t_duan.setBounds(60,35,100,25);
//			
//			
//			
//			
//			l_key.setBounds(5,65,50,25);
//			t_key.setBounds(60,65,100,25);
//
//			
//			this.add(l_duan);
//			this.add(t_duan);
//			this.add(l_net);
//			this.add(t_net);
//
////			this.add(l_key);
////			this.add(t_key);
//			
//	     start.setBounds(160,120,100,40);
//			this.add(start);		
//			this.setVisible(true);
//			
//			
//	}
//	
//	
//	public static void main(String[] args){
//		
//		final ClientUI cli = new ClientUI();
//		
//		// JOptionPane.showMessageDialog(null, "请输入IP地址,端口号");
//		    
//		 cli.start.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				
//				JOptionPane.showMessageDialog(null, "开始爬取，请等待");
//				
//				int port= Integer.parseInt(cli.t_duan.getText());
//			
//				
//		    	String ip =cli.t_net.getText().toString();
//				
//				Client cli1=new Client(port,ip);
//				
//		     	new Thread(cli1).start();
//			}
//			
//		});
//		 
//		 
//		
//
//	}
//}
