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
public class Match2D3_technology {

    Driver driver;

    public Match2D3_technology(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_technology()
    {
        Set nodeSet = new HashSet();
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_technology = session.run(
                //!!!查库语句在这!!!
             "MATCH p = (:Technology)-[]->(:Technology) RETURN p"
                    );



        StringBuffer nodes_technology = new StringBuffer();
        StringBuffer links_technology = new StringBuffer();
        nodes_technology.append("\"nodes_technology\":[");
        links_technology.append("\"links_technology\":[");

        while (result_technology.hasNext()) {
            Record record = result_technology.next();
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
                    nodes_technology.append("{");
                    //节点属性
                    while (nodeKeys.hasNext()) {
                        String nodeKey = nodeKeys.next();
                        nodes_technology.append("\"" + nodeKey + "\":");
                        //node.get(nodeKey).toString();
                        //System.out.println(node.get(nodeKey).asObject().toString());
                        String content = node.get(nodeKey).asObject().toString();
                        nodes_technology.append("\"" + content + "\",");
                    }
                    nodes_technology.append("\"id\":");
                    nodes_technology.append(node.id());

                    //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                    //但是我们这里只属于一个类别！
                    Iterator<String> nodeTypes = node.labels().iterator();
                    //得到节点类型了！
                    String nodeType = nodeTypes.next();

                    nodes_technology.append(",");
                    nodes_technology.append("\"type\":");
                    nodes_technology.append("\"" + nodeType + "\"");

                    //将节点添加到set集合中
                    nodeSet.add(node.id());
                    if (!nodes2.hasNext() && !result_technology.hasNext()) {
                        nodes_technology.append("}");
                    } else {
                        nodes_technology.append("},");
                    }

                }
                nodes_technology = new StringBuffer(nodes_technology.toString().substring(0, nodes_technology.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                  System.out.println(n.labels());
                    links_technology.append("{");
                    //        System.out.println(r);
                    int num = 0;
                    links_technology.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                    links_technology.append(",\"type\":\"" + r.type() + "\"");
                    links_technology.append("},");
                }
                links_technology = new StringBuffer(links_technology.toString().substring(0, links_technology.toString().length() - 1));
            }
            nodes_technology.append(",");
            links_technology.append(",");

        }
        nodes_technology=new StringBuffer(nodes_technology.toString().substring(0,nodes_technology.toString().length()-1));
        links_technology=new StringBuffer(links_technology.toString().substring(0,links_technology.toString().length()-1));

        nodes_technology.append("]");
        links_technology.append("]");
        System.out.println(nodes_technology.toString());
        System.out.println(links_technology.toString());
        String resultJson_technology = "{"+nodes_technology+","+links_technology+"}";    //
        System.out.println(resultJson_technology);
//        System.out.println(nodes_technology.toString());


        try {
            FileOutputStream fos_technology = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_technology.json");
            fos_technology.write(resultJson_technology.getBytes());
            fos_technology.close();
            fos_technology.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_technology example = new Match2D3_technology("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_technology();

    }

}
