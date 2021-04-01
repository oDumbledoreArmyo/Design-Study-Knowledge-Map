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
public class Match2D3_usedto {

    Driver driver;

    public Match2D3_usedto(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_usedto()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_usedto = session.run(
                //!!!查库语句在这!!!
             "MATCH p = ()-[:使用]->() RETURN p"
                    );



        StringBuffer nodes_usedto = new StringBuffer();
        StringBuffer links_usedto = new StringBuffer();
        nodes_usedto.append("\"nodes_usedto\":[");
        links_usedto.append("\"links_usedto\":[");

        while (result_usedto.hasNext()) {
            Record record = result_usedto.next();
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
                    nodes_usedto.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_usedto.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_usedto.append("\"" + content + "\",");
                    }
                    nodes_usedto.append("\"id\":");
                    nodes_usedto.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_usedto.append(",");
                    nodes_usedto.append("\"type\":");
                    nodes_usedto.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_usedto.hasNext()) {
                        nodes_usedto.append("}");
                    } else {
                        nodes_usedto.append("},");
                    }

                }
                nodes_usedto = new StringBuffer(nodes_usedto.toString().substring(0, nodes_usedto.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_usedto.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_usedto.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_usedto.append(",\"type\":\"" + r.type() + "\"");
                    links_usedto.append("},");
                }
                links_usedto = new StringBuffer(links_usedto.toString().substring(0, links_usedto.toString().length() - 1));
            }
            nodes_usedto.append(",");
            links_usedto.append(",");

        }
        nodes_usedto=new StringBuffer(nodes_usedto.toString().substring(0,nodes_usedto.toString().length()-1));
        links_usedto=new StringBuffer(links_usedto.toString().substring(0,links_usedto.toString().length()-1));

        nodes_usedto.append("]");
        links_usedto.append("]");
        System.out.println(nodes_usedto.toString());
        System.out.println(links_usedto.toString());
        String resultJson_usedto = "{"+nodes_usedto+","+links_usedto+"}";    //
        System.out.println(resultJson_usedto);
//        System.out.println(nodes_usedto.toString());


        try {
        //    FileOutputStream fos_usedto = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_usedto.json");

            FileOutputStream fos_usedto = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_usedto.json");
            fos_usedto.write(resultJson_usedto.getBytes());
            fos_usedto.close();
            fos_usedto.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_usedto example = new Match2D3_usedto("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_usedto();

    }

}
