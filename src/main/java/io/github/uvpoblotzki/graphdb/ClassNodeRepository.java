package io.github.uvpoblotzki.graphdb;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Einfaches Graph-Repository für {@link ClassNode} Objekte.
 */
@Repository
public interface ClassNodeRepository extends GraphRepository<ClassNode> {

  ClassNode findByName(String name);

}
