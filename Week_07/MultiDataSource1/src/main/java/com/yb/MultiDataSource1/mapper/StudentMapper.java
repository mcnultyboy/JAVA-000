package com.yb.MultiDataSource1.mapper;

import com.yb.MultiDataSource1.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentMapper {

    @Insert("insert into student(name,age) values (#{name}, #{age})")
    int save(Student student);

    @Select("select * from student")
    List<Student> selectAll();

    @Select("select * from student where name=#{name}")
    Student selectByName(String name);
}
