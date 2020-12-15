package com.yb.MultiDataSource1.controller;

import com.yb.MultiDataSource1.entity.Student;
import com.yb.MultiDataSource1.mapper.StudentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class OrderController {
    @Autowired
    private StudentMapper studentMapper;

    @RequestMapping("/save")
    public String saveStudentByMaster(Student student){
        log.info(student.toString());
        return studentMapper.save(student) + "";
    }

    @RequestMapping("/query/{name}") // query by name
    @ResponseBody
    public Student queryStudentBySlave1(@PathVariable("name") String name){
        return studentMapper.selectByName(name);
    }

}
