package com.cmcc;

import java.util.ArrayList;
import java.util.List;

public class TreeDemo {

    public static void main(String[] args) {
        List<MyNode> nodeList = new ArrayList<>();

        MyNode node = new MyNode();
        node.setId(0);
        node.setPid(-1);
        nodeList.add(node);

        MyNode node1 = new MyNode();
        node1.setId(1);
        node1.setPid(0);
        nodeList.add(node1);

        MyNode node2 = new MyNode();
        node2.setId(2);
        node2.setPid(0);
        nodeList.add(node2);

        MyNode node3 = new MyNode();
        node3.setId(3);
        node3.setPid(1);
        nodeList.add(node3);

        MyNode node4 = new MyNode();
        node4.setId(4);
        node4.setPid(1);
        nodeList.add(node4);

        MyNode node5 = new MyNode();
        node5.setId(5);
        node5.setPid(3);
        nodeList.add(node5);
        List<MyNode> resultList = new ArrayList<>();
        // 给出 id 为 0 的父类，求其子类与其的层级关系
        List<MyNode> result = getSon(nodeList, node5,resultList);
        
        System.out.println(result.size());
        
        for(MyNode myNode : result) {
        	System.out.println("Id is "+myNode.getId()+"===========pId is "+myNode.getPid());
        }

    }

    public static List<MyNode> getSon(List<MyNode> nodeList, MyNode node,List<MyNode> resultList){
    	if(node.getPid()<0) {
    		return resultList;
    	}else {
    		MyNode parentNode = null;
    		for (MyNode myNode : nodeList) {
    		     if(myNode.getId()==node.getPid()) {
    		    	 resultList.add(myNode);
    		    	 parentNode = myNode;
    		    	 break;
    		     }
            }
    		 getSon(nodeList,parentNode,resultList);
    	}
		return resultList;
    	
    }
   
}
