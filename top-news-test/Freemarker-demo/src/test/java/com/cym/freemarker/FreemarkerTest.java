package com.cym.freemarker;

import com.cym.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {
    @Autowired
    private Configuration configuration;

    @Test
    public void getFile() throws IOException, TemplateException {
        Template template = configuration.getTemplate("02-list.ftl");
        template.process(getData(), new FileWriter("D:\\Code\\Java\\heima-leadnews\\list.html"));
    }

    private Map getData(){
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setName("小明");
        student1.setAge(18);
        student1.setMoney(1000.23f);

        Student student2 = new Student();
        student2.setName("小红");
        student2.setAge(20);
        student2.setMoney(100f);

        map.put("stu1", student1);
        map.put("stu2", student2);

        students.add(student1);
        students.add(student2);
        map.put("stus", students);
        return map;
    }
}
