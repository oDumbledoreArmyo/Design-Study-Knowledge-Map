package com.gongqing.neo4j.driver;

import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.io.FileOutputStream;
import java.util.List;

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

        while (result_data.hasNext())
        {
            Record record_data = result_data.next();
            System.out.println(record_data);
            List<Value> list = record_data.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_data.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_data.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_data.append("\"id\":"+n.id());
                        }
                    }

                    nodes_data.append("},");
                }
                nodes_data=new StringBuffer(nodes_data.toString().substring(0,nodes_data.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_data.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_data.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_data.append(",\"type\":\""+r.type()+"\"");
                    links_data.append("},");
                }
                links_data=new StringBuffer(links_data.toString().substring(0,links_data.toString().length()-1));

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
            FileOutputStream fos_data = new FileOutputStream("E:\\勇攀学术高峰\\输入\\教程\\neo4j_web\\Neo4jSon_data.json");
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
