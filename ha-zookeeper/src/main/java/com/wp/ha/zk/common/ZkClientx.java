package com.wp.ha.zk.common;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import org.I0Itec.zkclient.IZkConnection;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.CreateMode;

import java.util.Map;

/**
 * 自定义的zkclient for zk connection
 */
public class ZkClientx extends ZkClient {

    private static Map<String, ZkClientx> clients = new MapMaker().makeComputingMap(new Function<String, ZkClientx>() {

                                                      public ZkClientx apply(String servers) {
                                                          return new ZkClientx(servers);
                                                      }
                                                  });

    public static ZkClientx getZkClient(String servers) {
        return clients.get(servers);
    }

    public ZkClientx(String serverstring){
        this(serverstring, Integer.MAX_VALUE);
    }

    public ZkClientx(String zkServers, int connectionTimeout){
        this(new ZooKeeperx(zkServers), connectionTimeout);
    }

    public ZkClientx(String zkServers, int sessionTimeout, int connectionTimeout){
        this(new ZooKeeperx(zkServers, sessionTimeout), connectionTimeout);
    }

    public ZkClientx(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer){
        this(new ZooKeeperx(zkServers, sessionTimeout), connectionTimeout, zkSerializer);
    }

    private ZkClientx(IZkConnection connection, int connectionTimeout){
        this(connection, connectionTimeout, new ByteSerializer());
    }

    private ZkClientx(IZkConnection zkConnection, int connectionTimeout, ZkSerializer zkSerializer){
        super(zkConnection, connectionTimeout, zkSerializer);
    }


    /**
     * Create a persistent Sequential node.
     *
     * @param path
     * @param data
     * @param createParents if true all parent dirs are created as well and no {@link org.I0Itec.zkclient.exception.ZkNodeExistsException} is thrown
     * in case the path already exists
     * @throws org.I0Itec.zkclient.exception.ZkInterruptedException if operation was interrupted, or a required reconnection got interrupted
     * @throws IllegalArgumentException if called from anything except the ZooKeeper event thread
     * @throws org.I0Itec.zkclient.exception.ZkException if any ZooKeeper exception occurred
     * @throws RuntimeException if any other exception occurs
     */
    public void createPersistent(String path, Object data, boolean createParents) throws ZkInterruptedException,
                                                                                 IllegalArgumentException, ZkException,
                                                                                 RuntimeException {
        try {
            create(path, data, CreateMode.PERSISTENT);
        } catch (ZkNodeExistsException e) {
            if (!createParents) {
                throw e;
            }
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir, createParents);
            createPersistent(path, data, createParents);
        }
    }
}
