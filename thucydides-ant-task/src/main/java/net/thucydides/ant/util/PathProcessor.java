package net.thucydides.ant.util;


import java.io.File;

public class PathProcessor {
    public String normalize(String path) {
        if (path.startsWith("classpath:")) {
            return classpath(path);
        } else {
            return path;
        }
    }

    private String classpath(String path) {
        String corePath = path.replace("classpath:","");
        return Thread.currentThread().getContextClassLoader().getResource(corePath).getPath();
    }
}
