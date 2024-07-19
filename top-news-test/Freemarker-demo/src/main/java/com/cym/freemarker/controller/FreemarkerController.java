package com.cym.freemarker.controller;

import com.cym.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class FreemarkerController {

    @GetMapping("/basic")
    public String freemarkerBasic(Model model){
        model.addAttribute("name", "freemarker");
        Student student = new Student();
        student.setName("小明");
        student.setAge(18);
        model.addAttribute("stu", student);
        return "01-basic";
    }

    @GetMapping("/list")
    public String freemarkerList(Model model){
        ArrayList<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.setName("小明");
        student1.setAge(18);
        student1.setMoney(1000.23f);

        Student student2 = new Student();
        student2.setName("小红");
        student2.setAge(20);
        student2.setMoney(100f);

        students.add(student1);
        students.add(student2);
        model.addAttribute("stus", students);
        System.err.println(students);

        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", student1);
        stuMap.put("stu2", student2);
        model.addAttribute("stusMap", stuMap);

        return "02-list";
    }
}
