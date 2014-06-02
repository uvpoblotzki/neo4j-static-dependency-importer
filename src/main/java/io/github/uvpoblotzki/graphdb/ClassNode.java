package io.github.uvpoblotzki.graphdb;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class ClassNode {

  private Long id;
  private String name;
  private Set<ClassNode> dependsOn;

  @GraphId
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @Indexed
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @RelatedTo(type = "DEPENDS_ON", direction = Direction.OUTGOING)
  public Set<ClassNode> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(final Set<ClassNode> dependsOn) {
    this.dependsOn = dependsOn;
  }
}
