package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface tableRepo  extends MongoRepository<Table,Integer> {
  @Query(sort = "{'_tableNo' :  -1}")
    Table findTopByOrderByTableNoDesc();

}
