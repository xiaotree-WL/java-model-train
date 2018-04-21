package org.bosszp.nlp.microser.controller;

import org.bosszp.nlp.microser.model.TaskInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


/**
 * Created by wanglin on 18-4-8.
 * 数据库操作方法定义接口
 */
public interface TaskRepository extends MongoRepository<TaskInfo, String> {

    TaskInfo findByName(String name);

    TaskInfo findById(Long id);

    Page<TaskInfo> findByName(String name, Pageable pageable);

    List<TaskInfo> findByNameAndErrorMessage(String name, String errorMessage);
}
