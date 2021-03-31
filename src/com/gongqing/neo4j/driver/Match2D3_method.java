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
public class Match2D3_method {

    Driver driver;

    public Match2D3_method(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_method()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_method = session.run(
                //!!!查库语句在这!!!
             "MATCH p = (:Method)-[]->(:Method) RETURN p"
                    );



        StringBuffer nodes_method = new StringBuffer();
        StringBuffer links_method = new StringBuffer();
        nodes_method.append("\"nodes_method\":[");
        links_method.append("\"links_method\":[");

        while (result_method.hasNext()) {
            Record record = result_method.next();
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
                    nodes_method.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_method.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_method.append("\"" + content + "\",");
                    }
                    nodes_method.append("\"id\":");
                    nodes_method.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_method.append(",");
                    nodes_method.append("\"type\":");
                    nodes_method.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_method.hasNext()) {
                        nodes_method.append("}");
                    } else {
                        nodes_method.append("},");
                    }

                }
                nodes_method = new StringBuffer(nodes_method.toString().substring(0, nodes_method.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_method.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_method.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_method.append(",\"type\":\"" + r.type() + "\"");
                    links_method.append("},");
                }
                links_method = new StringBuffer(links_method.toString().substring(0, links_method.toString().length() - 1));
            }
            nodes_method.append(",");
            links_method.append(",");

        }
        nodes_method=new StringBuffer(nodes_method.toString().substring(0,nodes_method.toString().length()-1));
        links_method=new StringBuffer(links_method.toString().substring(0,links_method.toString().length()-1));

        nodes_method.append("]");
        links_method.append("]");
        System.out.println(nodes_method.toString());
        System.out.println(links_method.toString());
        String resultJson_method = "{"+nodes_method+","+links_method+"}";    //
        System.out.println(resultJson_method);
//        System.out.println(nodes_method.toString());


        try {
            FileOutputStream fos_method = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_method.json");

          //  FileOutputStream fos_method = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_method.json");
            fos_method.write(resultJson_method.getBytes());
            fos_method.close();
            fos_method.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_method example = new Match2D3_method("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_method();

    }

}
