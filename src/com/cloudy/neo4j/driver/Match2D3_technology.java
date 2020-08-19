package com.cloudy.neo4j.driver;

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
public class Match2D3_technology {

    Driver driver;

    public Match2D3_technology(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_technology()
    {
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

        while (result_technology.hasNext())
        {
            Record record_technology = result_technology.next();
            System.out.println(record_technology);
            List<Value> list = record_technology.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_technology.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_technology.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_technology.append("\"id\":"+n.id());
                        }
                    }

                    nodes_technology.append("},");
                }
                nodes_technology=new StringBuffer(nodes_technology.toString().substring(0,nodes_technology.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_technology.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_technology.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_technology.append(",\"type\":\""+r.type()+"\"");
                    links_technology.append("},");
                }
                links_technology=new StringBuffer(links_technology.toString().substring(0,links_technology.toString().length()-1));

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
            FileOutputStream fos_technology = new FileOutputStream("E:\\勇攀学术高峰\\输入\\教程\\neo4j_web\\/Neo4jSon_technology.json");
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
