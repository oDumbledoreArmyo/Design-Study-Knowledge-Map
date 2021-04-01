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
public class Match2D3_belongto {

    Driver driver;

    public Match2D3_belongto(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_belongto()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_belongto = session.run(
                //!!!查库语句在这!!!
             "MATCH p = ()-[:从属]->() RETURN p"
                    );



        StringBuffer nodes_belongto = new StringBuffer();
        StringBuffer links_belongto = new StringBuffer();
        nodes_belongto.append("\"nodes_belongto\":[");
        links_belongto.append("\"links_belongto\":[");
        while (result_belongto.hasNext()) {
            Record record = result_belongto.next();
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
                    nodes_belongto.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_belongto.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_belongto.append("\"" + content + "\",");
                    }
                    nodes_belongto.append("\"id\":");
                    nodes_belongto.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_belongto.append(",");
                    nodes_belongto.append("\"type\":");
                    nodes_belongto.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_belongto.hasNext()) {
                        nodes_belongto.append("}");
                    } else {
                        nodes_belongto.append("},");
                    }

                }
                nodes_belongto = new StringBuffer(nodes_belongto.toString().substring(0, nodes_belongto.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_belongto.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_belongto.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_belongto.append(",\"type\":\"" + r.type() + "\"");
                    links_belongto.append("},");
                }
                links_belongto = new StringBuffer(links_belongto.toString().substring(0, links_belongto.toString().length() - 1));
            }
            nodes_belongto.append(",");
            links_belongto.append(",");

        }

        nodes_belongto=new StringBuffer(nodes_belongto.toString().substring(0,nodes_belongto.toString().length()-1));
        links_belongto=new StringBuffer(links_belongto.toString().substring(0,links_belongto.toString().length()-1));

        nodes_belongto.append("]");
        links_belongto.append("]");
        System.out.println(nodes_belongto.toString());
        System.out.println(links_belongto.toString());
        String resultJson_belongto = "{"+nodes_belongto+","+links_belongto+"}";    //
        System.out.println(resultJson_belongto);
//        System.out.println(nodes_belongto.toString());


        try {
           // FileOutputStream fos_belongto = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_belongto.json");

            FileOutputStream fos_belongto = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_belongto.json");
            fos_belongto.write(resultJson_belongto.getBytes());
            fos_belongto.close();
            fos_belongto.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_belongto example = new Match2D3_belongto("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_belongto();

    }

}
