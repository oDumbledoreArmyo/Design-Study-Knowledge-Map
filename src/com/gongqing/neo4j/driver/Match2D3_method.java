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
public class Match2D3_method {

    Driver driver;

    public Match2D3_method(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_method()
    {
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

        while (result_method.hasNext())
        {
            Record record_method = result_method.next();
            System.out.println(record_method);
            List<Value> list = record_method.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_method.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_method.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_method.append("\"id\":"+n.id());
                        }
                    }

                    nodes_method.append("},");
                }
                nodes_method=new StringBuffer(nodes_method.toString().substring(0,nodes_method.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_method.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_method.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_method.append(",\"type\":\""+r.type()+"\"");
                    links_method.append("},");
                }
                links_method=new StringBuffer(links_method.toString().substring(0,links_method.toString().length()-1));

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
            FileOutputStream fos_method = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_method.json");
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
