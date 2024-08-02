package com.test.sku.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmpDAO2 {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private Connection getConn()
	{
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:xe", "SCOTT", "TIGER");
			return conn;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PageItem getPage(int page, int ipp) {
		String sql = "SELECT * FROM\r\n"
				+ "(\r\n"
				+ "    SELECT t2.*, TRUNC((RN-1)/? + 1) AS page FROM\r\n"
				+ "    ( \r\n"
				+ "        SELECT t.*, ROWNUM RN FROM\r\n"
				+ "        (   \r\n"
				+ "            SELECT h.* FROM\r\n"
				+ "            (\r\n"
				+ "                SELECT empno, LPAD('  ', (LEVEL - 1)*3, '    ') || ename \"이름\", deptno, sal, mgr, hiredate, job FROM emp2\r\n"
				+ "                START WITH mgr IS NULL \r\n"
				+ "                CONNECT BY PRIOR empno = mgr  \r\n"
				+ "                ORDER SIBLINGS BY ename\r\n"
				+ "            )h\r\n"
				+ "        )t\r\n"
				+ "    )t2\r\n"
				+ ") CROSS JOIN (SELECT CEIL(COUNT(*)/?) total FROM emp2) WHERE page=?";
		conn = getConn();
		
		try {
			pstmt = conn.prepareStatement(sql);
			//int end = page * ipp; 	//끝번호가 나와 ipp 원래 sql에서는 3이었어
			//int start = end-(ipp-1);	//ipp가 3이었으니까
			//pstmt.setInt(1, start);
			//pstmt.setInt(2, end);
			pstmt.setInt(1, ipp);//5개
			pstmt.setInt(2, ipp);
			pstmt.setInt(3, page);
			rs = pstmt.executeQuery();
			List<EmpVO> list = new ArrayList<>();
			PageItem pageItems = new PageItem();
			int i = 0;
			while(rs.next()) {
				if(i==0) {
					int crrPage = rs.getInt("PAGE");
					int totalPages = rs.getInt("TOTAL");
					pageItems.setCurrPage(crrPage);
					pageItems.setTotalPages(totalPages);
				}
				int empno = rs.getInt("EMPNO");
				String ename = rs.getString("이름");
				java.sql.Date hiredate = rs.getDate("HIREDATE");
				int salary = rs.getInt("SAL");
				String job = rs.getString("JOB");
				int deptno = rs.getInt("DEPTNO");
				
				EmpVO emp = new EmpVO();
				emp.setEmpno(empno);
				emp.setEname(ename);
				emp.setHiredate(hiredate);
				emp.setSal(salary);
				emp.setJob(job);
				emp.setDeptno(deptno);
				list.add(emp);
				i++;
			}
			pageItems.setList(list);
			return pageItems;
			
		} catch (SQLException sqle) {			
			sqle.printStackTrace();
		}
		return null;
	}
	
	
	public List<EmpVO> getList() //empno,ename,sal,deptno,hiredate,mgr,comm
	{
		conn = getConn();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM emp2"); //홀 따옴표 대신 만들어줌 원문을 가져다주면됨
			rs = pstmt.executeQuery();	
			List<EmpVO> list = new ArrayList<>();
			while(rs.next())
			{
				int empno = rs.getInt("EMPNO");
				String ename = rs.getString("ENAME");
				java.sql.Date hiredate = rs.getDate("HIREDATE");
				int salary = rs.getInt("SAL");
				String job = rs.getString("JOB");
				int deptno = rs.getInt("DEPTNO");
				
				EmpVO emp = new EmpVO();
				emp.setEmpno(empno);
				emp.setEname(ename);
				emp.setHiredate(hiredate);
				emp.setSal(salary);
				emp.setJob(job);
				emp.setDeptno(deptno);
				list.add(emp);
			}
			return list;
		}catch(SQLException sqle) {
			sqle.printStackTrace();
		}finally {
			closeAll();
		}
		return null;
	}
	
	public boolean updateSal(EmpVO emp) {
		conn = getConn();
		try {
			String sql = "UPDATE emp2 SET sal=? WHERE empno=?";	//서식 emp.? 혹은 job이 ?로 들어감
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, emp.getSal());
			pstmt.setInt(2, emp.getEmpno());
			int rows = pstmt.executeUpdate();	//데이버베이스에 적용된 행수가 나온다
			return rows>0;	//0보다 같거나 작으면 바뀐게 없다
		}catch(SQLException sqle) {
			sqle.printStackTrace();
		}
		return false;
	}
	
	
	public boolean add(EmpVO emp)
	   {//empno, ename, job, mgr, hiredate, sal
	      conn = getConn();
	      
	      try {
	         String sql = "INSERT INTO emp2(empno, ename, deptno, sal, hiredate, job) VALUES (?,?,?,?,?,?)";
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setInt(1, emp.getEmpno());
	         pstmt.setString(2, emp.getEname());
	         pstmt.setInt(3, emp.getDeptno());
	         pstmt.setInt(4, emp.getSal());
	         pstmt.setDate(5, emp.getHiredate());   // 현재 날짜를 쓰고 싶으면 이 자리에 SYSDATE 사용
	         pstmt.setString(6, emp.getJob());

	          
	         int rows = pstmt.executeUpdate();
	         return rows>0;
	         
	      } catch (SQLException e) {
	         
	         e.printStackTrace();
	      }
	      
	      return false;
	      
	   }
	
	public List<EmpVO> findByJob(String job) //직무가 같은면 여러명 나오니까
	{
		conn = getConn();
		try {			
			//SELECT * FROM emp2 WHERE job = 'MANAGER';
			String sql = "SELECT empno,ename,hiredate,sal,job FROM emp2 WHERE job = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, job);
			rs = pstmt.executeQuery();
			
			List<EmpVO> list = new ArrayList<>();
			while(rs.next())
			{
				int empno = rs.getInt("EMPNO");
				String ename = rs.getString("ENAME");
				java.sql.Date hiredate = rs.getDate("HIREDATE");
				int salary = rs.getInt("SAL");
				String jb = rs.getString("JOB");
				
				EmpVO emp = new EmpVO();
				emp.setEmpno(empno);
				emp.setEname(ename);
				emp.setHiredate(hiredate);
				emp.setSal(salary);
				emp.setJob(jb);
				list.add(emp);
			}
			return list;	//부서를 갖고오지 않아서 0으로 됨
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteByEmpno(int empno) {	//주의 EmpVO에 추가해야 삭제 가능 그냥 데이터베이스에만 있으면 안됨
		conn = getConn();
		try {
			String sql = "DELETE FROM emp2 WHERE empno =?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, empno);
			int rows = pstmt.executeUpdate();
			return rows >0;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}		
		return false;
	}
	
	 public List<Map<String,String>> getJoinResult(int deptno){
	      String sql = "SELECT empno, ename, e.deptno, dname, grade \"호봉\"\r\n"
	               + "FROM emp2 e JOIN dept d ON e.deptno=d.deptno\r\n"
	               + "JOIN salgrade s ON e.sal BETWEEN s.losal AND s.hisal\r\n"
	               + "WHERE e.deptno=? ORDER BY 호봉 DESC";
	      conn = getConn();
	      try {
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setInt(1, deptno);
	         rs = pstmt.executeQuery();
	         List<Map<String,String>> list = new ArrayList<>();
	         Map<String, String> map = null;
	         while(rs.next()) {
	            map = new HashMap<>();
	            int empno = rs.getInt("EMPNO");
	            String ename = rs.getString("ENAME");
	            int dno = rs.getInt("DEPTNO");
	            String dname = rs.getString("DNAME");
	            int grade = rs.getInt("호봉");
	            
	            map.put("EMPNO", ""+empno); //문자열 + 정수 하면 문자열로 됨
	            map.put("ENAME", ename);
	            map.put("DEPTNO", ""+dno);
	            map.put("DNAME", dname);
	            map.put("GRADE", ""+grade);
	            list.add(map);
	         }
	         return list;
	      }catch(SQLException sqle) {
	         sqle.printStackTrace();
	      }
	      return null;
	   }
	private void closeAll() {
		try {
			if(rs!=null) rs.close();
			if(pstmt!=null) pstmt.close();
			if(conn!=null) conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
