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
 * ���ַ�����·�� ��������client����server�ڵ�ı仯 ��ʵ�ָ��ؾ���
 * @ClassName: ServiceDiscovery 
 * @author yangyue
 * @date 2017��10��13�� ����4:55:35 
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
	 * ����
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
					//���нڵ�ı��ʱ�����
					if (event.getType() == Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}
			});
			//�洢�����������нڵ�
			List<String> dataList = new ArrayList<String>();
			//ѭ���ӽڵ�
			for (String node : nodeList) {
				//��ȡ�ڵ��еķ�������ַ
				byte[] bytes = zk.getData(Constant.ZK_DATA_PATH + "/" + node, false, null);
				//�洢��list��
				dataList.add(new String(bytes));
			}
			LOGGER.debug("node data: {}", dataList);
			// ���ڵ���Ϣ��¼�ڳ�Ա����
			this.dataList = dataList;
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * ���ҽڵ�
	 * @return
	 */
	public String discover() {
		String data = null;
		int size = dataList.size();
		// �����½ڵ㣬ʹ�ü���
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
