package nguyentrinhan70.example.com.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.print.DocFlavor.STRING;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class KhachHangUI extends JFrame {
	DefaultTableModel dtmKhachHang;
	JTable tblKhachHang;

	Connection conn = null;
	Statement statement = null;
	ResultSet resultSet = null;

	JButton btnFirst, btnLast, btnNext, btnPrevious;

	JTextArea txtThongTin;
	JTextField txtMa, txtTen, txtNs;

	JButton btnLuu, btnDelete;
	int position = 0;

	public KhachHangUI(String title){
		super(title);
		addControls();
		addEvents();
		ketNoiCoSoDuLieu();
		hienThiToanBoKhachHang();
	}

	private void hienThiToanBoKhachHang() {
		// TODO Auto-generated method stub
		try{
			String sql = "select * from khachhang";
			resultSet = statement.executeQuery(sql);
			dtmKhachHang.setRowCount(0);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			while(resultSet.next()){
				String ma = resultSet.getString("MaKH");
				String ten = resultSet.getString("TenKH");
				Date ns = resultSet.getDate("NamSinh");
				Vector<Object> vec = new Vector<>();
				vec.add(ma);
				vec.add(ten);
				vec.add(sdf.format(ns));
				dtmKhachHang.addRow(vec);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	private void ketNoiCoSoDuLieu() {
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = 
					"jdbc:sqlserver://TRINHANNGUYEN\\SQLEXPRESS:1433; databaseName=dbKhachHang; integratedSecurity = true;";
			conn = DriverManager.getConnection(connectionUrl);
			//statement = conn.createStatement(); //chỉ di chuyển tới.
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY); //chỉ di chuyển tới.

		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	private void addEvents() {
		// TODO Auto-generated method stub
		btnLast.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					resultSet.last();
					//txtThongTin.setText(resultSet.getString(2));
					showDetail(resultSet);
				}catch(Exception exception)
				{
					exception.printStackTrace();
				}

			}
		});

		btnFirst.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try{
					resultSet.first();
					showDetail(resultSet);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					position ++;
					if(resultSet.isLast()==true)
						position=1;
					resultSet.absolute(position);
					showDetail(resultSet);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});

		btnPrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					position--;
					if(resultSet.isFirst()==true)
						position=1;
					//position = resultSet.getBytes(resultSet.isLast());
					//resultSet.absolute(position);
					showDetail(resultSet);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		btnLuu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				xuLyLuuKhachHang();

			}
		});
		
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				xuLyXoa();
				
			}
		});
	}

	protected void xuLyXoa() {
		// TODO Auto-generated method stub
		boolean  kt = kiemTraMaTonTai(txtMa.getText());
		if(kt==false){
			JOptionPane.showConfirmDialog(null, "Mã  "+txtMa.getText() +
					" không tồn tại nên không xóa được");
		}
		else
		{
			int ret = JOptionPane.showConfirmDialog(null, 
					"Bạn có muốn xóa không", "Xác nhận", JOptionPane.OK_CANCEL_OPTION);
			if(ret ==JOptionPane.CANCEL_OPTION)
				return;
			else
			{
				try{
					String sql ="Delete from KhachHang where makh = '"+txtMa.getText()+"'";
					int kq = statement.executeUpdate(sql);
					if(kq>0){
						hienThiToanBoKhachHang();
					}
				}catch(Exception ex){
					
				}
			}
			
		}
	}

	public boolean kiemTraMaTonTai(String ma){
		try{
			String sql = "select * from KhachHang where makh = '"+ma+"'";
			ResultSet resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	protected void xuLyLuuKhachHang() {
		// TODO Auto-generated method stub
		if(kiemTraMaTonTai(txtMa.getText())){
			
			int ret = JOptionPane.showConfirmDialog(null, "Mã ["+txtMa.getText() +"] đã tồn tại",
					"Bạn có muốn cập nhật không",JOptionPane.YES_NO_OPTION);
			if(ret==JOptionPane.NO_OPTION)
				return;
			try{
				String sql = "Update KhachHang set tenKh =N'"+txtTen.getText()+"',"
						+ "Namsinh = '"+txtNs.getText()+"'  where makh = '"+txtMa.getText()+"'";
				int x = statement.executeUpdate(sql);
				if(x>0)
					hienThiToanBoKhachHang();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return ;
		}
		else{
			try{
				String sql = "Insert into KhachHang values('"+txtMa.getText()+"',"
						+ "'"+txtTen.getText()+"','"+txtNs.getText()+"','mn3')";
				int x = statement.executeUpdate(sql);
				if(x>0){
					hienThiToanBoKhachHang();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private void addControls() {
		// TODO Auto-generated method stub
		Container con = getContentPane();
		con.setLayout(new BorderLayout());
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		dtmKhachHang = new DefaultTableModel();
		dtmKhachHang.addColumn("Mã khách hàng");
		dtmKhachHang.addColumn("Tên khách hàng");
		dtmKhachHang.addColumn("Năm sinh");
		tblKhachHang = new JTable(dtmKhachHang);
		JScrollPane scTable = new JScrollPane(tblKhachHang, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pnMain.add(scTable,BorderLayout.CENTER);
		con.add(pnMain, BorderLayout.CENTER);

		JPanel  pnNorth = new JPanel();
		pnNorth.setLayout(new BorderLayout());
		pnMain.add(pnNorth,BorderLayout.NORTH);
		btnFirst = new JButton("|<");
		btnLast = new JButton(">|");
		btnNext = new JButton(">>");
		btnPrevious = new JButton("<<");
		JPanel pnNorthOfNorth = new JPanel();

		pnNorthOfNorth.add(btnFirst);
		pnNorthOfNorth.add(btnPrevious);
		pnNorthOfNorth.add(btnNext);
		pnNorthOfNorth.add(btnLast);
		pnNorth.add(pnNorthOfNorth, BorderLayout.NORTH);



		JPanel pnThongTinChiTiet = new JPanel();
		pnThongTinChiTiet.setLayout(new BoxLayout(pnThongTinChiTiet, BoxLayout.Y_AXIS));
		pnMain.add(pnThongTinChiTiet, BorderLayout.SOUTH);
		JPanel pnMa = new JPanel();
		pnMa.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblMa = new JLabel("Mã:");
		txtMa = new JTextField(20);
		pnMa.add(lblMa);
		pnMa.add(txtMa);
		pnThongTinChiTiet.add(pnMa);

		JPanel pnTen = new JPanel();
		pnTen.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTen = new JLabel("Tên:");
		txtTen = new JTextField(20);
		pnTen.add(lblTen);
		pnTen.add(txtTen);
		pnThongTinChiTiet.add(pnTen);

		JPanel pnNs = new JPanel();
		pnNs.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblNs = new JLabel("Năm sinh:");
		txtNs = new JTextField(20);
		pnNs.add(lblNs);
		pnNs.add(txtNs);
		pnThongTinChiTiet.add(pnNs);

		JPanel pnButtonChiTiet = new JPanel();
		pnButtonChiTiet.setLayout(new FlowLayout(FlowLayout.LEFT));
		btnLuu = new JButton("Lưu");
		pnButtonChiTiet.add(btnLuu);
		pnThongTinChiTiet.add(pnButtonChiTiet);

		lblMa.setPreferredSize(lblNs.getPreferredSize());
		lblTen.setPreferredSize(lblNs.getPreferredSize());
		
		btnDelete = new JButton("Xóa");
		pnButtonChiTiet.add(btnDelete);
	}
	public void showWindow() {
		this.setSize(500, 550);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	private void showDetail(ResultSet rs) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		try{
			txtMa.setText(rs.getString("MaKH"));
			txtTen.setText(rs.getString("TenKH"));
			txtNs.setText(rs.getString("NamSinh"));
		}catch(Exception ex){
			ex.printStackTrace();
		}


	}

}
