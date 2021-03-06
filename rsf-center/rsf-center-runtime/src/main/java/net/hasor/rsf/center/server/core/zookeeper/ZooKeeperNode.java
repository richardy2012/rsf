/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.center.server.core.zookeeper;
import java.io.IOException;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface ZooKeeperNode {
    /** ZK系统中的基准节点 */
    public static final String ROOT_PATH     = "/rsf-center";
    /** servers信息保存的节点 */
    public static final String SERVER_PATH   = ROOT_PATH + "/servers";
    /** leader信息保存的节点 */
    public static final String LEADER_PATH   = ROOT_PATH + "/leader";
    /** services信息保存的节点 */
    public static final String SERVICES_PATH = ROOT_PATH + "/services";
    /** config信息保存的节点 */
    public static final String CONFIG_PATH   = ROOT_PATH + "/config";
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper(AppContext appContext) throws IOException, InterruptedException;
    /** 启动ZooKeeper*/
    public void startZooKeeper(RsfContext rsfContext, QuorumPeerConfig config) throws Throwable;
    //
    /** 监视节点改动 */
    public void watcherChildren(String nodePath, Watcher watcher) throws KeeperException, InterruptedException;
    /** 检测节点是否存在 */
    public boolean existsNode(String nodePath) throws KeeperException, InterruptedException;
    /** 查询子节点 */
    public List<String> getChildrenNode(String nodePath) throws KeeperException, InterruptedException;
    /** 创建一个节点 */
    public String createNode(ZkNodeType nodtType, String nodePath) throws KeeperException, InterruptedException;
    /** 删除一个节点 */
    public void deleteNode(String nodePath) throws KeeperException, InterruptedException;
    /** 设置或者更新数据 */
    public Stat saveOrUpdate(ZkNodeType nodtType, String nodePath, String data) throws KeeperException, InterruptedException;
    /**读取指定路径下的数据*/
    public String readData(String nodePath) throws KeeperException, InterruptedException;
}