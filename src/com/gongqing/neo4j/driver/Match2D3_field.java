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
public class Match2D3_field {

    Driver driver;

    public Match2D3_field(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_field()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_field = session.run(
                //!!!查库语句在这!!!
             "MATCH p = (:Field)-[]->(:Field) RETURN p"
                    );



        StringBuffer nodes_field = new StringBuffer();
        StringBuffer links_field = new StringBuffer();
        nodes_field.append("\"nodes_field\":[");
        links_field.append("\"links_field\":[");

        while (result_field.hasNext())
        {
            Record record_field = result_field.next();
            System.out.println(record_field);
            List<Value> list = record_field.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_field.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_field.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_field.append("\"id\":"+n.id());
                        }
                    }

                    nodes_field.append("},");
                }
                nodes_field=new StringBuffer(nodes_field.toString().substring(0,nodes_field.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_field.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_field.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_field.append(",\"type\":\""+r.type()+"\"");
                    links_field.append("},");
                }
                links_field=new StringBuffer(links_field.toString().substring(0,links_field.toString().length()-1));

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
