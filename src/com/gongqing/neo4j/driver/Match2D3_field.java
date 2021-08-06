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
public class Match2D3_field {

    Driver driver;
    String Lable;

    public Match2D3_field(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_field()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();
        Lable = "Field";

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_field = session.run(
        //!!!查库语句在这!!!
        "MATCH p= (:" + Lable + ")-[*1..2]->(:" + Lable + ") RETURN p"
        // "MATCH p = (:Field)-[]->(:Field) RETURN p"
                    );

        StringBuffer nodes_field = new StringBuffer();
        StringBuffer links_field = new StringBuffer();
        nodes_field.append("\"nodes_field\":[");
        links_field.append("\"links_field\":[");

        while (result_field.hasNext()) {
            Record record = result_field.next();
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
                    nodes_field.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_field.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_field.append("\"" + content + "\",");
                    }
                    nodes_field.append("\"id\":");
                    nodes_field.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_field.append(",");
                    nodes_field.append("\"type\":");
                    nodes_field.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_field.hasNext()) {
                        nodes_field.append("}");
                    } else {
                        nodes_field.append("},");
                    }

                }
                nodes_field = new StringBuffer(nodes_field.toString().substring(0, nodes_field.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_field.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_field.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_field.append(",\"type\":\"" + r.type() + "\"");
                    links_field.append("},");
                }
                links_field = new StringBuffer(links_field.toString().substring(0, links_field.toString().length() - 1));
            }
            nodes_field.append(",");
            links_field.append(",");

        }
        nodes_field=new StringBuffer(nodes_field.toString().substring(0,nodes_field.toString().length()-1));
        links_field=new StringBuffer(links_field.toString().substring(0,links_field.toString().length()-1));

        nodes_field.append("]");
        links_field.append("]");
        System.out.println(nodes_field.toString());
        System.out.println(links_field.toString());
        String resultJson_field = "{"+nodes_field+","+links_field+"}";    //
        System.out.println(resultJson_field);
//        System.out.println(nodes_field.toString());


        try {
           // FileOutputStream fos_field = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_field.json");

            FileOutputStream fos_field = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_field.json");
            fos_field.write(resultJson_field.getBytes());
            fos_field.close();
            fos_field.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_field example = new Match2D3_field("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_field();


    }

}
