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
public class Match2D3_search{

    Driver driver;
    String Lable;

    public Match2D3_search(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_search()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();
        Lable = "Field";

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_search = session.run(
                //!!!查库语句在这!!!
                "MATCH p= (:" + Lable + ")-->(:" + Lable + ") RETURN p"
                // "MATCH p = (:search)-[]->(:search) RETURN p"
        );

        StringBuffer nodes_search = new StringBuffer();
        StringBuffer links_search = new StringBuffer();
        nodes_search.append("\"nodes_search\":[");
        links_search.append("\"links_search\":[");

        while (result_search.hasNext()) {
            Record record = result_search.next();
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
                    nodes_search.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_search.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_search.append("\"" + content + "\",");
                    }
                    nodes_search.append("\"id\":");
                    nodes_search.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_search.append(",");
                    nodes_search.append("\"type\":");
                    nodes_search.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_search.hasNext()) {
                        nodes_search.append("}");
                    } else {
                        nodes_search.append("},");
                    }

                }
                nodes_search = new StringBuffer(nodes_search.toString().substring(0, nodes_search.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_search.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_search.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_search.append(",\"type\":\"" + r.type() + "\"");
                    links_search.append("},");
                }
                links_search = new StringBuffer(links_search.toString().substring(0, links_search.toString().length() - 1));
            }
            nodes_search.append(",");
            links_search.append(",");

        }
        nodes_search=new StringBuffer(nodes_search.toString().substring(0,nodes_search.toString().length()-1));
        links_search=new StringBuffer(links_search.toString().substring(0,links_search.toString().length()-1));

        nodes_search.append("]");
        links_search.append("]");
        System.out.println(nodes_search.toString());
        System.out.println(links_search.toString());
        String resultJson_search = "{"+nodes_search+","+links_search+"}";    //
        System.out.println(resultJson_search);
//        System.out.println(nodes_search.toString());


        try {
            FileOutputStream fos_search = new FileOutputStream("/Users/gongqing/Desktop/勇攀学术高峰/输出/Design-study-knowledge-map/Neo4jSon_search.json");

            // FileOutputStream fos_search = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_search.json");
            fos_search.write(resultJson_search.getBytes());
            fos_search.close();
            fos_search.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_search example = new Match2D3_search("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_search();

    }

}
