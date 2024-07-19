package com.cym.minio;

public class Test {
    public static void main(String[] args) {
        Father child = new Child("xiaoxue");
        child.setAge("30");
        child.setName("cheng");
        System.out.println(child);
    }
}
