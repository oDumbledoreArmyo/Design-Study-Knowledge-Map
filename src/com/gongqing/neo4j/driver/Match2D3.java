package com.gongqing.neo4j.driver;

import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 10/15.
 * Edited by GongQing on 2020/08/18
 */
public class Match2D3 {

    Driver driver;

    public Match2D3(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile() {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result = session.run(
                //!!!查库语句在这!!!
                //"MATCH p=()-[]->() RETURN p"
                "match p= ()-[]-() return p"
        );


        StringBuffer nodes = new StringBuffer();
        StringBuffer links = new StringBuffer();
        nodes.append("\"nodes\":[");
        links.append("\"links\":[");

        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record);
            List<Value> list = record.values();
            for (Value v : list) {
                Path p = v.asPath();
                Iterator<Node> nodes2 = p.nodes().iterator();
                while (nodes2.hasNext()) {
                    Node node = nodes2.next();
                    //在增加节点以前，先判断是否在集合中
                    boolean isExist = nodeSet.contains(node.id());
                    if (isExist) continue;
                    Iterator<String> nodeKeys = node.keys().iterator();
                    nodes.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes.append("\"" + content + "\",");
                    }
                    nodes.append("\"id\":");
                    nodes.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes.append(",");
                    nodes.append("\"type\":");
                    nodes.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result.hasNext()) {
                        nodes.append("}");
                    } else {
                        nodes.append("},");
                    }

                }
                nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
//                System.out.println(p);

                    for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                        links.append("{");
                //        System.out.println(r);
                        int num = 0;
                        links.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                        links.append(",\"type\":\"" + r.type() + "\"");
                        links.append("},");
                    }
                    links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));
            }
                nodes.append(",");
                links.append(",");

            }

            nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
            links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));

            nodes.append("]");
            links.append("]");
            System.out.println(nodes.toString());
            System.out.println(links.toString());
            String resultJson = "{" + nodes + "," + links + "}";    //
            System.out.println(resultJson);
//        System.out.println(nodes.toString());


            try {
                FileOutputStream fos = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon.json");
                fos.write(resultJson.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }



    public static void main(String... args)
    {
        Match2D3 example = new Match2D3("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile();

    }

}
