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
public class Match2D3_usedto {

    Driver driver;

    public Match2D3_usedto(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile_usedto()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result_usedto = session.run(
                //!!!查库语句在这!!!
             "MATCH p = ()-[:使用]->() RETURN p"
                    );



        StringBuffer nodes_usedto = new StringBuffer();
        StringBuffer links_usedto = new StringBuffer();
        nodes_usedto.append("\"nodes_usedto\":[");
        links_usedto.append("\"links_usedto\":[");

        while (result_usedto.hasNext())
        {
            Record record_usedto = result_usedto.next();
            System.out.println(record_usedto);
            List<Value> list = record_usedto.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
 //               System.out.println(n.labels());
                    nodes_usedto.append("{");
 //                  System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
                      System.out.println(k+"-"+n.get(k));
                      //怎么删除重复节点？

                        nodes_usedto.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes_usedto.append("\"id\":"+n.id());
                        }
                    }

                    nodes_usedto.append("},");
                }
                nodes_usedto=new StringBuffer(nodes_usedto.toString().substring(0,nodes_usedto.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                  System.out.println(n.labels());
                    links_usedto.append("{");
                    System.out.println(r);
                    int num = 0;
                    links_usedto.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links_usedto.append(",\"type\":\""+r.type()+"\"");
                    links_usedto.append("},");
                }
                links_usedto=new StringBuffer(links_usedto.toString().substring(0,links_usedto.toString().length()-1));

            }
            nodes_usedto.append(",");
            links_usedto.append(",");

        }
        nodes_usedto=new StringBuffer(nodes_usedto.toString().substring(0,nodes_usedto.toString().length()-1));
        links_usedto=new StringBuffer(links_usedto.toString().substring(0,links_usedto.toString().length()-1));

        nodes_usedto.append("]");
        links_usedto.append("]");
        System.out.println(nodes_usedto.toString());
        System.out.println(links_usedto.toString());
        String resultJson_usedto = "{"+nodes_usedto+","+links_usedto+"}";    //
        System.out.println(resultJson_usedto);
//        System.out.println(nodes_usedto.toString());


        try {
            FileOutputStream fos_usedto = new FileOutputStream("E:\\勇攀学术高峰\\输出\\Design-study-knowledge-map\\Neo4jSon_usedto.json");
            fos_usedto.write(resultJson_usedto.getBytes());
            fos_usedto.close();
            fos_usedto.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3_usedto example = new Match2D3_usedto("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        example.gernerateJsonFile_usedto();

    }

}
