package com.cloudy.neo4j.driver;

import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.graphdb.Relationship;

import java.util.Iterator;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class SmallExample
{
    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;

    public SmallExample(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    private void addPerson(String name)
    {
        // Sessions are lightweight and disposable connection wrappers.
        try (Session session = driver.session())
        {
            // Wrapping a Cypher Query in a Managed Transaction provides atomicity
            // and makes handling errors much easier.
            // Use `session.writeTransaction` for writes and `session.readTransaction` for reading data.
            // These methods are also able to handle connection problems and transient errors using an automatic retry mechanism.
            session.writeTransaction(tx -> tx.run("MERGE (a:People {name: $x})", parameters("x", name)));
        }
    }

    private void printPeople(String initial)
    {
        try (Session session = driver.session())
        {
            // A Managed transaction is a quick and easy way to wrap a Cypher Query.
            // The `session.run` method will run the specified Query.
            // This simpler method does not use any automatic retry mechanism.
            Result result = session.run(
                    "MATCH (a:People) WHERE a.name STARTS WITH $x RETURN a.name AS PeopleName",
                    parameters("x", initial));
            // Each Cypher execution returns a stream of records.
            while (result.hasNext())
            {
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record);
                System.out.println(record.get("PeopleName").asString()); //get里是别名
            }
        }
    }
    private void getPeoples()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result =  session.run("MATCH (b:People) RETURN b");
        // Each Cypher execution returns a stream of records.
        while (result.hasNext())
        {
            //Record 是一行记录，内容是什么取决于你return的东西
            Record record = result.next();
            System.out.println(record);

            List<Value> list = record.values();
            for(Value v : list)
            {
                Node n = v.asNode();
                System.out.println(n.labels().iterator().next()+"--"+n.id());

                for(String k:n.keys())
                {
                    System.out.println(k+"---"+n.get(k) );
                }
                System.out.println("==========================");

            }

            // Values can be extracted from a record by index or name.
//            System.out.println(record.get("b").asString());
        }

    }

    private void getPeoplesAndRelation()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        Result result =  session.run(
                "MATCH p=(b:People)-[]->(c) RETURN p");
//                "MATCH (b:People)-[]-(c) RETURN b,c");
        // Each Cypher execution returns a stream of records.
        while (result.hasNext())
        {
            //Record 是一行记录，内容是什么取决于你return的东西
            Record record = result.next();
            System.out.println(record);

            List<Value> list = record.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                Node start = p.start();
                for(String k:start.keys())
                {
                    System.out.println(k+"---"+start.get(k) );
                }
                System.out.println("==========================");
               Iterator i =  p.relationships().iterator();
                while(i.hasNext() )
                {
                    System.out.println(i.next()+"---------------------------");
                    Relationship r = (Relationship) i.next();
//                    System.out.println(r.type());
//                    System.out.println(r.startNodeId()+ "->" +r.endNodeId());
//                    System.out.println(r.id);
                }

                Node end = p.end();
                for(String k:start.keys())
                {
                    System.out.println(k+"---"+end.get(k) );
                }
                System.out.println("==========================");
//                Node n = v.asNode();
//                System.out.println(n.labels().iterator().next()+"--"+n.id());
//
//                for(String k:n.keys())
//                {
//                    System.out.println(k+"---"+n.get(k) );
//                }
//                System.out.println("==========================");

            }

            // Values can be extracted from a record by index or name.
//            System.out.println(record.get("b").asString());
        }

    }
    public void close()
    {
        // Closing a driver immediately shuts down all open connections.
        driver.close();
    }

    public static void main(String... args)
    {
        SmallExample example = new SmallExample("bolt://localhost:7687", "neo4j", "y2fbd7vx");
        //example.addPerson("Ada");
       // example.addPerson("Alice");
       // example.addPerson("Bob");
       // example.printPeople("A");
        //example.getPeoples();
        example.getPeoplesAndRelation();
        example.close();
    }
}