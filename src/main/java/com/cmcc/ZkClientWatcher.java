package com.cmcc;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class   ZkClientWatcher implements Watcher {
	 // 集群连接地址
	 private static final String CONNECT_ADDRES = "10.136.106.146:2181,10.136.106.147:2181,10.136.106.148:2181";
	 // 会话超时时间
	 private static final int SESSIONTIME = 2000;
	 // 信号量,让zk在连接之前等待,连接成功后才能往下走.
	 private static final CountDownLatch countDownLatch = new   CountDownLatch(1);
	 private static String LOG_MAIN = "【main】 ";
	 private ZooKeeper zk;
	 
	 public void createConnection(String connectAddres, int sessionTimeOut) {
		 try {
			 zk = new ZooKeeper(connectAddres, sessionTimeOut, this);
			 System.out.println(LOG_MAIN + "zk 开始启动连接服务器....");
			 countDownLatch.await();
		 } catch (Exception e) {
			e.printStackTrace();
		 }
	 }
	 
	 public boolean createPath(String path, String data) {
		 try {
			 this.exists(path, true);
			 this.zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			 System.out.println(LOG_MAIN + "节点创建成功, Path:" + path + ",data:" + data);
		 } catch (Exception e) {
			 e.printStackTrace();
			 return false;
		 }
		 	return true;
	 }
	 
	/**
	 * 判断指定节点是否存在
	 * @param path
	 * @param needWatch
	 * @return
	 */
	 public Stat exists(String path, boolean needWatch) {
		 try {
			 return this.zk.exists(path, needWatch);
		 }   catch (Exception e) {
			 e.printStackTrace();
			 return null;
		 }
	 }
	 
	 /**
	  * 更新路徑节点
	  * @param path
	  * @param data
	  * @return
	  * @throws KeeperException
	  * @throws InterruptedException
	  */
	 public boolean updateNode(String path,String data) throws KeeperException, InterruptedException {
		 exists(path, true);
		 this.zk.setData(path, data.getBytes(), -1);
		 return false;
	 }
	 
	 /**
	  * 删除路径节点
	  * @param path
	  * @return
	  * @throws KeeperException
	  * @throws InterruptedException
	  */
	 public boolean deleteNode(String path)throws KeeperException, InterruptedException {
		 exists(path, true);
		 this.zk.delete(path, -1);
		 return false;
	 }
	 
	 /**
	  * 获取节点下的数据
	  * @param path
	  * @return
	  * @throws KeeperException
	  * @throws InterruptedException
	  */
	 public String getNodeData(String path)throws KeeperException, InterruptedException{
		 exists(path, true);
		 return new String(this.zk.getData(path, true, null));
	 }
	 
	 /**
	  * 根据路径获取下面的子节点
	  * @param path
	  */
	 public void getChildNode(String path) {
	   try {
            //第一个参数为组名，即znode路径；第二个参数是否设置观察标识，如果为true，那么一旦znode状态改变，当前对象的Watcher会被触发
            List<String> children = this.zk.getChildren(path, true);
            if (children.isEmpty()) {
                System.out.printf("No childNode in path %s\n", path);
                System.exit(1);
            }
            for (String child : children) {
                System.out.println(child);
            }
        } catch (KeeperException.NoNodeException ex) {
            System.out.printf("node %s does not exist\n", path);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
	 }
	 
	 public void process(WatchedEvent watchedEvent) {	 
		 // 获取事件状态
		 KeeperState keeperState = watchedEvent.getState();
		 // 获取事件类型
		 EventType eventType = watchedEvent.getType();
		 //zk 路径
		 String path = watchedEvent.getPath();
		 System.out.println("进入到 process() keeperState:" + keeperState + ", eventType:" + eventType + ", path:" + path);
		 // 判断是否建立连接
		 if (KeeperState.SyncConnected == keeperState) {
			 if (EventType.None == eventType) {
				 // 如果建立建立成功,让后程序往下走
				 System.out.println(LOG_MAIN + "zk 建立连接成功!");
				 countDownLatch.countDown();
			 } else if (EventType.NodeCreated == eventType) {
				 System.out.println(LOG_MAIN + "事件通知,新增node节点" + path);
			 } else if (EventType.NodeDataChanged == eventType) {
				 try {
					System.out.println(new String(this.zk.getData(path, true, null)));
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 System.out.println(LOG_MAIN + "事件通知,当前node节点" + path + "被修改....");
			 }	else if (EventType.NodeDeleted == eventType) {
				 System.out.println(LOG_MAIN + "事件通知,当前node节点" + path + "被删除....");
			 }	 
		 }
		 System.out.println("--------------------------------------------------------");
	 }
	 
	 public static void main(String[] args) throws KeeperException, InterruptedException {
		 ZkClientWatcher   zkClientWatcher = new ZkClientWatcher();
		 zkClientWatcher.createConnection(CONNECT_ADDRES, SESSIONTIME);
//		 boolean createResult =   zkClientWatcher.createPath("/p15", "pa-644064");
		 zkClientWatcher.updateNode("/p15","7894561");
//		 zkClientWatcher.deleteNode("/p15");
		 System.out.println(zkClientWatcher.getNodeData("/p15"));
	 }
	 
	}
