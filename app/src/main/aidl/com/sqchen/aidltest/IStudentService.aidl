// IStudentService.aidl
package com.sqchen.aidltest;

// Declare any non-default types here with import statements
import com.sqchen.aidltest.Student;
import com.sqchen.aidltest.ITaskCallback;

interface IStudentService {

    List<Student> getStudentList();

    //定向tag
    void addStudent(in Student student);

    void register(ITaskCallback callback);

    void unregister(ITaskCallback callback);
}
