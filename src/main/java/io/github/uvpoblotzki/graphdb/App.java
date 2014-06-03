package io.github.uvpoblotzki.graphdb;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring-Konfiguration für die Anwendung.
 */
@Configuration
@EnableNeo4jRepositories
@EnableTransactionManagement
public class App extends Neo4jConfiguration {

  /**
   * Startet den Parser
   *
   * @param args eine Liste von Quell-Verzeichnissen
   */
  public static void main(final String[] args) {
    System.out.println("Starte import ...");
    final BeanFactory ctx = new AnnotationConfigApplicationContext(App.class);
    final ClassNodeParser parser = ctx.getBean(ClassNodeParser.class);
    final Collection<File> sourceDirs = new ArrayList<>(args.length);
    for (final String dirName : args) {
      sourceDirs.add(new File(dirName));
    }
    parser.saveClassNodes(sourceDirs);
  }

  @Bean
  public ClassNodeParser classNodeParser() {
    return new ClassNodeParser();
  }

  @Bean
  public GraphDatabaseService graphDatabaseService() {
    return new SpringRestGraphDatabase("http://localhost:7474/db/data");
  }
}
