package com.test.sku.jdbc;

import java.text.SimpleDateFormat;

public class EmpVO {
	//empno,ename,sal,deptno,hiredate,mgr,comm
	private int empno;
	private String ename;
	private int sal;
	private int deptno;
	private String job;
	private java.sql.Date hiredate;
	private int mgr;
	private float comm;
	

	public EmpVO() {}
	public EmpVO(int empno) {
		this.empno = empno;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sHiredate = sdf.format(hiredate);
		String str = String.format("%d\t%s\t%d\t%d\t%-10s\t%s",
				empno,ename,sal,deptno,job,sHiredate);
		return str;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		EmpVO other = (EmpVO) obj;
		return this.getEmpno()==other.getEmpno();
	}

	public int getEmpno() {
		return empno;
	}

	public void setEmpno(int empno) {
		this.empno = empno;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public int getSal() {
		return sal;
	}

	public void setSal(int sal) {
		this.sal = sal;
	}

	public int getDeptno() {
		return deptno;
	}

	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public java.sql.Date getHiredate() {
		return hiredate;
	}

	public void setHiredate(java.sql.Date hiredate) {
		this.hiredate = hiredate;
	}

	public int getMgr() {
		return mgr;
	}

	public void setMgr(int mgr) {
		this.mgr = mgr;
	}

	public float getComm() {
		return comm;
	}

	public void setComm(float comm) {
		this.comm = comm;
	}
	
}
