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
public class Match2D3_belongto {

    Driver driver;

    public Match2D3_belongto(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_belongto()
    {
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

        while (result_belongto.hasNext())
        {
            Record record_belongto = result_belongto.next();
            System.out.println(record_belongto);
            List<Value> list = record_belongto.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_belongto.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_belongto.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_belongto.append("\"id\":"+n.id());
                        }
                    }

                    nodes_belongto.append("},");
                }
                nodes_belongto=new StringBuffer(nodes_belongto.toString().substring(0,nodes_belongto.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_belongto.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_belongto.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_belongto.append(",\"type\":\""+r.type()+"\"");
                    links_belongto.append("},");
                }
                links_belongto=new StringBuffer(links_belongto.toString().substring(0,links_belongto.toString().length()-1));

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
