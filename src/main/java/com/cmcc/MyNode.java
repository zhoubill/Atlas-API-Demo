package com.cmcc;

public class MyNode {
	
    private int id;
    private int pid;
    private MyNode pNode; // 存 父节点
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public MyNode getpNode() {
		return pNode;
	}
	public void setpNode(MyNode pNode) {
		this.pNode = pNode;
	}

}
