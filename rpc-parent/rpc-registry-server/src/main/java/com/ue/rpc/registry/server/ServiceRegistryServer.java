package com.ue.rpc.registry.server;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc����� �ͻ���ע������ ����
 * ����ע�� ��ZK �ڸüܹ��а����ˡ�����ע����Ľ�ɫ������ע�����з������ĵ�ַ��˿ڣ����Կͻ����ṩ�����ֵĹ���
 * @ClassName: ServiceRegistryServer 
 * @author yangyue
 * @date 2017��10��12�� ����1:51:53 
 *
 */
public class ServiceRegistryServer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceRegistryServer.class);
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String registryAddress;
	
	//zookeeper�ĵ�ַ
	public ServiceRegistryServer(String registryAddress){
		this.registryAddress = registryAddress;
	}
	
	//����zookeeper����
	public void register(String data){
		if(data != null) {
			
			ZooKeeper zk = connectServer();
			if(zk != null){
				createNode(zk, data);
			}
		}
	}
	
	
	//����zookeeper���� ����
	private ZooKeeper connectServer(){
		ZooKeeper zKeeper = null;
		try {
			zKeeper = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
				
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}
		return zKeeper;
	}
	
	//�����ڵ�
	private void createNode(ZooKeeper zk, String data){
		
		try {
			byte[] bytes = data.getBytes();
			if(zk.exists(Constant.ZK_REGISTRY_PATH, null) == null) {
				zk.create(Constant.ZK_REGISTRY_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			String path = zk.create(Constant.ZK_DATA_PATH, bytes,
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.debug("create zookeeper node ({} => {})", path, data);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	
	
	
	
}
