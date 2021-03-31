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
public class Match2D3_data {

    Driver driver;

    public Match2D3_data(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_data()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_data = session.run(
                //!!!查库语句在这!!!
             "MATCH p = (:Data)-[]->(:Data) RETURN p"
                    );



        StringBuffer nodes_data = new StringBuffer();
        StringBuffer links_data = new StringBuffer();
        nodes_data.append("\"nodes_data\":[");
        links_data.append("\"links_data\":[");

        while (result_data.hasNext()) {
            Record record = result_data.next();
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
                    nodes_data.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_data.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_data.append("\"" + content + "\",");
                    }
                    nodes_data.append("\"id\":");
                    nodes_data.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_data.append(",");
                    nodes_data.append("\"type\":");
                    nodes_data.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_data.hasNext()) {
                        nodes_data.append("}");
                    } else {
                        nodes_data.append("},");
                    }

                }
                nodes_data = new StringBuffer(nodes_data.toString().substring(0, nodes_data.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_data.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_data.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_data.append(",\"type\":\"" + r.type() + "\"");
                    links_data.append("},");
                }
                links_data = new StringBuffer(links_data.toString().substring(0, links_data.toString().length() - 1));
            }
            nodes_data.append(",");
            links_data.append(",");

        }
        nodes_data=new StringBuffer(nodes_data.toString().substring(0,nodes_data.toString().length()-1));
        links_data=new StringBuffer(links_data.toString().substring(0,links_data.toString().length()-1));

        nodes_data.append("]");
        links_data.append("]");
        System.out.println(nodes_data.toString());
        System.out.println(links_data.toString());
        String resultJson_data = "{"+nodes_data+","+links_data+"}";    //
        System.out.println(resultJson_data);
//        System.out.println(nodes_data.toString());


        try {
      //      FileOutputStream fos_data = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_data.json");
            //windows的路径
            // FileOutputStream fos = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon.json");
            //mac的路径
            FileOutputStream fos_data = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_data.json");
            fos_data.write(resultJson_data.getBytes());
            fos_data.close();
            fos_data.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_data example = new Match2D3_data("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_data();

    }

}
