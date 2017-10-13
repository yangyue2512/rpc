package com.ue.rpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.rpc.constant.Constant;

/**
 * 发现服务器路径 本类用于client发现server节点的变化 ，实现负载均衡
 * @ClassName: ServiceDiscovery 
 * @author yangyue
 * @date 2017年10月13日 下午4:55:35 
 *
 */
public class ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceDiscovery.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private volatile List<String> dataList = new ArrayList<String>();

	private String registryAddress;
	
	public ServiceDiscovery(String registryAddress) {
		
		this.registryAddress = registryAddress;
		
		ZooKeeper zk = connectServer();
		if(zk != null) {
			watchNode(zk);
		}
	}
	
	/**
	 * 连接
	 * @return
	 */
	private ZooKeeper connectServer() {
		ZooKeeper zk = null;
		
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
				
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return zk;
	}
	
	private void watchNode(final ZooKeeper zk) {
		try {
			List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
				
				public void process(WatchedEvent event) {
					//当有节点改变的时候调用
					if (event.getType() == Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}
			});
			//存储服务器的所有节点
			List<String> dataList = new ArrayList<String>();
			//循环子节点
			for (String node : nodeList) {
				//获取节点中的服务器地址
				byte[] bytes = zk.getData(Constant.ZK_DATA_PATH + "/" + node, false, null);
				//存储到list中
				dataList.add(new String(bytes));
			}
			LOGGER.debug("node data: {}", dataList);
			// 将节点信息记录在成员变量
			this.dataList = dataList;
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 查找节点
	 * @return
	 */
	public String discover() {
		String data = null;
		int size = dataList.size();
		// 存在新节点，使用即可
		if (size > 0) {
			if (size == 1) {
				data = dataList.get(0);
				LOGGER.debug("using only data: {}", data);
			} else {
				data = dataList.get(ThreadLocalRandom.current().nextInt(size));
				LOGGER.debug("using random data: {}", data);
			}
		}
		return data;
	}
	
}
