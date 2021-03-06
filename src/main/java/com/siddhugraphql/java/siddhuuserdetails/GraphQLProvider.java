package com.siddhugraphql.java.siddhuuserdetails;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.siddhugraphql.java.siddhuuserdetails.resolvers.User;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Component
public class GraphQLProvider {


    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;
    
  /*  @Autowired
	com.siddhugraphql.java.siddhuuserdetails.resolvers.UserSubscription objUserSubscription;
*/
    private GraphQL graphQL;
    
    
    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

 
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("userById", graphQLDataFetchers.getUserByIdDataFetcher()))
                .type(newTypeWiring("User")
                        .dataFetcher("address", graphQLDataFetchers.getUserDataFetcher()))
                //by siddhu for mutation start[
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createUser", graphQLDataFetchers.createUserDataFetcher()))
                //by siddhu for mutation end]
                //by siddhu for subscription start[
              /* .type(newTypeWiring("Subscription")
                        .dataFetcher("stockQuotes", graphQLDataFetchers.stockQuotesDataFetcher()))*/
                
           /*   .type(newTypeWiring("Subscription")
                        .dataFetcher("newUser", graphQLDataFetchers.newUserDataFetcher()))*/
                //by siddhu for subscription end]
                .build();
    }    

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}
